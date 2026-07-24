package model.event;

import model.core.EventBus;
import model.core.GameState;
import model.event.events.*;
import model.quest.Quest;
import model.quest.QuestTracker;
import model.rule.RuleEngine;
import model.rule.SessionContext;
import model.storage.StorageManager;
import model.storage.user.User;

import java.util.Objects;

public class GameEventHub {
    private final EventBus eventBus;
    private final RuleEngine ruleEngine;
    private final QuestTracker questTracker;
    private final GameState gameState;
    private final StorageManager storage;

    private SessionContext context;
    private boolean registered;


    public GameEventHub(EventBus eventBus, RuleEngine ruleEngine, QuestTracker questTracker, GameState gameState, StorageManager storage) {
        this.eventBus = eventBus;
        this.ruleEngine = ruleEngine;
        this.questTracker = questTracker;
        this.gameState = gameState;
        this.storage = storage;
    }

    public void register() {
        if(registered) {
            return;
        }
        registered = true;

        eventBus.subscribe(PlantDiedEvent.class, this::onPlantDied);
        eventBus.subscribe(PlantPlacedEvent.class, this::onPlantPlaced);
        eventBus.subscribe(ZombieDiedEvent.class, this::onZombieDied);
        eventBus.subscribe(GlowingZombieDiedEvent.class, this::onGlowingZombieDied);
        eventBus.subscribe(ZombieSpawnedEvent.class, this::onZombieSpawned);
        eventBus.subscribe(WaveStartedEvent.class, this::onWaveStart);
        eventBus.subscribe(WaveCompleteEvent.class, this::onWaveComplete);
        eventBus.subscribe(SunCollectedEvent.class, this::onSunCollected);
        eventBus.subscribe(ZombieDroppedLootEvent.class, this::onZombieDroppedLoot);
        eventBus.subscribe(LevelCompleteEvent.class, this::onLevelComplete);
        eventBus.subscribe(GameOverEvent.class,this::onGameOver);


    }


    public void bindSession(SessionContext context){
        this.context = context;
    }

    public void unbindSession(){
        this.context = null;
    }

    private void onPlantDied(PlantDiedEvent e){
        if(e == null || e.plant == null) return;
        gameState.removePlant(e.plant);
        ruleEngine.onPlantDied(e.plant,gameState,eventBus);
        notifyQuests(e);
    }

    private void onPlantPlaced(PlantPlacedEvent e){
        if(e == null || e.plant == null) return;
        ruleEngine.onPlantPlaced(e.plant,gameState);
        notifyQuests(e);
    }

    private void onZombieDied(ZombieDiedEvent e){
        if(e == null || e.zombie == null) return;
        ruleEngine.onZombieDied(e.zombie,gameState,eventBus);
        notifyQuests(e);
    }

    private void onGlowingZombieDied(GlowingZombieDiedEvent e){
        if(e == null || e.zombie == null) return;
        ruleEngine.onZombieDied(e.zombie,gameState,eventBus);
        notifyQuests(new ZombieDiedEvent(e.zombie, e.zombie.lastHitBy));
    }

    private void onZombieSpawned(ZombieSpawnedEvent e){
        if(e == null || e.zombie == null) return;
        ruleEngine.onZombieSpawned(e.zombie, context, gameState);
    }

    private void onWaveStart(WaveStartedEvent e){
        if(context == null) return;
        ruleEngine.onWaveStart(context, gameState, eventBus);
        notifyQuests(e);
    }

    private void onWaveComplete(WaveCompleteEvent e){
        if(context == null) return;
        ruleEngine.onWaveEnd(context, gameState, eventBus);
    }

    private void onSunCollected(SunCollectedEvent e){
        if(e == null || e.sun == null) return;
        ruleEngine.onSunCollected(e.sun, gameState, eventBus);
        notifyQuests(e);
    }

    private void onZombieDroppedLoot(ZombieDroppedLootEvent e){
        User user = storage.getCurrentUser();
        if(user == null || e== null) return;
        switch (e.type){
            case COIN -> {
                user.coins += e.amount;
                storage.updateUserProfile(user);
            }
            case DIAMOND -> {
                user.gems += e.amount;
                storage.updateUserProfile(user);
            }
            case POT ->{
                if(user.greenhouse == null) return;
                user.greenhouse.unlockSlot();
                storage.updateUserProfile(user);
            }
        }
    }

    private void onLevelComplete(LevelCompleteEvent e){
        notifyQuests(e);
    }

    private void onGameOver(GameOverEvent e){
        notifyQuests(e);
    }

    private void notifyQuests(Object event){
        questTracker.onGameEvent(event,gameState,context);
    }
}
