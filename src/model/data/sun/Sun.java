package model.data.sun;

import model.game.Position;

public class Sun {
    private int amount;
    private int col;
    private int row;
    private Position position;
    private SunType type;

    public Sun(int amount, int col, int row, Position position, SunType type) {
        this.amount = amount;
        this.col = col;
        this.row = row;
        this.position = position;
        this.type = type;
    }
}
