package model.rule.rules.minigame;

import java.util.List;

import model.core.EventBus;
import model.core.GameState;
<<<<<<< Updated upstream
=======
import model.core.Position;
>>>>>>> Stashed changes
import model.core.SessionEnd;
import model.data.brain.Brain;
import model.data.content.minigame.IZombieShop;
import model.data.plant.PlantType;
<<<<<<< Updated upstream
import model.data.zombie.ZombieType;
import model.event.events.GameOverReason;
=======
import model.data.zombie.Zombie;
import model.data.zombie.ZombieType;
import model.event.events.GameOverReason;
import model.event.events.ZombieSpawnedEvent;
>>>>>>> Stashed changes
import model.rule.LevelRule;
import model.rule.SessionContext;
import shared.izombie.IZombiePlayMode;
import shared.izombie.MatchRole;
import shared.protocol.Protocol;

public class IZombiePvPRules implements LevelRule {
    private static final int MIN_ZOMBIE_SPAWN_COL = 6;
    private static final int STARTING_SUN = 150;
    private static final List<PlantType> PLANT_SHOP = List.of(
            PlantType.Sunflower,
            PlantType.PeaShooter,
            PlantType.Wall_nut,
            PlantType.SnowPea,
            PlantType.Repeater);

    private boolean sessionReady;
    private int startTick;

    @Override
    public boolean skipsPlantSelection() {
        return true;
    }

    @Override
    public boolean shouldDropSkySun() {
        return false;
    }

    @Override
    public boolean shouldSpawnWaves() {
        return false;
    }

    @Override
    public boolean lawnMowersEnabled() {
        return false;
    }

    @Override
    public boolean usesSunCurrency() {
        return true;
    }

    @Override
    public boolean canPlaceZombies() {
        return true;
    }

    @Override
    public boolean canPlant(PlantType type, int row, int col, GameState state, SessionContext context) {
        if (type == null || !PLANT_SHOP.contains(type)) {
            return false;
        }
        if (col < 0 || col > 5 || row < 0 || row >= GameState.GRID_ROWS) {
            return false;
        }
        MatchRole local = context.getConfig().localMatchRole;
        IZombiePlayMode mode = context.getConfig().iZombiePlayMode;
        if (mode == IZombiePlayMode.COUCH) {
            return true;
        }
        return local == MatchRole.PLANTS;
    }

    @Override
    public boolean canPlaceZombie(ZombieType type, int row, int col, GameState state, SessionContext context) {
        if (!IZombieShop.isPurchasable(type)) {
            return false;
        }
        if (col < MIN_ZOMBIE_SPAWN_COL || col >= GameState.GRID_COLS) {
            return false;
        }
        if (row < 0 || row >= GameState.GRID_ROWS) {
            return false;
        }
        if (state.getPlantAt(row, col) != null) {
            return false;
        }
        MatchRole local = context.getConfig().localMatchRole;
        IZombiePlayMode mode = context.getConfig().iZombiePlayMode;
        if (mode == IZombiePlayMode.COUCH) {
            return true;
        }
        return local == MatchRole.ZOMBIES;
    }

    @Override
    public void onSessionStart(SessionContext context, GameState state, EventBus bus) {
        sessionReady = false;
        state.dualSunMode = true;
<<<<<<< Updated upstream
=======
        IZombiePlayMode startMode = context.getConfig().iZombiePlayMode;
        state.networkSunAuthority = startMode == IZombiePlayMode.ONLINE_RANDOM
                || startMode == IZombiePlayMode.ONLINE_INVITE;
>>>>>>> Stashed changes
        state.plantSun = STARTING_SUN;
        state.zombieSun = STARTING_SUN;
        state.sunAmount = STARTING_SUN;
        state.brainsMode = true;
        state.brains.clear();
        for (int row = 0; row < GameState.GRID_ROWS; row++) {
            state.brains.add(new Brain(row));
        }
<<<<<<< Updated upstream
=======
        spawnSunZombies(state, bus);
>>>>>>> Stashed changes
        startTick = state.totalTicks;
        sessionReady = true;
    }

    @Override
    public void postTick(SessionContext context, GameState state, EventBus bus) {
        if (!sessionReady || state.gameOver || state.levelComplete) {
            return;
        }

<<<<<<< Updated upstream
        int elapsedTicks = state.totalTicks - startTick;
        int survivalTicks = Protocol.IZOMBIE_SURVIVAL_SECONDS * model.core.GameLoop.TICKS_PER_SECOND;
        if (elapsedTicks >= survivalTicks) {
=======
        // Online matches are server-authoritative for end conditions.
        IZombiePlayMode mode = context.getConfig().iZombiePlayMode;
        if (mode == IZombiePlayMode.ONLINE_RANDOM || mode == IZombiePlayMode.ONLINE_INVITE) {
            return;
        }

        int elapsedTicks = state.totalTicks - startTick;
        int survivalTicks = Protocol.IZOMBIE_SURVIVAL_SECONDS * model.core.GameLoop.TICKS_PER_SECOND;
        if (elapsedTicks >= survivalTicks) {
            markEnd(state, "Plants Win!", "Survived the full timer.");
>>>>>>> Stashed changes
            SessionEnd.win(state, bus);
            return;
        }

        if (state.getCollectedBrainCount() >= GameState.GRID_ROWS) {
<<<<<<< Updated upstream
            if (context.getConfig().localMatchRole == MatchRole.ZOMBIES
                    || context.getConfig().iZombiePlayMode == IZombiePlayMode.COUCH) {
=======
            markEnd(state, "Zombies Win!", "All brains collected.");
            if (context.getConfig().localMatchRole == MatchRole.ZOMBIES
                    || mode == IZombiePlayMode.COUCH) {
>>>>>>> Stashed changes
                SessionEnd.win(state, bus);
            } else {
                SessionEnd.lose(state, bus, GameOverReason.BRAINS_EATEN);
            }
            return;
        }

        boolean anyAlive = state.zombies.stream().anyMatch(z -> z.isAlive);
<<<<<<< Updated upstream
        if (!anyAlive && state.zombieSun < IZombieShop.getCheapestCost()) {
            if (context.getConfig().localMatchRole == MatchRole.PLANTS
                    || context.getConfig().iZombiePlayMode == IZombiePlayMode.COUCH) {
=======
        int zombieSun = state.dualSunMode ? state.zombieSun : state.sunAmount;
        if (!anyAlive && zombieSun < IZombieShop.getCheapestCost()) {
            markEnd(state, "Plants Win!", "Zombies ran out of resources.");
            if (context.getConfig().localMatchRole == MatchRole.PLANTS
                    || mode == IZombiePlayMode.COUCH) {
>>>>>>> Stashed changes
                SessionEnd.win(state, bus);
            } else {
                SessionEnd.lose(state, bus, GameOverReason.NO_RESOURCES);
            }
        }
    }

<<<<<<< Updated upstream
=======
    private static void markEnd(GameState state, String title, String detail) {
        state.sessionEndTitle = title;
        state.sessionEndDetail = detail;
    }

    private void spawnSunZombies(GameState state, EventBus bus) {
        for (int row = 0; row < GameState.GRID_ROWS; row++) {
            int col = GameState.GRID_COLS - 1;
            Zombie zombie = new Zombie(
                    ZombieType.SUN_ZOMBIE,
                    row,
                    col,
                    new Position(
                            col * GameState.CELL_WIDTH + GameState.CELL_WIDTH / 2f,
                            row * GameState.CELL_HEIGHT + GameState.CELL_HEIGHT / 2f),
                    bus);
            state.addZombie(zombie);
            bus.publish(new ZombieSpawnedEvent(zombie));
        }
    }

>>>>>>> Stashed changes
    public static List<PlantType> plantShop() {
        return PLANT_SHOP;
    }
}
