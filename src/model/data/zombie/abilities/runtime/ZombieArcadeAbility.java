package model.data.zombie.abilities.runtime;

import model.core.EventBus;
import model.core.GameState;
import model.data.zombie.Zombie;
import model.data.zombie.abilities.config.ZombieAbilityConfig;

public class ZombieArcadeAbility implements ZombieAbilityConfig {
    @Override
    public void onTick(Zombie zombie, GameState state, EventBus bus) {

    }

    @Override
    public ZombieAbilityConfig createInstance(Zombie zombie) {
        return new ZombieArcadeAbility();
    }
}
