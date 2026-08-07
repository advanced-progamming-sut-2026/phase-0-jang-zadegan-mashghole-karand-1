package model.event.events;

import model.data.seed.PlantSeedDrop;

public class SeedCollectedEvent {
    public final PlantSeedDrop seed;

    public SeedCollectedEvent(PlantSeedDrop seed) {
        this.seed = seed;
    }
}
