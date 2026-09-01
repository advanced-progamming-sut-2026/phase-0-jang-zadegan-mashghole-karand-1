package app;

import com.badlogic.gdx.Gdx;

import controller.ControllerManager;
import model.ModelManager;
import model.core.EventBus;
import model.core.GameLoop;
import model.core.ReadOnlyGameState;
import model.storage.SqlStorageManager;
import network.NetworkAuthBridge;
import network.NetworkConfig;
import network.NetworkEventRouter;
import network.NetworkSession;
import view.ScreenType;
import view.gdx.AssetContext;
import view.gdx.lawn.QuickMessageHud;
import view.gdx.ui.DesktopViewFacade;
import view.gdx.ui.UiNavigator;

public final class DesktopApp {
    private final ModelManager model;
    private final ControllerManager controller;
    private final GameLoop gameLoop;
    private final SqlStorageManager storage;
    private final UiNavigator navigator;
    private final QuickMessageHud quickMessageHud;

    private DesktopApp(ModelManager model, ControllerManager controller, GameLoop gameLoop,
            SqlStorageManager storage, UiNavigator navigator,
            QuickMessageHud quickMessageHud) {
        this.model = model;
        this.controller = controller;
        this.gameLoop = gameLoop;
        this.storage = storage;
        this.navigator = navigator;
        this.quickMessageHud = quickMessageHud;
    }

    public static DesktopApp create(AssetContext assets) {
        EventBus eventBus = new EventBus();
        GameLoop gameLoop = new GameLoop();
        SqlStorageManager storage = new SqlStorageManager();
        ModelManager model = new ModelManager(storage, eventBus);
        ControllerManager controller = new ControllerManager(model, eventBus, gameLoop, storage);

        NetworkSession networkSession = new NetworkSession(NetworkConfig.fromEnv());
        NetworkAuthBridge networkAuth = new NetworkAuthBridge(networkSession, storage);
        controller.setNetworkAuth(networkAuth);

        UiNavigator navigator = new UiNavigator(gameLoop);
        QuickMessageHud quickMessageHud = new QuickMessageHud();
        navigator.bindQuickMessageHud(quickMessageHud);
        DesktopViewFacade viewFacade = new DesktopViewFacade(navigator, assets);
        controller.setView(viewFacade);
        viewFacade.initialize();
        controller.start();

        new NetworkEventRouter(controller, model, navigator, networkSession, quickMessageHud);

        Gdx.app.log("DesktopApp", "started screen=" + controller.getCurrentScreen());
        return new DesktopApp(model, controller, gameLoop, storage, navigator, quickMessageHud);
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

    public QuickMessageHud quickMessageHud() {
        return quickMessageHud;
    }

    public void dispose() {
        gameLoop.stopAutoTick();
        try {
            controller.getSessionLifecycleController().leaveOnlineMatchIfNeeded();
        } catch (Exception ignored) {
        }
        try {
            NetworkSession net = controller.getNetworkSession();
            if (net != null) {
                net.socket().disconnect();
            }
        } catch (Exception ignored) {
        }
        try {
            storage.saveProgress();
        } catch (RuntimeException e) {
            Gdx.app.error("DesktopApp", "Failed to save progress", e);
        }
        navigator.dispose();
        if (quickMessageHud != null) {
            quickMessageHud.dispose();
        }
    }
}
