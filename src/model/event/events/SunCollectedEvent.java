package model.event.events;

import model.data.sun.Sun;

public class SunCollectedEvent {
    public final Sun sun;

    public SunCollectedEvent(Sun sun) {
        this.sun = sun;
    }
}
