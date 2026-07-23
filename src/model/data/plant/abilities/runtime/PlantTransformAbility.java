package model.data.plant.abilities.runtime;

import model.board.Tile;
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
            plant.isAlive = false;
            plant.hp = 0;
            Plant copiedPlant = new Plant(targetPlant, plant.row, plant.col,plant.level,event);
            if (plant.upgradeState.plantFoodOnEnteranc){
                copiedPlant.activatePlantFood(state,event);
            }
            state.removePlant(plant);
            state.addPlant(copiedPlant);
        }
    }

    public void setTargetPlant(PlantType targetPlant) {
        this.targetPlant = targetPlant;
    }
}