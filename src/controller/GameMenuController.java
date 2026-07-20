package controller;

import controller.CommandResult.CommandResult;
import model.data.content.chapter.ChapterCatalog;
import model.data.content.chapter.ChapterType;
import model.data.wave.LevelConfig;
import model.service.GameNavigationState;
import model.service.GameNavigationState.Phase;
import model.storage.StorageManager;
import model.storage.user.User;
import view.ScreenType;

public class GameMenuController {

    private final ControllerManager controllerManager;
    private final StorageManager storage;
    private final GameNavigationState gameNavigation;

    public GameMenuController(ControllerManager controllerManager, StorageManager storage,
            GameNavigationState gameNavigation) {
        this.controllerManager = controllerManager;
        this.storage = storage;
        this.gameNavigation = gameNavigation;
    }

    public CommandResult enterChapter(String chapterName) {
        if (controllerManager.getCurrentScreen() != ScreenType.LEVEL_SELECTOR
                || gameNavigation.phase != Phase.CHAPTER) {
            return failure("Select a chapter from the game menu first.");
        }
        CommandResult loggedInCheck = controllerManager.requireLoggedIn();
        if (loggedInCheck != null) {
            return loggedInCheck;
        }

        ChapterType chapter = ChapterCatalog.fromCommandName(chapterName);
        if (chapter == null) {
            return failure("Unknown chapter.");
        }
        if (!storage.isChapterUnlocked(chapter)) {
            return failure("This chapter is locked.");
        }

        gameNavigation.selectedChapter = chapter;
        gameNavigation.phase = Phase.LEVEL;
        controllerManager.refreshView();
        return success("Entered " + ChapterCatalog.displayName(chapter) + ". Select a level.");
    }

    public CommandResult selectLevel(int levelNumber) {
        if (controllerManager.getCurrentScreen() != ScreenType.LEVEL_SELECTOR
                || gameNavigation.phase != Phase.LEVEL) {
            return failure("Select a chapter first.");
        }
        CommandResult loggedInCheck = controllerManager.requireLoggedIn();
        if (loggedInCheck != null) {
            return loggedInCheck;
        }

        if (levelNumber < 1 || levelNumber > ChapterCatalog.LEVELS_PER_CHAPTER) {
            return failure("Invalid level number.");
        }

        gameNavigation.selectedLevel = levelNumber;
        gameNavigation.pendingLevel = LevelConfig.createDefault(gameNavigation.selectedChapter, levelNumber);
        gameNavigation.selectedPlants.clear();
        gameNavigation.phase = Phase.PLANT;
        controllerManager.refreshView();
        return success("Level " + levelNumber + " selected. Pick your plants.");
    }

    public CommandResult greenHouse() {
        return requireMainMenu("Greenhouse is not available right now.");
    }

    public CommandResult quest() {
        return requireMainMenu("Travel log is not available right now.");
    }

    public CommandResult leaderboard() {
        return controllerManager.enterMenu("leaderboard");
    }

    public CommandResult coin_wallet() {
        return requireMainMenu("Coin wallet is not available right now.");
    }

    public CommandResult gem_wallet() {
        return requireMainMenu("Gem wallet is not available right now.");
    }

    public CommandResult CHEAT_add_coin(int amount) {
        CommandResult screenCheck = requireMainMenu(null);
        if (screenCheck != null) {
            return screenCheck;
        }
        if (amount <= 0) {
            return failure("Amount must be positive.");
        }
        User user = storage.getCurrentUser();
        user.coins += amount;
        storage.saveProgress();
        return success("Added " + amount + " coins. Total: " + user.coins);
    }

    public CommandResult CHEAT_add_gem(int amount) {
        CommandResult screenCheck = requireMainMenu(null);
        if (screenCheck != null) {
            return screenCheck;
        }
        if (amount <= 0) {
            return failure("Amount must be positive.");
        }
        User user = storage.getCurrentUser();
        user.gems += amount;
        storage.saveProgress();
        return success("Added " + amount + " gems. Total: " + user.gems);
    }

    private CommandResult requireMainMenu(String unavailableMessage) {
        CommandResult screenCheck = controllerManager.requireScreen(ScreenType.MAIN);
        if (screenCheck != null) {
            return screenCheck;
        }
        CommandResult loggedInCheck = controllerManager.requireLoggedIn();
        if (loggedInCheck != null) {
            return loggedInCheck;
        }
        if (unavailableMessage != null) {
            return failure(unavailableMessage);
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
