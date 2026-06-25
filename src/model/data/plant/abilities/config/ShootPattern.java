package model.data.plant.abilities.config;

public class ShootPattern {
    public Direction dir;
    public int row;
    public int bulletCount;

    public ShootPattern(Direction dir, int row, int bulletCount) {
        this.dir = dir;
        this.row = row;
        this.bulletCount = bulletCount;
    }

    public Direction getDir() {
        return dir;
    }

    public int getRow() {
        return row;
    }

    public int getBulletCount() {
        return bulletCount;
    }
}
