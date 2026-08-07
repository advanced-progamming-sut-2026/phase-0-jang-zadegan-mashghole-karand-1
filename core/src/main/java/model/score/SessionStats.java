package model.score;

public class SessionStats {
    public int zombiesKilled;
    public int plantsLost;
    public int difficultyLevel = 3;

    public void reset() {
        zombiesKilled = 0;
        plantsLost = 0;
        difficultyLevel = 3;
    }
}
