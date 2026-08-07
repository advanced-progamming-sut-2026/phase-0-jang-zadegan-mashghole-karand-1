package model.data.plant.effects.runtime;

import model.core.EventBus;
import model.core.GameState;
import model.data.plant.Plant;
import model.data.plant.abilities.config.PlantAbilityConfig;
import model.data.plant.abilities.runtime.PlantHypnotizeAbility;
import model.data.plant.effects.config.PlantEffectConfig;

public class PlantHypnoGargEffect implements PlantEffectConfig {
    @Override
    public void onActivate(Plant plant, GameState state, EventBus event) {
        for (PlantAbilityConfig ability : plant.abilities) {
            if (ability instanceof PlantHypnotizeAbility hypnotize) {
                hypnotize.setTransformToGargantuar(true);
                break;
            }
        }
    }

    @Override
    public PlantEffectConfig createInstance(Plant plant) {
        return new PlantHypnoGargEffect();
    }
}
