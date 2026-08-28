package network;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import shared.dto.MatchStartPayload;
import shared.dto.MatchStatePayload;
import shared.dto.UserProfileDto;
import shared.protocol.Protocol;
import shared.protocol.WsMessageType;

public final class NetworkSession {
    private final NetworkConfig config;
    private final AuthApiClient authApi;
    private final GameSocketClient socket;

    private volatile String token;
    private volatile UserProfileDto profile;
    private volatile MatchStartPayload activeMatch;
    private volatile String pendingInviteFrom;

    private final List<Consumer<NetworkEvent>> listeners = new CopyOnWriteArrayList<>();

    public NetworkSession(NetworkConfig config) {
        this.config = config;
        this.authApi = new AuthApiClient(config);
        this.socket = new GameSocketClient(config);
        this.socket.addListener(this::onSocketMessage);
    }

    public NetworkConfig config() {
        return config;
    }

    public AuthApiClient authApi() {
        return authApi;
    }

    public GameSocketClient socket() {
        return socket;
    }

    public String token() {
        return token;
    }

    public UserProfileDto profile() {
        return profile;
    }

    public boolean isLoggedIn() {
        return token != null && profile != null;
    }

    public MatchStartPayload activeMatch() {
        return activeMatch;
    }

    public String pendingInviteFrom() {
        return pendingInviteFrom;
    }

    public void addListener(Consumer<NetworkEvent> listener) {
        listeners.add(listener);
    }

    public void removeListener(Consumer<NetworkEvent> listener) {
        listeners.remove(listener);
    }

    public void onLoginSuccess(String token, UserProfileDto profile) {
        this.token = token;
        this.profile = profile;
        socket.connect(token);
    }

    public void logout() {
        if (token != null) {
            authApi.logout(token);
        }
        socket.disconnect();
        token = null;
        profile = null;
        activeMatch = null;
        pendingInviteFrom = null;
    }

    public void clearPendingInvite() {
        pendingInviteFrom = null;
    }

    public void clearActiveMatch() {
        activeMatch = null;
    }

    private void onSocketMessage(Protocol.ParsedMessage msg) {
        switch (msg.type) {
            case INVITE_INCOMING -> {
                pendingInviteFrom = msg.getString("from");
                emit(NetworkEvent.inviteIncoming(pendingInviteFrom));
            }
            case INVITE_RESULT -> emit(NetworkEvent.inviteResult(
                    msg.getString("status"), msg.getString("to")));
            case QUEUE_STATUS -> emit(NetworkEvent.queueStatus(msg.getString("status")));
            case MATCH_START -> {
                activeMatch = msg.as(MatchStartPayload.class);
                pendingInviteFrom = null;
                emit(NetworkEvent.matchStart(activeMatch));
            }
            case MATCH_STATE -> emit(NetworkEvent.matchState(msg.as(MatchStatePayload.class)));
            case MATCH_END -> {
                activeMatch = null;
                emit(NetworkEvent.matchEnd(msg.payload.toString()));
            }
            case QUICK_MSG_RECV -> emit(NetworkEvent.quickMessage(
                    msg.getString("from"),
                    msg.getString("messageId"),
                    msg.getString("display"),
                    msg.getString("kind")));
            case LOOKUP_RESULT -> emit(NetworkEvent.lookupResult(
                    msg.getString("username"),
                    msg.payload.has("exists") && msg.payload.get("exists").getAsBoolean(),
                    msg.payload.has("online") && msg.payload.get("online").getAsBoolean()));
            case ERROR -> emit(NetworkEvent.error(msg.getString("error")));
            case PRESENCE -> emit(NetworkEvent.presence(msg.payload.toString()));
            default -> {
            }
        }
    }

    private void emit(NetworkEvent event) {
        for (Consumer<NetworkEvent> listener : new ArrayList<>(listeners)) {
            try {
                listener.accept(event);
            } catch (Exception ignored) {
            }
        }
    }

    public enum EventType {
        INVITE_INCOMING,
        INVITE_RESULT,
        QUEUE_STATUS,
        MATCH_START,
        MATCH_STATE,
        MATCH_END,
        QUICK_MSG,
        LOOKUP_RESULT,
        ERROR,
        PRESENCE
    }

    public static final class NetworkEvent {
        public final EventType type;
        public final String a;
        public final String b;
        public final String c;
        public final String d;
        public final boolean flag1;
        public final boolean flag2;
        public final MatchStartPayload matchStart;
        public final MatchStatePayload matchState;

        private NetworkEvent(EventType type, String a, String b, String c, String d,
                boolean flag1, boolean flag2, MatchStartPayload matchStart, MatchStatePayload matchState) {
            this.type = type;
            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
            this.flag1 = flag1;
            this.flag2 = flag2;
            this.matchStart = matchStart;
            this.matchState = matchState;
        }

        static NetworkEvent inviteIncoming(String from) {
            return new NetworkEvent(EventType.INVITE_INCOMING, from, null, null, null, false, false, null, null);
        }

        static NetworkEvent inviteResult(String status, String to) {
            return new NetworkEvent(EventType.INVITE_RESULT, status, to, null, null, false, false, null, null);
        }

        static NetworkEvent queueStatus(String status) {
            return new NetworkEvent(EventType.QUEUE_STATUS, status, null, null, null, false, false, null, null);
        }

        static NetworkEvent matchStart(MatchStartPayload payload) {
            return new NetworkEvent(EventType.MATCH_START, null, null, null, null, false, false, payload, null);
        }

        static NetworkEvent matchState(MatchStatePayload payload) {
            return new NetworkEvent(EventType.MATCH_STATE, null, null, null, null, false, false, null, payload);
        }

        static NetworkEvent matchEnd(String raw) {
            return new NetworkEvent(EventType.MATCH_END, raw, null, null, null, false, false, null, null);
        }

        static NetworkEvent quickMessage(String from, String id, String display, String kind) {
            return new NetworkEvent(EventType.QUICK_MSG, from, id, display, kind, false, false, null, null);
        }

        static NetworkEvent lookupResult(String username, boolean exists, boolean online) {
            return new NetworkEvent(EventType.LOOKUP_RESULT, username, null, null, null, exists, online, null, null);
        }

        static NetworkEvent error(String error) {
            return new NetworkEvent(EventType.ERROR, error, null, null, null, false, false, null, null);
        }

        static NetworkEvent presence(String raw) {
            return new NetworkEvent(EventType.PRESENCE, raw, null, null, null, false, false, null, null);
        }
    }
}
