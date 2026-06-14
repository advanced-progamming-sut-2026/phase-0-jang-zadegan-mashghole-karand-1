package model.data.plant.effects.runtime;

import model.data.plant.Plant;
import model.data.plant.effects.config.PlantEffectConfig;

public class PlantInstantSunEffect implements PlantEffectConfig {
    public final int amount;

    public PlantInstantSunEffect(int amount) {
        this.amount = amount;
    }

    public PlantInstantSunEffect createInstance(Plant plant) {
        return new PlantInstantSunEffect(amount);
    }
}
