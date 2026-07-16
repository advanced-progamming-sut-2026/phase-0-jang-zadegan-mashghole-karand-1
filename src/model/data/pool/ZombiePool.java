package model.data.pool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import model.data.content.chapter.ChapterType;
import model.data.zombie.ZombieType;

public class ZombiePool {
    private final List<ZombieType> availableZombies = new ArrayList<>();
    private final Random random = new Random();

    private ZombiePool() {
    }

    public static ZombiePool forChapter(ChapterType chapterType) {
        ZombiePool pool = new ZombiePool();

        switch (chapterType) {
            case ANCIENT_EGYPT:
                pool.addZombie(ZombieType.BASIC)
                        .addZombie(ZombieType.CONE_HEAD);
                break;
            case BIG_WAVE_BEACH:
                // ... add Big Wave Beach zombies
                break;

            case FROSTBITE_CAVES:
                // ... add Frostbite zombies
                break;

            case DARK_AGES:
                // ... add Dark Ages zombies
                break;

            default:
                pool.addZombie(ZombieType.BASIC)
                        .addZombie(ZombieType.CONE_HEAD);
                break;
        }

        return pool;
    }

    public ZombiePool addZombie(ZombieType type) {
        availableZombies.add(type);
        return this;
    }

    public int getCost(ZombieType type) {
        return type.baseStats.wavePointCost;
    }

    public List<ZombieType> getAvailableZombies() {
        return Collections.unmodifiableList(availableZombies);
    }

    public ZombieType getRandomZombie(int maxCost) {
        List<ZombieType> affordable = new ArrayList<>();
        for (ZombieType type : availableZombies) {
            if (getCost(type) <= maxCost) {
                affordable.add(type);
            }
        }
        if (affordable.isEmpty())
            return null;
        return affordable.get(random.nextInt(affordable.size()));
    }

    public ZombieType getRandomZombie() {
        if (availableZombies.isEmpty())
            return null;
        return availableZombies.get(random.nextInt(availableZombies.size()));
    }

    public ZombieType getCheapestZombie() {
        ZombieType cheapest = null;
        int minCost = Integer.MAX_VALUE;
        for (ZombieType type : availableZombies) {
            if (type.baseStats.wavePointCost < minCost) {
                minCost = type.baseStats.wavePointCost;
                cheapest = type;
            }
        }
        return cheapest;
    }

    public boolean isEmpty() {
        return availableZombies.isEmpty();
    }
}