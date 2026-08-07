package model.data.wave;

import model.data.content.chapter.ChapterCatalog;
import model.data.content.chapter.ChapterType;
import model.data.content.minigame.MiniGameType;
import model.gameSetting.GameSetting;

public final class WavePointBalance {
    public static final int BASE_WAVE_POINTS = 1000;
    public static final int MIN_WAVE_INCREASE = 500;
    public static final float WAVE_GROWTH = 1.25f;
    public static final float FINAL_WAVE_MULTIPLIER = 2.0f;

    private WavePointBalance() {
    }

    public static float chapterMultiplier(ChapterType chapter) {
        if (chapter == null) {
            return 1.0f;
        }
        return switch (chapter) {
            case ANCIENT_EGYPT -> 1.0f;
            case FROSTBITE_CAVES -> 1.25f;
            case BIG_WAVE_BEACH -> 1.5f;
            case DARK_AGES -> 1.75f;
        };
    }

    public static float levelMultiplier(int levelNumber) {
        int clamped = Math.max(1, Math.min(levelNumber, ChapterCatalog.LEVELS_PER_CHAPTER));
        return 1.0f + (clamped - 1) * 0.2f;
    }

    public static float minigameMultiplier(MiniGameType miniGameType) {
        if (miniGameType == null) {
            return 1.0f;
        }
        return switch (miniGameType) {
            case VASE_BREAKER -> 1.1f;
            case WALLNUT_BOWLING -> 1.2f;
            case I_ZOMBIE -> 1.1f;
            case BEGHOULED -> 1.15f;
            case ZOMBOTANY -> 1.15f;
        };
    }

    public static float difficultyMultiplier(int difficultyLevel) {
        int clamped = Math.max(GameSetting.MIN_DIFFICULTY,
                Math.min(difficultyLevel, GameSetting.MAX_DIFFICULTY));
        return clamped / (float) GameSetting.DEFAULT_DIFFICULTY;
    }

    public static float waveMultiplier(int waveNumber, boolean isFinalWave) {
        if (isFinalWave) {
            return FINAL_WAVE_MULTIPLIER;
        }
        int n = Math.max(1, waveNumber);
        return (float) Math.pow(WAVE_GROWTH, n - 1);
    }

    public static int calculate(
            ChapterType chapter,
            int levelNumber,
            MiniGameType miniGameType,
            int difficultyLevel,
            int waveNumber,
            boolean isFinalWave,
            int previousWaveBudget) {
        float budget = BASE_WAVE_POINTS
                * chapterMultiplier(chapter)
                * minigameMultiplier(miniGameType)
                * levelMultiplier(levelNumber)
                * difficultyMultiplier(difficultyLevel)
                * waveMultiplier(waveNumber, isFinalWave);
        int result = Math.round(budget);
        if (waveNumber > 1) {
            result = Math.max(result, previousWaveBudget + MIN_WAVE_INCREASE);
        }
        return result;
    }
}
