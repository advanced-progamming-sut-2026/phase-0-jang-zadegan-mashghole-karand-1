package model.data.plant.abilities.runtime;

import model.core.EventBus;
import model.core.GameLoop;
import model.core.GameState;
import model.data.plant.Plant;
import model.data.plant.PlantType;
import model.data.plant.abilities.config.PlantAbilityConfig;

public class PlantTransformAbility implements PlantAbilityConfig {
    private int transformTimer = 10 * GameLoop.TICKS_PER_SECOND;
    private PlantType targetPlant;
    private boolean done = false;
    public PlantTransformAbility() {
    }

    @Override
    public PlantAbilityConfig createInstance(Plant plant) {
         return  new PlantTransformAbility();
    }


    @Override
    public void onTick(Plant plant, GameState state, EventBus event) {
        if (done)return;
        if (transformTimer > 0) {
            transformTimer--;
        } else {
            done = true;
            int row = plant.row;
            int col = plant.col;
            int level = plant.level;
            state.removePlant(plant);
            Plant copiedPlant = new Plant(targetPlant, row, col, level, event);
            if (plant.upgradeState.plantFoodOnEnteranc){
                copiedPlant.activatePlantFood(state,event);
            }
            state.addPlant(copiedPlant);
        }
    }

    public void setTargetPlant(PlantType targetPlant) {
        this.targetPlant = targetPlant;
    }
}