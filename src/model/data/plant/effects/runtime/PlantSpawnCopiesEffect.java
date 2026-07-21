package model.data.plant.effects.runtime;

import model.data.plant.Plant;
import model.data.plant.effects.config.PlantEffectConfig;

public class PlantSpawnCopiesEffect implements PlantEffectConfig {
    @Override
    public PlantEffectConfig createInstance(Plant plant) {
        return new PlantSpawnCopiesEffect();
    }
}
