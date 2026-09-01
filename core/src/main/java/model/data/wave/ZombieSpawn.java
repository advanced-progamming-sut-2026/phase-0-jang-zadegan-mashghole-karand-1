package model.data.wave;

import model.data.zombie.ZombieType;

public class ZombieSpawn {
    public final ZombieType type;
    public int count;
    public final int wavePointCost;
    /** When non-null, spawn on this row instead of random. */
    public final Integer row;

    public ZombieSpawn(ZombieType type, int count) {
        this(type, count, null);
    }

    public ZombieSpawn(ZombieType type, int count, Integer row) {
        this.type = type;
        this.count = count;
        this.wavePointCost = type.baseStats.wavePointCost * count;
        this.row = row;
    }
}
