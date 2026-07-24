package model.event.events;

import model.data.Barrel.Barrel;

public class BarrelCreatedEvent {

    public final Barrel barrel;
    public BarrelCreatedEvent(Barrel barrel) { this.barrel = barrel; }

}
