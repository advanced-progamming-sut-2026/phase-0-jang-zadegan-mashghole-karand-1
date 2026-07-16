package model.data.plant.abilities.effects;

import model.core.EventBus;
import model.core.GameState;
import model.data.zombie.Zombie;

public class HypnotizeEffect implements HitEffect{
    @Override
    public void apply(Zombie zombie, GameState state, EventBus event) {
        if (!zombie.isAlive || zombie == null) return;
        zombie.isHypnotized = true;
        zombie.speed = -Math.abs(zombie.speed);
    }
}
