package model.data.plant.abilities.config;

public enum Direction {
    UP(0,-1),DOWN(0,1),FORWARD(1,0),
    BACK(-1,0),UP_RIGHT(1,-1),UP_LEFT(-1,-1),
    DOWN_RIGHT(1,1),DOWN_LEFT(-1,1);
    public final int vx;
    public final int vy;

    Direction(int vx, int vy) {
        this.vx = vx;
        this.vy = vy;
    }
}
