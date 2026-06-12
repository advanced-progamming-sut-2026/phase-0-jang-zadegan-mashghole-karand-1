package model.data.zombie;

import model.core.EventBus;
import model.core.GameState;

public interface ZombieAbility {
    default void onAttach(Zombie zombie) {
    }

    void onTick(Zombie zombie, GameState state, EventBus bus);
    // default void onDeath(Zombie zombie, GameState state, EventBus bus) {}
}
