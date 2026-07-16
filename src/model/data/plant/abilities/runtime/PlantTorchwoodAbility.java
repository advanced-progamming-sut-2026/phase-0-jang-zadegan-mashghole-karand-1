package model.data.plant.abilities.runtime;

import model.core.EventBus;
import model.core.GameState;
import model.data.plant.Plant;
import model.data.projectile.ProjectileType;
import model.data.plant.abilities.config.PlantAbilityConfig;
import model.data.projectile.Projectile;

public class PlantTorchwoodAbility implements PlantAbilityConfig {

    @Override
    public PlantAbilityConfig createInstance(Plant plant) {
        return new PlantTorchwoodAbility();
    }

    @Override
    public void onTick(Plant plant, GameState state, EventBus event) {
        for (Projectile p : state.projectiles) {
            if (p.row == plant.row && Math.abs(p.position.x - plant.getX()) < 30) {

                p.type = ProjectileType.FIRE;
                p.damage *= 2;
            }
        }
    }
}