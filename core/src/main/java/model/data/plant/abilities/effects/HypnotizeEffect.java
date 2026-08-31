package model.data.plant.abilities.effects;

import model.core.EventBus;
import model.core.GameState;
import model.data.plant.Plant;
import model.data.plant.PlantType;
import model.data.zombie.Zombie;

public class HypnotizeEffect implements HitEffect{
    @Override
    public void apply(Zombie zombie, GameState state, EventBus event, PlantType sourceType) {
        if (zombie == null || !zombie.isAlive || !zombie.canBeHypnotized()) {
            return;
        }
        zombie.isHypnotized = true;
    }
}
