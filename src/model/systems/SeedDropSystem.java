package model.systems;

import model.core.GameState;
import model.data.seed.PlantSeedDrop;

public class SeedDropSystem {

    public void update(GameState state) {
        for (PlantSeedDrop seed : state.seedDrops) {
            seed.age++;
        }
        state.seedDrops.removeIf(PlantSeedDrop::isExpired);
    }
}
