package view.gdx.lawn;

import model.core.Position;
import model.core.ReadOnlyGameState;

public final class LawnLayout {
    public float originX = 160f;
    public float originY = 80f;
    public float scaleX = 1.4f;
    public float scaleY = 1.2f;

    public float cellCenterX(int col) {
        return originX + (col + 0.5f) * ReadOnlyGameState.CELL_WIDTH * scaleX;
    }

    public float cellCenterY(int row) {
        int flipped = ReadOnlyGameState.GRID_ROWS - 1 - row;
        return originY + (flipped + 0.5f) * ReadOnlyGameState.CELL_HEIGHT * scaleY;
    }

    public float worldX(Position position) {
        return originX + position.x * scaleX;
    }

    public float worldYForRow(int row, Position position) {
        return cellCenterY(row);
    }
}
