package model.data.zombie.abilities.runtime;

import model.core.EventBus;
import model.core.GameState;
import model.data.plant.Plant;
import model.data.projectile.Projectile;
import model.data.zombie.Zombie;
import model.data.zombie.ZombieType;
import model.data.zombie.abilities.config.ZombieAbilityConfig;

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

        if (enabled) {
            Plant forwardPlant = state.getPlants().stream()
                    .filter(p -> p.isAlive && p.row == zombie.row && p.col <= zombie.col)
                    .max(Comparator.comparingInt(p -> p.col)).orElse(null);

            Zombie forwardHypnotizedZombie = state.getZombies().stream()
                    .filter(z -> z.isAlive && z.isHypnotized && z.row == zombie.row && z.col <= zombie.col)
                    .max(Comparator.comparingInt(z -> z.col)).orElse(null);

            if (forwardPlant == null && forwardHypnotizedZombie == null) return;

            if (forwardPlant != null && forwardPlant.col == zombie.col) {
                forwardPlant.kill(state, bus);
                handleAllStarEffect(zombie);
                return;
            }

            if (forwardHypnotizedZombie != null && forwardHypnotizedZombie.col == zombie.col) {
                forwardHypnotizedZombie.kill(state);
                handleAllStarEffect(zombie);
            }
        }
    }

    private void handleAllStarEffect(Zombie zombie) {
        if (zombie.type == ZombieType.ALL_STAR) {
            this.enabled = false;
            zombie.speed = zombie.speed * (1f / 3f);
        }
    }

    @Override
    public void onProjectileHit(Zombie zombie, Projectile projectile) {
        if(zombie.type != ZombieType.EXPLORER_ZOMBIE || projectile.type == null){
            return;
        }
        switch(projectile.type) {
            case ICE -> enabled = false;
            case FIRE -> enabled = true;
            default -> {
            }
        }
    }

    @Override
    public ZombieAbilityConfig createInstance(Zombie zombie) {
        return new ZombieKillForwardAbility();
    }
}
