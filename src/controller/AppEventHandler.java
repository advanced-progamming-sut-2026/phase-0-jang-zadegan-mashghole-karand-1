package controller;

import model.core.EventBus;
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
    }

    private void onZombieSpawned(ZombieSpawnedEvent event) {
        if (event == null || event.zombie == null || event.zombie.type == null) {
            return;
        }
        storage.unlockZombie(event.zombie.type);
    }
}
