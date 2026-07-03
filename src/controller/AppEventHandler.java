package controller;

import model.core.EventBus;
import model.events.ZombieUnlockedEvent;
import model.storage.StorageManager;

public class AppEventHandler {

    private final EventBus eventBus;
    private final StorageManager storage;

    public AppEventHandler(EventBus eventBus, StorageManager storage) {
        this.eventBus = eventBus;
        this.storage = storage;
    }

    public void register() {
        registerNewsHandlers();
    }

    private void registerNewsHandlers() {
        eventBus.subscribe(ZombieUnlockedEvent.class, this::onZombieUnlocked);
    }

    private void onZombieUnlocked(ZombieUnlockedEvent event) {
        storage.addNews("You unlocked a new zombie: " + event.zombieType.name);
    }
}
