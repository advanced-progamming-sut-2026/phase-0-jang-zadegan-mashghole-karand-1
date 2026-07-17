package model.board;

import model.core.GameState;
import model.lawnmower.LawnMower;

public class GameBoard {
    private final int rows;
    private final int cols;
    private final Tile[][] tiles;
    private LawnMower[] lawnMowers;

    public GameBoard(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;

        this.tiles = new Tile[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                tiles[i][j] = new Tile(i, j);
            }
        }
        this.lawnMowers = new LawnMower[rows];
        for (int i = 0 ; i < rows ; i++){
            lawnMowers[i] = new LawnMower(i);
        }
    }

    public Tile getTile(int row, int col) {
        if (!isValid(row, col))
            return null;
        return tiles[row][col];
    }

    public LawnMower getLawnMowers(int rows) {
        if (rows < 0 || rows >= this.rows) return null;
        return lawnMowers[rows];
    }

    public boolean isValid(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    public void reset() {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                tiles[row][col] = new Tile(row, col);
            }
        }
        for (int i = 0 ; i < rows ; i++){
            lawnMowers[i] = new LawnMower(i);
        }
    }
}
