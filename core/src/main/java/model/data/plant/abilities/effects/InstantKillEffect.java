package model.data.plant.abilities.effects;

import model.core.EventBus;
import model.core.GameState;
import model.data.plant.PlantType;
import model.data.zombie.Zombie;

public class InstantKillEffect implements HitEffect {
    @Override
    public void apply(Zombie zombie, GameState state, EventBus event, PlantType sourceType) {
        zombie.lastHitBy = sourceType;
        zombie.kill(state);
    }
}
