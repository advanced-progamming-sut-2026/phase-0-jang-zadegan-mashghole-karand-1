package controller;

import controller.CommandResult.CommandResult;
import model.ModelManager;
import model.core.EventBus;
import model.core.GameLoop;
import model.data.content.chapter.ChapterType;
import model.data.content.minigame.MiniGameType;
import model.data.content.progress.ProgressRewards;
import model.data.wave.LevelConfig;
import model.event.events.GameOverEvent;
import model.event.events.GameOverReason;
import model.event.events.LevelCompleteEvent;
import model.rule.SessionConfig;
import model.rule.SessionContext;
import model.service.GameNavigationState;
import model.service.GameNavigationState.Phase;
import model.storage.StorageManager;
import view.ScreenType;

public class SessionLifecycleController {

    private final ControllerManager controllerManager;
    private final EventBus eventBus;
    private final GameLoop gameLoop;
    private final ModelManager model;

    private boolean endHandled;

    public SessionLifecycleController(ControllerManager controllerManager, EventBus eventBus,
            GameLoop gameLoop, ModelManager model) {
        this.controllerManager = controllerManager;
        this.eventBus = eventBus;
        this.gameLoop = gameLoop;
        this.model = model;
    }

    public void register() {
        eventBus.subscribe(LevelCompleteEvent.class, this::onLevelComplete);
        eventBus.subscribe(GameOverEvent.class, this::onGameOver);
    }

    public void onSessionStart() {
        endHandled = false;
    }

    public boolean hasEnded() {
        return endHandled;
    }

    public CommandResult returnToLevelSelect() {
        if (controllerManager.getCurrentScreen() != ScreenType.GAME) {
            return new CommandResult("Not in a game session.", false);
        }

        gameLoop.stopAutoTick();
        restoreNavigationFromSession();
        model.endSession();
        controllerManager.setScreen(ScreenType.LEVEL_SELECTOR);
        return new CommandResult("Returned to level selection.", true);
    }

    private void onLevelComplete(LevelCompleteEvent event) {
        handleSessionEnd(true, null);
    }

    private void onGameOver(GameOverEvent event) {
        handleSessionEnd(false, event != null ? event.reason : null);
    }

    private void handleSessionEnd(boolean won, GameOverReason reason) {
        if (endHandled) {
            return;
        }
        endHandled = true;

        gameLoop.stopAutoTick();

        if (won) {
            applyProgressOnWin();
            controllerManager.sendMessage("Level complete! Use 'menu exit' to return.");
        } else {
            String detail = reason != null ? reason.message + " " : "";
            controllerManager.sendMessage("Game over! " + detail + "Use 'menu exit' to return.");
        }
        controllerManager.refreshView();
    }

    private void applyProgressOnWin() {
        StorageManager storage = controllerManager.getStorage();
        if (!storage.isLoggedIn()) {
            return;
        }

        SessionContext context = model.getPlayContext();
        if (context == null) {
            return;
        }
        SessionConfig config = context.getConfig();
        if (config == null) {
            return;
        }

        if (config.isMinigame() && config.miniGameType != null) {
            storage.markMinigameCompleted(config.miniGameType);
            storage.saveProgress();
            return;
        }

        LevelConfig level = config.levelConfig;
        if (level == null || level.chapterType == null) {
            return;
        }

        storage.markLevelCompleted(level.chapterType, level.levelNumber);

        ChapterType nextChapter = ProgressRewards.nextChapter(level.chapterType, level.levelNumber);
        if (nextChapter != null) {
            storage.unlockChapter(nextChapter);
        }

        for (MiniGameType miniGame : ProgressRewards.minigamesFor(level.chapterType, level.levelNumber)) {
            storage.unlockMinigame(miniGame);
        }

        storage.saveProgress();
    }

    private void restoreNavigationFromSession() {
        GameNavigationState nav = controllerManager.getGameNavigation();
        nav.reset();
        nav.phase = Phase.CHAPTER;

        SessionContext context = model.getPlayContext();
        if (context == null) {
            return;
        }
        SessionConfig config = context.getConfig();
        if (config == null) {
            return;
        }

        if (config.isMinigame()) {
            nav.pendingMiniGame = config.miniGameType;
            nav.phase = Phase.MINIGAME;
            return;
        }

        if (config.levelConfig == null) {
            return;
        }

        nav.selectedChapter = config.levelConfig.chapterType;
        nav.selectedLevel = config.levelConfig.levelNumber;
        nav.pendingLevel = config.levelConfig;
        nav.pendingSpecialLevel = config.specialLevelType != null
                ? config.specialLevelType
                : config.levelConfig.specialLevelType;
        nav.phase = Phase.LEVEL;
    }
}
