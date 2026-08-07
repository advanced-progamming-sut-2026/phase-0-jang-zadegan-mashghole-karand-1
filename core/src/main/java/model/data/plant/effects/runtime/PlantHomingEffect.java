package model.data.plant.effects.runtime;

import model.core.EventBus;
import model.core.GameState;
import model.data.plant.Plant;
import model.data.projectile.ProjectileType;
import model.data.plant.abilities.config.TargetStrategy;
import model.data.plant.abilities.effects.HitEffect;
import model.data.plant.abilities.runtime.PlantHomingAbility;
import model.data.plant.effects.config.PlantEffectConfig;
import model.data.projectile.Projectile;

public class PlantHomingEffect implements PlantEffectConfig {
    public final int shoutCount;
    public final int damage;
    public final ProjectileType projectileType;
    public final TargetStrategy strategy;

    public PlantHomingEffect(int shoutCount, int damage, ProjectileType projectileType, TargetStrategy strategy) {
        this.shoutCount = shoutCount;
        this.damage = damage;
        this.projectileType = projectileType;
        this.strategy = strategy;
    }

    @Override
    public void onActivate(Plant plant, GameState state, EventBus event) {
        for (int i = 0 ; i < shoutCount ; i++){
            PlantHomingAbility ph = new PlantHomingAbility(
                    damage,
                    0,
                    projectileType,
                    strategy
            );
            ph.createInstance(plant).onTick(plant, state, event);
        }
    }

    @Override
    public PlantEffectConfig createInstance(Plant plant) {
        return new PlantHomingEffect(shoutCount,damage,projectileType,strategy);
    }
}
