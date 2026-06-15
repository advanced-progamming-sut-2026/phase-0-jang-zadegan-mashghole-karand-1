package model.events;

import model.data.plant.Plant;

public class PlantDiedEvent {
    public final Plant plant;

    public PlantDiedEvent(Plant plant) {
        this.plant = plant;
    }
}
