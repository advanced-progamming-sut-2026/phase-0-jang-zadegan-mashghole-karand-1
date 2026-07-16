package model.events;

import model.core.Position;
import model.data.zombie.ZombieLootType;

public class ZombieDroppedLootEvent {
    public final ZombieLootType type;
    public final int amount;
    public final Position position;

    public ZombieDroppedLootEvent(ZombieLootType type,int amount, Position position) {
        this.type = type;
        this.amount = amount;
        this.position = position;
    }
}
