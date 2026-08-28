import controller.ControllerManager;
import controller.InputHandler;
import model.ModelManager;
import model.core.EventBus;
import model.core.GameLoop;
import model.storage.SqlStorageManager;
import network.NetworkAuthBridge;
import network.NetworkConfig;
import network.NetworkSession;
import view.ViewManager;
import view.renderer.*;

public class Application {
    public static void main(String[] args) {
        EventBus eventBus = new EventBus();
        GameLoop gameLoop = new GameLoop();

        SqlStorageManager storageManager = new SqlStorageManager();
        ModelManager model = new ModelManager(storageManager, eventBus);

        ControllerManager controller = new ControllerManager(model, eventBus, gameLoop, storageManager);
        NetworkSession networkSession = new NetworkSession(NetworkConfig.fromEnv());
        controller.setNetworkAuth(new NetworkAuthBridge(networkSession, storageManager));

        InputHandler inputHandler = new InputHandler(controller);

        Renderer renderer = new ConsoleRenderer();

        ViewManager view = new ViewManager(renderer, inputHandler);

        controller.setView(view);

        view.initialize();
        controller.start();
        view.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            storageManager.saveProgress();
            view.stop();
        }));
    }

    public void updateOnTick() {
    }

    public void run() {
    }

    public void shutdown() {
    }
}
