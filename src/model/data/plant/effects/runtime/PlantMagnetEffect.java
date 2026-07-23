package model.data.plant.effects.runtime;

import model.core.EventBus;
import model.core.GameLoop;
import model.core.GameState;
import model.data.plant.Plant;
import model.data.plant.abilities.config.PlantAbilityConfig;
import model.data.plant.abilities.runtime.PlantMagnetAbility;
import model.data.plant.effects.config.PlantEffectConfig;
import model.data.zombie.Zombie;

import java.util.List;

public class PlantMagnetEffect implements PlantEffectConfig {

    private int cooldown = 0;

    public PlantMagnetEffect() {
    }

    @Override
    public PlantEffectConfig createInstance(Plant plant) {
        return new PlantMagnetEffect();
    }
    @Override
    public void onActivate(Plant plant, GameState state, EventBus event) {
        for (Zombie z : state.zombies) {
            if (!z.isAlive || z.armor == null || !z.armor.isIntact()) continue;
            if (!"metallic".equals(z.armor.type.material)) continue;
            z.armor.currentHealth = 0;
            z.armor.isIntact = false;
        }
        for (PlantAbilityConfig ability : plant.abilities) {
            ability.resetCooldown();
        }
    }

    @Override
    public int getDurationTicks() {
        return 0;
    }
}
