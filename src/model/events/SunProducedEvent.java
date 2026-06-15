package model.events;

import model.data.plant.Plant;
import model.data.sun.Sun;

public class SunProducedEvent {
    public final Sun sun;
    public final Plant plant;

    public SunProducedEvent(Plant plant, Sun sun) {
        this.sun = sun;
        this.plant = plant;
    }
}
