package controller;

import model.core.EventBus;
import model.core.ReadOnlyGameState;
import model.data.content.minigame.MiniGameType;
import model.data.plant.PlantType;
import model.data.sun.Sun;
import model.data.zombie.Zombie;
import model.event.events.*;
import model.rule.LevelRule;
import model.rule.SessionConfig;
import model.rule.SessionContext;
import model.rule.rules.chapter.BigWaveBeachRules;
import model.rule.rules.chapter.DarkAgesRules;
import model.storage.StorageManager;
import shared.izombie.IZombiePlayMode;

public class AppEventHandler {

    private final EventBus eventBus;
    private final StorageManager storage;
    private final ControllerManager controller;
    private boolean necromancerWarned;
    private boolean beachSpawnWarned;

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

        if (event.waveNumber == 1) {
            necromancerWarned = false;
            beachSpawnWarned = false;
        }

        if (event.isFinalWave) {
            controller.sendMessage("The final wave has come.");
        } else {
            controller.sendMessage("Wave " + event.waveNumber + " started.");
        }

        if (shouldWarnNecromancer()) {
            controller.showAnnouncement("Beware the Necromancer!");
            necromancerWarned = true;
            return;
        }
        if (shouldWarnBeachSpawn()) {
            controller.showAnnouncement("Zombies are emerging from the beach!");
            beachSpawnWarned = true;
            return;
        }

        if (event.isFinalWave) {
            controller.showAnnouncement("The final wave is approaching!");
        } else if (event.waveNumber == 1) {
            controller.showAnnouncement("A huge wave of zombies is approaching!");
        } else {
            controller.showAnnouncement("Wave " + event.waveNumber + " is approaching!");
        }
    }

    private boolean shouldWarnNecromancer() {
        if (necromancerWarned) {
            return false;
        }
        SessionContext context = controller.getModel().getPlayContext();
        if (context == null || context.getRuleEngine() == null) {
            return false;
        }
        for (LevelRule rule : context.getRuleEngine().getActiveRules()) {
            if (rule instanceof DarkAgesRules darkAges && darkAges.hasPendingNecromancySpawn()) {
                return true;
            }
        }
        return false;
    }

    private boolean shouldWarnBeachSpawn() {
        if (beachSpawnWarned) {
            return false;
        }
        SessionContext context = controller.getModel().getPlayContext();
        if (context == null || context.getRuleEngine() == null) {
            return false;
        }
        for (LevelRule rule : context.getRuleEngine().getActiveRules()) {
            if (rule instanceof BigWaveBeachRules beach && beach.hasPendingBeachSpawn()) {
                return true;
            }
        }
        return false;
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
        if (event == null || event.sun == null) {
            return;
        }
        if (!event.sun.isFalling) {
            autoCollectZombieSunInCouch(event.sun);
        }
        controller.sendMessage("New " + event.sun.type.name() + " sun is dropping at position (" + event.sun.position.x
                + ", " + event.sun.targetY + ")");
    }

    private void onSunLanded(SunLandedEvent event) {
        if (event == null || event.sun == null) {
            return;
        }
        autoCollectZombieSunInCouch(event.sun);
        controller.sendMessage(
                "Sun reached the ground at position (" + event.sun.position.x + ", " + event.sun.targetY + ")");
    }

    private void autoCollectZombieSunInCouch(Sun sun) {
        if (sun.generatorPlant != null) {
            return;
        }
        ReadOnlyGameState state = controller.getModel().getState();
        SessionContext context = controller.getModel().getPlayContext();
        if (state == null || context == null || context.getConfig() == null) {
            return;
        }
        SessionConfig config = context.getConfig();
        if (config.iZombiePlayMode != IZombiePlayMode.COUCH) {
            return;
        }
        if (!state.isDualSunMode()) {
            return;
        }
        int index = state.getSunDrops().indexOf(sun);
        if (index >= 0) {
            controller.getModel().collectSun(index);
        }
    }
}
