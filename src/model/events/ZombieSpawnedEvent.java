package model.events;

import model.data.zombie.Zombie;

public class ZombieSpawnedEvent {
    public final Zombie zombie;

    public ZombieSpawnedEvent(Zombie zombie) {
        this.zombie = zombie;
    }
}
