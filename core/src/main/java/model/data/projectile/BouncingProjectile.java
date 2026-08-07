package model.data.projectile;

import model.core.Position;
import model.data.plant.PlantType;
import model.data.zombie.Zombie;

import java.util.HashSet;
import java.util.Set;

public class BouncingProjectile extends Projectile {
    private int bounceCount = 0;
    public final int maxBounces;
    public final Set<Zombie> hitZombies = new HashSet<>();
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