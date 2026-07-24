package model.data.projectile;

import model.core.Position;
import model.data.plant.PlantType;

public class BouncingProjectile extends Projectile {
    private int bounceCount = 0;
    public final int maxBounces;
    public BouncingProjectile(int damage, Position position, int row, int col, float speed,
                              ProjectileType type, ProjectileTarget target, PlantType sourcePlant, int maxBounces) {
        super(damage, position, row, col, speed, type, target, sourcePlant);
        this.maxBounces = maxBounces;
    }
    public int getBounceCount() {
        return bounceCount;
    }

    public void incrementBounceCount() {
        this.bounceCount++;
    }

    public boolean canBounce() {
        return this.bounceCount < this.maxBounces;
    }
}