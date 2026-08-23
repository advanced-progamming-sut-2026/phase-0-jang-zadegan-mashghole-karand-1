package view.gdx.lawn;

import model.core.Position;
import model.core.ReadOnlyGameState;

public final class LawnLayout {
    public float atlasInsetLeft = 256f;
    public float atlasInsetRight = 32f;
    public float atlasInsetBottom = 80f;
    public float atlasInsetTop = 200f;

    public float insetLeft;
    public float insetRight;
    public float insetBottom;
    public float insetTop;

    public float originX = 160f;
    public float originY = 80f;
    public float scaleX = 1.4f;
    public float scaleY = 1.2f;

    public void syncFromBackground(float drawX, float drawY,
            float leftW, float centerW, float stripH, float bgScale) {
        insetLeft = atlasInsetLeft * bgScale;
        insetRight = atlasInsetRight * bgScale;
        insetBottom = atlasInsetBottom * bgScale;
        insetTop = atlasInsetTop * bgScale;

        float lawnW = Math.max(1f, centerW - insetLeft - insetRight);
        float lawnH = Math.max(1f, stripH - insetBottom - insetTop);
        originX = drawX + leftW + insetLeft;
        originY = drawY + insetBottom;
        scaleX = lawnW / ReadOnlyGameState.SCREEN_WIDTH;
        scaleY = lawnH / ReadOnlyGameState.SCREEN_HEIGHT;
    }

    public float cellWidth() {
        return ReadOnlyGameState.CELL_WIDTH * scaleX;
    }

    public float cellHeight() {
        return ReadOnlyGameState.CELL_HEIGHT * scaleY;
    }

    public float cellLeft(int col) {
        return originX + col * cellWidth();
    }

    public float cellBottom(int row) {
        int flipped = ReadOnlyGameState.GRID_ROWS - 1 - row;
        return originY + flipped * cellHeight();
    }

    public float cellCenterX(int col) {
        return originX + (col + 0.5f) * cellWidth();
    }

    public float cellCenterY(int row) {
        int flipped = ReadOnlyGameState.GRID_ROWS - 1 - row;
        return originY + (flipped + 0.5f) * cellHeight();
    }

    public float worldX(Position position) {
        return originX + position.x * scaleX;
    }

    public float worldYForRow(int row, Position position) {
        return cellCenterY(row);
    }

    public boolean worldToCell(float worldX, float worldY, int[] outRowCol) {
        float localX = worldX - originX;
        float localY = worldY - originY;
        if (localX < 0f || localY < 0f) {
            return false;
        }
        int col = (int) (localX / cellWidth());
        int flipped = (int) (localY / cellHeight());
        int row = ReadOnlyGameState.GRID_ROWS - 1 - flipped;
        if (col < 0 || col >= ReadOnlyGameState.GRID_COLS
                || row < 0 || row >= ReadOnlyGameState.GRID_ROWS) {
            return false;
        }
        outRowCol[0] = row;
        outRowCol[1] = col;
        return true;
    }
}
