package model.data.zombie.abilities.runtime;

import model.core.EventBus;
import model.core.GameState;
import model.core.ReadOnlyGameState;
import model.data.zombie.Zombie;
import model.data.zombie.abilities.config.ZombieAbilityConfig;

public class ZombieDynamiteAbility implements ZombieAbilityConfig {
    private boolean enable = true;
    private int ticksElapsed = 0;

    @Override
    public void onTick(Zombie zombie, GameState state, EventBus bus) {
        if(!enable) return;

        if (zombie.isFrozen) return;

        ticksElapsed++;
        if (ticksElapsed >= 100) {
            zombie.col = 0;
            zombie.speed *= -1;
            zombie.position.x = ReadOnlyGameState.CELL_WIDTH / 2f;
            enable = false;
        }
    }

    @Override
    public ZombieAbilityConfig createInstance(Zombie zombie) {
        return new ZombieDynamiteAbility();
    }
}
