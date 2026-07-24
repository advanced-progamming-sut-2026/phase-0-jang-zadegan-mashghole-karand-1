package model.data.plant.abilities.runtime;

import model.data.plant.abilities.config.PlantAbilityConfig;
import model.core.EventBus;
import model.core.GameLoop;
import model.core.GameState;
import model.data.plant.Plant;
import model.data.plant.effects.config.PlantEffectConfig;
import model.data.zombie.Zombie;

import static model.core.GameLoop.TICKS_PER_SECOND;

public class PlantMagnetAbility implements PlantAbilityConfig {

    private int cooldown = 0;
    private final static int BASE_RANGE = 4;

    public PlantMagnetAbility() {

    }

    @Override
    public void onTick(Plant plant, GameState state, EventBus event) {
        if (cooldown > 0) {
            cooldown--;
            return;
        }
        int range = plant.upgradeState.rangeBonus;
        Zombie metalZombie = state.zombies.stream()
                .filter(z -> z.isAlive
                        && z.armor != null
                        && z.armor.isIntact()
                        && "metallic".equals(z.armor.type.material))
                .filter(z -> Math.abs((int)(z.position.x/GameState.CELL_WIDTH) - plant.col) <= BASE_RANGE + range)
                .findFirst()
                .orElse(null);

        if (metalZombie != null) {
            metalZombie.armor.currentHealth = 0;
            metalZombie.armor.isIntact = false;
            cooldown = (int) (plant.actionInterval * TICKS_PER_SECOND);
        }
    }


    @Override
    public PlantAbilityConfig createInstance(Plant plant) {
        return new PlantMagnetAbility();
    }

    @Override
    public void resetCooldown() {
        cooldown = 0;
    }
}

