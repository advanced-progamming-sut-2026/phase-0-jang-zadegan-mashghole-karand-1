package model.event.events;

import model.data.zombie.ZombieType;

public class ZombieUnlockedEvent {
    public final ZombieType zombieType;

    public ZombieUnlockedEvent(ZombieType zombieType) {
        this.zombieType = zombieType;
    }
}
