package network;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
<<<<<<< Updated upstream
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CopyOnWriteArrayList;
=======
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
>>>>>>> Stashed changes
import java.util.function.Consumer;

import shared.protocol.Protocol;
import shared.protocol.WsMessageType;

public final class GameSocketClient implements WebSocket.Listener {
<<<<<<< Updated upstream
    private final NetworkConfig config;
    private final HttpClient http = HttpClient.newHttpClient();
    private final List<Consumer<Protocol.ParsedMessage>> listeners = new CopyOnWriteArrayList<>();
    private final StringBuilder buffer = new StringBuilder();
=======
    private static final int MAX_PENDING = 32;

    private final NetworkConfig config;
    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();
    private final List<Consumer<Protocol.ParsedMessage>> listeners = new CopyOnWriteArrayList<>();
    private final StringBuilder buffer = new StringBuilder();
    private final Queue<PendingMessage> pending = new ArrayDeque<>();
>>>>>>> Stashed changes

    private volatile WebSocket socket;
    private volatile String token;
    private volatile boolean authenticated;
<<<<<<< Updated upstream
=======
    private volatile CompletableFuture<Boolean> authFuture = CompletableFuture.completedFuture(false);
>>>>>>> Stashed changes

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
<<<<<<< Updated upstream
        closeQuietly();
        http.newWebSocketBuilder()
                .buildAsync(URI.create(config.wsUrl()), this)
                .whenComplete((ws, err) -> {
                    if (err != null) {
=======
        this.authFuture = new CompletableFuture<>();
        pending.clear();
        closeQuietly();
        http.newWebSocketBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .buildAsync(URI.create(config.wsUrl()), this)
                .whenComplete((ws, err) -> {
                    if (err != null) {
                        authenticated = false;
                        completeAuth(false);
>>>>>>> Stashed changes
                        dispatch(Protocol.parse(Protocol.envelope(WsMessageType.ERROR,
                                Map.of("error", "WS_CONNECT_FAILED"))));
                    }
                });
    }

    public synchronized void disconnect() {
        authenticated = false;
        token = null;
<<<<<<< Updated upstream
=======
        pending.clear();
        completeAuth(false);
>>>>>>> Stashed changes
        closeQuietly();
    }

    public boolean isAuthenticated() {
        return authenticated && socket != null;
    }

<<<<<<< Updated upstream
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
=======
    public boolean awaitAuthenticated(long timeoutMs) {
        if (isAuthenticated()) {
            return true;
        }
        try {
            Boolean ok = authFuture.get(timeoutMs, TimeUnit.MILLISECONDS);
            return Boolean.TRUE.equals(ok) && isAuthenticated();
        } catch (Exception e) {
            return isAuthenticated();
        }
    }

    public synchronized boolean send(WsMessageType type, Object payload) {
        if (type == WsMessageType.AUTH) {
            return sendNow(type, payload);
        }
        if (isAuthenticated()) {
            return sendNow(type, payload);
        }
        if (pending.size() >= MAX_PENDING) {
            return false;
        }
        pending.offer(new PendingMessage(type, payload));
        return true;
    }

    public boolean joinQueue() {
        return send(WsMessageType.QUEUE_JOIN, Map.of());
    }

    public boolean leaveQueue() {
        return send(WsMessageType.QUEUE_LEAVE, Map.of());
    }

    public boolean invite(String username) {
        return send(WsMessageType.INVITE, Map.of("username", username));
    }

    public boolean acceptInvite() {
        return send(WsMessageType.INVITE_ACCEPT, Map.of());
    }

    public boolean rejectInvite() {
        return send(WsMessageType.INVITE_REJECT, Map.of());
    }

    public boolean lookupUser(String username) {
        return send(WsMessageType.LOOKUP_USER, Map.of("username", username));
    }

    public boolean placePlant(String type, int row, int col) {
        return send(WsMessageType.PLACE_PLANT, Map.of("type", type, "row", row, "col", col));
    }

    public boolean placeZombie(String type, int row, int col) {
        return send(WsMessageType.PLACE_ZOMBIE, Map.of("type", type, "row", row, "col", col));
    }

    public boolean quickMessage(String messageId) {
        return send(WsMessageType.QUICK_MSG, Map.of("messageId", messageId));
    }

    public boolean reportBrain(int row) {
        return send(WsMessageType.BRAIN_COLLECTED, Map.of("brainRow", row));
    }

    public boolean reportNoResources() {
        return send(WsMessageType.REPORT_NO_RESOURCES, Map.of());
    }

    public boolean leaveMatch() {
        return send(WsMessageType.MATCH_LEAVE, Map.of());
    }

    public boolean requestRestart() {
        return send(WsMessageType.MATCH_RESTART_REQUEST, Map.of());
    }

    public boolean acceptRestart() {
        return send(WsMessageType.MATCH_RESTART_ACCEPT, Map.of());
    }

    public boolean rejectRestart() {
        return send(WsMessageType.MATCH_RESTART_REJECT, Map.of());
    }

    public boolean cancelRestart() {
        return send(WsMessageType.MATCH_RESTART_CANCEL, Map.of());
>>>>>>> Stashed changes
    }

    @Override
    public void onOpen(WebSocket webSocket) {
        this.socket = webSocket;
        webSocket.request(1);
        if (token != null) {
<<<<<<< Updated upstream
            send(WsMessageType.AUTH, Map.of("token", token));
=======
            sendNow(WsMessageType.AUTH, Map.of("token", token));
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
                } else if (msg.type == WsMessageType.AUTH_FAIL) {
                    authenticated = false;
=======
                    completeAuth(true);
                    flushPending();
                } else if (msg.type == WsMessageType.AUTH_FAIL) {
                    authenticated = false;
                    pending.clear();
                    completeAuth(false);
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
=======
        pending.clear();
        completeAuth(false);
>>>>>>> Stashed changes
        if (this.socket == webSocket) {
            this.socket = null;
        }
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public void onError(WebSocket webSocket, Throwable error) {
        authenticated = false;
<<<<<<< Updated upstream
=======
        completeAuth(false);
    }

    private synchronized boolean sendNow(WsMessageType type, Object payload) {
        WebSocket ws = socket;
        if (ws == null) {
            return false;
        }
        try {
            ws.sendText(Protocol.envelope(type, payload), true);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private synchronized void flushPending() {
        while (!pending.isEmpty() && isAuthenticated()) {
            PendingMessage msg = pending.poll();
            if (msg != null) {
                sendNow(msg.type, msg.payload);
            }
        }
    }

    private void completeAuth(boolean ok) {
        CompletableFuture<Boolean> future = authFuture;
        if (future != null && !future.isDone()) {
            future.complete(ok);
        }
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
=======

    private static final class PendingMessage {
        final WsMessageType type;
        final Object payload;

        PendingMessage(WsMessageType type, Object payload) {
            this.type = type;
            this.payload = payload;
        }
    }
>>>>>>> Stashed changes
}
