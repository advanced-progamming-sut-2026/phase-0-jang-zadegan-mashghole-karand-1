package model.data.zombie.abilities.runtime;

import model.core.EventBus;
import model.core.GameState;
import model.data.plant.Plant;
import model.data.zombie.Zombie;
import model.data.zombie.ZombieType;
import model.data.zombie.abilities.config.ZombieAbilityConfig;
import model.events.PlantDiedEvent;
import model.events.ZombieDiedEvent;

import java.util.Comparator;

public class ZombieKillForwardAbility implements ZombieAbilityConfig {
    public boolean enabled;

    @Override
    public void onTick(Zombie zombie, GameState state, EventBus bus) {
        if (zombie.type == ZombieType.ARCADE_ZOMBIE) {
            if (!zombie.armor.isIntact) {
                enabled = false;
            }
        }

        if(zombie.type == ZombieType.EXPLORER_ZOMBIE){
            if(zombie.isFrozen){
                enabled = false;
            }
//            if(hitByFire){
//                enabled = true;
//            }
        }

        if (enabled) {
            Plant forwardPlant = state.getPlants().stream()
                    .filter(p -> p.row == zombie.row && p.col <= zombie.col)
                    .max(Comparator.comparingInt(p -> p.col)).orElse(null);

            Zombie forwardHypnotizedZombie = state.getZombies().stream()
                    .filter(z -> z.isHypnotized && z.row == zombie.row && z.col <= zombie.col)
                    .max(Comparator.comparingInt(z -> z.col)).orElse(null);

            if (forwardPlant == null && forwardHypnotizedZombie == null) return;

            if (forwardPlant != null && forwardPlant.col == zombie.col) {
                killPlant(bus,forwardPlant, zombie);
                return;
            }

            if (forwardHypnotizedZombie != null && forwardHypnotizedZombie.col == zombie.col) {
                killHypnotizedZombie(bus,forwardHypnotizedZombie, zombie);
                return;
            }

        }
    }

    private void killPlant(EventBus bus ,Plant plant, Zombie zombie) {
        plant.isAlive = false;
        bus.publish(new PlantDiedEvent(plant));
        handleAllStarEffect(zombie);
    }

    private void killHypnotizedZombie(EventBus bus, Zombie hypZombie, Zombie zombie) {
        hypZombie.isAlive = false;
        bus.publish(new ZombieDiedEvent(hypZombie));
        handleAllStarEffect(zombie);
    }

    private void handleAllStarEffect(Zombie zombie) {
        if (zombie.type == ZombieType.ALL_STAR) {
            this.enabled = false;
            zombie.speed = zombie.speed * (1f / 3f);
        }
    }

    @Override
    public ZombieAbilityConfig createInstance(Zombie zombie) {
        return new ZombieKillForwardAbility();
    }
}
