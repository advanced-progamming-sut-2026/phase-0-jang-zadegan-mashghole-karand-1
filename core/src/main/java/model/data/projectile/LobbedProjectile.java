package model.data.projectile;

import model.core.Position;
import model.data.plant.PlantType;

public class LobbedProjectile extends Projectile{
    public Position startPosition;
    public Position targetPosition;
    public float flightProgress = 0f;
    public float flightDuration = 60f;
    public float arcHeight = 80f;
    public float butterChance = 0f;
    public int butterDamage = 0;
    public int aoeRadius = 0;
    public int aoeDamage = 0;
    private boolean landed = false;

    public LobbedProjectile(int damage, Position position, int row, int col, float speed, ProjectileType type,
                            ProjectileTarget target, PlantType sourcePlant, Position targetPosition, float
                                    flightProgress,
                            float flightDuration, float arcHeight, float butterChance, int butterDamage, int aoeRadius,
                                    int aoeDamage
                            ) {
        super(damage, position, row, col, speed, type, target, sourcePlant);
        this.startPosition = new Position(position.x,position.y);
        this.targetPosition = targetPosition;
        this.flightProgress = flightProgress;
        this.flightDuration = flightDuration;
        this.arcHeight = arcHeight;
        this.butterChance = butterChance;
        this.butterDamage = butterDamage;
        this.aoeRadius = aoeRadius;
        this.aoeDamage = aoeDamage;
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

    public boolean hasLanded() {
        return landed;
    }

    public void updateMovement() {
        if (landed) {
            position.x = targetPosition.x;
            position.y = targetPosition.y;
            return;
        }
        if (flightDuration <= 0f) {
            position.x = targetPosition.x;
            position.y = targetPosition.y;
            landed = true;
            return;
        }

        flightProgress += 1f;
        float t = Math.min(1f, flightProgress / flightDuration);

        float linearX = startPosition.x + (targetPosition.x - startPosition.x) * t;
        float linearY = startPosition.y + (targetPosition.y - startPosition.y) * t;
        float arc = arcHeight * 4f * t * (1f - t);
        position.x = linearX;
        position.y = linearY - arc;

        if (t >= 1f) {
            position.x = targetPosition.x;
            position.y = targetPosition.y;
            landed = true;
        }
    }

    public float normalizedProgress() {
        if (flightDuration <= 0f) {
            return 1f;
        }
        return Math.min(1f, flightProgress / flightDuration);
    }
}
