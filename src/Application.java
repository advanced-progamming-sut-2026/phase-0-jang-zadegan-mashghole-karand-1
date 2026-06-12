import controller.ControllerManager;
import model.ModelManager;
import model.core.EventBus;
import model.core.GameLoop;
import model.storage.JsonStorageManager;
import view.ViewManager;

public class Application {
    public static void main(String[] args) {
        EventBus eventBus = new EventBus();
        GameLoop gameLoop = new GameLoop();

        JsonStorageManager storageManager = new JsonStorageManager();
        ModelManager model = new ModelManager(storageManager, eventBus);

        ViewManager view = new ViewManager();

        ControllerManager controller = new ControllerManager(model, view, eventBus, gameLoop);

        controller.start();
    }

    public void updateOnTick() {
    }

    public void run() {
    }

    public void shutdown() {
    }
}
