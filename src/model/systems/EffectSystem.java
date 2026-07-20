package model.systems;

import model.core.GameState;
import model.data.plant.Plant;
import model.data.plant.PlantTag;
import model.data.zombie.Zombie;

public class EffectSystem {
    private static final int NEARBY_FIRE_FROSTBITE_FREEZE_DAMAGE_PER_TICK = 6;

    public void update(GameState state) {
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
        for (Plant plant : state.plants) {
            if (!plant.isFrostbiteFreezeActive())
                continue;

            boolean hasNearbyFirePlant = false;
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
                        hasNearbyFirePlant = true;
                        break;
                    }
                }
                if (hasNearbyFirePlant)
                    break;
            }

            if (hasNearbyFirePlant) {
                plant.damageFrostbiteFreeze(NEARBY_FIRE_FROSTBITE_FREEZE_DAMAGE_PER_TICK);
            }
        }
    }

}
