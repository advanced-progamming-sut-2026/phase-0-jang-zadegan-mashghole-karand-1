package model.data.plant.abilities.runtime;

import model.core.EventBus;
import model.core.GameLoop;
import model.core.GameState;
import model.core.Position;
import model.data.plant.Plant;
import model.data.plant.PlantType;
import model.data.projectile.ProjectileType;
import model.data.plant.abilities.config.PlantAbilityConfig;
import model.data.plant.abilities.config.TargetStrategy;
import model.data.projectile.HomingProjectile;
import model.data.projectile.Projectile;
import model.data.projectile.ProjectileTarget;
import model.data.zombie.Zombie;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class PlantHomingAbility implements PlantAbilityConfig {

    public final int damage;
    public final float cooldownSeconds;
    public final ProjectileType projectileType;
    public final TargetStrategy strategy;
    private int currentCooldown = 0;

    public PlantHomingAbility(int damage, float cooldownSeconds, ProjectileType projectileType,
            TargetStrategy strategy) {
        this.damage = damage;
        this.cooldownSeconds = cooldownSeconds;
        this.projectileType = projectileType;
        this.strategy = strategy;
    }

    @Override
    public PlantAbilityConfig createInstance(Plant plant) {
        int finalDamage = damage + plant.damage - plant.type.baseStats.damage;
        return new PlantHomingAbility(finalDamage, plant.actionInterval, projectileType, strategy);
    }

    @Override
    public void onTick(Plant plant, GameState state, EventBus event) {
        if (currentCooldown > 0) {
            currentCooldown--;
            return;
        }
        List<Zombie> aliveZombies = state.zombies.stream()
                .filter(z -> z.isAlive)
                .toList();
        if (aliveZombies.isEmpty()) {
            return;
        }

        Zombie target = selectTarget(plant, state, aliveZombies);
        if (target != null) {
            fireAtTarget(plant, state, target);
        }
    }

    private Zombie selectTarget(Plant plant, GameState state, List<Zombie> aliveZombies) {
        if (plant.upgradeState.targetPriorityBonus > 0) {
            return Collections.max(
                    aliveZombies,
                    Comparator.comparingInt(this::priority));
        }
        if (this.strategy == TargetStrategy.CLOSEST) {
            return aliveZombies.stream()
                    .min((z1, z2) -> Float.compare(plant.getX() - z1.position.x, plant.getX() - z2.position.x))
                    .orElse(null);
        }
        if (this.strategy == TargetStrategy.RANDOM) {
            return selectRandomTarget(plant, state, aliveZombies);
        }
        return null;
    }

    private Zombie selectRandomTarget(Plant plant, GameState state, List<Zombie> aliveZombies) {
        if (plant.type == PlantType.Caulipower) {
            List<Zombie> hypnotizable = aliveZombies.stream()
                    .filter(z -> !z.isHypnotized && z.canBeHypnotized())
                    .toList();
            if (hypnotizable.isEmpty()) {
                return null;
            }
            return hypnotizable.get(new Random().nextInt(hypnotizable.size()));
        }
        List<Zombie> instakillable = aliveZombies.stream()
                .filter(Zombie::canBeInstakilled)
                .toList();
        if (plant.type == PlantType.Electric_Blueberry && !instakillable.isEmpty()) {
            Zombie target = instakillable.get(new Random().nextInt(instakillable.size()));
            target.kill(state);
            return target;
        }
        return aliveZombies.get(new Random().nextInt(aliveZombies.size()));
    }

    private void fireAtTarget(Plant plant, GameState state, Zombie target) {
        Position ps = new Position(plant.getX(), plant.getY());
        Projectile pj = new HomingProjectile(
                damage,
                ps,
                plant.row,
                plant.col,
                10,
                projectileType,
                ProjectileTarget.ZOMBIE,
                target, plant.type);
        state.projectiles.add(pj);
        currentCooldown = (int) (cooldownSeconds * GameLoop.TICKS_PER_SECOND);
        plant.startAttackAnim();
    }

    private int priority(Zombie zombie) {
        return switch (zombie.type) {
            case GARGANTUAR -> 4;
            case KING -> 3;
            case IMP -> 2;
            case BASIC -> 1;
            default -> 0;
        };
    }

    @Override
    public void resetCooldown() {
        currentCooldown = 0;
    }
}
