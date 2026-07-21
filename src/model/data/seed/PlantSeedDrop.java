package model.data.seed;

import model.core.GameLoop;
import model.core.GameState;
import model.core.Position;
import model.data.plant.PlantType;

public class PlantSeedDrop {
    public static final int TTL_SECONDS = 5;
    public static final int TTL_TICKS = TTL_SECONDS * GameLoop.TICKS_PER_SECOND;

    public final int row;
    public final int col;
    public final PlantType plantType;
    public final Position position;
    public int age = 0;

    public PlantSeedDrop(int row, int col, PlantType plantType) {
        this.row = row;
        this.col = col;
        this.plantType = plantType;
        this.position = new Position(
                col * GameState.CELL_WIDTH + GameState.CELL_WIDTH / 2f,
                row * GameState.CELL_HEIGHT + GameState.CELL_HEIGHT / 2f);
    }

    public boolean isExpired() {
        return age >= TTL_TICKS;
    }
}
