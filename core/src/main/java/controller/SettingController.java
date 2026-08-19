package controller;

import controller.CommandResult.CommandResult;
import model.gameSetting.GameSetting;
import model.storage.StorageManager;
import view.MenuType;
import view.ScreenType;

public class SettingController {

    private final ControllerManager controllerManager;
    private final StorageManager storage;

    public SettingController(ControllerManager controllerManager, StorageManager storage) {
        this.controllerManager = controllerManager;
        this.storage = storage;
    }

    public CommandResult changeDifficulty(int difficultyLevel) {
        CommandResult menuCheck = requireSettingsMenu();
        if (menuCheck != null) {
            return menuCheck;
        }

        if (difficultyLevel < GameSetting.MIN_DIFFICULTY || difficultyLevel > GameSetting.MAX_DIFFICULTY) {
            return failure("Difficulty must be between "
                    + GameSetting.MIN_DIFFICULTY + " and " + GameSetting.MAX_DIFFICULTY + ".");
        }

        int currentLevel = storage.getCurrentUser().preferredSetting.getDifficultyLevel();
        if (difficultyLevel == currentLevel) {
            return failure("Difficulty is already set to " + difficultyLevel + ".");
        }

        storage.getCurrentUser().preferredSetting.setDifficultyLevel(difficultyLevel);
        storage.saveProgress();
        return success("Difficulty changed to " + difficultyLevel + ".");
    }

    public CommandResult changeGameSpeed(int gameSpeed) {
        CommandResult menuCheck = requireSettingsMenu();
        if (menuCheck != null) {
            return menuCheck;
        }
        if (gameSpeed < GameSetting.MIN_GAME_SPEED || gameSpeed > GameSetting.MAX_GAME_SPEED) {
            return failure("Game speed must be between "
                    + GameSetting.MIN_GAME_SPEED + " and " + GameSetting.MAX_GAME_SPEED + ".");
        }
        int current = storage.getCurrentUser().preferredSetting.getGameSpeed();
        if (gameSpeed == current) {
            return failure("Game speed is already set to " + gameSpeed + ".");
        }
        storage.getCurrentUser().preferredSetting.setGameSpeed(gameSpeed);
        storage.saveProgress();
        return success("Game speed changed to " + gameSpeed + ".");
    }

    public CommandResult setDebugMode(boolean debugMode) {
        CommandResult menuCheck = requireSettingsMenu();
        if (menuCheck != null) {
            return menuCheck;
        }
        GameSetting gameSetting = storage.getCurrentUser().preferredSetting;
        if(gameSetting.isDebugMode() ==  debugMode) {
            return failure("Debug mode is already " + onOff(debugMode) + ".");
        }
        gameSetting.setDebugMode(debugMode);
        storage.saveProgress();
        return success("Debug mode  " + onOff(debugMode) + ".");
    }

    public CommandResult setShowGroundWebbing(boolean showGroundWebbing) {
        CommandResult menuCheck = requireSettingsMenu();
        if (menuCheck != null) {
            return menuCheck;
        }
        GameSetting gameSetting = storage.getCurrentUser().preferredSetting;
        if(gameSetting.isShowGroundWebbing() == showGroundWebbing) {
            return failure("Ground webbing is already "+onOff(showGroundWebbing)+".");
        }
        gameSetting.setShowGroundWebbing(showGroundWebbing);
        storage.saveProgress();
        return success("Ground webbing is now "+onOff(showGroundWebbing));
    }

    private static String onOff(boolean showGroundWebbing) {
        return showGroundWebbing ? "on" : "off";
    }

    private CommandResult requireSettingsMenu() {
        CommandResult screenCheck = controllerManager.requireScreen(ScreenType.MAIN);
        if (screenCheck != null) {
            return screenCheck;
        }
        CommandResult loggedInCheck = controllerManager.requireLoggedIn();
        if (loggedInCheck != null) {
            return loggedInCheck;
        }
        if (controllerManager.getCurrentMenu() != MenuType.SETTING) {
            return failure("Open the settings menu first with: menu enter settings");
        }
        return null;
    }

    private CommandResult success(String message) {
        return new CommandResult(message, true);
    }

    private CommandResult failure(String message) {
        return new CommandResult(message, false);
    }
}
