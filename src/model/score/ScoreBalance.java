package model.score;

import model.core.GameLoop;
import model.core.GameState;
import model.data.wave.LevelConfig;
import model.data.wave.WavePointBalance;
import model.lawnmower.LawnMower;
import model.rule.SessionConfig;

public final class ScoreBalance {
    public static final float KILL_RATE_WEIGHT = 2000f;
    public static final int SURVIVAL_BUDGET = 5000;
    public static final int SURVIVAL_COST_PER_LOSS = 400;
    public static final int MOWER_BONUS = 800;

    private ScoreBalance() {
    }

    public static SessionScore calculate(
            SessionStats stats,
            SessionConfig config,
            GameState state) {
        if (stats == null || config == null || config.isMinigame() || state == null) {
            return zero();
        }
        LevelConfig level = config.levelConfig;
        if (level == null || level.chapterType == null) {
            return zero();
        }

        int seconds = Math.max(1, state.totalTicks / GameLoop.TICKS_PER_SECOND);
        float killRate = stats.zombiesKilled / (float) seconds;
        int killRateScore = Math.round(killRate * KILL_RATE_WEIGHT);

        int survivalScore = Math.max(0, SURVIVAL_BUDGET - stats.plantsLost * SURVIVAL_COST_PER_LOSS);

        int unusedMowers = countUnusedMowers(state);
        int mowerBonus = unusedMowers * MOWER_BONUS;

        int baseScore = killRateScore + survivalScore + mowerBonus;
        float multiplier = WavePointBalance.chapterMultiplier(level.chapterType)
                * WavePointBalance.levelMultiplier(level.levelNumber)
                * WavePointBalance.difficultyMultiplier(stats.difficultyLevel);
        int total = Math.max(0, Math.round(baseScore * multiplier));

        return new SessionScore(
                killRateScore,
                survivalScore,
                mowerBonus,
                baseScore,
                multiplier,
                total,
                stats.zombiesKilled,
                stats.plantsLost,
                unusedMowers,
                seconds);
    }

    public static boolean isCampaignScoring(SessionConfig config) {
        return config != null && !config.isMinigame() && config.levelConfig != null
                && config.levelConfig.chapterType != null;
    }

    private static int countUnusedMowers(GameState state) {
        int count = 0;
        for (int row = 0; row < GameState.GRID_ROWS; row++) {
            LawnMower mower = state.getBoard().getLawnMowers(row);
            if (mower != null && mower.isActive()) {
                count++;
            }
        }
        return count;
    }

    private static SessionScore zero() {
        return new SessionScore(0, 0, 0, 0, 1f, 0, 0, 0, 0, 0);
    }
}
