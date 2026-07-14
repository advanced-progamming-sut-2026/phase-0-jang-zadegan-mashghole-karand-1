package model.data.projectile;

import model.core.Position;
import model.data.plant.Plant;
import model.data.plant.ProjectileType;
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
    public Plant owner;
    public float butterChance = 0f;
    public int butterDamage = 0;
    public int aoeRadius = 0;
    public int bounceCount = 0;
    public boolean isLobbed = false;
    public Position startPosition;
    public Position targetPosition;
    public float flightProgress = 0f;
    public float flightDuration = 60f;
    public float arcHeight = 80f;
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

    public void setButterChance(float butterChance) {
        this.butterChance = butterChance;
    }

    public void setButterDamage(int butterDamage) {
        this.butterDamage = butterDamage;
    }

    public void setAoeRadius(int aoeRadius) {
        this.aoeRadius = aoeRadius;
    }

    public int getBounceCount() {
        return bounceCount;
    }

    public void incrementBounceCount() {
        this.bounceCount++;
    }
}
