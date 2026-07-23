package model.data.plant.abilities.runtime;

import model.core.EventBus;
import model.core.GameState;
import model.data.plant.Plant;
import model.data.plant.abilities.config.AreaShape;
import model.data.plant.abilities.config.ExplodeTrigger;
import model.data.plant.abilities.config.PlantAbilityConfig;
import model.data.plant.abilities.effects.DamageEffect;
import model.data.plant.abilities.effects.FreezeEffect;
import model.data.plant.abilities.effects.HitEffect;
import model.data.zombie.Zombie;

import java.util.List;
import java.util.stream.Collectors;

import static model.core.GameLoop.TICKS_PER_SECOND;

public class PlantExplodeAbility implements PlantAbilityConfig {
    private final ExplodeTrigger trigger;
    private final AreaShape shape;
    private final int maxTargets;
    private final int delayTicks;
    private final boolean requiresWater;
    private final  List<HitEffect> onHit;
    private int timer;
    private boolean hasExploded = false;


    public PlantExplodeAbility(ExplodeTrigger trigger, AreaShape shape, int maxTargets, int delayTicks, boolean requiresWater,
                               List<HitEffect> onHit ) {
        this.trigger = trigger;
        this.shape = shape;
        this.maxTargets = maxTargets;
        this.delayTicks = delayTicks;
        this.requiresWater = requiresWater;
        this.onHit = onHit;
        this.timer = 0;

    }


    @Override
    public PlantAbilityConfig createInstance(Plant plant) {
        List<HitEffect> boosted = onHit.stream().map(e -> {
            if (e instanceof FreezeEffect f) {
                return new FreezeEffect(f.getTicks() + plant.upgradeState.effectDurationBonus);
            }
            if (e instanceof DamageEffect d) {
                return new DamageEffect(d.getAmount() + plant.damage - plant.type.baseStats.damage);
            }
            return e;
        }).toList();
        int finalTarget = maxTargets;
        AreaShape finalShape = shape;
        if (plant.upgradeState.targetPriorityBonus>0){
            finalTarget += plant.upgradeState.targetPriorityBonus;
            finalShape = AreaShape.RIGHT_LEFT_FRONT;

        }
        if (plant.upgradeState.doubleCrushCount > 1) {
            finalTarget = plant.upgradeState.doubleCrushCount;
        }
         int finalDelay = Math.max(0,delayTicks + plant.upgradeState.cooldownBonus * TICKS_PER_SECOND);
        return new PlantExplodeAbility(trigger,finalShape,finalTarget,finalDelay,
                requiresWater,boosted);
    }

    @Override
    public void onTick(Plant plant, GameState state, EventBus event) {
        if (hasExploded) return;
        if (timer < delayTicks) {
            timer++;
            return;
        }
        if (!shouldActivate(plant,state)) return;
        hasExploded = true;
        plant.hp = 0;
        plant.isAlive = false;
        List<Zombie> targets = findTarget(state , plant);
        for (Zombie z : targets) {
            for (HitEffect effect : onHit) {
                effect.apply(z, state, event);
            }
        }


    }
    private List<Zombie> findTarget(GameState state , Plant plant){
        List<Zombie> targets = state.zombies.stream()
                .filter(z -> z.isAlive)
                .filter(z -> isInShape(z , plant , shape))
                .collect(Collectors.toList());
        if (maxTargets <= 0 || maxTargets >= targets.size()) {
            return targets;
        }
        return targets.subList(0, maxTargets);

    }
    private boolean isInShape(Zombie z, Plant plant, AreaShape shape) {
        int zombieCol = (int) (z.position.x / GameState.CELL_WIDTH);

        switch (shape) {
            case SINGLE_TILE:
                return z.row == plant.row && zombieCol == plant.col;
            case ADJACENT:
                return z.row == plant.row && zombieCol == plant.col + 1;
            case ROW:
                return z.row == plant.row;
            case RADIUS_3x3:
                return Math.abs(z.row - plant.row) <= 1
                        && Math.abs(zombieCol - plant.col) <= 1;
            case RIGHT_LEFT_FRONT:
                return zombieCol == plant.col &&  (Math.abs(plant.row - z.row) <=1);
            case FULL_BOARD:
                return true;
            default:
                return false;
        }
    }
    private boolean shouldActivate(Plant plant, GameState state) {
        if (requiresWater && !state.getBoard().getTile(plant.row, plant.col).isWater()) {
            return false;
        }
        switch (trigger) {
            case INSTANT:
                return true;

            case ON_ZOMBIE_ENTER:
                return hasZombieOnPlantTile(plant, state);

            case ON_ADJACENT_ZOMBIE:
                return hasAdjacentZombie(plant, state);

            default:
                return false;
        }
    }
    private boolean hasZombieOnPlantTile(Plant plant , GameState state){
        for (Zombie z : state.zombies){
            if (!z.isAlive)
                continue;
            if (z.row != plant.row)
                continue;
           int zombieCol = (int) z.position.x / GameState.CELL_WIDTH;
           if (zombieCol == plant.col)
               return true;
        }
        return false;
    }
    private boolean hasAdjacentZombie(Plant plant , GameState state){
        for (Zombie z : state.zombies){
            if (!z.isAlive) continue;
            if (z.row != plant.row ) continue;
            int zombieCol = (int) z.position.x / GameState.CELL_WIDTH;
            if (zombieCol == plant.col + 1)
                return true;
        }
        return false;
    }
    public void forceArm(){

        this.timer = delayTicks;

    }

}
