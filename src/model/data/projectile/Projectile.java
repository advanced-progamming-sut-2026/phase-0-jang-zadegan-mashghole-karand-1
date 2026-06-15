package model.data.projectile;

import model.core.Position;

public class Projectile {
    public int damage;
    public Position position;
    public int row;
    public int col;
    public float speed;

    public Projectile(int damage, Position position, int row, int col, float speed) {
        this.damage = damage;
        this.position = position;
        this.row = row;
        this.col = col;
        this.speed = speed;
    }
}
