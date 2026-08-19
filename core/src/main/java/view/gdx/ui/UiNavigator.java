package view.gdx.ui;

import java.util.EnumMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
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
import view.gdx.ui.screens.map.LevelSelectorScreen;
import view.gdx.ui.screens.auth.LoginScreen;
import view.gdx.ui.screens.main.MainScreen;
import view.gdx.ui.screens.auth.RegisterScreen;
import view.gdx.ui.screens.menus.Garden.GardenScreen;
import view.gdx.ui.screens.menus.GlobalTopBar;
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
        screens.put(ScreenType.MAIN , new MainScreen());
        screens.put(ScreenType.LEVEL_SELECTOR , new LevelSelectorScreen());
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

    private void syncGameLoop(ScreenType screen) {
        if (screen == ScreenType.GAME) {
            if (!gameLoop.isAutoTickRunning()) {
                gameLoop.startAutoTick();
            }
        } else {
            gameLoop.stopAutoTick();
        }
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

    private boolean shouldDrawScreenLayer() {
        return activeScreen != null && lastContext != null && lastContext.screen != ScreenType.GAME
                && lastContext.menu != MenuType.SETTING;
    }

    private void updateInputProcessors() {
        InputMultiplexer mux = new InputMultiplexer();
        if (activeOverlay != null) {
            mux.addProcessor(activeOverlay.stage());
        }
        if (activeScreen != null) {
            mux.addProcessor(activeScreen.stage());
        }
        mux.addProcessor(toastStage);
        mux.addProcessor(topBar.stage());
        Gdx.input.setInputProcessor(mux);
    }

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
        for (UiScreen overlay : overlays.values()) {
            overlay.dispose();
        }
        topBar.dispose();
        toastStage.dispose();
    }
}
