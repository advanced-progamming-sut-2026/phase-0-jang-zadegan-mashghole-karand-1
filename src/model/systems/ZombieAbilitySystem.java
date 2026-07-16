package model.systems;

import model.core.GameState;
import model.data.zombie.Zombie;

public class ZombieAbilitySystem {
    public void update(GameState state) {
        for(Zombie zombie : state.zombies) {
            if(!zombie.isAlive) continue;
            zombie.onTickAbilities(state);
        }
    }
}
