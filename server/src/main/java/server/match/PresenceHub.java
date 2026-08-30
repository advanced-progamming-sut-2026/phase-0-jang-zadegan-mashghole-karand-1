package server.match;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import io.javalin.websocket.WsContext;
import shared.protocol.Protocol;
import shared.protocol.WsMessageType;

public final class PresenceHub {
<<<<<<< Updated upstream
    private final Map<String, WsContext> online = new ConcurrentHashMap<>();
=======
    private final Map<String, WsContext> onlineByUser = new ConcurrentHashMap<>();
    private final Map<String, String> sessionToUser = new ConcurrentHashMap<>();
>>>>>>> Stashed changes
    private Predicate<String> userExists = u -> false;

    public void setUserExistsCheck(Predicate<String> userExists) {
        this.userExists = userExists;
    }

    public void connect(String username, WsContext ctx) {
<<<<<<< Updated upstream
        WsContext previous = online.put(username, ctx);
        if (previous != null && previous != ctx) {
=======
        String sessionId = sessionId(ctx);
        WsContext previous = onlineByUser.put(username, ctx);
        if (previous != null && previous != ctx) {
            String prevSession = sessionId(previous);
            sessionToUser.remove(prevSession, username);
>>>>>>> Stashed changes
            try {
                previous.closeSession();
            } catch (Exception ignored) {
            }
        }
<<<<<<< Updated upstream
=======
        sessionToUser.put(sessionId, username);
>>>>>>> Stashed changes
        broadcastPresence();
    }

    public void disconnect(WsContext ctx) {
<<<<<<< Updated upstream
        online.entrySet().removeIf(e -> e.getValue().equals(ctx));
=======
        String sessionId = sessionId(ctx);
        String username = sessionToUser.remove(sessionId);
        if (username == null) {
            username = findUserByContext(ctx);
        }
        if (username != null) {
            WsContext current = onlineByUser.get(username);
            if (current == null || sameSession(current, ctx)) {
                onlineByUser.remove(username);
                if (current != null) {
                    sessionToUser.remove(sessionId(current), username);
                }
            }
        }
>>>>>>> Stashed changes
        broadcastPresence();
    }

    public String usernameOf(WsContext ctx) {
<<<<<<< Updated upstream
        for (Map.Entry<String, WsContext> e : online.entrySet()) {
            if (e.getValue().equals(ctx)) {
=======
        String bySession = sessionToUser.get(sessionId(ctx));
        if (bySession != null) {
            return bySession;
        }
        return findUserByContext(ctx);
    }

    private String findUserByContext(WsContext ctx) {
        for (Map.Entry<String, WsContext> e : onlineByUser.entrySet()) {
            if (sameSession(e.getValue(), ctx)) {
>>>>>>> Stashed changes
                return e.getKey();
            }
        }
        return null;
    }

    public boolean isOnline(String username) {
<<<<<<< Updated upstream
        return online.containsKey(username);
=======
        return onlineByUser.containsKey(username);
>>>>>>> Stashed changes
    }

    public boolean userExists(String username) {
        return userExists.test(username);
    }

    public Set<String> onlineUsers() {
<<<<<<< Updated upstream
        return Set.copyOf(online.keySet());
    }

    public void send(String username, WsMessageType type, Object payload) {
        WsContext ctx = online.get(username);
        if (ctx != null) {
            ctx.send(Protocol.envelope(type, payload));
=======
        return Set.copyOf(onlineByUser.keySet());
    }

    public void send(String username, WsMessageType type, Object payload) {
        WsContext ctx = onlineByUser.get(username);
        if (ctx != null) {
            try {
                ctx.send(Protocol.envelope(type, payload));
            } catch (Exception ignored) {
            }
>>>>>>> Stashed changes
        }
    }

    public void broadcastPresence() {
        Object payload = Map.of("online", onlineUsers());
<<<<<<< Updated upstream
        for (WsContext ctx : online.values()) {
            ctx.send(Protocol.envelope(WsMessageType.PRESENCE, payload));
=======
        String envelope = Protocol.envelope(WsMessageType.PRESENCE, payload);
        for (WsContext ctx : onlineByUser.values()) {
            try {
                ctx.send(envelope);
            } catch (Exception ignored) {
            }
>>>>>>> Stashed changes
        }
    }

    public void forEachOnline(BiConsumer<String, WsContext> consumer) {
<<<<<<< Updated upstream
        online.forEach(consumer);
=======
        onlineByUser.forEach(consumer);
    }

    private static String sessionId(WsContext ctx) {
        try {
            String id = ctx.sessionId();
            if (id != null && !id.isBlank()) {
                return id;
            }
        } catch (Exception ignored) {
        }
        return Integer.toHexString(System.identityHashCode(ctx));
    }

    private static boolean sameSession(WsContext a, WsContext b) {
        if (a == b) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        return sessionId(a).equals(sessionId(b));
>>>>>>> Stashed changes
    }
}
