package model.data.plant.effects.runtime;

import model.data.plant.Plant;
import model.data.plant.effects.config.PlantEffectConfig;

public class PlantRapidFireEffect implements PlantEffectConfig {
    public final int duration;
    public final float fireRatePerSec;

    public PlantRapidFireEffect(int duration, float fireRatePerSec) {
        this.duration = duration;
        this.fireRatePerSec = fireRatePerSec;
    }

    public PlantRapidFireEffect createInstance(Plant plant) {
        return new PlantRapidFireEffect(duration, fireRatePerSec);
    }
}
