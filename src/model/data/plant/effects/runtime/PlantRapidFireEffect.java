package model.data.plant.effects.runtime;

import model.data.plant.Plant;
import model.data.plant.abilities.runtime.PlantShootAbility;
import model.data.plant.effects.config.PlantEffectConfig;

import java.util.ArrayList;
import java.util.List;

public class PlantRapidFireEffect implements PlantEffectConfig {
    public final int duration;
    public final float fireRatePerSec;
    public final List<PlantShootAbility> configAbilities;
    public final List<PlantShootAbility> runtimeAbilities = new ArrayList<>();

    public PlantRapidFireEffect(int duration, float fireRatePerSec ,List<PlantShootAbility> configAbilities ) {
        this.duration = duration;
        this.fireRatePerSec = fireRatePerSec;
        this.configAbilities = configAbilities;
    }

    public PlantRapidFireEffect createInstance(Plant plant) {
        PlantRapidFireEffect instance = new PlantRapidFireEffect(this.duration, this.fireRatePerSec, this.configAbilities);
        for (PlantShootAbility config : this.configAbilities) {
            PlantShootAbility runtimeAbility = (PlantShootAbility) config.createInstance(plant);
            if (runtimeAbility != null) {
                instance.runtimeAbilities.add(runtimeAbility);
            }
        }
        return instance;
    }
}
