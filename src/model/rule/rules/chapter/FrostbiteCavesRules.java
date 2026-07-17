package model.rule.rules.chapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import model.board.IceDirection;
import model.board.Tile;
import model.board.TileType;
import model.core.EventBus;
import model.core.GameState;
import model.data.plant.Plant;
import model.data.plant.PlantTag;
import model.events.FrostbiteZombieSpawnEvent;
import model.rule.LevelRule;
import model.rule.SessionContext;

public class FrostbiteCavesRules implements LevelRule {
    private static final Random RANDOM = new Random();
    private static final int MIN_COL = 3;
    private static final int MIN_ICE_TILES = 3;
    private static final int MAX_ICE_TILES = 5;
    private static final int MIN_ICE_WIND_ROWS = 1;
    private static final int MAX_ICE_WIND_ROWS = 4;
    private static final int MIN_FROZEN_ZOMBIES = 1;
    private static final int MAX_FROZEN_ZOMBIES = 5;

    @Override
    public boolean freezeProjectilesEnabled() {
        return false;
    }

    @Override
    public void onSessionStart(SessionContext context, GameState state, EventBus bus) {
        placeIceTiles(state);
        spawnFrozenZombies(state, bus);
    }

    private void placeIceTiles(GameState state) {
        int iceTileCount = MIN_ICE_TILES + RANDOM.nextInt(MAX_ICE_TILES - MIN_ICE_TILES + 1);
        int placed = 0;
        int attempts = 0;

        while (placed < iceTileCount && attempts < 200) {
            attempts++;
            int row = RANDOM.nextInt(GameState.GRID_ROWS);
            int col = MIN_COL + RANDOM.nextInt(GameState.GRID_COLS - MIN_COL);

            Tile tile = state.getBoard().getTile(row, col);
            if (tile.getType() != TileType.ICE) {
                boolean goUp;
                if (row == 0) {
                    goUp = false;
                } else if (row == GameState.GRID_ROWS - 1) {
                    goUp = true;
                } else {
                    goUp = RANDOM.nextBoolean();
                }

                tile.setType(TileType.ICE);
                tile.setDirection(goUp ? IceDirection.UP : IceDirection.DOWN);
                placed++;
            }
        }
    }

    private void spawnFrozenZombies(GameState state, EventBus bus) {
        int frozenCount = MIN_FROZEN_ZOMBIES + RANDOM.nextInt(MAX_FROZEN_ZOMBIES - MIN_FROZEN_ZOMBIES + 1);

        boolean[] rowUsed = new boolean[GameState.GRID_ROWS];

        for (int i = 0; i < frozenCount; i++) {
            int row = RANDOM.nextInt(GameState.GRID_ROWS);
            int attempts = 0;
            while (rowUsed[row] && attempts < 20) {
                row = RANDOM.nextInt(GameState.GRID_ROWS);
                attempts++;
            }

            if (rowUsed[row])
                continue;

            rowUsed[row] = true;
            bus.publish(new FrostbiteZombieSpawnEvent(row));
        }
    }

    @Override
    public void onWaveStart(SessionContext context, GameState state, EventBus bus) {
        int rowsToAffect = MIN_ICE_WIND_ROWS + RANDOM.nextInt(MAX_ICE_WIND_ROWS - MIN_ICE_WIND_ROWS + 1);
        List<Integer> affectedRows = new ArrayList<>();

        for (int i = 0; i < rowsToAffect; i++) {
            int row = RANDOM.nextInt(GameState.GRID_ROWS);
            if (!affectedRows.contains(row)) {
                affectedRows.add(row);
            }
        }

        for (Plant plant : state.plants) {
            if (affectedRows.contains(plant.row)) {
                if (plant.hasTag(PlantTag.FIRE))
                    continue;
                plant.increaseFrostbiteFreezeLevel();
            }
        }
    }
}
