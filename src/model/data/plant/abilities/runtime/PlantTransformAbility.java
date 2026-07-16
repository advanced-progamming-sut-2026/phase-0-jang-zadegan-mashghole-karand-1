package model.data.plant.abilities.runtime;

import model.core.EventBus;
import model.core.GameState;
import model.data.plant.Plant;
import model.data.plant.PlantType;
import model.data.plant.abilities.config.PlantAbilityConfig;

public class PlantTransformAbility implements PlantAbilityConfig {
    private int transformTimer = 10;
    private PlantType targetPlant;
    public PlantTransformAbility() {
    }

    @Override
    public PlantAbilityConfig createInstance(Plant plant) {
         return  null;
    }


    @Override
    public void onTick(Plant plant, GameState state, EventBus event) {
//        if (transformTimer > 0) {
//            transformTimer--;
//        } else {
//            plant.isAlive = false;
//            Plant copiedPlant = new Plant( , plant.row, plant.col,plant.level,event);
//
//            state.plants.add(copiedPlant);
//        }
    }

}