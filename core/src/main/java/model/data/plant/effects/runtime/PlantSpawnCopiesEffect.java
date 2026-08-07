package model.data.plant.effects.runtime;

import model.board.Tile;
import model.board.TileType;
import model.core.EventBus;
import model.core.GameState;
import model.data.plant.Plant;
import model.data.plant.PlantType;
import model.data.plant.effects.config.PlantEffectConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlantSpawnCopiesEffect implements PlantEffectConfig {
    @Override
    public PlantEffectConfig createInstance(Plant plant) {
        return new PlantSpawnCopiesEffect();
    }
    private static final int MAX_COPIES = 3;
    @Override
    public void onActivate(Plant plant, GameState state, EventBus event) {
        List<int[]> emptyWater = new ArrayList<>();
        for (int row = 0; row < GameState.GRID_ROWS; row++) {
            for (int col = 0; col < GameState.GRID_COLS; col++) {
                if (row == plant.row && col == plant.col) continue;
                Tile tile = state.getBoard().getTile(row, col);
                if (tile == null) continue;
                if (tile.getType() != TileType.WATER) continue;
                if (tile.hasLilyPad() || tile.hasPlant()) continue;
                if (tile.hasGrave() || tile.hasVase() || tile.hasBeachPost()) continue;
                emptyWater.add(new int[]{row, col});
            }
        }
        Collections.shuffle(emptyWater);
        int count = Math.min(MAX_COPIES, emptyWater.size());
        for (int i = 0; i < count; i++) {
            int r = emptyWater.get(i)[0];
            int c = emptyWater.get(i)[1];
            Plant copy = new Plant(PlantType.Lily_Pad, r, c, plant.level, event);
            state.addPlant(copy);
        }
    }
}
