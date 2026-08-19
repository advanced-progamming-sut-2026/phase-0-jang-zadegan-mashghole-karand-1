package model.service;

import model.gameSetting.GameSetting;
import model.storage.user.User;

public class SettingsViewState {
    public int difficultyLevel;
    public final int minDifficulty;
    public final int maxDifficulty;

    public final int gameSpeed;
    public final int MIN_GAME_SPEED;
    public final int MAX_GAME_SPEED;

    public final boolean debugMode;
    public final boolean showGroundWebbing;

    public SettingsViewState(int difficultyLevel, int minDifficulty, int maxDifficulty, int gameSpeed, int minGameSpeed, int maxGameSpeed, boolean debugMode, boolean showGroundWebbing) {
        this.difficultyLevel = difficultyLevel;
        this.minDifficulty = minDifficulty;
        this.maxDifficulty = maxDifficulty;
        this.gameSpeed = gameSpeed;
        MIN_GAME_SPEED = minGameSpeed;
        MAX_GAME_SPEED = maxGameSpeed;
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
                empty().MIN_GAME_SPEED,
                empty().MAX_GAME_SPEED,
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
                GameSetting.MAX_GAME_SPEED,false,
                false );
    }
}
