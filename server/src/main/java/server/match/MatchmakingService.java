package server.match;

import java.util.ArrayDeque;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import server.db.UserRepository;
import shared.dto.MatchStartPayload;
import shared.izombie.MatchRole;
import shared.protocol.Protocol;
import shared.protocol.WsMessageType;

public final class MatchmakingService {
    private final PresenceHub presence;
    private final Queue<String> queue = new ArrayDeque<>();
    private final Map<String, PendingInvite> invites = new ConcurrentHashMap<>();
    private final Map<String, IZombieRoom> rooms = new ConcurrentHashMap<>();
    private final Map<String, String> userRoom = new ConcurrentHashMap<>();
    private UserRepository stats;

    public MatchmakingService(PresenceHub presence) {
        this.presence = presence;
    }

    public void setStats(UserRepository stats) {
        this.stats = stats;
    }

    public synchronized void joinQueue(String username) {
        if (userRoom.containsKey(username) || queue.contains(username)) {
            presence.send(username, WsMessageType.QUEUE_STATUS, Map.of("status", "WAITING"));
            return;
        }
        queue.offer(username);
        presence.send(username, WsMessageType.QUEUE_STATUS, Map.of("status", "WAITING"));
        tryPair();
    }

    public synchronized void leaveQueue(String username) {
        queue.remove(username);
        presence.send(username, WsMessageType.QUEUE_STATUS, Map.of("status", "LEFT"));
    }

    private void tryPair() {
        while (queue.size() >= 2) {
            String a = queue.poll();
            String b = queue.poll();
            if (a == null || b == null) {
                break;
            }
            if (!presence.isOnline(a)) {
                if (b != null) {
                    queue.offer(b);
                }
                continue;
            }
            if (!presence.isOnline(b)) {
                queue.offer(a);
                continue;
            }
            startMatch(a, b, MatchRole.ZOMBIES, MatchRole.PLANTS);
        }
    }

    public synchronized void invite(String from, String to) {
        if (to == null || to.isBlank()) {
            presence.send(from, WsMessageType.ERROR, Map.of("error", "INVALID_USERNAME"));
            return;
        }
        if (from.equalsIgnoreCase(to)) {
            presence.send(from, WsMessageType.ERROR, Map.of("error", "INVALID_USERNAME"));
            return;
        }
        if (!presence.userExists(to)) {
            presence.send(from, WsMessageType.ERROR, Map.of("error", "INVALID_USERNAME"));
            return;
        }
        if (!presence.isOnline(to)) {
            presence.send(from, WsMessageType.ERROR, Map.of("error", "USER_OFFLINE"));
            return;
        }
        if (userRoom.containsKey(from) || userRoom.containsKey(to) || invites.containsKey(to)) {
            presence.send(from, WsMessageType.ERROR, Map.of("error", "USER_BUSY"));
            return;
        }
        PendingInvite invite = new PendingInvite(from, to, System.currentTimeMillis());
        invites.put(to, invite);
        presence.send(to, WsMessageType.INVITE_INCOMING, Map.of("from", from));
        presence.send(from, WsMessageType.INVITE_RESULT, Map.of("status", "SENT", "to", to));
    }

    public synchronized void acceptInvite(String username) {
        PendingInvite invite = invites.remove(username);
        if (invite == null) {
            presence.send(username, WsMessageType.ERROR, Map.of("error", "NO_INVITE"));
            return;
        }
        if (!presence.isOnline(invite.from)) {
            presence.send(username, WsMessageType.ERROR, Map.of("error", "USER_OFFLINE"));
            return;
        }
        startMatch(invite.from, invite.to, MatchRole.ZOMBIES, MatchRole.PLANTS);
    }

    public synchronized void rejectInvite(String username) {
        PendingInvite invite = invites.remove(username);
        if (invite == null) {
            return;
        }
        presence.send(invite.from, WsMessageType.INVITE_RESULT,
                Map.of("status", "REJECTED", "to", username));
    }

    private void startMatch(String userA, String userB, MatchRole roleA, MatchRole roleB) {
        String roomId = UUID.randomUUID().toString();
        IZombieRoom room = new IZombieRoom(roomId, userA, roleA, userB, roleB, presence, this);
        if (stats != null) {
            room.setStats(stats);
        }
        rooms.put(roomId, room);
        userRoom.put(userA, roomId);
        userRoom.put(userB, roomId);
        queue.remove(userA);
        queue.remove(userB);

        presence.send(userA, WsMessageType.MATCH_START, new MatchStartPayload(
                roomId, userB, roleA, Protocol.IZOMBIE_SURVIVAL_SECONDS, 150, 150));
        presence.send(userB, WsMessageType.MATCH_START, new MatchStartPayload(
                roomId, userA, roleB, Protocol.IZOMBIE_SURVIVAL_SECONDS, 150, 150));
        room.start();
    }

    public Optional<IZombieRoom> roomForUser(String username) {
        String id = userRoom.get(username);
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(rooms.get(id));
    }

    public void clearUserRoom(String username, String roomId) {
        userRoom.remove(username, roomId);
        rooms.remove(roomId);
    }

    public void onDisconnect(String username) {
        leaveQueue(username);
        PendingInvite asTarget = invites.remove(username);
        if (asTarget != null) {
            presence.send(asTarget.from, WsMessageType.INVITE_RESULT,
                    Map.of("status", "CANCELLED", "to", username));
        }
        invites.entrySet().removeIf(e -> {
            if (e.getValue().from.equals(username)) {
                presence.send(e.getKey(), WsMessageType.INVITE_RESULT,
                        Map.of("status", "CANCELLED", "to", e.getKey()));
                return true;
            }
            return false;
        });
        roomForUser(username).ifPresent(room -> room.forfeit(username));
    }

    public void expireStaleInvites() {
        long now = System.currentTimeMillis();
        invites.entrySet().removeIf(e -> {
            if (now - e.getValue().createdAt > Protocol.INVITE_TIMEOUT_MS) {
                presence.send(e.getValue().from, WsMessageType.INVITE_RESULT,
                        Map.of("status", "TIMEOUT", "to", e.getKey()));
                return true;
            }
            return false;
        });
    }

    private static final class PendingInvite {
        final String from;
        final String to;
        final long createdAt;

        PendingInvite(String from, String to, long createdAt) {
            this.from = from;
            this.to = to;
            this.createdAt = createdAt;
        }
    }
}
