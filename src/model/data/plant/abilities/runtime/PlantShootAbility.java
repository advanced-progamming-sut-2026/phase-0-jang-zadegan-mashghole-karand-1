package model.data.plant.abilities.runtime;

import model.data.plant.PlantProjectileType;
import model.data.plant.abilities.config.PlantAbilityConfig;
import model.data.projectile.Projectile;
import model.core.EventBus;
import model.core.GameLoop;
import model.core.GameState;
import model.core.Position;
import model.data.plant.Plant;

public class PlantShootAbility implements PlantAbilityConfig {
    public final int damage;
    public final float cooldownSeconds;
    public final PlantProjectileType projectileType;

    private int currentCooldown = 0;

    public PlantShootAbility(int damage, float cooldownSeconds, PlantProjectileType projectileType) {
        this.damage = damage;
        this.cooldownSeconds = cooldownSeconds;
        this.projectileType = projectileType;
    }

    public PlantShootAbility createInstance(Plant plant) {
        int finalDamage = damage + plant.damage - plant.type.baseStats.damage;
        int cooldownTicks = (int) (plant.actionInterval * 10);

        return new PlantShootAbility(finalDamage, cooldownTicks, projectileType);
    }

    @Override
    public void onTick(Plant plant, GameState state, EventBus event) {
        if (currentCooldown > 0) {
            currentCooldown--;
            return;
        }

        boolean hasZombie = state.zombies.stream()
                .anyMatch(z -> z.row == plant.row && z.isAlive);

        if (hasZombie) {
            Projectile p = new Projectile(damage, new Position(plant.getX() + 40, plant.getY()), plant.row, plant.col,
                    10);
            state.projectiles.add(p);
            currentCooldown = (int) (cooldownSeconds * GameLoop.TICKS_PER_SECOND);
        }
    }
}
