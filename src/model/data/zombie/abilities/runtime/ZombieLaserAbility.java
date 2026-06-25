package model.data.zombie.abilities.runtime;

import model.core.EventBus;
import model.core.GameState;
import model.data.plant.Plant;
import model.data.zombie.Zombie;
import model.data.zombie.abilities.config.ZombieAbilityConfig;
import model.events.PlantDiedEvent;

import java.util.List;

public class ZombieLaserAbility implements ZombieAbilityConfig {
    private boolean enabledCharging = false;
    private int chargeStartTime;
    @Override
    public void onTick(Zombie zombie, GameState state, EventBus bus) {
        List<Plant> targets = state.getPlants().stream()
                .filter(p -> p.row == zombie.row &&
                        p.col<= zombie.col && p.col>= zombie.col-4)
                .toList();
        if(enabledCharging) {
           int chargedTime = state.totalTicks - chargeStartTime;
           if(chargedTime >=50){
               for(Plant p : targets) {
                   p.isAlive = false;
                   bus.publish(new PlantDiedEvent(p));
               }
               enabledCharging = false;
           }
        }
        else if(!targets.isEmpty()) {
            enabledCharging = true;
            chargeStartTime = state.totalTicks;
        }

    }

    @Override
    public ZombieAbilityConfig createInstance(Zombie zombie) {
        return null;
    }
}
