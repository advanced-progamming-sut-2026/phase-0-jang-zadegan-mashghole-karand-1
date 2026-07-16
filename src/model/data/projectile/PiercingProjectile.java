package model.data.projectile;

import model.core.Position;
import model.data.plant.Plant;
import model.data.projectile.ProjectileType;
import model.data.plant.abilities.effects.HitEffect;
import model.data.zombie.Zombie;
import java.util.HashSet;
import java.util.Set;

public class PiercingProjectile extends Projectile {
    public int pierceCount;
    public float maxRange;
    public float traveledDistance = 0f;
    public final Set<Zombie> hitZombies = new HashSet<>();

    public PiercingProjectile(int damage, Position position, int row, int col, float speed,
                              ProjectileType type, ProjectileTarget target,
                              int pierceCount, float maxRange ) {
        super(damage, position, row, col, speed, type, target);
        this.pierceCount = pierceCount;
        this.maxRange = maxRange;
    }

    public int getPierceCount() {
        return pierceCount;
    }

    public void setPierceCount(int pierceCount) {
        this.pierceCount = pierceCount;
    }

    public float getMaxRange() {
        return maxRange;
    }

    public void setMaxRange(float maxRange) {
        this.maxRange = maxRange;
    }

    public float getTraveledDistance() {
        return traveledDistance;
    }

    public void setTraveledDistance(float traveledDistance) {
        this.traveledDistance = traveledDistance;
    }

    public Set<Zombie> getHitZombies() {
        return hitZombies;
    }
}