package model.systems;

import model.core.EventBus;
import model.core.GameState;
import model.data.plant.Plant;
import model.data.plant.abilities.config.PlantAbilityConfig;

import java.util.ArrayList;

public class PlantAbilitySystem {

    public void update(GameState state, EventBus eventBus) {
        for (Plant plant : new ArrayList<>(state.plants)) {
            if (!plant.isAlive || plant.hp <= 0) {
                continue;
            }
            if (plant.isFrostbiteFreezeActive()) {
                continue;
            }
            for (PlantAbilityConfig ability : plant.abilities) {
                if (!plant.canUseAbilities()) continue;
                ability.onTick(plant, state, eventBus);
            }
        }
    }
}
