package model.data.zombie.abilities.runtime;

import model.core.EventBus;
import model.core.GameState;
import model.data.plant.Plant;
import model.data.zombie.Zombie;
import model.data.zombie.abilities.config.ZombieAbilityConfig;

import java.util.Comparator;

public class ZombieFishingAbility implements ZombieAbilityConfig {
    private int startTime;
    private boolean started = false;
    @Override
    public void onTick(Zombie zombie, GameState state, EventBus bus) {
        if (!started) {
            started = true;
            startTime = state.totalTicks;
        }else {
            int elapsedTime = state.totalTicks - startTime;
            if(elapsedTime %50 ==0){
                Plant frontPlant = state.getPlants().stream()
                        .filter(p -> p.row == zombie.row)
                        .max(Comparator.comparingInt(p -> p.col))
                        .orElse(null);
                if(frontPlant != null){
                    if(frontPlant.col+1 == zombie.col || frontPlant.col == zombie.col){
                        frontPlant.kill(state, bus);
                    }else {
                        frontPlant.col++;
                    }
                }
            }
        }

    }

    @Override
    public ZombieAbilityConfig createInstance(Zombie zombie) {
        return new  ZombieFishingAbility();
    }
}
