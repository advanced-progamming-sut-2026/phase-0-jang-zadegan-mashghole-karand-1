package model.systems;

import model.core.EventBus;
import model.core.GameState;
import model.data.zombie.Zombie;
import model.data.zombie.abilities.config.ZombieAbilityConfig;

public class ZombieAbilitySystem {
    public void update(GameState state, EventBus eventBus) {
        for (Zombie zombie : state.zombies) {
            if (!zombie.isAlive)
                continue;
            if (zombie.isIced())
                continue;
            if(zombie.stunned)
                continue;
            for (ZombieAbilityConfig ability : zombie.abilities) {
                ability.onTick(zombie, state, eventBus);
            }
        }
    }
}
