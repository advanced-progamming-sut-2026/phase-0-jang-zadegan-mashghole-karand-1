package model.event.events;

import model.data.seed.PlantSeedDrop;

public class SeedDroppedEvent {
    public final PlantSeedDrop seed;

    public SeedDroppedEvent(PlantSeedDrop seed) {
        this.seed = seed;
    }
}
