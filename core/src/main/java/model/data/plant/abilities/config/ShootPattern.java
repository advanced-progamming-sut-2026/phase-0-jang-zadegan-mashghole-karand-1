package model.data.plant.abilities.config;

public class ShootPattern {
    public Direction dir;
    public int row;
    public int bulletCount;
    private boolean isRandom;

    public ShootPattern(Direction dir, int row, int bulletCount, boolean isRandom) {
        this.dir = dir;
        this.row = row;
        this.bulletCount = bulletCount;
        this.isRandom = isRandom;
    }
    public ShootPattern(Direction dir, int rowOffset, int bulletCount) {
        this(dir, rowOffset, bulletCount, false);
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

    public boolean isRandom() {
        return isRandom;
    }
}
