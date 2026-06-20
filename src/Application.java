import controller.ControllerManager;
import controller.InputHandler;
import model.ModelManager;
import model.core.EventBus;
import model.core.GameLoop;
import model.events.SunCollectedEvent;
import model.events.SunProducedEvent;
import model.storage.JsonStorageManager;
import view.ConsoleRenderer;
import view.Renderer;
import view.ViewManager;

public class Application {
    public static void main(String[] args) {
        EventBus eventBus = new EventBus();
        GameLoop gameLoop = new GameLoop();

        JsonStorageManager storageManager = new JsonStorageManager();
        ModelManager model = new ModelManager(storageManager, eventBus);

        ControllerManager controller = new ControllerManager(model, eventBus, gameLoop);

        InputHandler inputHandler = new InputHandler(controller);

        Renderer renderer = new ConsoleRenderer();

        ViewManager view = new ViewManager(renderer, inputHandler);

        controller.setView(view);

        view.start();

        controller.start();
    }

    public void updateOnTick() {
    }

    public void run() {
    }

    public void shutdown() {
    }
}
