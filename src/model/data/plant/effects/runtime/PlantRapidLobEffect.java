package model.data.plant.effects.runtime;

import model.data.plant.Plant;
import model.data.plant.abilities.runtime.PlantLobAbility;
import model.data.plant.effects.config.PlantEffectConfig;

import java.util.ArrayList;
import java.util.List;

public class PlantRapidLobEffect implements PlantEffectConfig {
    public final int duration;
    public final float fireRatePerSec;
    public final List<PlantLobAbility> configAbilities;
    public final List<PlantLobAbility> runtimeAbilities = new ArrayList<>();
    public PlantRapidLobEffect(int duration, float fireRatePerSec, List<PlantLobAbility> configAbilities) {
        this.duration = duration;
        this.fireRatePerSec = fireRatePerSec;
        this.configAbilities = configAbilities;
    }

    @Override
    public PlantEffectConfig createInstance(Plant plant) {
        PlantRapidLobEffect runtimeEffect = new PlantRapidLobEffect(this.duration, this.fireRatePerSec, this.configAbilities);
        for (PlantLobAbility configAbility : this.configAbilities) {
            runtimeEffect.runtimeAbilities.add((PlantLobAbility) configAbility.createInstance(plant));
        }

        return runtimeEffect;
    }
}
