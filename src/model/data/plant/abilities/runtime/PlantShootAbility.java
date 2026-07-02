package model.data.plant.abilities.runtime;

import model.data.plant.ProjectileType;
import model.data.plant.abilities.config.Direction;
import model.data.plant.abilities.config.PlantAbilityConfig;
import model.data.plant.abilities.config.ShootPattern;
import model.data.plant.effects.config.EffectPhase;
import model.data.projectile.Projectile;
import model.core.EventBus;
import model.core.GameLoop;
import model.core.GameState;
import model.core.Position;
import model.data.plant.Plant;
import model.data.projectile.ProjectileTarget;

public class PlantShootAbility implements PlantAbilityConfig {
    public final int damage;
    public final float cooldownSeconds;
    public final ProjectileType projectileType;
    public final ShootPattern shootPattern;
    public final EffectPhase phase;
    private int currentCooldown = 0;

    public PlantShootAbility(int damage, float cooldownSeconds, ProjectileType projectileType , ShootPattern shootPattern) {
        this.damage = damage;
        this.cooldownSeconds = cooldownSeconds;
        this.projectileType = projectileType;
        this.shootPattern = shootPattern;
        this.phase = EffectPhase.ALWAYS;
    }

    public PlantShootAbility(int damage, ProjectileType projectileType, EffectPhase phase) {
        this.damage = damage;
        this.cooldownSeconds = 0;
        this.projectileType = projectileType ;
        this.shootPattern = new ShootPattern(Direction.FORWARD , 0 , 1) ;
        this.phase = phase;
    }


    public PlantShootAbility createInstance(Plant plant) {
        int finalDamage = damage + plant.damage - plant.type.baseStats.damage;
        int cooldownTicks = (int) (plant.actionInterval * 10);

        return new PlantShootAbility(finalDamage, cooldownTicks, projectileType , shootPattern);
    }

    @Override
    public void onTick(Plant plant, GameState state, EventBus event) {
        if (currentCooldown > 0) {
            currentCooldown--;
            return;
        }

        boolean hasZombie = state.zombies.stream()
                .anyMatch(z -> z.row == plant.row && z.isAlive);

        if (hasZombie) {
            int targetRow = plant.row + shootPattern.getRow();
            if (targetRow>= 0 && targetRow < 5){
                for (int i = 0 ; i < shootPattern.getBulletCount();i++){
                    int xOffset = 40 - (i *20);
                    Projectile p = new Projectile(damage, new Position(plant.getX() + xOffset, plant.getY()), plant.row, plant.col,
                            10, projectileType, ProjectileTarget.ZOMBIE);
                    p.setDirection(shootPattern.getDir());
                    state.projectiles.add(p);
                }
            }

            currentCooldown = (int) (cooldownSeconds * GameLoop.TICKS_PER_SECOND);
        }
    }

    public void resetCooldown() {
        currentCooldown = 0;
    }
}
