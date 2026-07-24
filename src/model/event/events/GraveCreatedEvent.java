package model.event.events;

import model.data.Grave.Grave;

public class GraveCreatedEvent {
    public final Grave grave;
    public GraveCreatedEvent(Grave grave){
        this.grave = grave;
    }
}
