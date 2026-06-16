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
        // InputHandler inputHandler = new InputHandler(view); handle input handler
        // InputListener inputListener = new InputListener(); // add input handler
        // ViewManager view = new ViewManager(renderer , inputListener);
        // view.start();

        // ControllerManager controller = new ControllerManager(model, view, eventBus, gameLoop);
        // controller.start();

        // test code

        // model.placePlant(2, 3, "Peashooter", 1);

        // model.addTestSun();

        // model.spawnZombie(2, "ZombieTutorialDefault");

        // eventBus.subscribe(PlantPlacedEvent.class, e -> System.out.println("Plant
        // placed: " + e.plant.type.name));
        // eventBus.subscribe(ZombieDiedEvent.class, e -> System.out.println("Zombie
        // died!"));
        // eventBus.subscribe(ZombieSpawnedEvent.class, e -> System.out.println("Zombie
        // spawned!"));

        // int i = 0;
        // while (true) {
        // System.out.println("\n--- TICK " + (i + 1) + " ---");
        // model.tick();
        // System.out.println("Zombie X: " +
        // (model.getState().zombies.isEmpty() ? "dead" :
        // model.getState().zombies.get(0).position.x));
        // System.out.println("Zombie HP: " +
        // (model.getState().zombies.isEmpty() ? "dead" :
        // model.getState().zombies.get(0).hp));
        // System.out.println("Plants alive: " + model.getState().plants.size());
        // System.out.println("Plant HP: " +
        // (model.getState().plants.isEmpty() ? "dead" :
        // model.getState().plants.get(0).hp));

        // if (model.getState().zombies.isEmpty()) {
        // break;
        // }
        // i++;
        // }

    }

    public void updateOnTick() {
    }

    public void run() {
    }

    public void shutdown() {
    }
}
