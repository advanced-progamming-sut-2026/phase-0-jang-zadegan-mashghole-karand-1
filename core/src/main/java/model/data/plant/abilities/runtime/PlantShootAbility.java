package model.data.plant.abilities.runtime;

import model.data.plant.PlantType;
import model.data.projectile.*;
import model.data.plant.abilities.config.Direction;
import model.data.plant.abilities.config.PlantAbilityConfig;
import model.data.plant.abilities.config.ShootPattern;
import model.data.plant.effects.config.EffectPhase;
import model.core.EventBus;
import model.core.GameLoop;
import model.core.GameState;
import model.core.Position;
import model.data.plant.Plant;

import java.util.ArrayList;
import java.util.List;

public class PlantShootAbility implements PlantAbilityConfig {
    public final int damage;
    public final float cooldownSeconds;
    public final ProjectileType projectileType;
    public final ShootPattern shootPattern;
    public final EffectPhase phase;
    public final int pierceCount;
    public final float maxRange;
    public final int knockBack;
    private int currentCooldown = 0;
    private int lifeSpan = 0;
    private int chargeTicks = 0;

    private PlantShootAbility(Builder b) {
        this.damage = b.damage;
        this.cooldownSeconds = b.cooldownSeconds;
        this.projectileType = b.projectileType;
        this.shootPattern = b.shootPattern;
        this.pierceCount = b.pierceCount;
        this.maxRange = b.maxRange;
        this.knockBack = b.knockBack;
        this.phase = b.phase;
        this.lifeSpan = b.lifespan;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int damage;
        private float cooldownSeconds = 1.5f;
        private ProjectileType projectileType = ProjectileType.PEA;
        private ShootPattern shootPattern = new ShootPattern(Direction.FORWARD, 0, 1);
        private int pierceCount = 0;
        private float maxRange = -1;
        private int knockBack = 0;
        private int lifespan = 0;
        private EffectPhase phase = EffectPhase.ALWAYS;

        public Builder damage(int v) {
            damage = v;
            return this;
        }

        public Builder cooldown(float v) {
            cooldownSeconds = v;
            return this;
        }

        public Builder projectile(ProjectileType v) {
            projectileType = v;
            return this;
        }

        public Builder pattern(ShootPattern v) {
            shootPattern = v;
            return this;
        }

        public Builder pierce(int v) {
            pierceCount = v;
            return this;
        }

        public Builder maxRange(float v) {
            maxRange = v;
            return this;
        }

        public Builder knockBack(int v) {
            knockBack = v;
            return this;
        }

        public Builder phase(EffectPhase v) {
            phase = v;
            return this;
        }

        public Builder lifespan(int v) {
            lifespan = v;
            return this;
        }

        public PlantShootAbility build() {
            return new PlantShootAbility(this);
        }
    }

    public PlantShootAbility createInstance(Plant plant) {
        int finalDamage = damage + plant.damage - plant.type.baseStats.damage;
        int finalPierce = pierceCount + plant.upgradeState.pierceBonus;
        float finalRange = maxRange + plant.upgradeState.rangeBonus;
        if (projectileType == ProjectileType.POISON) {
            finalDamage += plant.upgradeState.poisonDamagePerTickBonus;
        }
        float finalCooldown = plant.type == PlantType.BowlingBulb ? cooldownSeconds : plant.actionInterval;
        int finalSpan = (lifeSpan + plant.upgradeState.lifeSpanBonus) * 10;
        return PlantShootAbility.builder()
                .damage(finalDamage)
                .cooldown(finalCooldown)
                .projectile(projectileType)
                .pattern(shootPattern)
                .pierce(finalPierce)
                .maxRange(finalRange)
                .knockBack(knockBack)
                .phase(phase)
                .lifespan(finalSpan)
                .build();
    }

    @Override
    public void onTick(Plant plant, GameState state, EventBus event) {
        if (lifeSpan > 0) {
            lifeSpan--;
            if (lifeSpan <= 0) {
                plant.hp = 0;
                plant.isAlive = false;
            }
        }
        if (plant.type == PlantType.BowlingBulb) {
            boolean zombieInLane = state.zombies.stream()
                    .anyMatch(z -> z.isAlive && z.row == plant.row);
            if (zombieInLane) {
                chargeTicks++;
            }
        }
        if (currentCooldown > 0) {
            currentCooldown--;
            return;
        }
        int targetRow = plant.row + shootPattern.getRow();
        boolean hasZombie;
        if (plant.isPlantFoodActive) {
            hasZombie = state.zombies.stream().anyMatch(z -> z.isAlive);
        } else if (plant.type == PlantType.Starfruit) {
            hasZombie = state.zombies.stream().anyMatch(z ->
                    z.isAlive && (
                            canHitZombie(plant, z, Direction.BACK) ||
                                    canHitZombie(plant, z, Direction.UP) ||
                                    canHitZombie(plant, z, Direction.DOWN) ||
                                    canHitZombie(plant, z, Direction.UP_RIGHT) ||
                                    canHitZombie(plant, z, Direction.DOWN_RIGHT)
                    ));
        } else if (plant.type == PlantType.Rotobaga) {
            hasZombie = state.zombies.stream()
                    .anyMatch(z -> z.isAlive && canHitZombie(plant, z, shootPattern.getDir()));
        } else if (plant.type == PlantType.Threepeater) {
            hasZombie = state.zombies.stream()
                    .anyMatch(z -> z.isAlive && Math.abs(z.row - plant.row) <= 1);
        } else {
            hasZombie = state.zombies.stream()
                    .anyMatch(z -> z.isAlive && z.row == targetRow);
        }
        if (plant.type == PlantType.BowlingBulb && hasZombie) {
            chargeTicks++;
        }

        if (hasZombie && targetRow >= 0 && targetRow < 5) {
            if (plant.type == PlantType.BowlingBulb) {
                spawnBowlingBulb(plant, state, targetRow);
                chargeTicks = 0;
            } else {
                spawnBullets(plant, state, targetRow);
            }
            currentCooldown = (int) (cooldownSeconds * GameLoop.TICKS_PER_SECOND);
            plant.startAttackAnim();
        }
    }

    private boolean canHitZombie(Plant plant, model.data.zombie.Zombie z, Direction dir) {
        if (z == null || !z.isAlive) return false;

        float px = plant.getX();
        float py = plant.getY();
        float zx = z.position.x;
        float zy = z.row * GameState.CELL_HEIGHT + GameState.CELL_HEIGHT / 2f;

        float dx = zx - px;
        float dy = zy - py;
        if (Math.abs(dx) < 1f && Math.abs(dy) < 1f) return false;

        switch (dir) {
            case FORWARD:
                return z.row == plant.row && dx > 0;

            case BACK:
                return z.row == plant.row && dx < 0;

            case UP:
                return z.row < plant.row && Math.abs(dx) <= GameState.CELL_WIDTH / 2f;

            case DOWN:
                return z.row > plant.row && Math.abs(dx) <= GameState.CELL_WIDTH / 2f;

            case UP_RIGHT:
                return dx > 0 && dy < 0 && isNearDiagonal(dx, dy);

            case UP_LEFT:
                return dx < 0 && dy < 0 && isNearDiagonal(dx, dy);

            case DOWN_RIGHT:
                return dx > 0 && dy > 0 && isNearDiagonal(dx, dy);

            case DOWN_LEFT:
                return dx < 0 && dy > 0 && isNearDiagonal(dx, dy);

            default:
                return false;
        }
    }

    private boolean isNearDiagonal(float dx, float dy) {
        float adx = Math.abs(dx);
        float ady = Math.abs(dy);
        float ratio = adx / Math.max(ady, 1f);
        return ratio > 0.5f && ratio < 2.0f;
    }

    private void spawnBullets(Plant plant, GameState state, int targetRow) {
        for (int i = 0; i < shootPattern.getBulletCount(); i++) {
            int xOffset = 40 - (i * 20);
            float targetY = targetRow * GameState.CELL_HEIGHT + GameState.CELL_HEIGHT / 2;
            Position bulletPosition = new Position(plant.getX() + xOffset, targetY);
            Projectile p = createProjectile(plant, bulletPosition, targetRow);
            p.setDirection(shootPattern.getDir());
            state.projectiles.add(p);
        }
    }

    private Projectile createProjectile(Plant plant, Position bulletPosition, int targetRow) {
        if (isBouncingType()){
            Projectile p = new BouncingProjectile(damage,bulletPosition,targetRow, plant.col,10,
                    projectileType,ProjectileTarget.ZOMBIE,plant.type,
                    maxBouncesForType());
            return p;
        }
        if (this.pierceCount != 0 || this.maxRange != -1) {
            Projectile p = new PiercingProjectile(
                    damage, bulletPosition, targetRow, plant.col, 10,
                    projectileType, ProjectileTarget.ZOMBIE, plant.type,
                    this.pierceCount, this.maxRange);
            p.knockBack = knockBack;
            return p;
        }
        Projectile p = new Projectile(
                damage, bulletPosition, targetRow, plant.col, 10,
                projectileType, ProjectileTarget.ZOMBIE, plant.type);
        p.effectDurationBonus = plant.upgradeState.effectDurationBonus;
        return p;
    }
    private void spawnBowlingBulb(Plant plant, GameState state, int targetRow) {
        int shotDamage;
        ProjectileType shotType;
        int maxBounces;

        int five = 5 * GameLoop.TICKS_PER_SECOND;
        int ten  = 10 * GameLoop.TICKS_PER_SECOND;

        if (chargeTicks >= ten) {
            shotDamage = 120 + plant.damage - plant.type.baseStats.damage;
            shotType = ProjectileType.BOUNCING_BLUE;
            maxBounces = 3;
        } else if (chargeTicks >= five) {
            shotDamage = 80 + plant.damage - plant.type.baseStats.damage;
            shotType = ProjectileType.BOUNCING_ORANGE;
            maxBounces = 2;
        } else {
            shotDamage = 40 + plant.damage - plant.type.baseStats.damage;
            shotType = ProjectileType.BOUNCING_AQUA;
            maxBounces = 1;
        }

        float targetY = targetRow * GameState.CELL_HEIGHT + GameState.CELL_HEIGHT / 2;
        Position pos = new Position(plant.getX() + 40, targetY);
        Projectile p = new BouncingProjectile(
                shotDamage, pos, targetRow, plant.col, 10,
                shotType, ProjectileTarget.ZOMBIE, plant.type, maxBounces);
        p.setDirection(shootPattern.getDir());
        state.projectiles.add(p);
    }
    private boolean isBouncingType() {
        return projectileType == ProjectileType.BOUNCING_AQUA
                || projectileType == ProjectileType.BOUNCING_ORANGE
                || projectileType == ProjectileType.BOUNCING_BLUE
                || projectileType == ProjectileType.SUPER_BULB;
    }

    private int maxBouncesForType() {
        return switch (projectileType) {
            case BOUNCING_AQUA -> 1;
            case BOUNCING_ORANGE -> 2;
            case BOUNCING_BLUE, SUPER_BULB -> 3;
            default -> 0;
        };
    }
    public void resetCooldown() {
        currentCooldown = 0;
    }
//    public int getChargeLevel() {
//        int five = 5 * GameLoop.TICKS_PER_SECOND;
//        int ten  = 10 * GameLoop.TICKS_PER_SECOND;
//        if (chargeTicks >= ten) return 2;
//        if (chargeTicks >= five) return 1;
//        return 0;
//    }
}
