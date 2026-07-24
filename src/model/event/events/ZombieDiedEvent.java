package model.event.events;

import model.data.plant.PlantType;
import model.data.zombie.Zombie;

public class ZombieDiedEvent {
    public final Zombie zombie;
    public final PlantType killerPlant;

    public ZombieDiedEvent(Zombie zombie, PlantType killerPlant) {
        this.zombie = zombie;
        this.killerPlant = killerPlant;
    }
}
