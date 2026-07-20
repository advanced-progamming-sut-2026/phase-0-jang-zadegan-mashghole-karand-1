package model.data.plant.abilities.runtime;

import model.core.EventBus;
import model.core.GameState;
import model.core.Position;
import model.data.plant.Plant;
import model.data.plant.abilities.config.PlantAbilityConfig;
import model.data.zombie.Zombie;
import model.data.zombie.ZombieType;

public class PlantHypnotizeAbility implements PlantAbilityConfig {
    private boolean transformToGargantuar = false;
    public PlantHypnotizeAbility() {
    }

    @Override
    public PlantAbilityConfig createInstance(Plant plant) {
        return new PlantHypnotizeAbility();
    }
    @Override
    public void onTick(Plant plant, GameState state, EventBus event) {
    }
    @Override
    public void onDeath(Plant plant, Zombie killer, GameState state,EventBus event) {
        if (killer != null && killer.isAlive) {
            if (this.transformToGargantuar) {
                killer.isAlive = false;
                Zombie garg = new Zombie(ZombieType.GARGANTUAR, killer.row, killer.col,new Position(killer.row, killer.col),event);
                garg.isHypnotized = true;
                state.zombies.add(garg);
            } else {
                killer.isHypnotized = true;
            }
        }
    }

    public void setTransformToGargantuar(boolean transformToGargantuar) {
        this.transformToGargantuar = transformToGargantuar;
    }
}