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

    public static List<ZombieType> rosterFor(ChapterType chapterType, int levelNumber) {
        int level = Math.max(1, levelNumber);
        List<ZombieType> roster = new ArrayList<>();
        roster.add(ZombieType.BASIC);
        roster.add(ZombieType.CONE_HEAD);

        if (chapterType == null) {
            roster.add(ZombieType.BUCKET_HEAD);
            return roster;
        }

        switch (chapterType) {
            case ANCIENT_EGYPT -> addAncientEgyptRoster(roster, level);
            case FROSTBITE_CAVES -> addFrostbiteCavesRoster(roster, level);
            case BIG_WAVE_BEACH -> addBigWaveBeachRoster(roster, level);
            case DARK_AGES -> addDarkAgesRoster(roster, level);
        }
        return roster;
    }

    private static void addAncientEgyptRoster(List<ZombieType> roster, int level) {
        roster.add(ZombieType.RA_ZOMBIE);
        roster.add(ZombieType.EXPLORER_ZOMBIE);
        if (level >= 2) {
            roster.add(ZombieType.BUCKET_HEAD);
            roster.add(ZombieType.TOMB_RAISER);
        }
        if (level >= 3) {
            roster.add(ZombieType.BRICK_HEAD);
            roster.add(ZombieType.NEWSPAPER_ZOMBIE);
        }
        if (level >= 4) {
            roster.add(ZombieType.GARGANTUAR);
            roster.add(ZombieType.PROSPECTOR_ZOMBIE);
        }
    }

    private static void addFrostbiteCavesRoster(List<ZombieType> roster, int level) {
        roster.add(ZombieType.HUNTER);
        roster.add(ZombieType.TROGLOBITE);
        if (level >= 2) {
            roster.add(ZombieType.BUCKET_HEAD);
            roster.add(ZombieType.DODO_RIDER_ZOMBIE);
        }
        if (level >= 3) {
            roster.add(ZombieType.BRICK_HEAD);
            roster.add(ZombieType.PARASOL_ZOMBIE);
        }
        if (level >= 4) {
            roster.add(ZombieType.GARGANTUAR);
            roster.add(ZombieType.ALL_STAR);
        }
    }

    private static void addBigWaveBeachRoster(List<ZombieType> roster, int level) {
        roster.add(ZombieType.SNORKEL_ZOMBIE);
        roster.add(ZombieType.IMP);
        if (level >= 2) {
            roster.add(ZombieType.BUCKET_HEAD);
            roster.add(ZombieType.OCTOPUS_ZOMBIE);
            roster.add(ZombieType.BARREL_ROLLER);
        }
        if (level >= 3) {
            roster.add(ZombieType.FISHERMAN_ZOMBIE);
            roster.add(ZombieType.ALL_STAR);
        }
        if (level >= 4) {
            roster.add(ZombieType.GARGANTUAR);
            roster.add(ZombieType.ARCADE_ZOMBIE);
        }
    }

    private static void addDarkAgesRoster(List<ZombieType> roster, int level) {
        roster.add(ZombieType.KNIGHT);
        roster.add(ZombieType.IMP_DRAGON);
        if (level >= 2) {
            roster.add(ZombieType.BUCKET_HEAD);
            roster.add(ZombieType.JESTER_ZOMBIE);
        }
        if (level >= 3) {
            roster.add(ZombieType.WIZARD_ZOMBIE);
            roster.add(ZombieType.KING);
            roster.add(ZombieType.PIANIST);
        }
        if (level >= 4) {
            roster.add(ZombieType.GARGANTUAR);
            roster.add(ZombieType.TURQUOISE_ZOMBIE);
        }
    }

    public static ZombiePool forChapter(ChapterType chapterType) {
        return fromTypes(rosterFor(chapterType, 4));
    }

    public static ZombiePool forChapter(ChapterType chapterType, int levelNumber) {
        return fromTypes(rosterFor(chapterType, levelNumber));
    }

    public static ZombiePool fromTypes(List<ZombieType> types) {
        ZombiePool pool = new ZombiePool();
        if (types != null) {
            for (ZombieType type : types) {
                if (type != null && type.isWaveSpawnable() && !pool.availableZombies.contains(type)) {
                    pool.addZombie(type);
                }
            }
        }
        if (pool.isEmpty()) {
            pool.addZombie(ZombieType.BASIC).addZombie(ZombieType.CONE_HEAD);
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
        return getRandomZombie(maxCost, null);
    }

    public ZombieType getRandomZombie(int maxCost, ZombieType avoidIfPossible) {
        List<ZombieType> affordable = affordableTypes(maxCost);
        if (affordable.isEmpty()) {
            return null;
        }
        if (avoidIfPossible != null && affordable.size() > 1) {
            List<ZombieType> withoutLast = new ArrayList<>();
            for (ZombieType type : affordable) {
                if (type != avoidIfPossible) {
                    withoutLast.add(type);
                }
            }
            if (!withoutLast.isEmpty()) {
                affordable = withoutLast;
            }
        }
        return pickWeighted(affordable);
    }

    public ZombieType getRandomZombie() {
        if (availableZombies.isEmpty()) {
            return null;
        }
        return pickWeighted(availableZombies);
    }

    public ZombieType getCheapestZombie() {
        ZombieType cheapest = null;
        int minCost = Integer.MAX_VALUE;
        for (ZombieType type : availableZombies) {
            if (!type.isWaveSpawnable()) {
                continue;
            }
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

    private List<ZombieType> affordableTypes(int maxCost) {
        List<ZombieType> affordable = new ArrayList<>();
        for (ZombieType type : availableZombies) {
            if (!type.isWaveSpawnable()) {
                continue;
            }
            if (getCost(type) <= maxCost) {
                affordable.add(type);
            }
        }
        return affordable;
    }

    private ZombieType pickWeighted(List<ZombieType> types) {
        int totalWeight = 0;
        for (ZombieType type : types) {
            totalWeight += type.spawnWeight();
        }
        if (totalWeight <= 0) {
            return types.get(random.nextInt(types.size()));
        }
        int roll = random.nextInt(totalWeight);
        int accumulated = 0;
        for (ZombieType type : types) {
            accumulated += type.spawnWeight();
            if (roll < accumulated) {
                return type;
            }
        }
        return types.get(types.size() - 1);
    }
}
