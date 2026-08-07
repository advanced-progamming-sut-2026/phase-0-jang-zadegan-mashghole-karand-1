package model.data.plant.abilities.config;

import model.core.EventBus;
import model.core.GameState;
import model.data.plant.Plant;
import model.data.zombie.Zombie;

public interface PlantAbilityConfig {
    public PlantAbilityConfig createInstance(Plant plant);

    public void onTick(Plant plant, GameState state, EventBus event);
    default void resetCooldown() {
    }
    default void onDeath(Plant plant, Zombie killer, GameState state,EventBus event) {
    }
}
