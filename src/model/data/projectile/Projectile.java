package model.data.projectile;

import model.core.Position;
import model.data.plant.abilities.config.Direction;

public class Projectile {
    public int damage;
    public Position position;
    public int row;
    public int col;
    public float speed;
    public Direction direction = Direction.FORWARD;
    public ProjectileType type;
    public ProjectileTarget target;
    public int knockBack = 0;
    public int effectDurationBonus = 0;
    public Projectile(int damage, Position position ,int row, int col, float speed, ProjectileType type,
            ProjectileTarget target) {
        this.damage = damage;
        this.position = position;
        this.row = row;
        this.col = col;
        this.speed = speed;
        this.type = type;
        this.target = target;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }
}
