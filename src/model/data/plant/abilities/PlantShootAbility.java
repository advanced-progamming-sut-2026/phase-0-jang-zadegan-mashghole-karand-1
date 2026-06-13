package model.data.plant.abilities;

import model.data.plant.PlantProjectileType;
import model.data.plant.Plant;

public class PlantShootAbility extends PlantAbilityConfig {
    public final int damage;
    public final float cooldownSeconds;
    public final PlantProjectileType projectileType;

    public PlantShootAbility(int damage, float cooldownSeconds, PlantProjectileType projectileType) {
        this.damage = damage;
        this.cooldownSeconds = cooldownSeconds;
        this.projectileType = projectileType;
    }

    @Override
    public PlantShootAbility createInstance(Plant plant) {
        int finalDamage = damage + plant.currentDamage - plant.type.baseStats.damage;
        int cooldownTicks = (int) (plant.currentActionInterval * 10);

        return new PlantShootAbility(finalDamage, cooldownTicks, projectileType);
    }
}
