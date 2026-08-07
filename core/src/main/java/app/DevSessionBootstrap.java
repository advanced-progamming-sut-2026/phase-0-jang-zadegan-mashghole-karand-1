package app;

import java.util.List;

import com.badlogic.gdx.Gdx;

import controller.ControllerManager;
import model.ModelManager;
import model.core.EventBus;
import model.core.GameLoop;
import model.data.content.chapter.ChapterCatalog;
import model.data.content.chapter.ChapterType;
import model.data.plant.PlantStats;
import model.data.plant.PlantType;
import model.data.wave.LevelConfig;
import model.data.zombie.ZombieType;
import model.rule.SessionConfig;
import model.storage.SqlStorageManager;
import view.ScreenType;

public final class DevSessionBootstrap {
    public static final String DEMO_USER = "player";
    public static final String DEMO_PASSWORD = "password";

    private DevSessionBootstrap() {
    }

    public static SessionRuntime start() {
        EventBus eventBus = new EventBus();
        GameLoop gameLoop = new GameLoop();
        SqlStorageManager storage = new SqlStorageManager();
        ModelManager model = new ModelManager(storage, eventBus);
        ControllerManager controller = new ControllerManager(model, eventBus, gameLoop, storage);

        gameLoop.setOnTickHandler(() -> {
            model.tick();
            model.getState().totalTicks++;
        });

        storage.loadProgress();
        if (!storage.isLoggedIn()) {
            boolean ok = storage.login(DEMO_USER, DEMO_PASSWORD, true);
            if (!ok) {
                throw new IllegalStateException(
                        "Dev login failed for '" + DEMO_USER + "'. Is the demo user seeded in data/game.db?");
            }
        }
        controller.start();

        LevelConfig level = ChapterCatalog.getLevel(ChapterType.ANCIENT_EGYPT, 1);
        if (level == null) {
            throw new IllegalStateException("Missing Ancient Egypt level 1 config.");
        }

        List<PlantType> selected = List.of(
                PlantType.PeaShooter,
                PlantType.Sunflower,
                PlantType.Wall_nut);

        SessionConfig config = SessionConfig.builder()
                .levelConfig(level)
                .selectedPlants(selected)
                .build();

        model.startSession(config);
        storage.recordGamePlayed();
        controller.setScreen(ScreenType.GAME);

        seedStarterBoard(model);
        Gdx.app.log("DevSessionBootstrap",
                "Started Egypt L1 as " + DEMO_USER
                        + " plants=" + model.getState().getPlants().size()
                        + " zombies=" + model.getState().getZombies().size());

        return new SessionRuntime(model, controller, gameLoop, storage);
    }

    private static void seedStarterBoard(ModelManager model) {
        int level = PlantStats.DEFAULT_LEVEL;
        model.placePlant(2, 1, PlantType.Sunflower, level, false);
        model.placePlant(2, 2, PlantType.PeaShooter, level, false);
        model.placePlant(1, 2, PlantType.PeaShooter, level, false);
        model.placePlant(3, 2, PlantType.PeaShooter, level, false);
        model.placePlant(2, 0, PlantType.Wall_nut, level, false);
        model.cheatSpawnZombie(2, 8, ZombieType.BASIC);
        model.cheatSpawnZombie(1, 8, ZombieType.CONE_HEAD);
    }
}
