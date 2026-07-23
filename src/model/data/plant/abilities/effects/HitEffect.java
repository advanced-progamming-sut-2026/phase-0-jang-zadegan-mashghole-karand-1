package model.data.plant.abilities.effects;

import model.core.EventBus;
import model.core.GameState;
import model.data.plant.PlantType;
import model.data.zombie.Zombie;

public interface HitEffect {
    void apply(Zombie zombie, GameState state, EventBus event, PlantType sourceType);
}

