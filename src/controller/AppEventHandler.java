package controller;

import model.core.EventBus;
import model.data.content.minigame.MiniGameType;
import model.data.plant.PlantType;
import model.events.MinigameStartedEvent;
import model.events.ZombieSpawnedEvent;
import model.storage.StorageManager;

public class AppEventHandler {

    private final EventBus eventBus;
    private final StorageManager storage;

    public AppEventHandler(EventBus eventBus, StorageManager storage) {
        this.eventBus = eventBus;
        this.storage = storage;
    }

    public void register() {
        registerCollectionHandlers();
    }

    private void registerCollectionHandlers() {
        eventBus.subscribe(ZombieSpawnedEvent.class, this::onZombieSpawned);
        eventBus.subscribe(MinigameStartedEvent.class, this::onMinigameStarted);
    }

    private void onZombieSpawned(ZombieSpawnedEvent event) {
        if (event == null || event.zombie == null || event.zombie.type == null) {
            return;
        }
        storage.unlockZombie(event.zombie.type);
    }

    private void onMinigameStarted(MinigameStartedEvent event) {
        if (event == null || event.miniGameType != MiniGameType.WALLNUT_BOWLING) {
            return;
        }
        for (PlantType plant : PlantType.bowlingPlants()) {
            storage.unlockPlant(plant);
        }
    }
}
