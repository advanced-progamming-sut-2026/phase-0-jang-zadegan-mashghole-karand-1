package model.data.plant;

import model.core.EventBus;
import model.core.GameState;

public interface PlantAbility {
    // Called when ability is attached to a plant
    default void onAttach(Plant plant) {
    }

    // Called every tick
    void onTick(Plant plant, GameState state, EventBus bus);
    // default void onDeath(Plant plant, GameState state, EventBus bus) {}
}