package model.score;

public final class SessionScore {
    public final int killRateScore;
    public final int survivalScore;
    public final int mowerBonus;
    public final int baseScore;
    public final float multiplier;
    public final int total;
    public final int zombiesKilled;
    public final int plantsLost;
    public final int unusedMowers;
    public final int seconds;

    public SessionScore(
            int killRateScore,
            int survivalScore,
            int mowerBonus,
            int baseScore,
            float multiplier,
            int total,
            int zombiesKilled,
            int plantsLost,
            int unusedMowers,
            int seconds) {
        this.killRateScore = killRateScore;
        this.survivalScore = survivalScore;
        this.mowerBonus = mowerBonus;
        this.baseScore = baseScore;
        this.multiplier = multiplier;
        this.total = total;
        this.zombiesKilled = zombiesKilled;
        this.plantsLost = plantsLost;
        this.unusedMowers = unusedMowers;
        this.seconds = seconds;
    }
}
