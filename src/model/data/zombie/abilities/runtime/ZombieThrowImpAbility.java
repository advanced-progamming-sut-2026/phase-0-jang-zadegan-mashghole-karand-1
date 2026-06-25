package model.data.zombie.abilities.runtime;

import model.core.EventBus;
import model.core.GameState;
import model.core.Position;
import model.core.ReadOnlyGameState;
import model.data.zombie.Zombie;
import model.data.zombie.ZombieType;
import model.data.zombie.abilities.config.ZombieAbilityConfig;
import model.events.ZombieSpawnedEvent;

public class ZombieThrowImpAbility implements ZombieAbilityConfig {

    private boolean used = false;

    @Override
    public void onTick(Zombie zombie, GameState state, EventBus bus) {
        if(!used && zombie.hp<=1800) {
            Zombie imp = new Zombie(ZombieType.IMP, zombie.row, 2,
                    new Position((float) 2.5* ReadOnlyGameState.CELL_WIDTH
                            ,zombie.position.y),bus);
            state.zombies.add(imp);
            bus.publish(new ZombieSpawnedEvent(imp));
            used = true;
        }
    }

    @Override
    public ZombieAbilityConfig createInstance(Zombie zombie) {
        return  new ZombieThrowImpAbility();
    }
}
