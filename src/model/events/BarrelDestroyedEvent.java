package model.events;

import model.data.Barrel.Barrel;

public class BarrelDestroyedEvent {
    public final Barrel barrel;
    public BarrelDestroyedEvent(Barrel barrel) {this.barrel = barrel;}
}
