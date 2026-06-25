package model.data.zombie.abilities.runtime;

import model.core.EventBus;
import model.core.GameState;
import model.data.plant.Plant;
import model.data.sun.Sun;
import model.data.sun.SunType;
import model.data.zombie.Zombie;
import model.data.zombie.ZombieType;
import model.data.zombie.abilities.config.ZombieAbilityConfig;
import model.events.SunDroppedEvent;

import java.util.ArrayList;
import java.util.List;

public class ZombieStealSunAbility implements ZombieAbilityConfig {
    private boolean enabled =false;
    private int startTicks;
    private int sunStollen;
    private List<Sun> StollenSuns= new ArrayList<>();

    public void onTick(Zombie zombie, GameState state, EventBus bus) {
        if(zombie.type == ZombieType.TURQUOISE_ZOMBIE){
            List<Plant> targets = state.getPlants().stream()
                    .filter(p -> p.row == zombie.row &&
                            p.col<= zombie.col && p.col>= zombie.col-4)
                    .toList();
            if(enabled){
                int passedTicks = state.totalTicks-startTicks;
                if(passedTicks>0 && passedTicks%10==0){
                    state.sunAmount -= 25;
                    sunStollen += 25;
                }
                return;
            }
            if(targets.isEmpty()){
                return;
            }
            enabled = true;
            startTicks = state.totalTicks;
        }
        if(zombie.type == ZombieType.RA_ZOMBIE){
            StollenSuns.addAll(state.sunDrops);
            for(Sun sun : state.sunDrops){
                state.sunDrops.remove(sun);
            }
        }
    }

    @Override
    public void onDeath(Zombie zombie, GameState state, EventBus bus) {
        if(zombie.type == ZombieType.TURQUOISE_ZOMBIE){
            Sun sun = new Sun(zombie.row,zombie.position,sunStollen/2, SunType.NORMAL);
            state.sunDrops.add(sun);
            bus.publish(new SunDroppedEvent(sun));
        }
        if(zombie.type == ZombieType.RA_ZOMBIE){
            for(Sun sun : StollenSuns){
                state.sunDrops.add(sun);
                bus.publish(new SunDroppedEvent(sun));
            }
        }
    }
    public ZombieAbilityConfig createInstance(Zombie zombie) {
        return new ZombieStealSunAbility();
    };

}
