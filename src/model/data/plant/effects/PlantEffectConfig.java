package model.data.plant.effects;

import model.data.plant.Plant;

public abstract class PlantEffectConfig {
    public abstract PlantEffectConfig createInstance(Plant plant);
}
