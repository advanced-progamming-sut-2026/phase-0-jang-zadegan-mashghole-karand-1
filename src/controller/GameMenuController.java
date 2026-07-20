package controller;

import controller.CommandResult.CommandResult;
import model.ModelManager;
import model.data.content.chapter.ChapterCatalog;
import model.data.content.chapter.ChapterType;
import model.data.content.specialLevel.SpecialLevelCatalog;
import model.data.content.specialLevel.SpecialLevelType;
import model.data.wave.LevelConfig;
import model.rule.SessionConfig;
import model.service.GameNavigationState;
import model.service.GameNavigationState.Phase;
import model.storage.StorageManager;
import view.ScreenType;

public class GameMenuController {

    private final ControllerManager controllerManager;
    private final ModelManager model;
    private final StorageManager storage;
    private final GameNavigationState gameNavigation;

    public GameMenuController(ControllerManager controllerManager, ModelManager model,
            StorageManager storage, GameNavigationState gameNavigation) {
        this.controllerManager = controllerManager;
        this.model = model;
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

        ChapterType chapter = ChapterCommands.fromCommandName(chapterName);
        if (chapter == null) {
            return failure("Unknown chapter.");
        }
        if (!storage.isChapterUnlocked(chapter)) {
            return failure("This chapter is locked.");
        }

        gameNavigation.selectedChapter = chapter;
        gameNavigation.phase = Phase.LEVEL;
        controllerManager.refreshView();
        return success("Entered " + ChapterCommands.displayName(chapter) + ". Select a level.");
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

        LevelConfig level = ChapterCatalog.getLevel(gameNavigation.selectedChapter, levelNumber);
        if (level == null) {
            return failure("Level " + levelNumber + " is not defined for this chapter.");
        }

        gameNavigation.selectedLevel = levelNumber;
        gameNavigation.pendingLevel = level;
        gameNavigation.selectedPlants.clear();

        SpecialLevelType special = level.specialLevelType;
        gameNavigation.pendingSpecialLevel = special;

        if (SpecialLevelCatalog.skipsPlantSelection(special)) {
            return startConveyorSession(special);
        }

        gameNavigation.phase = Phase.PLANT;
        controllerManager.refreshView();
        if (special != null) {
            return success("Level " + levelNumber + " selected (" + special.name().toLowerCase().replace('_', ' ')
                    + "). Pick your plants.");
        }
        return success("Level " + levelNumber + " selected. Pick your plants.");
    }

    private CommandResult startConveyorSession(SpecialLevelType special) {
        SessionConfig session = SessionConfig.builder()
                .levelConfig(gameNavigation.pendingLevel)
                .specialLevel(special)
                .selectedPlants(storage.getUnlockedPlants())
                .build();

        model.startSession(session);
        storage.recordGamePlayed();
        gameNavigation.reset();
        controllerManager.setScreen(ScreenType.GAME);
        return success("Conveyor Belt started! Plants will be offered every 12 seconds.");
    }

    public CommandResult greenHouse() {
        return requireMainMenu("Greenhouse is not available right now.");
    }

    public CommandResult quest() {
        return requireMainMenu("Travel log is not available right now.");
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
        return failure("Coin cheat is not available yet.");
    }

    public CommandResult CHEAT_add_gem(int amount) {
        CommandResult screenCheck = requireMainMenu(null);
        if (screenCheck != null) {
            return screenCheck;
        }
        return failure("Gem cheat is not available yet.");
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
