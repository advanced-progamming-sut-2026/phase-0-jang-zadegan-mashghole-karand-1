package shared.ranked;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import shared.dto.RankedChallengeDto;
import shared.dto.RankedSpawnDto;
import shared.dto.RankedWaveDto;

public final class RankedChallengeGenerator {
    public static final int GRID_ROWS = 5;
    public static final int DEFAULT_DIFFICULTY = 3;

    private static final String[] CHAPTERS = {
            "ANCIENT_EGYPT", "FROSTBITE_CAVES", "BIG_WAVE_BEACH", "DARK_AGES"
    };

    private static final String[] POOL = {
            "BASIC", "CONE_HEAD", "BUCKET_HEAD", "BRICK_HEAD", "KNIGHT",
            "IMP", "RA_ZOMBIE", "EXPLORER_ZOMBIE", "TOMB_RAISER",
            "SNORKEL_ZOMBIE", "OCTOPUS_ZOMBIE", "HUNTER", "TROGLOBITE",
            "DODO_RIDER_ZOMBIE", "JESTER_ZOMBIE", "IMP_DRAGON",
            "NEWSPAPER_ZOMBIE", "BARREL_ROLLER", "WIZARD_ZOMBIE",
            "FISHERMAN_ZOMBIE", "ALL_STAR", "GARGANTUAR"
    };

    private static final int BASE_WAVE_POINTS = 1000;
    private static final int MIN_WAVE_INCREASE = 500;
    private static final float WAVE_GROWTH = 1.25f;
    private static final float FINAL_WAVE_MULTIPLIER = 2.0f;

    private RankedChallengeGenerator() {
    }

    public static RankedChallengeDto forUtcDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("date");
        }
        long seed = hash64("pvz2-ranked|" + date);
        Random rng = new Random(seed);

        RankedChallengeDto dto = new RankedChallengeDto();
        dto.date = date.toString();
        dto.seed = seed;
        dto.chapter = CHAPTERS[rng.nextInt(CHAPTERS.length)];
        dto.levelNumber = 1 + rng.nextInt(8);
        dto.totalWaves = 4 + rng.nextInt(3);
        dto.startingSun = 50 + rng.nextInt(6) * 25;

        int poolSize = 5 + rng.nextInt(4);
        List<String> pool = new ArrayList<>();
        while (pool.size() < poolSize) {
            String z = POOL[rng.nextInt(POOL.length)];
            if (!pool.contains(z)) {
                pool.add(z);
            }
        }
        dto.availableZombies = pool;

        int previousBudget = 0;
        List<RankedWaveDto> waves = new ArrayList<>();
        for (int w = 1; w <= dto.totalWaves; w++) {
            boolean isFinal = w == dto.totalWaves;
            int budget = waveBudget(dto.chapter, dto.levelNumber, w, isFinal, previousBudget);
            previousBudget = budget;
            waves.add(buildWave(rng, pool, budget));
        }
        dto.waves = waves;
        return dto;
    }

    private static RankedWaveDto buildWave(Random rng, List<String> pool, int budget) {
        List<RankedSpawnDto> spawns = new ArrayList<>();
        int remaining = budget;
        int guard = 0;
        while (remaining > 0 && guard++ < 64) {
            List<String> affordable = new ArrayList<>();
            for (String type : pool) {
                if (cost(type) <= remaining) {
                    affordable.add(type);
                }
            }
            if (affordable.isEmpty()) {
                String cheapest = cheapest(pool);
                if (cheapest != null && remaining > 0) {
                    spawns.add(new RankedSpawnDto(cheapest, 1, rng.nextInt(GRID_ROWS)));
                }
                break;
            }
            String type = affordable.get(rng.nextInt(affordable.size()));
            int c = cost(type);
            int maxCount = Math.min(3, Math.max(1, remaining / c));
            int count = 1 + rng.nextInt(Math.min(2, maxCount));
            int row = rng.nextInt(GRID_ROWS);
            spawns.add(new RankedSpawnDto(type, count, row));
            remaining -= c * count;
        }
        if (spawns.isEmpty()) {
            spawns.add(new RankedSpawnDto("BASIC", 1, rng.nextInt(GRID_ROWS)));
        }
        return new RankedWaveDto(spawns);
    }

    private static int waveBudget(String chapter, int levelNumber, int waveNumber,
            boolean isFinalWave, int previousBudget) {
        float budget = BASE_WAVE_POINTS
                * chapterMultiplier(chapter)
                * levelMultiplier(levelNumber)
                * (DEFAULT_DIFFICULTY / 3f)
                * (isFinalWave ? FINAL_WAVE_MULTIPLIER : (float) Math.pow(WAVE_GROWTH, waveNumber - 1));
        int result = Math.round(budget);
        if (waveNumber > 1) {
            result = Math.max(result, previousBudget + MIN_WAVE_INCREASE);
        }
        return result;
    }

    private static float chapterMultiplier(String chapter) {
        if (chapter == null) {
            return 1f;
        }
        return switch (chapter) {
            case "ANCIENT_EGYPT" -> 1.0f;
            case "FROSTBITE_CAVES" -> 1.25f;
            case "BIG_WAVE_BEACH" -> 1.5f;
            case "DARK_AGES" -> 1.75f;
            default -> 1.0f;
        };
    }

    private static float levelMultiplier(int levelNumber) {
        int clamped = Math.max(1, Math.min(levelNumber, 8));
        return 1.0f + (clamped - 1) * 0.2f;
    }

    private static int cost(String type) {
        if (type == null) {
            return 100;
        }
        return switch (type) {
            case "BASIC", "IMP", "RA_ZOMBIE", "IMP_DRAGON" -> 100;
            case "CONE_HEAD", "EXPLORER_ZOMBIE", "SNORKEL_ZOMBIE", "BARREL_ROLLER" -> 200;
            case "TOMB_RAISER", "NEWSPAPER_ZOMBIE" -> 300;
            case "BUCKET_HEAD", "JESTER_ZOMBIE", "HUNTER" -> 400;
            case "KNIGHT", "TROGLOBITE", "DODO_RIDER_ZOMBIE" -> 550;
            case "BRICK_HEAD", "WIZARD_ZOMBIE", "FISHERMAN_ZOMBIE", "OCTOPUS_ZOMBIE" -> 700;
            case "ALL_STAR" -> 1000;
            case "GARGANTUAR" -> 1500;
            default -> 100;
        };
    }

    private static String cheapest(List<String> pool) {
        String best = null;
        int bestCost = Integer.MAX_VALUE;
        for (String t : pool) {
            int c = cost(t);
            if (c < bestCost) {
                bestCost = c;
                best = t;
            }
        }
        return best;
    }

    private static long hash64(String s) {
        byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        long h = 0xcbf29ce484222325L;
        for (byte b : bytes) {
            h ^= (b & 0xff);
            h *= 0x100000001b3L;
        }
        return h;
    }
}
