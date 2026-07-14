package model.data.plant.abilities.effects;

import model.core.EventBus;
import model.core.GameState;
import model.data.plant.abilities.effects.HitEffect;
import model.data.zombie.Zombie;
import model.events.ZombieDiedEvent;

public class InstantKillEffect implements HitEffect {
    @Override
    public void apply(Zombie zombie, GameState state, EventBus event) {
        zombie.hp = 0;
        zombie.isAlive = false;
        event.publish(new ZombieDiedEvent(zombie));
    }
}