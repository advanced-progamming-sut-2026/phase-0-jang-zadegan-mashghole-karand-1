package model.data.zombie.abilities.config;

import model.core.EventBus;
import model.core.GameState;
import model.data.zombie.Zombie;

public interface ZombieAbilityConfig {
    public void onTick(Zombie zombie, GameState state, EventBus bus);

    default public void onDeath(Zombie zombie, GameState state, EventBus bus) {
    }

    default public void onAttach(Zombie zombie) {
    }

    public ZombieAbilityConfig createInstance(Zombie zombie);
}