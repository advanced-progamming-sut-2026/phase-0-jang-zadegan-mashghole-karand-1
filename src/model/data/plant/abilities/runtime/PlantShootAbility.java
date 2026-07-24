package model.data.plant.abilities.runtime;

import model.data.plant.PlantType;
import model.data.projectile.ProjectileType;
import model.data.plant.abilities.config.Direction;
import model.data.plant.abilities.config.PlantAbilityConfig;
import model.data.plant.abilities.config.ShootPattern;
import model.data.plant.effects.config.EffectPhase;
import model.data.projectile.PiercingProjectile;
import model.data.projectile.Projectile;
import model.core.EventBus;
import model.core.GameLoop;
import model.core.GameState;
import model.core.Position;
import model.data.plant.Plant;
import model.data.projectile.ProjectileTarget;

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
    public static Builder builder() { return new Builder(); }
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
        public Builder damage(int v) { damage = v; return this; }
        public Builder cooldown(float v) { cooldownSeconds = v; return this; }
        public Builder projectile(ProjectileType v) { projectileType = v; return this; }
        public Builder pattern(ShootPattern v) { shootPattern = v; return this; }
        public Builder pierce(int v) { pierceCount = v; return this; }
        public Builder maxRange(float v) { maxRange = v; return this; }
        public Builder knockBack(int v) { knockBack = v; return this; }
        public Builder phase(EffectPhase v) { phase = v; return this; }
        public Builder lifespan(int v){ lifespan = v; return this;}
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
        int finalSpan = ( lifeSpan + plant.upgradeState.lifeSpanBonus) * 10;
        return PlantShootAbility.builder()
                .damage(finalDamage)
                .cooldown(plant.actionInterval)
                .projectile(projectileType)
                .pattern(shootPattern)
                .pierce(finalPierce)
                .maxRange(finalRange)
                .knockBack(knockBack)
                .phase(phase)
                .lifespan(finalSpan)
                .build();    }

    @Override
    public void onTick(Plant plant, GameState state, EventBus event) {
        if (lifeSpan > 0) {
            lifeSpan--;
            if (lifeSpan <= 0) plant.hp = 0;
        }
        if (currentCooldown > 0) {
            currentCooldown--;
            return;
        }
        int targetRow = plant.row + shootPattern.getRow();
        boolean hasZombie = state.zombies.stream()
                .anyMatch(z -> z.row == targetRow && z.isAlive);

        if (hasZombie && targetRow >= 0 && targetRow < 5) {
            for (int i = 0; i < shootPattern.getBulletCount(); i++) {
                int xOffset = 40 - (i * 20);
                Position bulletPosition = new Position(plant.getX() + xOffset, plant.getY());
                Projectile p;

                if (this.pierceCount != 0 || this.maxRange != -1) {
                    p = new PiercingProjectile(
                            damage,
                            bulletPosition,
                            targetRow,
                            plant.col,
                            10,
                            projectileType,
                            ProjectileTarget.ZOMBIE,
                            plant.type,
                            this.pierceCount,
                            this.maxRange
                    );
                    p.knockBack = knockBack;

                } else {
                    p = new Projectile(
                            damage,
                            bulletPosition,
                            targetRow,
                            plant.col,
                            10,
                            projectileType,
                            ProjectileTarget.ZOMBIE,plant.type
                    );
                    p.effectDurationBonus = plant.upgradeState.effectDurationBonus;

                }

                p.setDirection(shootPattern.getDir());
                state.projectiles.add(p);
            }

            currentCooldown = (int) (cooldownSeconds * GameLoop.TICKS_PER_SECOND);
        }
    }

    public void resetCooldown() {
        currentCooldown = 0;
    }
}
