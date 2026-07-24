package model.data.plant.abilities.runtime;

import model.core.EventBus;
import model.core.GameState;
import model.core.Position;
import model.data.plant.Plant;
import model.data.plant.abilities.config.PlantAbilityConfig;
import model.data.zombie.Zombie;
import model.data.zombie.ZombieType;
import model.event.events.ZombieSpawnedEvent;

public class PlantHypnotizeAbility implements PlantAbilityConfig {
    private boolean transformToGargantuar = false;
    public PlantHypnotizeAbility() {
    }

    @Override
    public PlantAbilityConfig createInstance(Plant plant) {
        return new PlantHypnotizeAbility();
    }
    @Override
    public void onTick(Plant plant, GameState state, EventBus event) {
    }
    @Override
    public void onDeath(Plant plant, Zombie killer, GameState state,EventBus event) {
        if (killer != null && killer.isAlive) {
            if (this.transformToGargantuar) {
                killer.kill(state);
                Zombie garg = new Zombie(ZombieType.GARGANTUAR, killer.row, killer.col,new Position(killer.row, killer.col),event);
                garg.isHypnotized = true;
                applyBuffs(garg,plant);
                state.zombies.add(garg);
                event.publish(new ZombieSpawnedEvent(garg));
            } else {
                killer.isHypnotized = true;
                applyBuffs(killer,plant);
            }
        }
    }
    private void applyBuffs(Zombie z, Plant plant) {
        if (plant.upgradeState.zombieHpBuff) {
            z.hp = (int) (z.hp * 1.5f);
            z.totalHp = Math.max(z.totalHp, z.hp);
        }
        if (plant.upgradeState.zombieDamageBuff) {
            z.DPS_MULTIPLIER *= 1.5f;
        }
    }
    public void setTransformToGargantuar(boolean transformToGargantuar) {
        this.transformToGargantuar = transformToGargantuar;
    }
}