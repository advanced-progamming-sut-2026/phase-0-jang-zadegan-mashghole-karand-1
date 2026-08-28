package server.ws;

import java.util.Map;

import io.javalin.Javalin;
import io.javalin.websocket.WsContext;
import server.auth.AuthService;
import server.match.MatchmakingService;
import server.match.PresenceHub;
import shared.dto.PlaceIntent;
import shared.protocol.Protocol;
import shared.protocol.WsMessageType;

public final class GameWebSocket {
    private final AuthService auth;
    private final PresenceHub presence;
    private final MatchmakingService matchmaking;

    public GameWebSocket(AuthService auth, PresenceHub presence, MatchmakingService matchmaking) {
        this.auth = auth;
        this.presence = presence;
        this.matchmaking = matchmaking;
    }

    public void register(Javalin app) {
        app.ws(Protocol.WS_PATH, ws -> {
            ws.onConnect(ctx -> ctx.send(Protocol.envelope(WsMessageType.PONG, Map.of("msg", "connected"))));

            ws.onClose(ctx -> {
                String user = presence.usernameOf(ctx);
                presence.disconnect(ctx);
                if (user != null) {
                    matchmaking.onDisconnect(user);
                }
            });

            ws.onMessage(ctx -> {
                try {
                    handle(ctx, ctx.message());
                } catch (Exception e) {
                    ctx.send(Protocol.envelope(WsMessageType.ERROR, Map.of("error", "BAD_MESSAGE")));
                }
            });
        });
    }

    private void handle(WsContext ctx, String raw) {
        Protocol.ParsedMessage msg = Protocol.parse(raw);
        if (msg.type == WsMessageType.AUTH) {
            String token = msg.getString("token");
            var username = auth.usernameForToken(token);
            if (username.isEmpty()) {
                ctx.send(Protocol.envelope(WsMessageType.AUTH_FAIL, Map.of("error", "UNAUTHORIZED")));
                return;
            }
            presence.connect(username.get(), ctx);
            ctx.send(Protocol.envelope(WsMessageType.AUTH_OK, Map.of("username", username.get())));
            return;
        }

        String user = presence.usernameOf(ctx);
        if (user == null) {
            ctx.send(Protocol.envelope(WsMessageType.ERROR, Map.of("error", "UNAUTHORIZED")));
            return;
        }

        switch (msg.type) {
            case PING -> ctx.send(Protocol.envelope(WsMessageType.PONG, Map.of()));
            case QUEUE_JOIN -> matchmaking.joinQueue(user);
            case QUEUE_LEAVE -> matchmaking.leaveQueue(user);
            case INVITE -> matchmaking.invite(user, msg.getString("username"));
            case INVITE_ACCEPT -> matchmaking.acceptInvite(user);
            case INVITE_REJECT -> matchmaking.rejectInvite(user);
            case LOOKUP_USER -> {
                String target = msg.getString("username");
                boolean exists = presence.userExists(target);
                boolean online = exists && presence.isOnline(target);
                ctx.send(Protocol.envelope(WsMessageType.LOOKUP_RESULT, Map.of(
                        "username", target == null ? "" : target,
                        "exists", exists,
                        "online", online)));
            }
            case PLACE_PLANT -> {
                PlaceIntent intent = msg.as(PlaceIntent.class);
                matchmaking.roomForUser(user).ifPresent(r -> r.placePlant(user, intent));
            }
            case PLACE_ZOMBIE -> {
                PlaceIntent intent = msg.as(PlaceIntent.class);
                matchmaking.roomForUser(user).ifPresent(r -> r.placeZombie(user, intent));
            }
            case BRAIN_COLLECTED -> matchmaking.roomForUser(user)
                    .ifPresent(r -> r.reportBrainCollected(user, msg.getInt("brainRow", -1)));
            case REPORT_NO_RESOURCES -> matchmaking.roomForUser(user)
                    .ifPresent(r -> r.reportNoResources(user));
            case QUICK_MSG -> matchmaking.roomForUser(user)
                    .ifPresent(r -> r.quickMessage(user, msg.getString("messageId")));
            default -> ctx.send(Protocol.envelope(WsMessageType.ERROR, Map.of("error", "UNKNOWN_TYPE")));
        }
    }
}
