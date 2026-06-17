package controller;

import model.gameSetting.GameSetting;

public class SettingController {
    private GameSetting setting;


    public void applySettings(GameSetting setting) {
        this.setting = setting;
    }

    public void changeDifficulty(int difficultyLevel) {}
}
