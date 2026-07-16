package model.data.plant.abilities.runtime;

import model.core.EventBus;
import model.core.GameLoop;
import model.core.GameState;
import model.core.Position;
import model.data.plant.Plant;
import model.data.projectile.ProjectileType;
import model.data.plant.abilities.config.PlantAbilityConfig;
import model.data.plant.abilities.config.ShootPattern;
import model.data.projectile.LobbedProjectile;
import model.data.projectile.Projectile;
import model.data.projectile.ProjectileTarget;
import model.data.zombie.Zombie;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PlantLobAbility implements PlantAbilityConfig {
    public final int damage;
    public final float cooldownSeconds;
    public final ProjectileType projectileType;
    public final ShootPattern shootPattern;
    public final float butterChance;
    public final int butterDamage;
    public final int aoeRadius;
    private int currentCooldown = 0;

    public PlantLobAbility(int damage, float cooldownSeconds, ProjectileType projectileType, ShootPattern shootPattern, float butterChance, int butterDamage, int aoeRadius) {
        this.damage = damage;
        this.cooldownSeconds = cooldownSeconds;
        this.projectileType = projectileType;
        this.shootPattern = shootPattern;
        this.butterChance = butterChance;
        this.butterDamage = butterDamage;
        this.aoeRadius = aoeRadius;
    }

    public PlantLobAbility(int damage, float cooldownSeconds,
                           ProjectileType type, ShootPattern pattern) {
        this(damage, cooldownSeconds, type, pattern, 0f, 40, 0);
    }

    public PlantAbilityConfig createInstance(Plant plant) {
        int finalDamage = damage + plant.damage - plant.type.baseStats.damage;
        return new PlantLobAbility(finalDamage, plant.actionInterval, projectileType, shootPattern,
                butterChance, butterDamage, aoeRadius);
    }

    @Override
    public void onTick(Plant plant, GameState state, EventBus event) {
        if (currentCooldown > 0) {
            currentCooldown--;
            return;
        }

        List<Zombie> targets;
        if (shootPattern.isRandom()) {
            targets = state.zombies.stream()
                    .filter(z -> z.isAlive)
                    .collect(Collectors.toList());
            Collections.shuffle(targets);
        } else {
            int targetRow = plant.row + shootPattern.getRow();
            targets = state.zombies.stream()
                    .filter(z -> z.row == targetRow && z.isAlive)
                    .collect(Collectors.toList());
        }
        if (!targets.isEmpty()) {
            int count = Math.min(shootPattern.getBulletCount(), targets.size());

            for (int i = 0; i < count; i++) {
                Zombie target = targets.get(i);

                int xOffset = 40 - (i * 20);

                Projectile p = new LobbedProjectile(
                        damage,
                        new Position(plant.getX() + xOffset, plant.getY()),
                        target.row,
                        target.col,
                        5,
                        projectileType,
                        ProjectileTarget.ZOMBIE,
                        new Position(target.position.x , target.position.y),
                        0,
                        50f,
                        60f,
                        this.butterChance,
                        this.butterDamage,
                        this.aoeRadius
                );
                p.setDirection(shootPattern.getDir());


                state.projectiles.add(p);
            }

            currentCooldown = (int) (cooldownSeconds * GameLoop.TICKS_PER_SECOND);
        }
    }
}
