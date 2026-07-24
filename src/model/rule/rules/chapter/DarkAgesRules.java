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
    private static final int MIN_COL = 3; // don't spawn graves and necromancy tiles in the first 3 columns

    @Override
    public boolean shouldDropSkySun() {
        return false;
    }

    @Override
    public void onSessionStart(SessionContext context, GameState state, EventBus bus) {
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
    public void onWaveStart(SessionContext context, GameState state, EventBus bus) {
        spawnDynamicGraves(state, bus);
        spawnZombiesFromNecromancy(state, bus);
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
                    ZombieType type = RANDOM.nextInt(2)==0? ZombieType.BASIC : ZombieType.CONE_HEAD;
                    Zombie zombie = new Zombie(type, row, col, new Position((col + 0.5f) * GameState.CELL_WIDTH,
                            (row + 0.5f) * GameState.CELL_HEIGHT), bus);
                    state.addZombie(zombie);
                    bus.publish(new ZombieSpawnedEvent(zombie));
                }
            }
        }
    }

    private GraveContent decideGraveContent() {
        // 30% chance to drop sun, 20% chance to drop plant food
        int roll = RANDOM.nextInt(100);
        if (roll < 30) {
            return GraveContent.SUN_50;
        } else if (roll < 50) {
            return GraveContent.PLANT_FOOD;
        }
        return GraveContent.NONE;
    }
}