package model.service;

import model.gameSetting.GameSetting;
import model.storage.user.User;

public class SettingsViewState {
    public final int difficultyLevel;
    public final int minDifficulty;
    public final int maxDifficulty;

    public SettingsViewState(int difficultyLevel, int minDifficulty, int maxDifficulty) {
        this.difficultyLevel = difficultyLevel;
        this.minDifficulty = minDifficulty;
        this.maxDifficulty = maxDifficulty;
    }

    public static SettingsViewState fromUser(User user) {
        if (user == null || user.preferredSetting == null) {
            return empty();
        }
        return new SettingsViewState(
                user.preferredSetting.getDifficultyLevel(),
                GameSetting.MIN_DIFFICULTY,
                GameSetting.MAX_DIFFICULTY);
    }

    public static SettingsViewState empty() {
        return new SettingsViewState(
                GameSetting.DEFAULT_DIFFICULTY,
                GameSetting.MIN_DIFFICULTY,
                GameSetting.MAX_DIFFICULTY);
    }
}
