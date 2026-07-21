package model.events;

import model.data.vase.Vase;

public class VaseBrokenEvent {
    public final Vase vase;

    public VaseBrokenEvent(Vase vase) {
        this.vase = vase;
    }
}
