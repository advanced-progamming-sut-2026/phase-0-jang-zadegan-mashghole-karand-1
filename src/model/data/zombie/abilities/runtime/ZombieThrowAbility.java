package model.data.zombie.abilities.runtime;

import model.core.EventBus;
import model.core.GameLoop;
import model.core.GameState;
import model.core.Position;
import model.data.plant.ProjectileType;
import model.data.plant.abilities.config.Direction;
import model.data.projectile.Projectile;
import model.data.projectile.ProjectileTarget;
import model.data.zombie.Zombie;
import model.data.zombie.ZombieType;
import model.data.zombie.abilities.config.ZombieAbilityConfig;


public class ZombieThrowAbility implements ZombieAbilityConfig {

    public int damage;
    public float cooldownSeconds;
    public ProjectileType projectileType;
    private int currentCooldown = 0;
    private float speed;

    public ZombieThrowAbility(int damage,float speed, float cooldownSeconds, ProjectileType projectileType) {
        this.damage = damage;
        this.cooldownSeconds = cooldownSeconds;
        this.projectileType = projectileType;
        this.speed = speed;
    }

    @Override
    public void onTick(Zombie zombie, GameState state, EventBus bus) {
        if(currentCooldown > 0){
            currentCooldown--;
            return;
        }

        boolean hasTarget = state.plants.stream().anyMatch(plant -> plant.row == zombie.row);
        if(hasTarget) {
            Projectile projectile = new Projectile(damage,
                    new Position(zombie.position.x, zombie.position.y),
                    zombie.row, zombie.col, speed, projectileType,
                    ProjectileTarget.PLANT);
            projectile.setDirection(Direction.BACK);
            state.projectiles.add(projectile);
        }
        currentCooldown = (int) (cooldownSeconds * GameLoop.TICKS_PER_SECOND);
    }

    @Override
    public ZombieAbilityConfig createInstance(Zombie zombie) {
        return new ZombieThrowAbility(damage,speed, cooldownSeconds, projectileType);
    }
}
