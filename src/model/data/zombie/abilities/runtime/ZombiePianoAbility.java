package model.data.zombie.abilities.runtime;

import model.core.EventBus;
import model.core.GameState;
import model.core.ReadOnlyGameState;
import model.data.zombie.Zombie;
import model.data.zombie.abilities.config.ZombieAbilityConfig;

import java.util.Random;

public class ZombiePianoAbility implements ZombieAbilityConfig {
    private int tickCounter = 0;
    Random rand = new Random();
    @Override
    public void onTick(Zombie zombie, GameState state, EventBus bus) {

        tickCounter++;

        if (tickCounter % 30 != 0) return;

        for(Zombie z : state.getZombies()) {
            if(z.isAlive && !z.equals(zombie)) {
                int random = rand.nextInt(3)-1;
                if(z.row + random >=0 && z.row + random <=4) {
                    z.row += random;
                    z.position.y += ReadOnlyGameState.CELL_HEIGHT*random;
                }
            }
        }
    }

    @Override
    public ZombieAbilityConfig createInstance(Zombie zombie) {
        return new  ZombiePianoAbility();
    }
}
