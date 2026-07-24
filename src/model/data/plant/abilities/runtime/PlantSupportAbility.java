package model.data.plant.abilities.runtime;

import model.core.EventBus;
import model.core.GameState;
import model.data.plant.Plant;
import model.data.plant.abilities.config.PlantAbilityConfig;

public class PlantSupportAbility implements PlantAbilityConfig {
    @Override
    public PlantAbilityConfig createInstance(Plant plant) {
        return new PlantSupportAbility();
    }

    @Override
    public void onTick(Plant plant, GameState state, EventBus event) {

    }
}
