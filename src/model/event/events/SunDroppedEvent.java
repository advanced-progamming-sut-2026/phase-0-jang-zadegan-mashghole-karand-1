package model.event.events;

import model.data.sun.Sun;

public class SunDroppedEvent {
    public final Sun sun;

    public SunDroppedEvent(Sun sun) {
        this.sun = sun;
    }
}
