package model.data.plant.abilities.effects;

import model.core.EventBus;
import model.core.GameState;
import model.data.zombie.Zombie;

public class FreezeEffect implements HitEffect {
    private final int ticks;

    public FreezeEffect(int ticks) {
        this.ticks = ticks;
    }

    @Override
    public void apply(Zombie zombie, GameState state, EventBus event) {
        if(!zombie.canBeFrozen) return;
        zombie.isFrozen = true;
        zombie.frozenTicks = Math.max(zombie.frozenTicks, ticks);
    }
}
