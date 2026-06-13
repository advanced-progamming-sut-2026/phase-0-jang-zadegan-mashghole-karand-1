import controller.ControllerManager;
import controller.InputHandler;
import model.ModelManager;
import model.core.EventBus;
import model.core.GameLoop;
import model.storage.JsonStorageManager;
import view.ConsoleRenderer;
import view.InputListener;
import view.Renderer;
import view.ViewManager;

public class Application {
    public static void main(String[] args) {
        EventBus eventBus = new EventBus();
        GameLoop gameLoop = new GameLoop();

        JsonStorageManager storageManager = new JsonStorageManager();
        ModelManager model = new ModelManager(storageManager, eventBus);
        Renderer renderer = new ConsoleRenderer();
//        InputHandler inputHandler = new InputHandler(view); handle input handler
        InputListener inputListener = new InputListener(); // add input handler
        ViewManager view = new ViewManager(renderer , inputListener);
        view.start();

//        ControllerManager controller = new ControllerManager(model, view, eventBus, gameLoop);
//        controller.start(); check
    }

    public void updateOnTick() {
    }

    public void run() {
    }

    public void shutdown() {
    }
}
