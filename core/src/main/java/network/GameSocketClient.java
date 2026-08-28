package network;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import shared.protocol.Protocol;
import shared.protocol.WsMessageType;

public final class GameSocketClient implements WebSocket.Listener {
    private final NetworkConfig config;
    private final HttpClient http = HttpClient.newHttpClient();
    private final List<Consumer<Protocol.ParsedMessage>> listeners = new CopyOnWriteArrayList<>();
    private final StringBuilder buffer = new StringBuilder();

    private volatile WebSocket socket;
    private volatile String token;
    private volatile boolean authenticated;

    public GameSocketClient(NetworkConfig config) {
        this.config = config;
    }

    public void addListener(Consumer<Protocol.ParsedMessage> listener) {
        listeners.add(listener);
    }

    public void removeListener(Consumer<Protocol.ParsedMessage> listener) {
        listeners.remove(listener);
    }

    public synchronized void connect(String sessionToken) {
        this.token = sessionToken;
        this.authenticated = false;
        closeQuietly();
        http.newWebSocketBuilder()
                .buildAsync(URI.create(config.wsUrl()), this)
                .whenComplete((ws, err) -> {
                    if (err != null) {
                        dispatch(Protocol.parse(Protocol.envelope(WsMessageType.ERROR,
                                Map.of("error", "WS_CONNECT_FAILED"))));
                    }
                });
    }

    public synchronized void disconnect() {
        authenticated = false;
        token = null;
        closeQuietly();
    }

    public boolean isAuthenticated() {
        return authenticated && socket != null;
    }

    public void send(WsMessageType type, Object payload) {
        WebSocket ws = socket;
        if (ws == null) {
            return;
        }
        ws.sendText(Protocol.envelope(type, payload), true);
    }

    public void joinQueue() {
        send(WsMessageType.QUEUE_JOIN, Map.of());
    }

    public void leaveQueue() {
        send(WsMessageType.QUEUE_LEAVE, Map.of());
    }

    public void invite(String username) {
        send(WsMessageType.INVITE, Map.of("username", username));
    }

    public void acceptInvite() {
        send(WsMessageType.INVITE_ACCEPT, Map.of());
    }

    public void rejectInvite() {
        send(WsMessageType.INVITE_REJECT, Map.of());
    }

    public void lookupUser(String username) {
        send(WsMessageType.LOOKUP_USER, Map.of("username", username));
    }

    public void placePlant(String type, int row, int col) {
        send(WsMessageType.PLACE_PLANT, Map.of("type", type, "row", row, "col", col));
    }

    public void placeZombie(String type, int row, int col) {
        send(WsMessageType.PLACE_ZOMBIE, Map.of("type", type, "row", row, "col", col));
    }

    public void quickMessage(String messageId) {
        send(WsMessageType.QUICK_MSG, Map.of("messageId", messageId));
    }

    public void reportBrain(int row) {
        send(WsMessageType.BRAIN_COLLECTED, Map.of("brainRow", row));
    }

    public void reportNoResources() {
        send(WsMessageType.REPORT_NO_RESOURCES, Map.of());
    }

    @Override
    public void onOpen(WebSocket webSocket) {
        this.socket = webSocket;
        webSocket.request(1);
        if (token != null) {
            send(WsMessageType.AUTH, Map.of("token", token));
        }
    }

    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
        buffer.append(data);
        if (last) {
            String raw = buffer.toString();
            buffer.setLength(0);
            try {
                Protocol.ParsedMessage msg = Protocol.parse(raw);
                if (msg.type == WsMessageType.AUTH_OK) {
                    authenticated = true;
                } else if (msg.type == WsMessageType.AUTH_FAIL) {
                    authenticated = false;
                }
                dispatch(msg);
            } catch (Exception ignored) {
            }
        }
        webSocket.request(1);
        return null;
    }

    @Override
    public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
        authenticated = false;
        if (this.socket == webSocket) {
            this.socket = null;
        }
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public void onError(WebSocket webSocket, Throwable error) {
        authenticated = false;
    }

    private void dispatch(Protocol.ParsedMessage msg) {
        for (Consumer<Protocol.ParsedMessage> listener : new ArrayList<>(listeners)) {
            try {
                listener.accept(msg);
            } catch (Exception ignored) {
            }
        }
    }

    private void closeQuietly() {
        WebSocket ws = socket;
        socket = null;
        if (ws != null) {
            try {
                ws.sendClose(WebSocket.NORMAL_CLOSURE, "bye");
            } catch (Exception ignored) {
            }
        }
    }
}
