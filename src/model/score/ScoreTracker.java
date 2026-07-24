package model.score;

import model.core.GameState;
import model.rule.SessionConfig;
import model.rule.SessionContext;

public class ScoreTracker {
    private final SessionStats stats = new SessionStats();
    private SessionScore lastScore;
    private boolean lastScoreIsRecord;

    public void beginSession(SessionContext context, int difficultyLevel) {
        stats.reset();
        stats.difficultyLevel = difficultyLevel;
        lastScore = null;
        lastScoreIsRecord = false;
        if (context == null || !ScoreBalance.isCampaignScoring(context.getConfig())) {
            return;
        }
    }

    public void clear() {
        stats.reset();
        lastScore = null;
        lastScoreIsRecord = false;
    }

    public boolean isTracking(SessionContext context) {
        return context != null && ScoreBalance.isCampaignScoring(context.getConfig());
    }

    public void onZombieKilled(SessionContext context) {
        if (!isTracking(context)) {
            return;
        }
        stats.zombiesKilled++;
    }

    public void onPlantLost(SessionContext context) {
        if (!isTracking(context)) {
            return;
        }
        stats.plantsLost++;
    }

    public SessionStats getStats() {
        return stats;
    }

    public SessionScore finalizeScore(SessionContext context, GameState state) {
        if (!isTracking(context)) {
            lastScore = null;
            lastScoreIsRecord = false;
            return null;
        }
        lastScore = ScoreBalance.calculate(stats, context.getConfig(), state);
        return lastScore;
    }

    public SessionScore getLastScore() {
        return lastScore;
    }

    public void setLastScoreIsRecord(boolean isRecord) {
        this.lastScoreIsRecord = isRecord;
    }

    public boolean isLastScoreRecord() {
        return lastScoreIsRecord;
    }

    public SessionConfig scoringConfig(SessionContext context) {
        return context != null ? context.getConfig() : null;
    }
}
