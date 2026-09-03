package model.rule.rules.chapter;

import java.util.Random;

import model.board.Tile;
import model.board.TileType;
import model.core.EventBus;
import model.core.GameState;
import model.core.Position;
import model.data.Grave.Grave;
import model.data.Grave.GraveContent;
import model.data.zombie.Zombie;
import model.data.zombie.ZombieType;
import model.event.events.GraveCreatedEvent;
import model.event.events.NecromancySpawnEvent;
import model.event.events.ZombieSpawnedEvent;
import model.rule.LevelRule;
import model.rule.SessionContext;

public class DarkAgesRules implements LevelRule {
    private static final Random RANDOM = new Random();
    private static final int MIN_COL = 3;
    private static final int NECROMANCY_SPAWN_DELAY_TICKS = 20;
    private static final ZombieType[] NECROMANCY_POOL = {
            ZombieType.BASIC,
            ZombieType.CONE_HEAD,
            ZombieType.KNIGHT,
            ZombieType.IMP_DRAGON,
            ZombieType.JESTER_ZOMBIE,
            ZombieType.BUCKET_HEAD
    };

    private int pendingNecromancyTicks = -1;

    @Override
    public boolean shouldDropSkySun() {
        return false;
    }

    @Override
    public void onSessionStart(SessionContext context, GameState state, EventBus bus) {
        pendingNecromancyTicks = -1;
        placeNecromancyTiles(state);
        placeInitialGraves(state);
    }

    private void placeNecromancyTiles(GameState state) {
        int placed = 0;
        int attempts = 0;
        int target = 10;

        while (placed < target && attempts < 200) {
            attempts++;
            int row = RANDOM.nextInt(GameState.GRID_ROWS);
            int col = MIN_COL + RANDOM.nextInt(GameState.GRID_COLS - MIN_COL);

            state.getBoard().getTile(row, col).setType(TileType.NECROMANCY);
            placed++;
        }
    }

    private void placeInitialGraves(GameState state) {
        int placed = 0;
        int attempts = 0;
        int target = 5;

        while (placed < target && attempts < 200) {
            attempts++;
            int row = RANDOM.nextInt(GameState.GRID_ROWS);
            int col = MIN_COL + RANDOM.nextInt(GameState.GRID_COLS - MIN_COL);

            Tile tile = state.getBoard().getTile(row, col);
            if (tile.canSetGrave()) {
                state.addGrave(new Grave(row, col, decideGraveContent()));
                placed++;
            }
        }
    }

    @Override
    public void preTick(SessionContext context, GameState state, EventBus bus) {
        if (pendingNecromancyTicks <= 0) {
            return;
        }
        pendingNecromancyTicks--;
        if (pendingNecromancyTicks == 0) {
            spawnZombiesFromNecromancy(state, bus);
            pendingNecromancyTicks = -1;
        }
    }

    @Override
    public void onWaveStart(SessionContext context, GameState state, EventBus bus) {
        spawnDynamicGraves(state, bus);
        if (hasPendingNecromancySpawns(state)) {
            pendingNecromancyTicks = NECROMANCY_SPAWN_DELAY_TICKS;
        }
    }

    public boolean hasPendingNecromancySpawn() {
        return pendingNecromancyTicks > 0;
    }

    private boolean hasPendingNecromancySpawns(GameState state) {
        for (int row = 0; row < GameState.GRID_ROWS; row++) {
            for (int col = MIN_COL; col < GameState.GRID_COLS; col++) {
                Tile tile = state.getBoard().getTile(row, col);
                if (tile.getType() == TileType.NECROMANCY && tile.hasGrave()) {
                    return true;
                }
            }
        }
        return false;
    }

    private void spawnDynamicGraves(GameState state, EventBus bus) {
        int newGraves = 1 + RANDOM.nextInt(3);
        int placed = 0;
        int attempts = 0;

        while (placed < newGraves && attempts < 100) {
            attempts++;
            int row = RANDOM.nextInt(GameState.GRID_ROWS);
            int col = MIN_COL + RANDOM.nextInt(GameState.GRID_COLS - MIN_COL);

            Tile tile = state.getBoard().getTile(row, col);
            if (tile.canSetGrave()) {
                Grave grave = new Grave(row, col, decideGraveContent());
                state.addGrave(grave);
                bus.publish(new GraveCreatedEvent(grave));
                placed++;
            }
        }
    }

    private void spawnZombiesFromNecromancy(GameState state, EventBus bus) {
        for (int row = 0; row < GameState.GRID_ROWS; row++) {
            for (int col = MIN_COL; col < GameState.GRID_COLS; col++) {
                Tile tile = state.getBoard().getTile(row, col);
                if (tile.getType() == TileType.NECROMANCY && tile.hasGrave()) {
                    bus.publish(new NecromancySpawnEvent(row, col));
                    ZombieType type = necromancyZombie();
                    Zombie zombie = new Zombie(type, row, col, new Position((col + 0.5f) * GameState.CELL_WIDTH,
                            (row + 0.5f) * GameState.CELL_HEIGHT), bus, state.getGlowingChance());
                    state.addZombie(zombie);
                    bus.publish(new ZombieSpawnedEvent(zombie));
                }
            }
        }
    }

    private GraveContent decideGraveContent() {
        int roll = RANDOM.nextInt(100);
        if (roll < 30) {
            return GraveContent.SUN_50;
        } else if (roll < 50) {
            return GraveContent.PLANT_FOOD;
        }
        return GraveContent.NONE;
    }

    private ZombieType necromancyZombie() {
        return NECROMANCY_POOL[RANDOM.nextInt(NECROMANCY_POOL.length)];
    }
}
