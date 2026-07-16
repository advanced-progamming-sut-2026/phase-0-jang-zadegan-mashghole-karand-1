package model.data.projectile;

import model.core.Position;
import model.data.plant.Plant;
import model.data.plant.ProjectileType;
import model.data.plant.abilities.config.Direction;
import model.data.plant.abilities.effects.HitEffect;

public class Projectile {
    public int damage;
    public Position position;
    public int row;
    public int col;
    public float speed;
    public Direction direction = Direction.FORWARD;
    public ProjectileType type;
    public ProjectileTarget target;
    public Plant owner;
    public int knockBack = 0;
    public Projectile(int damage, Position position, int row, int col, float speed, ProjectileType type, ProjectileTarget target , Plant owner) {
        this.damage = damage;
        this.position = position;
        this.row = row;
        this.col = col;
        this.speed = speed;
        this.type = type;
        this.target = target;
        this.owner = owner;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

}
