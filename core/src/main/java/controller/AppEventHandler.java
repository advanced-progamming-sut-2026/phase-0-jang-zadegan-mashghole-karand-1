package controller;

import model.core.EventBus;
import model.data.content.chapter.ChapterType;
import model.data.content.minigame.MiniGameType;
import model.data.plant.PlantType;
import model.data.zombie.Zombie;
import model.event.events.*;
import model.rule.SessionContext;
import model.storage.StorageManager;

public class AppEventHandler {

    private final EventBus eventBus;
    private final StorageManager storage;
    private final ControllerManager controller;

    public AppEventHandler(EventBus eventBus, StorageManager storage, ControllerManager controller) {
        this.eventBus = eventBus;
        this.storage = storage;
        this.controller = controller;
    }

    public void register() {
        registerCollectionHandlers();
    }

    private void registerCollectionHandlers() {
        eventBus.subscribe(ZombieSpawnedEvent.class, this::onZombieSpawned);
        eventBus.subscribe(MinigameStartedEvent.class, this::onMinigameStarted);
        eventBus.subscribe(WaveStartedEvent.class, this::onWaveStarted);
        eventBus.subscribe(ZombieDiedEvent.class, this::onZombieDied);
        eventBus.subscribe(GlowingZombieDiedEvent.class, this::onGlowingZombieDied);
        eventBus.subscribe(SunDroppedEvent.class, this::onSunDropped);
        eventBus.subscribe(SunLandedEvent.class, this::onSunLanded);
    }

    private void onZombieSpawned(ZombieSpawnedEvent event) {
        if (event == null || event.zombie == null || event.zombie.type == null) {
            return;
        }
        controller.sendMessage("Zombie " + event.zombie.type.name + " spawned in lane " + event.zombie.row
                + " which costed " + event.zombie.type.baseStats.wavePointCost + ".");
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

    private void onWaveStarted(WaveStartedEvent event) {
        if (event == null) {
            return;
        }

        if (event.isFinalWave) {
            controller.sendMessage("The final wave has come.");
        } else {
            controller.sendMessage("Wave " + event.waveNumber + " started.");
        }

        ChapterType chapter = resolveChapter();
        if (chapter == ChapterType.DARK_AGES) {
            controller.showAnnouncement("Beware the Necromancer!");
        } else if (chapter == ChapterType.BIG_WAVE_BEACH) {
            controller.showAnnouncement("Zombies are emerging from the beach!");
        } else if (event.isFinalWave) {
            controller.showAnnouncement("The final wave is approaching!");
        } else if (event.waveNumber == 1) {
            controller.showAnnouncement("A huge wave of zombies is approaching!");
        } else {
            controller.showAnnouncement("Wave " + event.waveNumber + " is approaching!");
        }
    }

    private ChapterType resolveChapter() {
        SessionContext context = controller.getModel().getPlayContext();
        if (context == null || context.getConfig() == null || context.getConfig().levelConfig == null) {
            return null;
        }
        return context.getConfig().levelConfig.chapterType;
    }

    private void onZombieDied(ZombieDiedEvent event) {
        if (event == null || event.zombie == null || event.zombie.type == null)
            return;
        Zombie zombie = event.zombie;
        controller.sendMessage("Zombie of type " + zombie.type.name + " is dead at (" + zombie.position.x + ", "
                + zombie.position.y + ").");
    }

    private void onGlowingZombieDied(GlowingZombieDiedEvent event) {
        if (event == null)
            return;

        int amount = controller.getModel().getState().getPlantFoodAmount();
        controller.sendMessage("The glowing zombie dropped a plant food; you have " + amount + " plant foods now.");
    }

    private void onSunDropped(SunDroppedEvent event) {
        if (event == null || event.sun == null)
            return;
        controller.sendMessage("New " + event.sun.type.name() + " sun is dropping at position (" + event.sun.position.x
                + ", " + event.sun.targetY + ")");
    }

    private void onSunLanded(SunLandedEvent event) {
        if (event == null || event.sun == null)
            return;
        controller.sendMessage(
                "Sun reached the ground at position (" + event.sun.position.x + ", " + event.sun.targetY + ")");
    }
}
