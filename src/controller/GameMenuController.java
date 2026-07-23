package controller;

import controller.CommandResult.CommandResult;
import model.ModelManager;
import model.data.content.chapter.ChapterCatalog;
import model.data.content.chapter.ChapterType;
import model.data.content.minigame.MiniGameCatalog;
import model.data.content.minigame.MiniGameType;
import model.data.content.specialLevel.SpecialLevelType;
import model.data.plant.PlantType;
import model.data.wave.LevelConfig;
import model.rule.SessionConfig;
import model.rule.SessionRules;
import model.service.GameNavigationState;
import model.service.GameNavigationState.Phase;
import model.storage.StorageManager;
import model.storage.user.User;
import view.ScreenType;

import java.util.List;

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

    public CommandResult enterMinigames() {
        if (controllerManager.getCurrentScreen() != ScreenType.LEVEL_SELECTOR
                || gameNavigation.phase != Phase.CHAPTER) {
            return failure("Open minigames from the game menu.");
        }
        CommandResult loggedInCheck = controllerManager.requireLoggedIn();
        if (loggedInCheck != null) {
            return loggedInCheck;
        }

        gameNavigation.pendingMiniGame = null;
        gameNavigation.phase = Phase.MINIGAME;
        controllerManager.refreshView();
        return success("Minigames. Select one with select minigame -m <name>.");
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
        gameNavigation.pendingMiniGame = null;
        gameNavigation.selectedPlants.clear();

        SpecialLevelType special = level.specialLevelType;
        gameNavigation.pendingSpecialLevel = special;

        SessionConfig.Builder probe = SessionConfig.builder()
                .levelConfig(level)
                .selectedPlants(List.of());
        if (special != null) {
            probe.specialLevel(special);
        }

        if (SessionRules.skipsPlantSelection(probe.build())) {
            return startSessionSkippingPlantSelection(special);
        }

        gameNavigation.phase = Phase.PLANT;
        controllerManager.refreshView();
        if (special != null) {
            return success("Level " + levelNumber + " selected (" + special.name().toLowerCase().replace('_', ' ')
                    + "). Pick your plants.");
        }
        return success("Level " + levelNumber + " selected. Pick your plants.");
    }

    public CommandResult selectMinigame(String name) {
        if (controllerManager.getCurrentScreen() != ScreenType.LEVEL_SELECTOR
                || gameNavigation.phase != Phase.MINIGAME) {
            return failure("Open minigames first.");
        }
        CommandResult loggedInCheck = controllerManager.requireLoggedIn();
        if (loggedInCheck != null) {
            return loggedInCheck;
        }

        MiniGameType type = MiniGameCommands.fromCommandName(name);
        if (type == null) {
            return failure("Unknown minigame.");
        }
        if (!storage.isMinigameUnlocked(type)) {
            return failure("This minigame is locked.");
        }
        if (!MiniGameCatalog.isPlayable(type)) {
            return failure(MiniGameCommands.displayName(type) + " is not available yet.");
        }

        LevelConfig levelConfig = MiniGameCatalog.levelConfig(type);
        if (levelConfig == null) {
            return failure("Minigame configuration missing.");
        }

        gameNavigation.pendingMiniGame = type;
        gameNavigation.pendingLevel = levelConfig;
        gameNavigation.pendingSpecialLevel = null;
        gameNavigation.selectedPlants.clear();

        SessionConfig probe = SessionConfig.builder()
                .miniGame(type)
                .levelConfig(levelConfig)
                .selectedPlants(List.of())
                .build();

        if (SessionRules.skipsPlantSelection(probe)) {
            model.startSession(probe);
            storage.recordGamePlayed();
            gameNavigation.reset();
            controllerManager.setScreen(ScreenType.GAME);
            return success(MiniGameCommands.displayName(type) + " started!");
        }

        gameNavigation.phase = Phase.PLANT;
        controllerManager.refreshView();
        return success(MiniGameCommands.displayName(type) + " selected. Pick your plants.");
    }

    private CommandResult startSessionSkippingPlantSelection(SpecialLevelType special) {
        List<PlantType> plants = List.of();
        if (special == SpecialLevelType.CONVEYOR_BELT) {
            plants = storage.getUnlockedPlants().stream()
                    .filter(p -> !p.isBowlingExclusive())
                    .toList();
        }

        SessionConfig.Builder sessionBuilder = SessionConfig.builder()
                .levelConfig(gameNavigation.pendingLevel)
                .selectedPlants(plants);
        if (special != null) {
            sessionBuilder.specialLevel(special);
        }

        model.startSession(sessionBuilder.build());
        storage.recordGamePlayed();
        gameNavigation.reset();
        controllerManager.setScreen(ScreenType.GAME);
        if (special == SpecialLevelType.CONVEYOR_BELT) {
            return success("Conveyor Belt started! Plants will be offered every 12 seconds.");
        }
        return success("Game started!");
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
