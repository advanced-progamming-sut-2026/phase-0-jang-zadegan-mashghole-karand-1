package model.data.plant.effects.runtime;

import model.core.EventBus;
import model.core.GameLoop;
import model.core.GameState;
import model.data.plant.Plant;
import model.data.plant.abilities.runtime.PlantLobAbility;
import model.data.plant.abilities.runtime.PlantShootAbility;
import model.data.plant.effects.config.PlantEffectConfig;

import java.util.ArrayList;
import java.util.List;

public class PlantRapidLobEffect implements PlantEffectConfig {
    public final int duration;
    public final float fireRatePerSec;
    public final List<PlantLobAbility> configAbilities;
    public final List<PlantLobAbility> runtimeAbilities = new ArrayList<>();
    private int fireCooldown = 0;
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
    @Override
    public int getDurationTicks() {
        return (int) (duration * GameLoop.TICKS_PER_SECOND);
    }

    @Override
    public void onTick(Plant plant, GameState state, EventBus event) {
        if (fireCooldown > 0) {
            fireCooldown--;
            return;
        }
        for (PlantLobAbility a : runtimeAbilities) {
            a.resetCooldown();
            a.onTick(plant, state, event);
        }
        fireCooldown = Math.max(0, (int) (GameLoop.TICKS_PER_SECOND / fireRatePerSec) - 1);

    }
}
