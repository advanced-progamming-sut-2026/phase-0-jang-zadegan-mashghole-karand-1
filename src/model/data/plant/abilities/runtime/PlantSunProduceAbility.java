package model.data.plant.abilities.runtime;

import model.data.plant.Plant;
import model.data.plant.abilities.config.PlantAbilityConfig;

public class PlantSunProduceAbility implements PlantAbilityConfig {
    public final int amount;
    public final float intervalSeconds;

    public PlantSunProduceAbility(int amount, float intervalSeconds) {
        this.amount = amount;
        this.intervalSeconds = intervalSeconds;
    }

    public PlantSunProduceAbility createInstance(Plant plant) {
        // should implement upgrades effect here
        return new PlantSunProduceAbility(amount, intervalSeconds);
    }
}
