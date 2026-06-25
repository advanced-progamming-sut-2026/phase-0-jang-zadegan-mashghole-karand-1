package model.data.wave;

import model.data.zombie.ZombieType;

public class ZombieSpawn {
    public final ZombieType type;
    public int count;
    public final int wavePointCost;

    public ZombieSpawn(ZombieType type, int count) {
        this.type = type;
        this.count = count;
        this.wavePointCost = type.baseStats.wavePointCost * count;
    }
}
