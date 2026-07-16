package model.data.plant.effects.runtime;

import model.core.EventBus;
import model.core.GameState;
import model.data.plant.Plant;
import model.data.projectile.ProjectileType;
import model.data.plant.effects.config.PlantEffectConfig;
import model.data.projectile.Projectile;

public class PlantTorchEffect implements PlantEffectConfig {

    @Override
    public void onTick(Plant plant, GameState state, EventBus event) {
        for (Projectile p : state.projectiles) {
            if (p.row == plant.row && Math.abs(p.position.x - plant.getX()) < 30) {

                p.type = ProjectileType.BLUE_FIRE;
                p.damage *= 3;
            }
        }
    }

    @Override
    public PlantEffectConfig createInstance(Plant plant) {
        return new PlantTorchEffect();
    }
}
