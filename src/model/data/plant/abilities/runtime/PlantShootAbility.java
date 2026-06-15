package model.data.plant.abilities.runtime;

import model.data.plant.PlantProjectileType;
import model.data.plant.abilities.config.PlantAbilityConfig;
import model.core.EventBus;
import model.core.GameState;
import model.data.plant.Plant;

public class PlantShootAbility implements PlantAbilityConfig {
    public final int damage;
    public final float cooldownSeconds;
    public final PlantProjectileType projectileType;

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

    public void onTick(Plant plant, GameState state, EventBus event) {
    }
}
