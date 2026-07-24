package model.data.plant.abilities.runtime;

import model.core.EventBus;
import model.core.GameState;
import model.data.plant.Plant;
import model.data.projectile.ProjectileType;
import model.data.plant.abilities.config.PlantAbilityConfig;
import model.data.projectile.Projectile;
import model.data.zombie.Zombie;

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

    @Override
    public void onDeath(Plant plant, Zombie killer, GameState state, EventBus event) {
        if (!plant.upgradeState.aoeOnDeath)return;
        for (Zombie z : state.zombies){
            if (!z.isAlive) continue;
            int col = (int) (z.position.x /GameState.CELL_WIDTH);
            if (Math.abs(z.row - plant.row) <= 1 && Math.abs(col - plant.col) <= 1 ){
                z.lastHitBy = plant.type;
                z.takeDamage(300);
                if (!z.isAlive) {
                    z.kill(state);
                }
            }
        }
    }
}