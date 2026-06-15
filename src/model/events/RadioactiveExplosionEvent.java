package model.events;

import model.data.sun.Sun;

public class RadioactiveExplosionEvent {
    public final Sun sun;

    public RadioactiveExplosionEvent(Sun sun) {
        this.sun = sun;
    }
}
