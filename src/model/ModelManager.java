package model;

import model.core.*;
import model.data.*;
import model.storage.*;
import model.systems.*;

public class ModelManager {
    private GameState state;
    private EventBus eventBus;
    private StorageManager storage;

    private CombatSystem combatSystem;
    private MovementSystem movementSystem;
    private ShootingSystem shootingSystem;
    private SpawnSystem spawnSystem;

    public ModelManager(StorageManager storageManager, EventBus eventBus) {
        this.state = new GameState();
        this.eventBus = eventBus;
        this.storage = storageManager;

        this.combatSystem = new CombatSystem();
        this.movementSystem = new MovementSystem();
        this.shootingSystem = new ShootingSystem();
        this.spawnSystem = new SpawnSystem();

        loadGame();
    }

    /**
     * Called every frame by game loop
     */
    public void update() {
        // if (state.gameOver || state.levelComplete)
        // return;

        // movementSystem.update(state);
        // shootingSystem.update(state, eventBus);
        // combatSystem.update(state, eventBus);
        // spawnSystem.update(state, eventBus);

        // checkGameOver();
        // checkLevelComplete();
    }

    public void saveGame() {
        // storage.save(state);
        // eventBus.publish(new GameSavedEvent());
    }

    public void loadGame() {
        // GameState loaded = storage.load();
        // if (loaded != null) {
        // this.state = loaded;
        // eventBus.publish(new GameLoadedEvent());
        // }
    }
}
