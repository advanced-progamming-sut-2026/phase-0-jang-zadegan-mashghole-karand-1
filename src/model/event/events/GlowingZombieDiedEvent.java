package model.event.events;

import model.data.zombie.Zombie;

public class GlowingZombieDiedEvent {
    public final Zombie zombie;

    public GlowingZombieDiedEvent(Zombie zombie) {
        this.zombie = zombie;
    }
}
