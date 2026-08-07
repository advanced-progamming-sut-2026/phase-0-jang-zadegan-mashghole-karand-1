package model.event.events;

import model.data.plant.PlantType;

public class PlantOfferedEvent {
    public final PlantType plantType;

    public PlantOfferedEvent(PlantType plantType) {
        this.plantType = plantType;
    }
}
