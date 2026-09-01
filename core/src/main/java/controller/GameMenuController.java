package controller;

import controller.CommandResult.CommandResult;
import model.ModelManager;
import model.data.content.chapter.ChapterCatalog;
import model.data.content.chapter.ChapterType;
import model.data.content.minigame.MiniGameCatalog;
import model.data.content.minigame.MiniGameType;
import model.data.content.specialLevel.LockedPlantsMode;
import model.data.content.specialLevel.SpecialLevelType;
import model.data.plant.PlantType;
import model.data.wave.LevelConfig;
import model.rule.SessionConfig;
import model.rule.SessionRules;
import model.service.GameNavigationState;
import model.service.GameNavigationState.Phase;
import model.storage.StorageManager;
import model.storage.user.User;
import network.NetworkSession;
import shared.izombie.IZombiePlayMode;
import shared.izombie.MatchRole;
import view.MenuType;
import view.ScreenType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
                || controllerManager.getCurrentMenu() != MenuType.TRAVEL_LOG) {
            return failure("Open minigames from the travel log (menu enter travel-log).");
        }
        CommandResult loggedInCheck = controllerManager.requireLoggedIn();
        if (loggedInCheck != null) {
            return loggedInCheck;
        }

        controllerManager.clearCurrentMenu();
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
        if (!gameNavigation.isLevelUnlocked(gameNavigation.selectedChapter, levelNumber)) {
            return failure("This level is locked. Complete previous levels first.");
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
        return levelSelectedMessage(levelNumber, level, special);
    }

    private CommandResult levelSelectedMessage(int levelNumber, LevelConfig level, SpecialLevelType special) {
        if (special == SpecialLevelType.PLANT_WHAT_YOU_GET) {
            return success("Level " + levelNumber + " selected (plant what you get). "
                    + "Pick non-sun plants, then plant with 800 sun and run 'start zombie waves'.");
        }
        if (special == SpecialLevelType.LOCKED_PLANTS
                && level.lockedPlantsConfig != null
                && level.lockedPlantsConfig.mode == LockedPlantsMode.ONE_PER_FAMILY) {
            return success("Level " + levelNumber + " selected (locked plants). "
                    + "Pick at most one plant from each family.");
        }
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
        gameNavigation.pendingIZombieMode = null;
        gameNavigation.pendingMatchRole = null;
        gameNavigation.selectedPlants.clear();

        if (type == MiniGameType.I_ZOMBIE) {
            return openIZombieModeMenu();
        }
        return startStandardMinigame(type, levelConfig);
    }

    private CommandResult openIZombieModeMenu() {
        controllerManager.openMenu(MenuType.I_ZOMBIE_MODE);
        return success("Choose I, Zombie play mode.");
    }

    private CommandResult startStandardMinigame(MiniGameType type, LevelConfig levelConfig) {
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

    public CommandResult startIZombieOffline() {
        return startIZombie(IZombiePlayMode.OFFLINE, MatchRole.ZOMBIES);
    }

    public CommandResult startIZombieCouch() {
        return startIZombie(IZombiePlayMode.COUCH, MatchRole.ZOMBIES);
    }

    public CommandResult startIZombieOnlineRandom() {
        NetworkSession net = controllerManager.getNetworkSession();
        if (net == null || !net.isLoggedIn()) {
            return failure("Log in to the game server first.");
        }
        String readyError = net.ensureOnlineReady(5_000);
        if (readyError != null) {
            return failure(mapOnlineError(readyError));
        }
        if (!net.socket().joinQueue()) {
            return failure("Could not reach the matchmaking server. Try again.");
        }
        controllerManager.clearCurrentMenu();
        controllerManager.openMenu(MenuType.I_ZOMBIE_QUEUE);
        return success("Joining matchmaking queue...");
    }

    public CommandResult startIZombieInvite(String targetUsername) {
        NetworkSession net = controllerManager.getNetworkSession();
        if (net == null || !net.isLoggedIn()) {
            return failure("Log in to the game server first.");
        }
        if (targetUsername == null || targetUsername.isBlank()) {
            return failure("Enter a username to invite.");
        }
        String readyError = net.ensureOnlineReady(5_000);
        if (readyError != null) {
            return failure(mapOnlineError(readyError));
        }
        String target = targetUsername.trim();
        if (!net.socket().invite(target)) {
            return failure("Could not reach the matchmaking server. Try again.");
        }
        controllerManager.clearCurrentMenu();
        controllerManager.openMenu(MenuType.I_ZOMBIE_QUEUE);
        return success("Sending invite to " + target + "...");
    }

    private static String mapOnlineError(String code) {
        return switch (code) {
            case "WS_NOT_READY" -> "Still connecting to the game server. Wait a moment and try again.";
            case "NOT_LOGGED_IN" -> "Log in to the game server first.";
            default -> "Online connection not ready.";
        };
    }

    public CommandResult startIZombie(IZombiePlayMode mode, MatchRole localRole) {
        if (gameNavigation.pendingMiniGame != MiniGameType.I_ZOMBIE || gameNavigation.pendingLevel == null) {
            return failure("Select I, Zombie first.");
        }
        SessionConfig config = SessionConfig.builder()
                .miniGame(MiniGameType.I_ZOMBIE)
                .levelConfig(gameNavigation.pendingLevel)
                .selectedPlants(List.of())
                .iZombiePlayMode(mode)
                .localMatchRole(localRole)
                .build();
        model.startSession(config);
        storage.recordGamePlayed();
        gameNavigation.reset();
        controllerManager.clearCurrentMenu();
        controllerManager.setScreen(ScreenType.GAME);
        return success("I, Zombie (" + mode.name().toLowerCase().replace('_', ' ') + ") started!");
    }

    public CommandResult beginOnlineMatch(shared.dto.MatchStartPayload payload) {
        if (payload == null) {
            return failure("Invalid match payload.");
        }
        LevelConfig levelConfig = MiniGameCatalog.levelConfig(MiniGameType.I_ZOMBIE);
        if (levelConfig == null) {
            return failure("Minigame configuration missing.");
        }
        gameNavigation.pendingMiniGame = MiniGameType.I_ZOMBIE;
        gameNavigation.pendingLevel = levelConfig;
        return startIZombie(IZombiePlayMode.ONLINE_RANDOM, payload.yourRole);
    }

    private CommandResult startSessionSkippingPlantSelection(SpecialLevelType special) {
        List<PlantType> plants = List.of();
        if (special == SpecialLevelType.CONVEYOR_BELT
                || special == SpecialLevelType.ZOMBOSS) {
            plants = storage.getUnlockedPlants().stream()
                    .filter(p -> !p.isBowlingExclusive())
                    .toList();
        } else if (special == SpecialLevelType.LOCKED_PLANTS
                && gameNavigation.pendingLevel != null
                && gameNavigation.pendingLevel.lockedPlantsConfig != null
                && gameNavigation.pendingLevel.lockedPlantsConfig.mode == LockedPlantsMode.PRESET) {
            List<PlantType> pool = storage.getUnlockedPlants().stream()
                    .filter(p -> p != null && !p.isBowlingExclusive())
                    .collect(Collectors.toCollection(ArrayList::new));
            Collections.shuffle(pool);
            int count = Math.min(PickPlantsController.MAX_SELECTED_PLANTS, pool.size());
            plants = List.copyOf(pool.subList(0, count));
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
        if (special == SpecialLevelType.ZOMBOSS) {
            return success("Zomboss battle! Plants arrive on the conveyor.");
        }
        if (special == SpecialLevelType.LOCKED_PLANTS) {
            if (plants.isEmpty()) {
                return success("Locked Plants started, but you have no unlocked plants yet.");
            }
            String names = plants.stream().map(p -> p.name).collect(Collectors.joining(", "));
            return success("Locked Plants started with a random loadout: " + names + ".");
        }
        return success("Game started!");
    }

    public CommandResult cheatAddCoin(int amount) {
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

    public CommandResult cheatAddGem(int amount) {
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
