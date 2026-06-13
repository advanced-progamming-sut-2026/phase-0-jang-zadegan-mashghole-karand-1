package model.data.plant.abilities;

import model.data.plant.Plant;

public class PlantSunProduceAbility extends PlantAbilityConfig {
    public final int amount;
    public final float intervalSeconds;

    public PlantSunProduceAbility(int amount, float intervalSeconds) {
        this.amount = amount;
        this.intervalSeconds = intervalSeconds;
    }

    @Override
    public PlantSunProduceAbility createInstance(Plant plant) {
        // should implement upgrades effect here
        return new PlantSunProduceAbility(amount, intervalSeconds);
    }
}
