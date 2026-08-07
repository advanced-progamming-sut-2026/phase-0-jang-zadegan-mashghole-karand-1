package model.event.events;

import model.data.Grave.Grave;

public class GraveDestroyedEvent {
    public final Grave grave;

    public GraveDestroyedEvent(Grave grave){
        this.grave = grave;
    }
}
