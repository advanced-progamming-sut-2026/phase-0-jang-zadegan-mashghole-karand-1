package model.event.events;

import model.data.sun.Sun;

public class SunLandedEvent {
    public final Sun sun;

    public SunLandedEvent(Sun sun) {
        this.sun = sun;
    }
}
