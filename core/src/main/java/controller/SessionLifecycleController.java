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
import model.gameSetting.GameSetting;
import model.rule.SessionConfig;
import model.rule.SessionContext;
import model.service.GameNavigationState;
import model.service.GameNavigationState.Phase;
import model.service.MatchResultUi;
import model.storage.StorageManager;
import model.storage.user.User;
import network.NetworkSession;
import shared.izombie.IZombiePlayMode;
import view.MenuType;
import view.ScreenType;

public class SessionLifecycleController {

    private final ControllerManager controllerManager;
    private final EventBus eventBus;
    private final GameLoop gameLoop;
    private final ModelManager model;

    private boolean endHandled;
    private MatchResultUi matchResultUi;

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
        matchResultUi = null;
        applyGameSpeed();
    }

    private void applyGameSpeed() {
        int speed = GameSetting.DEFAULT_GAME_SPEED;
        StorageManager storage = controllerManager.getStorage();
        if (storage != null && storage.isLoggedIn()) {
            User user = storage.getCurrentUser();
            if (user != null && user.preferredSetting != null) {
                speed = user.preferredSetting.getGameSpeed();
            }
        }
        gameLoop.setGameSpeed(speed);
    }

    public boolean hasEnded() {
        return endHandled;
    }

    public MatchResultUi matchResultUi() {
        return matchResultUi;
    }

    public void showMatchResult(MatchResultUi ui) {
        if (ui == null) {
            return;
        }
        endHandled = true;
        matchResultUi = ui;
        gameLoop.stopAutoTick();
        Runnable open = () -> {
            if (controllerManager.getCurrentScreen() != ScreenType.GAME) {
                return;
            }
            controllerManager.openMenu(MenuType.MATCH_RESULT);
        };
        if (com.badlogic.gdx.Gdx.app != null
                && Thread.currentThread().getName().startsWith("AutoTick")) {
            com.badlogic.gdx.Gdx.app.postRunnable(open);
        } else {
            open.run();
        }
    }

    public CommandResult returnToLevelSelect() {
        if (controllerManager.getCurrentScreen() != ScreenType.GAME) {
            return new CommandResult("Not in a game session.", false);
        }

        leaveOnlineMatchIfNeeded();
        gameLoop.stopAutoTick();
        SessionContext rankedCheck = model.getPlayContext();
        boolean ranked = rankedCheck != null && rankedCheck.getConfig() != null
                && rankedCheck.getConfig().isRanked();
        restoreNavigationFromSession();
        model.endSession();
        matchResultUi = null;
        endHandled = false;
        controllerManager.clearCurrentMenu();
        if (ranked) {
            controllerManager.setScreen(ScreenType.MAIN);
        } else {
            controllerManager.setScreen(ScreenType.LEVEL_SELECTOR);
        }
        return new CommandResult(ranked ? "Returned to main menu." : "Returned to level selection.", true);
    }

    public CommandResult restartLevel() {
        if (controllerManager.getCurrentScreen() != ScreenType.GAME) {
            return new CommandResult("not in a game session.", false);
        }
        SessionContext context = model.getPlayContext();
        if (context == null || context.getConfig() == null) {
            return new CommandResult("no session to restart", false);
        }
        SessionConfig config = context.getConfig();
        gameLoop.stopAutoTick();
        controllerManager.clearCurrentMenu();
        matchResultUi = null;
        model.startSession(config);
        onSessionStart();
        controllerManager.startLevelIntro();
        controllerManager.refreshView();
        return new CommandResult("Level restarted", true);
    }

    public void leaveOnlineMatchIfNeeded() {
        if (!isOnlineMatch()) {
            return;
        }
        NetworkSession net = controllerManager.getNetworkSession();
        if (net == null) {
            return;
        }
        net.socket().leaveMatch();
        net.clearActiveMatch();
    }

    public boolean isOnlineMatch() {
        NetworkSession net = controllerManager.getNetworkSession();
        if (net != null && net.activeMatch() != null) {
            return true;
        }
        SessionContext session = model.getPlayContext();
        if (session == null || session.getConfig() == null) {
            return false;
        }
        IZombiePlayMode mode = session.getConfig().iZombiePlayMode;
        return mode == IZombiePlayMode.ONLINE_RANDOM || mode == IZombiePlayMode.ONLINE_INVITE;
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
        if (isOnlineMatch()) {
            return;
        }

        endHandled = true;
        gameLoop.stopAutoTick();

        Runnable ui = () -> {
            if (won) {
                applyProgressOnWin();
            } else {
                submitRankedComplete(false, 0);
            }
            matchResultUi = buildLocalResult(won, reason);
            controllerManager.openMenu(MenuType.MATCH_RESULT);
        };

        if (com.badlogic.gdx.Gdx.app != null
                && Thread.currentThread().getName().startsWith("AutoTick")) {
            com.badlogic.gdx.Gdx.app.postRunnable(ui);
        } else {
            ui.run();
        }
    }

    private MatchResultUi buildLocalResult(boolean won, GameOverReason reason) {
        if (model.getState().sessionEndTitle != null) {
            return new MatchResultUi(
                    model.getState().sessionEndTitle,
                    model.getState().sessionEndDetail,
                    won,
                    true,
                    false,
                    false);
        }

        SessionContext context = model.getPlayContext();
        SessionConfig config = context != null ? context.getConfig() : null;
        boolean iZombiePvP = config != null && config.isIZombiePvP();

        if (iZombiePvP) {
            String title = won ? "Victory!" : "Defeat";
            String detail = reason != null ? reason.message : (won ? "You cleared the challenge." : "Try again.");
            return new MatchResultUi(title, detail, won, true, false, false);
        }

        String title = won ? "Level Complete!" : "Game Over";
        if (config != null && config.isRanked()) {
            title = won ? "Ranked Challenge Complete!" : "Ranked Challenge Failed";
        }
        String detail;
        if (won) {
            String scoreNote = "";
            if (model.getState().hasSessionScore()) {
                scoreNote = " Score: " + model.getState().getSessionScore() + ".";
                if (model.getState().isSessionScoreNewRecord()) {
                    scoreNote += " New high score!";
                }
            }
            detail = "Nice work." + scoreNote;
            if (config == null || !config.isRanked()) {
                controllerManager.startZombossEndDialogue(true);
            }
        } else {
            if (config != null && config.isRanked()) {
                detail = "Today's attempt is used. Come back tomorrow.";
            } else {
                detail = reason != null ? reason.message : "Better luck next time.";
                controllerManager.startZombossEndDialogue(false);
            }
        }
        return new MatchResultUi(title, detail, won, true, false, false);
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

        if (config.isRanked()) {
            var score = model.getScoreTracker().finalizeScore(context, model.getState());
            int total = score != null ? score.total : 0;
            boolean newRecord = submitRankedComplete(true, total);
            if (score != null) {
                model.getScoreTracker().setLastScoreIsRecord(newRecord);
                model.getState().setSessionScore(score.total, newRecord);
            }
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

    private boolean submitRankedComplete(boolean won, int score) {
        SessionContext context = model.getPlayContext();
        SessionConfig config = context != null ? context.getConfig() : null;
        if (config == null || !config.isRanked() || config.rankedChallenge == null) {
            return false;
        }
        NetworkSession net = controllerManager.getNetworkSession();
        if (net == null || !net.isLoggedIn()) {
            return false;
        }
        try {
            shared.dto.RankedCompleteResponse res = net.authApi().rankedComplete(
                    net.token(),
                    new shared.dto.RankedCompleteRequest(config.rankedChallenge.date, won, score));
            if (res == null || !res.ok) {
                return false;
            }
            StorageManager storage = controllerManager.getStorage();
            storage.recordRankedScore(res.highestScore);
            storage.saveProgress();
            return res.newRecord;
        } catch (Exception e) {
            return false;
        }
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
            nav.pendingLevel = config.levelConfig;
            nav.phase = Phase.MINIGAME;
            return;
        }

        if (config.isRanked()) {
            nav.pendingRankedChallenge = config.rankedChallenge;
            nav.pendingLevel = config.levelConfig;
            nav.phase = Phase.NONE;
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
