package model.systems;

import model.core.GameState;
import model.data.plant.Plant;
import model.data.plant.PlantTag;
import model.data.plant.stuns.CatStun;
import model.data.vfx.LawnEffect;
import model.data.zombie.Zombie;

public class EffectSystem {
    private static final int NEARBY_FIRE_FROSTBITE_FREEZE_DAMAGE_PER_TICK = 6;

    public void update(GameState state) {
        updateZombieStatuses(state);
        clearDeadWizardStuns(state);
        applyFrostbiteFireDamage(state);
        tickFireTiles(state);
        tickLawnEffects(state);
    }

    private void updateZombieStatuses(GameState state) {
        for (Zombie zombie : state.zombies) {
            if (zombie.isFrozen) {
                zombie.frozenTicks--;
                if (zombie.frozenTicks <= 0) {
                    zombie.isFrozen = false;
                }
            }
            if(zombie.stunned){
                zombie.stunTicks--;
                if (zombie.stunTicks <= 0) {
                    zombie.stunned = false;
                }
            }
        }
    }

    private void clearDeadWizardStuns(GameState state) {
        for (Plant plant : state.plants) {
            if (plant.getActiveStun() instanceof CatStun cat
                    && cat.isWizardDead(state.zombies)) {
                plant.clearStun();
            }
        }
    }

    private void applyFrostbiteFireDamage(GameState state) {
        for (Plant plant : state.plants) {
            if (!plant.isFrostbiteFreezeActive())
                continue;

            if (hasNearbyFirePlant(state, plant)) {
                plant.damageFrostbiteFreeze(NEARBY_FIRE_FROSTBITE_FREEZE_DAMAGE_PER_TICK);
            }
        }
    }

    private boolean hasNearbyFirePlant(GameState state, Plant plant) {
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0)
                    continue;
                int checkRow = plant.row + dr;
                int checkCol = plant.col + dc;
                if (checkRow < 0 || checkRow >= GameState.GRID_ROWS)
                    continue;
                if (checkCol < 0 || checkCol >= GameState.GRID_COLS)
                    continue;

                Plant neighbor = state.getPlantAt(checkRow, checkCol);
                if (neighbor != null && neighbor.hasTag(PlantTag.FIRE)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void tickFireTiles(GameState state) {
        for (int r = 0; r < GameState.GRID_ROWS; r++) {
            for (int c = 0; c < GameState.GRID_COLS; c++) {
                var tile = state.getBoard().getTile(r, c);
                if (tile != null) {
                    tile.tickFire();
                }
            }
        }
    }

    private void tickLawnEffects(GameState state) {
        for (int i = state.lawnEffects.size() - 1; i >= 0; i--) {
            LawnEffect effect = state.lawnEffects.get(i);
            effect.ticksRemaining--;
            if (effect.ticksRemaining <= 0) {
                state.lawnEffects.remove(i);
            }
        }
    }

}
