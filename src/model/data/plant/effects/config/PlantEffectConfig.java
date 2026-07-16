package model.data.plant.effects.config;

import model.core.EventBus;
import model.core.GameState;
import model.data.plant.Plant;

public interface PlantEffectConfig {
    default void onActivate(Plant plant, GameState state, EventBus event) {}
    default void onTick(Plant plant, GameState state, EventBus event) {}
    default void onDeactivate(Plant plant, GameState state, EventBus event) {}
    default int getDurationTicks() {
        return 0;
    }
    default void trigger(Plant plant, GameState state){}
    public PlantEffectConfig createInstance(Plant plant);
}
