package model.data.wave;

import java.util.Collections;
import java.util.List;

public class ZombieWave {
    private final List<ZombieSpawn> spawns;
    private final int totalZombies;
    private final int totalCost;

    public ZombieWave(List<ZombieSpawn> spawns) {
        this.spawns = Collections.unmodifiableList(spawns);
        this.totalZombies = spawns.stream().mapToInt(s -> s.count).sum();
        this.totalCost = spawns.stream().mapToInt(s -> s.wavePointCost * s.count).sum();
    }

    public List<ZombieSpawn> getSpawns() {
        return spawns;
    }

    public int getTotalZombies() {
        return totalZombies;
    }

    public int getTotalCost() {
        return totalCost;
    }

    public boolean isEmpty() {
        return spawns.isEmpty();
    }
}