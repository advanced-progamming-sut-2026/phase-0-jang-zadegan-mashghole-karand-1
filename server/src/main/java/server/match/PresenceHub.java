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
    private final Map<String, WsContext> online = new ConcurrentHashMap<>();
    private Predicate<String> userExists = u -> false;

    public void setUserExistsCheck(Predicate<String> userExists) {
        this.userExists = userExists;
    }

    public void connect(String username, WsContext ctx) {
        WsContext previous = online.put(username, ctx);
        if (previous != null && previous != ctx) {
            try {
                previous.closeSession();
            } catch (Exception ignored) {
            }
        }
        broadcastPresence();
    }

    public void disconnect(WsContext ctx) {
        online.entrySet().removeIf(e -> e.getValue().equals(ctx));
        broadcastPresence();
    }

    public String usernameOf(WsContext ctx) {
        for (Map.Entry<String, WsContext> e : online.entrySet()) {
            if (e.getValue().equals(ctx)) {
                return e.getKey();
            }
        }
        return null;
    }

    public boolean isOnline(String username) {
        return online.containsKey(username);
    }

    public boolean userExists(String username) {
        return userExists.test(username);
    }

    public Set<String> onlineUsers() {
        return Set.copyOf(online.keySet());
    }

    public void send(String username, WsMessageType type, Object payload) {
        WsContext ctx = online.get(username);
        if (ctx != null) {
            ctx.send(Protocol.envelope(type, payload));
        }
    }

    public void broadcastPresence() {
        Object payload = Map.of("online", onlineUsers());
        for (WsContext ctx : online.values()) {
            ctx.send(Protocol.envelope(WsMessageType.PRESENCE, payload));
        }
    }

    public void forEachOnline(BiConsumer<String, WsContext> consumer) {
        online.forEach(consumer);
    }
}
