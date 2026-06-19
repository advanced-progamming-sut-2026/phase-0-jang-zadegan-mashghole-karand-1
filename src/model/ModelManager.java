package model;

import model.core.EventBus;
import model.core.GameState;
import model.core.Position;
import model.data.plant.Plant;
import model.data.plant.PlantType;
import model.data.sun.Sun;
import model.data.sun.SunType;
import model.data.zombie.Zombie;
import model.data.zombie.ZombieType;
import model.events.PlantPlacedEvent;
import model.events.SunCollectedEvent;
import model.events.ZombieSpawnedEvent;
import model.storage.StorageManager;
import model.systems.CombatSystem;
import model.systems.MovementSystem;
import model.systems.PlantAbilitySystem;
import model.systems.SunSpawnSystem;
import model.systems.SunSystem;

public class ModelManager {
    private final GameState state;
    private final EventBus eventBus;
    private final StorageManager storage;

    private final MovementSystem movementSystem;
    private final CombatSystem combatSystem;
    private final PlantAbilitySystem plantAbilitySystem;
    private final SunSpawnSystem sunSpawnSystem;
    private final SunSystem sunSystem;

    public ModelManager(StorageManager storage, EventBus eventBus) {
        this.state = new GameState();
        this.eventBus = eventBus;
        this.storage = storage;

        this.movementSystem = new MovementSystem();
        this.combatSystem = new CombatSystem(eventBus);
        this.plantAbilitySystem = new PlantAbilitySystem();
        this.sunSpawnSystem = new SunSpawnSystem(eventBus);
        this.sunSystem = new SunSystem(eventBus);
    }

    public void tick() {
        if (state.gameOver) {
            return;
        }

        plantAbilitySystem.update(state, eventBus);

        sunSpawnSystem.update(state);
        sunSystem.update(state);

        movementSystem.update(state);

        combatSystem.update(state, eventBus);

        // we should move to event queue processing if we faced any problems with the
        // current setup
        // eventBus.processEvents();
    }

    public GameState getState() {
        return state;
    }

    public boolean placePlant(int row, int col, String plantName, int level) {
        if (row < 0 || row >= GameState.GRID_ROWS)
            return false;
        if (col < 0 || col >= GameState.GRID_COLS)
            return false;

        if (state.getPlantAt(row, col) != null)
            return false;

        PlantType type = PlantType.fromName(plantName);
        if (type == null)
            return false;

        if (state.sunAmount < type.baseStats.cost)
            return false;

        Plant plant = new Plant(type, row, col, level, eventBus);
        state.plants.add(plant);
        state.sunAmount -= plant.cost;

        eventBus.publish(new PlantPlacedEvent(plant));
        return true;
    }

    public void spawnZombie(int row, String zombieTypeName) {
        ZombieType type = ZombieType.fromName(zombieTypeName);
        if (type == null)
            return;
        Zombie zombie = new Zombie(type, row, GameState.GRID_COLS - 1,
                new Position(GameState.SCREEN_WIDTH, GameState.CELL_HEIGHT * row + (GameState.CELL_HEIGHT / 2)),
                eventBus);
        state.zombies.add(zombie);
        eventBus.publish(new ZombieSpawnedEvent(zombie));
    }

    public boolean collectSun(int index) {
        return sunSystem.collectSun(state, eventBus, index);
    }
}