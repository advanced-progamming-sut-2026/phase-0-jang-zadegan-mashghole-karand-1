package model.data.projectile;

import model.core.Position;
import model.data.plant.Plant;
import model.data.plant.ProjectileType;
import model.data.plant.abilities.effects.HitEffect;

public class BouncingProjectile extends Projectile {
    private int bounceCount = 0;
    public final int maxBounces;
    public BouncingProjectile(int damage, Position position, int row, int col, float speed,
                              ProjectileType type, ProjectileTarget target, Plant owner, int maxBounces) {
        super(damage, position, row, col, speed, type, target, owner);
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