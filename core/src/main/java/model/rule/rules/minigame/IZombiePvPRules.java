package model.rule.rules.minigame;

import java.util.List;

import model.core.EventBus;
import model.core.GameState;
import model.core.SessionEnd;
import model.data.brain.Brain;
import model.data.content.minigame.IZombieShop;
import model.data.plant.PlantType;
import model.data.zombie.ZombieType;
import model.event.events.GameOverReason;
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
        state.plantSun = STARTING_SUN;
        state.zombieSun = STARTING_SUN;
        state.sunAmount = STARTING_SUN;
        state.brainsMode = true;
        state.brains.clear();
        for (int row = 0; row < GameState.GRID_ROWS; row++) {
            state.brains.add(new Brain(row));
        }
        startTick = state.totalTicks;
        sessionReady = true;
    }

    @Override
    public void postTick(SessionContext context, GameState state, EventBus bus) {
        if (!sessionReady || state.gameOver || state.levelComplete) {
            return;
        }

        int elapsedTicks = state.totalTicks - startTick;
        int survivalTicks = Protocol.IZOMBIE_SURVIVAL_SECONDS * model.core.GameLoop.TICKS_PER_SECOND;
        if (elapsedTicks >= survivalTicks) {
            SessionEnd.win(state, bus);
            return;
        }

        if (state.getCollectedBrainCount() >= GameState.GRID_ROWS) {
            if (context.getConfig().localMatchRole == MatchRole.ZOMBIES
                    || context.getConfig().iZombiePlayMode == IZombiePlayMode.COUCH) {
                SessionEnd.win(state, bus);
            } else {
                SessionEnd.lose(state, bus, GameOverReason.BRAINS_EATEN);
            }
            return;
        }

        boolean anyAlive = state.zombies.stream().anyMatch(z -> z.isAlive);
        if (!anyAlive && state.zombieSun < IZombieShop.getCheapestCost()) {
            if (context.getConfig().localMatchRole == MatchRole.PLANTS
                    || context.getConfig().iZombiePlayMode == IZombiePlayMode.COUCH) {
                SessionEnd.win(state, bus);
            } else {
                SessionEnd.lose(state, bus, GameOverReason.NO_RESOURCES);
            }
        }
    }

    public static List<PlantType> plantShop() {
        return PLANT_SHOP;
    }
}
