package app;

import com.badlogic.gdx.Gdx;

import controller.ControllerManager;
import model.ModelManager;
import model.core.EventBus;
import model.core.GameLoop;
import model.core.ReadOnlyGameState;
import model.storage.SqlStorageManager;
import view.ScreenType;
import view.gdx.AssetContext;
import view.gdx.ui.DesktopViewFacade;
import view.gdx.ui.UiNavigator;

public final class DesktopApp {
    private final ModelManager model;
    private final ControllerManager controller;
    private final GameLoop gameLoop;
    private final SqlStorageManager storage;
    private final UiNavigator navigator;
    private final DesktopViewFacade viewFacade;

    private DesktopApp(ModelManager model, ControllerManager controller, GameLoop gameLoop,
            SqlStorageManager storage, UiNavigator navigator, DesktopViewFacade viewFacade) {
        this.model = model;
        this.controller = controller;
        this.gameLoop = gameLoop;
        this.storage = storage;
        this.navigator = navigator;
        this.viewFacade = viewFacade;
    }

    public static DesktopApp create(AssetContext assets) {
        EventBus eventBus = new EventBus();
        GameLoop gameLoop = new GameLoop();
        SqlStorageManager storage = new SqlStorageManager();
        ModelManager model = new ModelManager(storage, eventBus);
        ControllerManager controller = new ControllerManager(model, eventBus, gameLoop, storage);

        UiNavigator navigator = new UiNavigator(gameLoop);
        DesktopViewFacade viewFacade = new DesktopViewFacade(navigator, assets);
        controller.setView(viewFacade);
        viewFacade.initialize();
        controller.start();

        Gdx.app.log("DesktopApp", "started screen=" + controller.getCurrentScreen());
        return new DesktopApp(model, controller, gameLoop, storage, navigator, viewFacade);
    }

    public ModelManager model() {
        return model;
    }

    public ControllerManager controller() {
        return controller;
    }

    public GameLoop gameLoop() {
        return gameLoop;
    }

    public UiNavigator navigator() {
        return navigator;
    }

    public ReadOnlyGameState gameState() {
        return model.getStateView();
    }

    public boolean isGameScreen() {
        return controller.getCurrentScreen() == ScreenType.GAME;
    }

    public void dispose() {
        gameLoop.stopAutoTick();
        try {
            storage.saveProgress();
        } catch (RuntimeException e) {
            Gdx.app.error("DesktopApp", "Failed to save progress", e);
        }
        navigator.dispose();
    }
}
