package view.gdx.ui;

import java.util.EnumMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import model.core.GameLoop;
import view.MenuType;
import view.ScreenType;
import view.gdx.ui.screens.GameScreenShell;
import view.gdx.ui.screens.PlaceholderOverlayScreen;
import view.gdx.ui.screens.PlaceholderScreen;
import view.gdx.ui.screens.menus.ShopOverlayScreen;
import view.gdx.ui.screens.map.LevelSelectorScreen;
import view.gdx.ui.screens.auth.LoginScreen;
import view.gdx.ui.screens.main.MainScreen;
import view.gdx.ui.screens.auth.RegisterScreen;
import view.gdx.ui.screens.Garden.GardenScreen;
import view.gdx.ui.screens.GlobalTopBar;
<<<<<<< Updated upstream
import view.gdx.ui.screens.izombie.IZombieInviteOverlayScreen;
import view.gdx.ui.screens.izombie.IZombieModeOverlayScreen;
import view.gdx.ui.screens.izombie.IZombieQueueOverlayScreen;
=======
import view.gdx.ui.screens.menus.NewsOverlayScreen;
import view.gdx.ui.screens.menus.PauseOverlayScreen;
import view.gdx.ui.screens.izombie.IZombieInviteOverlayScreen;
import view.gdx.ui.screens.izombie.IZombieModeOverlayScreen;
import view.gdx.ui.screens.izombie.IZombieQueueOverlayScreen;
import view.gdx.ui.screens.izombie.MatchRestartOverlayScreen;
import view.gdx.ui.screens.izombie.MatchResultOverlayScreen;
>>>>>>> Stashed changes
import view.gdx.ui.screens.izombie.QuickMessageOverlayScreen;
import view.gdx.ui.screens.menus.SettingsOverlayScreen;

public final class UiNavigator implements Disposable {
    private final Map<ScreenType, UiScreen> screens = new EnumMap<>(ScreenType.class);
    private final Map<MenuType, UiScreen> overlays = new EnumMap<>(MenuType.class);
    private final Stage toastStage;
    private final Label toastLabel;
    private final GameLoop gameLoop;
    private final GlobalTopBar topBar;

    private UiScreen activeScreen;
    private UiScreen activeOverlay;
    private UiViewContext lastContext;
    private InputProcessor gameWorldInput;

    public UiNavigator(GameLoop gameLoop) {
        this.gameLoop = gameLoop;
        this.topBar = new GlobalTopBar();
        registerDefaults();
        toastStage = new Stage(new ScreenViewport());
        Table toastRoot = new Table();
        toastRoot.setFillParent(true);
        toastRoot.bottom().left().pad(16f);
        toastLabel = new Label("", UiSkin.get(), "default");
        toastRoot.add(toastLabel);
        toastStage.addActor(toastRoot);
    }

    private void registerDefaults() {
        screens.put(ScreenType.LOGIN, new LoginScreen());
        screens.put(ScreenType.REGISTER, new RegisterScreen());
        screens.put(ScreenType.MAIN, new MainScreen());
        screens.put(ScreenType.LEVEL_SELECTOR, new LevelSelectorScreen());
        screens.put(ScreenType.GREEN_HOUSE, new GardenScreen());
        for (ScreenType type : ScreenType.values()) {
            if (screens.containsKey(type)) {
                continue;
            }
            if (type == ScreenType.GAME) {
                screens.put(type, new GameScreenShell());
            } else {
                screens.put(type, new PlaceholderScreen(prettyName(type)));
            }
        }
        overlays.put(MenuType.PAUSE, new PlaceholderOverlayScreen("Pause"));
        overlays.put(MenuType.SETTING, new SettingsOverlayScreen());
        overlays.put(MenuType.PROFILE, new PlaceholderOverlayScreen("Profile"));
        overlays.put(MenuType.NEWS, new PlaceholderOverlayScreen("News"));
        overlays.put(MenuType.TRAVEL_LOG, new PlaceholderOverlayScreen("Travel Log"));
        overlays.put(MenuType.PLANT_SELECTOR, new PlaceholderOverlayScreen("Plant Selector"));
        overlays.put(MenuType.SHOP, new ShopOverlayScreen());
        overlays.put(MenuType.I_ZOMBIE_MODE, new IZombieModeOverlayScreen());
        overlays.put(MenuType.I_ZOMBIE_QUEUE, new IZombieQueueOverlayScreen());
        overlays.put(MenuType.I_ZOMBIE_INVITE, new IZombieInviteOverlayScreen());
        overlays.put(MenuType.QUICK_MESSAGES, new QuickMessageOverlayScreen());
<<<<<<< Updated upstream
=======
        MatchRestartOverlayScreen restartOverlay = new MatchRestartOverlayScreen();
        overlays.put(MenuType.MATCH_RESTART, restartOverlay);
        overlays.put(MenuType.MATCH_RESTART_WAIT, restartOverlay);
        overlays.put(MenuType.MATCH_RESULT, new MatchResultOverlayScreen());
>>>>>>> Stashed changes
    }

    public void show(UiViewContext context) {
        lastContext = context;
        UiScreen next = screens.get(context.screen);
        if (next == null) {
            Gdx.app.error("UiNavigator", "No screen registered for " + context.screen);
            return;
        }
        if (next != activeScreen) {
            activeScreen = next;
        }
        activeScreen.show(context);
        syncOverlay(context);
        syncGameLoop(context.screen);
        int width = Math.max(1, Gdx.graphics.getWidth());
        int height = Math.max(1, Gdx.graphics.getHeight());
        activeScreen.resize(width, height);
        if (activeOverlay != null) {
            activeOverlay.resize(width, height);
        }
        toastStage.getViewport().update(width, height, true);
        topBar.bind(context);
        topBar.resize(width, height);
        updateInputProcessors();
    }

    private void syncOverlay(UiViewContext context) {
        if (context.menu == null || context.menu == MenuType.NONE) {
            activeOverlay = null;
            return;
        }
        activeOverlay = overlays.get(context.menu);
        if (activeOverlay != null) {
            activeOverlay.show(context);
        }
    }

<<<<<<< Updated upstream
    private void syncGameLoop(ScreenType screen) {
        if (screen == ScreenType.GAME) {
=======
    private void syncGameLoop(UiViewContext context) {
        boolean playing = context.screen == ScreenType.GAME && !pausesGameplay(context.menu);
        if (playing) {
>>>>>>> Stashed changes
            if (!gameLoop.isAutoTickRunning()) {
                gameLoop.startAutoTick();
            }
        } else {
            gameLoop.stopAutoTick();
        }
    }

    private static boolean pausesGameplay(MenuType menu) {
        return menu == MenuType.PAUSE
                || menu == MenuType.MATCH_RESTART
                || menu == MenuType.MATCH_RESTART_WAIT
                || menu == MenuType.MATCH_RESULT
                || menu == MenuType.QUICK_MESSAGES;
    }

    private void updateInputProcessors() {
        InputMultiplexer mux = new InputMultiplexer();
        if (activeOverlay != null) {
            mux.addProcessor(activeOverlay.stage());
        }
        if (activeScreen != null) {
            mux.addProcessor(activeScreen.stage());
        }
        // Block lawn input while a gameplay-pausing overlay is up.
        boolean blockWorld = lastContext != null && pausesGameplay(lastContext.menu);
        if (gameWorldInput != null && isGameScreen() && !blockWorld) {
            mux.addProcessor(gameWorldInput);
        }
        mux.addProcessor(toastStage);
        mux.addProcessor(topBar.stage());
        Gdx.input.setInputProcessor(mux);
    }

    public void showToast(String message) {
        toastLabel.setText(message == null ? "" : message);
    }

    public void act(float deltaSeconds) {
        if (activeScreen != null) {
            activeScreen.act(deltaSeconds);
        }
        if (activeOverlay != null) {
            activeOverlay.act(deltaSeconds);
        }
        topBar.act(deltaSeconds);
        toastStage.act(deltaSeconds);
    }

    public void draw() {
        if (shouldDrawScreenLayer()) {
            activeScreen.stage().getViewport().apply();
            activeScreen.stage().draw();
        }
        if (activeOverlay != null) {
            activeOverlay.stage().getViewport().apply();
            activeOverlay.stage().draw();
        }
        topBar.draw();
        toastStage.getViewport().apply();
        toastStage.draw();
    }

    public void resize(int width, int height) {
        if (activeScreen != null) {
            activeScreen.resize(width, height);
        }
        if (activeOverlay != null) {
            activeOverlay.resize(width, height);
        }
        topBar.resize(width, height);
        toastStage.getViewport().update(width, height, true);
    }

    public UiViewContext lastContext() {
        return lastContext;
    }

    public boolean isGameScreen() {
        return lastContext != null && lastContext.screen == ScreenType.GAME;
    }

    public void setGameWorldInput(InputProcessor processor) {
        gameWorldInput = processor;
        updateInputProcessors();
    }

    private boolean shouldDrawScreenLayer() {
        return activeScreen != null && lastContext != null && lastContext.screen != ScreenType.GAME;
    }

<<<<<<< Updated upstream
    private void updateInputProcessors() {
        InputMultiplexer mux = new InputMultiplexer();
        if (activeOverlay != null) {
            mux.addProcessor(activeOverlay.stage());
        }
        if (gameWorldInput != null && isGameScreen()) {
            mux.addProcessor(gameWorldInput);
        }
        if (activeScreen != null) {
            mux.addProcessor(activeScreen.stage());
        }
        mux.addProcessor(toastStage);
        mux.addProcessor(topBar.stage());
        Gdx.input.setInputProcessor(mux);
    }

=======
>>>>>>> Stashed changes
    private static String prettyName(ScreenType type) {
        return switch (type) {
            case REGISTER -> "Register";
            case LOGIN -> "Login";
            case MAIN -> "Main Menu";
            case LEVEL_SELECTOR -> "Level Select";
            case GAME -> "Game";
            case COLLECTION -> "Collection";
            case LEADERBOARD -> "Leaderboard";
            case GREEN_HOUSE -> "Greenhouse";
            case SHOP -> "Shop";
        };
    }

    @Override
    public void dispose() {
        for (UiScreen screen : screens.values()) {
            screen.dispose();
        }
        java.util.IdentityHashMap<UiScreen, Boolean> seen = new java.util.IdentityHashMap<>();
        for (UiScreen overlay : overlays.values()) {
            if (seen.put(overlay, Boolean.TRUE) == null) {
                overlay.dispose();
            }
        }
        topBar.dispose();
        toastStage.dispose();
    }
}
