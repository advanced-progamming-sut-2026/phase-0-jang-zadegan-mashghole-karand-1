package model.data.projectile;

import model.core.Position;
import model.data.plant.Plant;
import model.data.plant.ProjectileType;
import model.data.plant.abilities.effects.HitEffect;

public class LobbedProjectile extends Projectile{
    public Position startPosition;
    public Position targetPosition;
    public float flightProgress = 0f;
    public float flightDuration = 60f;
    public float arcHeight = 80f;
    public float butterChance = 0f;
    public int butterDamage = 0;
    public int aoeRadius = 0;

    public LobbedProjectile(int damage, Position position, int row, int col, float speed, ProjectileType type,
                            ProjectileTarget target, Plant owner, Position targetPosition, float flightProgress,
                            float flightDuration, float arcHeight, float butterChance, int butterDamage, int aoeRadius
                            ) {
        super(damage, position, row, col, speed, type, target, owner);
        this.startPosition = new Position(position.x,position.y);
        this.targetPosition = targetPosition;
        this.flightProgress = flightProgress;
        this.flightDuration = flightDuration;
        this.arcHeight = arcHeight;
        this.butterChance = butterChance;
        this.butterDamage = butterDamage;
        this.aoeRadius = aoeRadius;
    }

    public Position getTargetPosition() {
        return targetPosition;
    }

    public void setTargetPosition(Position targetPosition) {
        this.targetPosition = targetPosition;
    }

    public Position getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(Position startPosition) {
        this.startPosition = startPosition;
    }

    public float getFlightDuration() {
        return flightDuration;
    }

    public void setFlightDuration(float flightDuration) {
        this.flightDuration = flightDuration;
    }

    public float getFlightProgress() {
        return flightProgress;
    }

    public void setFlightProgress(float flightProgress) {
        this.flightProgress = flightProgress;
    }

    public float getArcHeight() {
        return arcHeight;
    }

    public void setArcHeight(float arcHeight) {
        this.arcHeight = arcHeight;
    }

    public float getButterChance() {
        return butterChance;
    }

    public void setButterChance(float butterChance) {
        this.butterChance = butterChance;
    }

    public int getButterDamage() {
        return butterDamage;
    }

    public void setButterDamage(int butterDamage) {
        this.butterDamage = butterDamage;
    }

    public int getAoeRadius() {
        return aoeRadius;
    }

    public void setAoeRadius(int aoeRadius) {
        this.aoeRadius = aoeRadius;
    }
}
