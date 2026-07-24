package model.data.plant.effects.runtime;

import model.core.EventBus;
import model.core.GameLoop;
import model.core.GameState;
import model.data.plant.Plant;
import model.data.projectile.ProjectileType;
import model.data.plant.effects.config.PlantEffectConfig;
import model.data.projectile.Projectile;

import java.util.ArrayList;
import java.util.List;

public class PlantTorchEffect implements PlantEffectConfig {
    private static final int DURATION = 5;

    @Override
    public void onTick(Plant plant, GameState state, EventBus event) {
        List<Projectile> removedIce = new ArrayList<>();
        for (Projectile p : state.projectiles) {
            if (p.row == plant.row && Math.abs(p.position.x - plant.getX()) < 30) {
                if(p.type == ProjectileType.ICE){
                    removedIce.add(p);
                }
                else if (p.type == ProjectileType.PEA) {
                    p.type = ProjectileType.FIRE;
                    p.damage *= 3;
                }
            }
        }
        state.projectiles.removeAll(removedIce);
    }

    @Override
    public PlantEffectConfig createInstance(Plant plant) {
        return new PlantTorchEffect();
    }

    @Override
    public int getDurationTicks() {
        return  DURATION * GameLoop.TICKS_PER_SECOND;
    }
}
