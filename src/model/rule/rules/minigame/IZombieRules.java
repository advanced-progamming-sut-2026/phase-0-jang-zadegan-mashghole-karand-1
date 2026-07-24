package model.rule.rules.minigame;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import model.core.EventBus;
import model.core.GameState;
import model.core.Position;
import model.core.SessionEnd;
import model.data.brain.Brain;
import model.data.content.minigame.IZombieShop;
import model.data.plant.Plant;
import model.data.plant.PlantType;
import model.data.zombie.Zombie;
import model.data.zombie.ZombieType;
import model.events.GameOverReason;
import model.events.ZombieSpawnedEvent;
import model.rule.LevelRule;
import model.rule.SessionContext;

public class IZombieRules implements LevelRule {
    private static final Random RANDOM = new Random();
    private static final int PLANT_COLS = 6;
    private static final int MIN_ZOMBIE_SPAWN_COL = 6;
    private static final int STARTING_SUN = 150;

    private static final List<PlantType> FALLBACK_PLANTS = List.of(
            PlantType.Sunflower,
            PlantType.PeaShooter,
            PlantType.Wall_nut,
            PlantType.SnowPea,
            PlantType.Repeater);

    private boolean sessionReady;

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
        return false;
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
        return state.getPlantAt(row, col) == null;
    }

    @Override
    public void onSessionStart(SessionContext context, GameState state, EventBus bus) {
        sessionReady = false;
        state.sunAmount = STARTING_SUN;
        state.brainsMode = true;
        state.brains.clear();
        for (int row = 0; row < GameState.GRID_ROWS; row++) {
            state.brains.add(new Brain(row));
        }

        placePlants(context, state, bus);
        spawnSunZombies(state, bus);
        sessionReady = true;
    }

    @Override
    public void postTick(SessionContext context, GameState state, EventBus bus) {
        if (!sessionReady || state.gameOver || state.levelComplete) {
            return;
        }

        if (state.getCollectedBrainCount() >= GameState.GRID_ROWS) {
            SessionEnd.win(state, bus);
            return;
        }

        boolean anyAlive = state.zombies.stream().anyMatch(z -> z.isAlive);
        if (!anyAlive && state.sunAmount < IZombieShop.getCheapestCost()) {
            SessionEnd.lose(state, bus, GameOverReason.NO_RESOURCES);
        }
    }

    private void placePlants(SessionContext context, GameState state, EventBus bus) {
        List<PlantType> pool = resolvePlantPool(context);
        for (int row = 0; row < GameState.GRID_ROWS; row++) {
            for (int col = 0; col < PLANT_COLS; col++) {
                if (state.getPlantAt(row, col) != null) {
                    continue;
                }
                PlantType type = pool.get(RANDOM.nextInt(pool.size()));
                Plant plant = new Plant(type, row, col, 1, bus);
                state.addPlant(plant);
            }
        }
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

    private List<PlantType> resolvePlantPool(SessionContext context) {
        List<PlantType> selected = context.getConfig().selectedPlants;
        List<PlantType> filtered = new ArrayList<>();
        if (selected != null) {
            for (PlantType type : selected) {
                if (type != null && !type.isBowlingExclusive()) {
                    filtered.add(type);
                }
            }
        }
        if (filtered.isEmpty()) {
            return FALLBACK_PLANTS;
        }
        return filtered;
    }
}
