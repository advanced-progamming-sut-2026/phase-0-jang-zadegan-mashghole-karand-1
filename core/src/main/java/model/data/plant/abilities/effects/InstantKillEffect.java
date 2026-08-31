package model.data.plant.abilities.effects;

import model.core.EventBus;
import model.core.GameState;
import model.data.plant.PlantType;
import model.data.zombie.Zombie;

public class InstantKillEffect implements HitEffect {
    @Override
    public void apply(Zombie zombie, GameState state, EventBus event, PlantType sourceType) {
        if (zombie == null) {
            return;
        }
        zombie.lastHitBy = sourceType;
        if (!zombie.canBeInstakilled()) {
            zombie.takeDamage(1500);
            if (!zombie.isAlive) {
                zombie.kill(state);
            }
            return;
        }
        zombie.kill(state);
    }
}
