package model.data.zombie.abilities.runtime;

import model.core.EventBus;
import model.core.GameLoop;
import model.core.GameState;
import model.core.Position;
import model.data.plant.Plant;
import model.data.plant.abilities.config.Direction;
import model.data.plant.stuns.StunKind;
import model.data.projectile.LobbedProjectile;
import model.data.projectile.Projectile;
import model.data.projectile.ProjectileTarget;
import model.data.projectile.ProjectileType;
import model.data.zombie.Zombie;
import model.data.zombie.ZombieType;
import model.data.zombie.abilities.config.ZombieAbilityConfig;

import java.util.Comparator;

public class ZombieThrowAbility implements ZombieAbilityConfig {

    public int damage;
    public float cooldownSeconds;
    public ProjectileType projectileType;
    private int currentCooldown = 0;
    private float speed;

    public ZombieThrowAbility(int damage, float speed, float cooldownSeconds, ProjectileType projectileType) {
        this.damage = damage;
        this.cooldownSeconds = cooldownSeconds;
        this.projectileType = projectileType;
        this.speed = speed;
    }

    @Override
    public void onTick(Zombie zombie, GameState state, EventBus bus) {
        if (currentCooldown > 0) {
            currentCooldown--;
            return;
        }

        if(zombie.type == ZombieType.HUNTER) {
            boolean hasTarget = state.plants.stream().anyMatch(plant -> plant.row == zombie.row);;
            if (hasTarget) {
                Projectile projectile = new Projectile(damage,
                        new Position(zombie.position.x, zombie.position.y),
                        zombie.row, zombie.col, speed, projectileType,
                        ProjectileTarget.PLANT);
                projectile.setDirection(Direction.BACK);
                state.projectiles.add(projectile);
            }
        } else if (zombie.type == ZombieType.OCTOPUS_ZOMBIE) {
            Plant target = state.plants.stream()
                    .filter(plant -> plant.row == zombie.row && (plant.getActiveStun() ==null
                            || plant.getActiveStun().getKind()!= StunKind.OCTOPUS))
                    .max(Comparator.comparingDouble(p ->p.getX())).orElse(null);
            if (target != null) {
                Projectile p = new LobbedProjectile(0,new Position(zombie.position.x, zombie.position.y),
                        target.row,target.col,
                        speed,
                        ProjectileType.OCTOPUS,
                        ProjectileTarget.PLANT,
                        new Position(target.getX(), target.getY()),
                        0f,50f,60f,0f,0,0);
                        p.direction = Direction.BACK;
                        state.projectiles.add(p);
            }

        }
        currentCooldown = (int) (cooldownSeconds * GameLoop.TICKS_PER_SECOND);
    }

    @Override
    public ZombieAbilityConfig createInstance(Zombie zombie) {
        return new ZombieThrowAbility(damage, speed, cooldownSeconds, projectileType);
    }
}
