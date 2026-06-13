package model.data.plant.effects;

import model.data.plant.Plant;

public class PlantInstantSunEffect extends PlantEffectConfig {
    public final int amount;

    public PlantInstantSunEffect(int amount) {
        this.amount = amount;
    }

    @Override
    public PlantInstantSunEffect createInstance(Plant plant) {
        return new PlantInstantSunEffect(amount);
    }
}
