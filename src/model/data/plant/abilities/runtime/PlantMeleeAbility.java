package model.data.plant.abilities.runtime;

import model.core.EventBus;
import model.core.GameLoop;
import model.core.GameState;
import model.data.plant.Plant;
import model.data.plant.PlantCategory;
import model.data.plant.abilities.config.AreaShape;
import model.data.plant.abilities.config.PlantAbilityConfig;
import model.data.plant.abilities.effects.DamageEffect;
import model.data.plant.abilities.effects.FreezeEffect;
import model.data.plant.abilities.effects.HitEffect;
import model.data.zombie.Zombie;

import java.util.List;
import java.util.stream.Collectors;

import static model.core.GameLoop.TICKS_PER_SECOND;

public class PlantMeleeAbility implements PlantAbilityConfig {
    private final AreaShape shape;
    private final int maxTargets;
    private final float cooldownSeconds;
    private final List<HitEffect> onHit;
    private int currentCooldown = 0;
    private final int digestTicksOnKill;
    private int digestTicksRemaining = 0;
    public PlantMeleeAbility(AreaShape shape, int maxTargets, float cooldownSeconds, List<HitEffect> onHit , int digestTicksOnKill) {
        this.shape = shape;
        this.maxTargets = maxTargets;
        this.cooldownSeconds = cooldownSeconds;
        this.onHit = onHit;
        this.digestTicksOnKill = digestTicksOnKill ;
    }


    @Override
    public PlantAbilityConfig createInstance(Plant plant) {
        List<HitEffect> boosted = onHit.stream().map(e -> {
            if (e instanceof DamageEffect f) {
                return new DamageEffect(f.getAmount()  + plant.damage - plant.type.baseStats.damage);
            }
            return e;
        }).toList();
        int finalDigest = digestTicksOnKill;
        if (digestTicksOnKill > 0) {
            finalDigest = (int) (plant.actionInterval * TICKS_PER_SECOND);
        }
        return new PlantMeleeAbility(shape,maxTargets,plant.actionInterval,boosted,finalDigest);
    }

    @Override
    public void onTick(Plant plant, GameState state, EventBus event) {
        if (digestTicksRemaining > 0) {
            digestTicksRemaining--;
            return;
        }
        if (currentCooldown > 0) {
            currentCooldown--;
            return;
        }

        List<Zombie> targets = findMeleeTargets(state, plant, shape, maxTargets);
        if (targets.isEmpty()) return;

        for (Zombie z : targets) {
            for (HitEffect e : onHit) e.apply(z, state, event);
        }
        if (digestTicksOnKill > 0) {
            digestTicksRemaining = digestTicksOnKill;
        } else {
            currentCooldown = (int) (cooldownSeconds * TICKS_PER_SECOND);
        }
    }
    private List<Zombie> findMeleeTargets(GameState state , Plant plant , AreaShape shape , int maxTargets){
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
            case FULL_BOARD:
                return true;
            case FRONT_OR_BACK:
                int dist = Math.abs(zombieCol - plant.col);
                int maxDist = 1 + plant.upgradeState.rangeBonus;
                return z.row == plant.row && ( dist >=1 && dist <= maxDist );
            default:
                return false;
        }
    }
    public void forceAttack(Plant plant , GameState state , EventBus event){
        List<Zombie> targets = findMeleeTargets(state,plant,shape,maxTargets);
        for (Zombie z : targets){
            for (HitEffect effect : onHit){
                effect.apply(z,state,event);
            }
        }
    }
}