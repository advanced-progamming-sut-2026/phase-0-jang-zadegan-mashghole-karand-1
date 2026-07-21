package model.data.zombie.abilities.runtime;

import model.core.EventBus;
import model.core.GameState;
import model.data.plant.Plant;
import model.data.plant.stuns.CatStun;
import model.data.plant.stuns.StunKind;
import model.data.zombie.Zombie;
import model.data.zombie.abilities.config.ZombieAbilityConfig;

import java.util.List;
import java.util.Random;

public class ZombieStunAbility implements ZombieAbilityConfig {
    Random rand = new Random();
    private final int COOLDOWN = 50;
    private int cooldown = COOLDOWN;

    @Override
    public void onTick(Zombie zombie, GameState state, EventBus bus) {
        if(cooldown> 0){
            cooldown--;
            return;
        }
        List<Plant> plants = state.getPlants().stream()
                .filter(p -> p.getActiveStun() ==null || p.getActiveStun().getKind() != StunKind.CAT)
                .toList();
        if(!plants.isEmpty()){
            Plant target = plants.get(rand.nextInt(plants.size()));
            target.applyStun(new CatStun(zombie));
            cooldown = COOLDOWN;
        }
    }

    @Override
    public ZombieAbilityConfig createInstance(Zombie zombie) {
        return new  ZombieStunAbility();
    }
}
