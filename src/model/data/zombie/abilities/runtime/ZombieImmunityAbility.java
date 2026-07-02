package model.data.zombie.abilities.runtime;

import model.core.EventBus;
import model.core.GameState;
import model.data.plant.ProjectileType;
import model.data.projectile.Projectile;
import model.data.zombie.Zombie;
import model.data.zombie.abilities.config.ZombieAbilityConfig;

import java.util.Set;

public class ZombieImmunityAbility implements ZombieAbilityConfig {

    private final Set<ProjectileType>  immuneTo;

    public ZombieImmunityAbility(ProjectileType... immuneTo) {
        this.immuneTo = Set.of(immuneTo);
    }

    @Override
    public boolean blocksProjectiles(Zombie zombie, Projectile projectile){
        return projectile.type != null && immuneTo.contains(projectile.type);
    }


    @Override
    public void onTick(Zombie zombie, GameState state, EventBus bus) {

    }

    @Override
    public ZombieAbilityConfig createInstance(Zombie zombie) {
        return new ZombieImmunityAbility();
    }
}
