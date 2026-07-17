package model.systems;

import model.core.EventBus;
import model.core.GameState;
import model.data.plant.Plant;
import model.data.plant.abilities.config.PlantAbilityConfig;

public class PlantAbilitySystem {

    public void update(GameState state, EventBus eventBus) {
        for (Plant plant : state.plants) {
            if (plant.isFrostbiteFreezeActive()) {
                continue;
            }
            for (PlantAbilityConfig ability : plant.abilities) {
                ability.onTick(plant, state, eventBus);
            }
        }
    }
}
