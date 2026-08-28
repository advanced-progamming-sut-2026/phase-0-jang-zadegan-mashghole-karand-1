package server.match;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import server.db.UserRepository;
import shared.dto.MatchStatePayload;
import shared.dto.PlaceIntent;
import shared.izombie.MatchRole;
import shared.message.QuickMessageId;
import shared.protocol.Protocol;
import shared.protocol.WsMessageType;

public final class IZombieRoom {
    private static final int START_SUN = 150;
    private static final int PLANT_MAX_COL = 5;
    private static final int ZOMBIE_MIN_COL = 6;
    private static final int GRID_COLS = 9;
    private static final int GRID_ROWS = 5;

    private static final Map<String, Integer> ZOMBIE_COSTS = Map.of(
            "BASIC", 50,
            "CONE_HEAD", 75,
            "BUCKET_HEAD", 125,
            "IMP", 50,
            "NEWSPAPER_ZOMBIE", 100);

    private static final Map<String, Integer> PLANT_COSTS = Map.of(
            "Sunflower", 50,
            "PeaShooter", 100,
            "Wall_nut", 50,
            "SnowPea", 175,
            "Repeater", 200);

    private final String roomId;
    private final String userA;
    private final MatchRole roleA;
    private final String userB;
    private final MatchRole roleB;
    private final PresenceHub presence;
    private final MatchmakingService matchmaking;

    private final AtomicBoolean ended = new AtomicBoolean(false);
    private int plantSun = START_SUN;
    private int zombieSun = START_SUN;
    private int elapsedSeconds;
    private final boolean[] brainsCollected = new boolean[GRID_ROWS];
    private final List<MatchStatePayload.PlacedEntity> plants = new ArrayList<>();
    private final List<MatchStatePayload.PlacedEntity> zombies = new ArrayList<>();
    private final Map<String, Long> lastQuickMsg = new java.util.concurrent.ConcurrentHashMap<>();

    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> tickFuture;
    private UserRepository stats;

    public IZombieRoom(String roomId, String userA, MatchRole roleA, String userB, MatchRole roleB,
            PresenceHub presence, MatchmakingService matchmaking) {
        this.roomId = roomId;
        this.userA = userA;
        this.roleA = roleA;
        this.userB = userB;
        this.roleB = roleB;
        this.presence = presence;
        this.matchmaking = matchmaking;
    }

    public void setStats(UserRepository stats) {
        this.stats = stats;
    }

    public void start() {
        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "izombie-room-" + roomId);
            t.setDaemon(true);
            return t;
        });
        tickFuture = scheduler.scheduleAtFixedRate(this::tick, 1, 1, TimeUnit.SECONDS);
        broadcastState();
    }

    private void tick() {
        if (ended.get()) {
            return;
        }
        elapsedSeconds++;
        if (elapsedSeconds >= Protocol.IZOMBIE_SURVIVAL_SECONDS) {
            endMatch(MatchRole.PLANTS, "SURVIVED");
            return;
        }
        broadcastState();
    }

    public synchronized void placePlant(String username, PlaceIntent intent) {
        if (ended.get() || intent == null) {
            return;
        }
        MatchRole role = roleOf(username);
        if (role != MatchRole.PLANTS) {
            presence.send(username, WsMessageType.ERROR, Map.of("error", "WRONG_ROLE"));
            return;
        }
        Integer cost = PLANT_COSTS.get(intent.type);
        if (cost == null) {
            presence.send(username, WsMessageType.ERROR, Map.of("error", "INVALID_TYPE"));
            return;
        }
        if (intent.row < 0 || intent.row >= GRID_ROWS || intent.col < 0 || intent.col > PLANT_MAX_COL) {
            presence.send(username, WsMessageType.ERROR, Map.of("error", "INVALID_CELL"));
            return;
        }
        if (occupied(intent.row, intent.col) || plantSun < cost) {
            presence.send(username, WsMessageType.ERROR, Map.of("error", "CANNOT_PLACE"));
            return;
        }
        plantSun -= cost;
        plants.add(new MatchStatePayload.PlacedEntity(intent.type, intent.row, intent.col, 100, MatchRole.PLANTS));
        broadcastState();
    }

    public synchronized void placeZombie(String username, PlaceIntent intent) {
        if (ended.get() || intent == null) {
            return;
        }
        MatchRole role = roleOf(username);
        if (role != MatchRole.ZOMBIES) {
            presence.send(username, WsMessageType.ERROR, Map.of("error", "WRONG_ROLE"));
            return;
        }
        Integer cost = ZOMBIE_COSTS.get(intent.type);
        if (cost == null) {
            presence.send(username, WsMessageType.ERROR, Map.of("error", "INVALID_TYPE"));
            return;
        }
        if (intent.row < 0 || intent.row >= GRID_ROWS
                || intent.col < ZOMBIE_MIN_COL || intent.col >= GRID_COLS) {
            presence.send(username, WsMessageType.ERROR, Map.of("error", "INVALID_CELL"));
            return;
        }
        if (occupied(intent.row, intent.col) || zombieSun < cost) {
            presence.send(username, WsMessageType.ERROR, Map.of("error", "CANNOT_PLACE"));
            return;
        }
        zombieSun -= cost;
        zombies.add(new MatchStatePayload.PlacedEntity(intent.type, intent.row, intent.col, 100, MatchRole.ZOMBIES));
        broadcastState();
        checkZombieResources();
    }

    public synchronized void reportBrainCollected(String username, int row) {
        if (ended.get() || row < 0 || row >= GRID_ROWS) {
            return;
        }
        if (roleOf(username) != MatchRole.ZOMBIES && roleOf(username) != MatchRole.PLANTS) {
            return;
        }
        brainsCollected[row] = true;
        int count = 0;
        for (boolean b : brainsCollected) {
            if (b) {
                count++;
            }
        }
        broadcastState();
        if (count >= GRID_ROWS) {
            endMatch(MatchRole.ZOMBIES, "ALL_BRAINS");
        }
    }

    public synchronized void reportNoResources(String username) {
        if (ended.get()) {
            return;
        }
        if (roleOf(username) != MatchRole.ZOMBIES) {
            return;
        }
        if (zombies.isEmpty() && zombieSun < cheapestZombie()) {
            endMatch(MatchRole.PLANTS, "NO_RESOURCES");
        }
    }

    public void quickMessage(String username, String messageId) {
        if (ended.get()) {
            return;
        }
        QuickMessageId id = QuickMessageId.fromName(messageId);
        if (id == null) {
            presence.send(username, WsMessageType.ERROR, Map.of("error", "INVALID_MESSAGE"));
            return;
        }
        long now = System.currentTimeMillis();
        Long last = lastQuickMsg.get(username);
        if (last != null && now - last < Protocol.QUICK_MSG_RATE_LIMIT_MS) {
            return;
        }
        lastQuickMsg.put(username, now);
        String other = otherUser(username);
        presence.send(other, WsMessageType.QUICK_MSG_RECV,
                Map.of("from", username, "messageId", id.name(), "display", id.display, "kind", id.kind.name()));
    }

    public void forfeit(String username) {
        MatchRole loser = roleOf(username);
        if (loser == null) {
            return;
        }
        MatchRole winner = loser == MatchRole.PLANTS ? MatchRole.ZOMBIES : MatchRole.PLANTS;
        endMatch(winner, "FORFEIT");
    }

    private void checkZombieResources() {
        if (zombies.isEmpty() && zombieSun < cheapestZombie()) {
            endMatch(MatchRole.PLANTS, "NO_RESOURCES");
        }
    }

    private int cheapestZombie() {
        return ZOMBIE_COSTS.values().stream().mapToInt(Integer::intValue).min().orElse(Integer.MAX_VALUE);
    }

    private boolean occupied(int row, int col) {
        for (MatchStatePayload.PlacedEntity e : plants) {
            if (e.row == row && e.col == col) {
                return true;
            }
        }
        for (MatchStatePayload.PlacedEntity e : zombies) {
            if (e.row == row && e.col == col) {
                return true;
            }
        }
        return false;
    }

    private MatchRole roleOf(String username) {
        if (userA.equals(username)) {
            return roleA;
        }
        if (userB.equals(username)) {
            return roleB;
        }
        return null;
    }

    private String otherUser(String username) {
        return userA.equals(username) ? userB : userA;
    }

    private String usernameForRole(MatchRole role) {
        if (roleA == role) {
            return userA;
        }
        return userB;
    }

    private synchronized void endMatch(MatchRole winner, String reason) {
        if (!ended.compareAndSet(false, true)) {
            return;
        }
        if (tickFuture != null) {
            tickFuture.cancel(false);
        }
        if (scheduler != null) {
            scheduler.shutdownNow();
        }
        MatchStatePayload state = snapshot();
        state.gameOver = true;
        state.winnerRole = winner.name();
        state.endReason = reason;
        Object endPayload = Map.of(
                "winnerRole", winner.name(),
                "reason", reason,
                "state", state);
        presence.send(userA, WsMessageType.MATCH_END, endPayload);
        presence.send(userB, WsMessageType.MATCH_END, endPayload);
        if (stats != null) {
            String winnerUser = usernameForRole(winner);
            String loserUser = otherUser(winnerUser);
            stats.recordIZombieWin(winnerUser);
            stats.recordGamePlayed(loserUser);
        }
        matchmaking.clearUserRoom(userA, roomId);
        matchmaking.clearUserRoom(userB, roomId);
    }

    private void broadcastState() {
        MatchStatePayload state = snapshot();
        presence.send(userA, WsMessageType.MATCH_STATE, state);
        presence.send(userB, WsMessageType.MATCH_STATE, state);
    }

    private MatchStatePayload snapshot() {
        MatchStatePayload s = new MatchStatePayload();
        s.roomId = roomId;
        s.plantSun = plantSun;
        s.zombieSun = zombieSun;
        s.elapsedSeconds = elapsedSeconds;
        s.survivalSeconds = Protocol.IZOMBIE_SURVIVAL_SECONDS;
        s.gameOver = ended.get();
        s.plants = new ArrayList<>(plants);
        s.zombies = new ArrayList<>(zombies);
        s.brainsCollected = brainsCollected.clone();
        return s;
    }
}
