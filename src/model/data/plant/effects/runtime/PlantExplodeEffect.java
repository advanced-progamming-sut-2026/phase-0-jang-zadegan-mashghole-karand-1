package model.data.plant.effects.runtime;

import model.core.EventBus;
import model.core.GameState;
import model.data.plant.Plant;
import model.data.plant.abilities.config.AreaShape;
import model.data.plant.abilities.config.PlantAbilityConfig;
import model.data.plant.abilities.effects.HitEffect;
import model.data.plant.abilities.runtime.PlantExplodeAbility;
import model.data.plant.effects.config.PlantEffectConfig;
import model.data.zombie.Zombie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PlantExplodeEffect implements PlantEffectConfig{

    private final AreaShape shape;
    private final int maxTargets;
    private final List<HitEffect> onHit;
    private final boolean forceArm;
    private final int spawnClones;

    public PlantExplodeEffect(AreaShape shape, int maxTargets, List<HitEffect> onHit, boolean forceArm, int spawnClones) {
        this.shape = shape;
        this.maxTargets = maxTargets;
        this.onHit = onHit;
        this.forceArm = forceArm;
        this.spawnClones = spawnClones;
    }

    public void onActivate(Plant plant, GameState state, EventBus event) {
        if (forceArm) armPlant(plant);
        if (spawnClones > 0) spawnArmedClones(plant, state, event, spawnClones);
        if (shape != null && !onHit.isEmpty()) {
            List<Zombie> targets = findTargets(state, plant, shape, maxTargets);
            for (Zombie z : targets) {
                for (HitEffect e : onHit) e.apply(z, state, event);
            }
        }
    }

    @Override
    public PlantEffectConfig createInstance(Plant plant) {
        return new PlantExplodeEffect(shape,maxTargets,onHit,forceArm,spawnClones);
    }

    private void armPlant(Plant plant) {
        for (PlantAbilityConfig ability : plant.abilities) {
            if (ability instanceof PlantExplodeAbility explode) {
                explode.forceArm();
            }
        }
    }
    private List<Zombie> findTargets(GameState state, Plant plant,
                                     AreaShape shape, int maxTargets) {
        List<Zombie> targets = state.zombies.stream()
                .filter(z -> z.isAlive)
                .filter(z -> isInShape(z, plant, shape))
                .collect(Collectors.toList());
        Collections.shuffle(targets);
        if (maxTargets <= 0 || maxTargets >= targets.size()) {
            return targets.subList(0,maxTargets);
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
            default:
                return false;
        }
    }
    private void spawnArmedClones(Plant plant , GameState state , EventBus event , int spawnClone){
        List<int[]> emptyTiles = new ArrayList<>();
        for (int row = 0 ; row < GameState.GRID_ROWS ; row++){
            for (int col = 0; col < GameState.GRID_COLS ; col++){
                if (row == plant.row && col == plant.col) continue;
                if (state.getPlantAt(row,col) == null)
                    emptyTiles.add(new int[]{row,col});
            }
        }
        Collections.shuffle(emptyTiles);

        int count = Math.min(spawnClone, emptyTiles.size());
        for (int i = 0; i < count; i++) {
            int[] tile = emptyTiles.get(i);
            int row = tile[0];
            int col = tile[1];
            Plant clone = new Plant(plant.type, row, col, plant.level, plant.eventBus);
            armPlant(clone);
            state.plants.add(clone);
        }
    }
}
