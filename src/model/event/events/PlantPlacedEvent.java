package model.event.events;

import model.data.plant.Plant;

public class PlantPlacedEvent {
    public final Plant plant;

    public PlantPlacedEvent(Plant plant) {
        this.plant = plant;
    }
}
