package model.service;

import model.gameSetting.GameSetting;
import model.storage.user.User;

public class SettingsViewState {
    public int difficultyLevel;
    public final int minDifficulty;
    public final int maxDifficulty;

    public final int gameSpeed;
    public final int minGameSpeed;
    public final int maxGameSpeed;

    public final boolean debugMode;
    public final boolean showGroundWebbing;

    public SettingsViewState(
            int difficultyLevel,
            int minDifficulty,
            int maxDifficulty,
            int gameSpeed,
            int minGameSpeed,
            int maxGameSpeed,
            boolean debugMode,
            boolean showGroundWebbing) {
        this.difficultyLevel = difficultyLevel;
        this.minDifficulty = minDifficulty;
        this.maxDifficulty = maxDifficulty;
        this.gameSpeed = gameSpeed;
        this.minGameSpeed = minGameSpeed;
        this.maxGameSpeed = maxGameSpeed;
        this.debugMode = debugMode;
        this.showGroundWebbing = showGroundWebbing;
    }

    public static SettingsViewState fromUser(User user) {
        if (user == null || user.preferredSetting == null) {
            return empty();
        }
        return new SettingsViewState(
                user.preferredSetting.getDifficultyLevel(),
                GameSetting.MIN_DIFFICULTY,
                GameSetting.MAX_DIFFICULTY,
                user.preferredSetting.getGameSpeed(),
                empty().minGameSpeed,
                empty().maxGameSpeed,
                user.preferredSetting.isDebugMode(),
                user.preferredSetting.isShowGroundWebbing());
    }

    public static SettingsViewState empty() {
        return new SettingsViewState(
                GameSetting.DEFAULT_DIFFICULTY,
                GameSetting.MIN_DIFFICULTY,
                GameSetting.MAX_DIFFICULTY,
                GameSetting.DEFAULT_GAME_SPEED,
                GameSetting.MIN_GAME_SPEED,
                GameSetting.MAX_GAME_SPEED,
                false,
                false);
    }
}
