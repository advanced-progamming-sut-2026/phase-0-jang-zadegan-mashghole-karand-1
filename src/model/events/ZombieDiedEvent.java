package model.events;

import model.data.zombie.Zombie;

public class ZombieDiedEvent {
    public final Zombie zombie;

    public ZombieDiedEvent(Zombie zombie) {
        this.zombie = zombie;
    }
}
