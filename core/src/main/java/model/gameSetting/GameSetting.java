package model.gameSetting;

public class GameSetting {

    public static final int DEFAULT_DIFFICULTY = 3;
    public static final int MIN_DIFFICULTY = 1;
    public static final int MAX_DIFFICULTY = 5;

    private int difficultyLevel = DEFAULT_DIFFICULTY;

    public int getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(int difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }
}
