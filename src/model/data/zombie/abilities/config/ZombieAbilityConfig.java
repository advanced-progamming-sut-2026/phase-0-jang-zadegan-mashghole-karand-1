package model.data.zombie.abilities.config;

import model.core.EventBus;
import model.core.GameState;
import model.data.projectile.Projectile;
import model.data.zombie.Zombie;

public interface ZombieAbilityConfig {
    public void onTick(Zombie zombie, GameState state, EventBus bus);

    default public void onDeath(Zombie zombie, GameState state, EventBus bus) {
    }

    default public void onAttach(Zombie zombie) {
    }

    default public boolean blocksProjectiles(Zombie zombie, Projectile projectile) {
        return false;
    }

    default public void onProjectileHit(Zombie zombie, Projectile projectile) {
    }

    public ZombieAbilityConfig createInstance(Zombie zombie);
}