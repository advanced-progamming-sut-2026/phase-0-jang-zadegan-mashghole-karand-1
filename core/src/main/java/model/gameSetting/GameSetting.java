package model.gameSetting;

public class GameSetting {

    public static final int DEFAULT_DIFFICULTY = 3;
    public static final int MIN_DIFFICULTY = 1;
    public static final int MAX_DIFFICULTY = 5;

    public static final int DEFAULT_GAME_SPEED = 2;
    public static final int MIN_GAME_SPEED = 1;
    public static final int MAX_GAME_SPEED = 3;

    private int difficultyLevel = DEFAULT_DIFFICULTY;
    private int gameSpeed = DEFAULT_GAME_SPEED;

    private boolean showGroundWebbing = false;
    private boolean debugMode = false;

    public int getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(int difficultyLevel) {
        if (difficultyLevel < MIN_DIFFICULTY || difficultyLevel > MAX_DIFFICULTY) {
            return;
        }
        this.difficultyLevel = difficultyLevel;
    }

    public int getGameSpeed() {
        return gameSpeed;
    }

    public void setGameSpeed(int gameSpeed) {
        if (gameSpeed < MIN_GAME_SPEED || gameSpeed > MAX_GAME_SPEED) {
            return;
        }
        this.gameSpeed = gameSpeed;
    }

    public boolean isShowGroundWebbing() {
        return showGroundWebbing;
    }

    public void setShowGroundWebbing(boolean showGroundWebbing) {
        this.showGroundWebbing = showGroundWebbing;
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }
}
