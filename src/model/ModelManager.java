package model;

import java.util.List;

import model.board.Tile;
import model.core.EventBus;
import model.core.GameState;
import model.core.ReadOnlyGameState;
import model.data.plant.Plant;
import model.data.plant.PlantStats;
import model.data.plant.PlantType;
import model.data.plant.abilities.config.PlantAbilityConfig;
import model.data.plant.abilities.runtime.PlantTransformAbility;
import model.data.seed.PlantSeedDrop;
import model.data.vase.Vase;
import model.data.zombie.Zombie;
import model.data.zombie.ZombieType;
import model.events.GlowingZombieDiedEvent;
import model.events.GameOverEvent;
import model.events.LevelCompleteEvent;
import model.events.PlantDiedEvent;
import model.events.PlantPlacedEvent;
import model.events.SeedCollectedEvent;
import model.events.SunCollectedEvent;
import model.events.WaveCompleteEvent;
import model.events.WaveStartedEvent;
import model.events.ZombieDiedEvent;
import model.events.ZombieDroppedLootEvent;
import model.events.ZombieSpawnedEvent;
import model.core.Position;
import model.data.content.minigame.IZombieShop;
import model.quest.QuestTracker;
import model.rule.LevelRule;
import model.rule.RuleEngine;
import model.rule.SessionConfig;
import model.rule.SessionContext;
import model.rule.SessionRules;
import model.gameSetting.GameSetting;
import model.storage.StorageManager;
import model.storage.user.User;
import model.systems.*;

public class ModelManager {
    private final GameState state;
    private final EventBus eventBus;
    private final StorageManager storage;
    private final WaveManager waveManager;
    private final RuleEngine ruleEngine;
    private SessionContext sessionContext;

    private final MovementSystem movementSystem;
    private final CombatSystem combatSystem;
    private final PlantAbilitySystem plantAbilitySystem;
    private final ZombieAbilitySystem zombieAbilitySystem;
    private final SunSpawnSystem sunSpawnSystem;
    private final SunSystem sunSystem;
    private final SeedDropSystem seedDropSystem;
    private final EffectSystem effectSystem;
    private final QuestTracker questTracker;
    private PlantType imitatorTarget;
  
    public ModelManager(StorageManager storage, EventBus eventBus) {
        this.state = new GameState();
        this.waveManager = new WaveManager();
        this.ruleEngine = new RuleEngine();
        this.eventBus = eventBus;
        this.storage = storage;

        this.movementSystem = new MovementSystem();
        this.combatSystem = new CombatSystem(eventBus);
        this.plantAbilitySystem = new PlantAbilitySystem();
        this.zombieAbilitySystem = new ZombieAbilitySystem();
        this.sunSpawnSystem = new SunSpawnSystem(eventBus);
        this.sunSystem = new SunSystem(eventBus);
        this.seedDropSystem = new SeedDropSystem();
        this.effectSystem = new EffectSystem();
        this.questTracker = new QuestTracker(storage);

        registerEventBridges();
    }

    private void registerEventBridges() {
        eventBus.subscribe(PlantDiedEvent.class, e -> {
            if (e == null || e.plant == null) {
                return;
            }
            ruleEngine.onPlantDied(e.plant, state, eventBus);
        });
        eventBus.subscribe(WaveStartedEvent.class, e -> {
            if (sessionContext == null) {
                return;
            }
            ruleEngine.onWaveStart(sessionContext, state, eventBus);
            questTracker.onGameEvent(e, state, sessionContext);
        });
        eventBus.subscribe(WaveCompleteEvent.class, e -> {
            if (sessionContext == null) {
                return;
            }
            ruleEngine.onWaveEnd(sessionContext, state, eventBus);
        });
        eventBus.subscribe(ZombieDiedEvent.class, e -> {
            if (e == null || e.zombie == null) return;
            ruleEngine.onZombieDied(e.zombie, state, eventBus);
            questTracker.onGameEvent(e, state, sessionContext);
        });
        eventBus.subscribe(GlowingZombieDiedEvent.class, e -> {
            if (e == null || e.zombie == null) {
                return;
            }
            ruleEngine.onZombieDied(e.zombie, state, eventBus);
            questTracker.onGameEvent(
                    new ZombieDiedEvent(e.zombie, e.zombie.lastHitBy),state, sessionContext);
        });
        eventBus.subscribe(ZombieSpawnedEvent.class, e -> {
            if (e == null || e.zombie == null || sessionContext == null) {
                return;
            }
            ruleEngine.onZombieSpawned(e.zombie, sessionContext, state);
        });
        eventBus.subscribe(PlantPlacedEvent.class, e -> {
            if (e == null || e.plant == null) {
                return;
            }
            ruleEngine.onPlantPlaced(e.plant, state);
        });
        eventBus.subscribe(LevelCompleteEvent.class, e -> {
            questTracker.onGameEvent(e, state, sessionContext);
        });
        eventBus.subscribe(GameOverEvent.class, e -> {
            questTracker.onGameEvent(e, state, sessionContext);
        });
        eventBus.subscribe(SunCollectedEvent.class, e -> {
            if (e == null || e.sun == null) {
                return;
            }
            ruleEngine.onSunCollected(e.sun, state, eventBus);
        });
        eventBus.subscribe(ZombieDroppedLootEvent.class, e -> {
            User user = storage.getCurrentUser();
            if (user == null) {
                return;
            }
            switch (e.type) {
                case COIN -> {
                    user.coins += e.amount;
                    storage.updateUserProfile(user);
                }
                case DIAMOND -> {
                    user.gems += e.amount;
                    storage.updateUserProfile(user);
                }
                case POT -> {
                    if (user.greenhouse == null) {
                        return;
                    }
                    user.greenhouse.unlockSlot();
                    storage.updateUserProfile(user);
                }
            }
        });
    }

    public void tick() {
        if (state.gameOver || state.levelComplete) {
            return;
        }

        ruleEngine.preTick(sessionContext, state, eventBus);

        plantAbilitySystem.update(state, eventBus);
        for (Plant plant : state.plants) {
            plant.tickPlantFood(state, eventBus);
        }
        zombieAbilitySystem.update(state, eventBus);
        if (ruleEngine.shouldDropSkySun()) {
            sunSpawnSystem.update(state);
        }
        sunSystem.update(state);
        seedDropSystem.update(state);
        movementSystem.update(state, eventBus);
        effectSystem.update(state);
        combatSystem.update(state, eventBus, ruleEngine.freezeProjectilesEnabled());
        if (ruleEngine.shouldSpawnWaves()) {
            waveManager.update(state, eventBus, ruleEngine.winsOnWaveClear());
        }

        ruleEngine.postTick(sessionContext, state, eventBus);
    }

    public void startSession(SessionConfig config) {
        state.reset();
        state.sunAmount = config.levelConfig.startingSun;
        ruleEngine.clearRules();
        ruleEngine.addRules(SessionRules.resolve(config));

        this.sessionContext = new SessionContext(config, ruleEngine, waveManager);
        this.imitatorTarget = config.imitatorTarget;

        int difficulty = GameSetting.DEFAULT_DIFFICULTY;
        User user = storage.getCurrentUser();
        if (user != null && user.preferredSetting != null) {
            difficulty = user.preferredSetting.getDifficultyLevel();
        }
        waveManager.initialize(config.levelConfig, config.miniGameType, difficulty);

        ruleEngine.onSessionStart(sessionContext, state, eventBus);

        if (!ruleEngine.lawnMowersEnabled()) {
            for (int row = 0; row < GameState.GRID_ROWS; row++) {
                var mower = state.getBoard().getLawnMowers(row);
                if (mower != null) {
                    mower.deactivate();
                }
            }
        }
    }

    public GameState getState() {
        return state;
    }

    public ReadOnlyGameState getStateView() {
        return state;
    }

    public RuleEngine getRuleEngine() {
        return ruleEngine;
    }

    public SessionContext getPlayContext() {
        return sessionContext;
    }

    public boolean shouldDropSkySun() {
        return ruleEngine.shouldDropSkySun();
    }

    public boolean canPlant(PlantType type, int row, int col) {
        return ruleEngine.canPlant(type, row, col, state, sessionContext);
    }

    public boolean startDeferredWaves() {
        return ruleEngine.startDeferredWaves();
    }

    public boolean placePlant(int row, int col, PlantType plantType, int level) {
        return placePlant(row, col, plantType, level, true);
    }

    public boolean placePlant(int row, int col, PlantType plantType, int level, boolean chargeSun) {
        Tile tile = state.getBoard().getTile(row, col);
        if (tile == null)
            return false;
        if (!tile.isPlantable(plantType))
            return false;

        if (!ruleEngine.canPlant(plantType, row, col, state, sessionContext))
            return false;

        boolean shouldChargeSun = chargeSun && ruleEngine.usesSunCurrency();

        Plant plant = new Plant(plantType, row, col, level, eventBus);
        if (plantType == PlantType.Imitater){
            if (imitatorTarget == null || imitatorTarget == PlantType.Imitater)return false;
            for (PlantAbilityConfig a : plant.abilities){
                if (a instanceof PlantTransformAbility){
                    ((PlantTransformAbility) a).setTargetPlant(imitatorTarget);
                }
            }
        }
        if (shouldChargeSun && state.sunAmount < plant.cost)
            return false;

        state.addPlant(plant);
        if (shouldChargeSun) {
            state.sunAmount -= plant.cost;
        }
        if (sessionContext != null && sessionContext.hasHeldSeed(plantType)) {
            sessionContext.consumeHeldSeed(plantType);
        }

        eventBus.publish(new PlantPlacedEvent(plant));
        return true;
    }

    public boolean placeConveyorPlant(int row, int col) {
        if (sessionContext == null || !sessionContext.isConveyorMode()) {
            return false;
        }
        PlantType offered = sessionContext.getConveyorOffer();
        if (offered == null) {
            return false;
        }
        int level = PlantStats.DEFAULT_LEVEL;
        if (storage.getCurrentUser() != null) {
            level = storage.getCurrentUser().getPlantLevel(offered);
        }
        if (!placePlant(row, col, offered, level, false)) {
            return false;
        }
        sessionContext.consumeConveyorOffer();
        return true;
    }

    public boolean collectSun(int index) {
        return sunSystem.collectSun(state, eventBus, index);
    }

    public boolean collectSunAt(int row, int col) {
        return sunSystem.collectSunAt(state, eventBus, row, col);
    }

    public Vase breakVase(int row, int col) {
        Vase vase = state.getVaseAt(row, col);
        if (vase == null) {
            return null;
        }
        vase.breakOpen(state, eventBus);
        return vase;
    }

    public boolean collectSeedAt(int row, int col) {
        if (sessionContext == null) {
            return false;
        }
        PlantSeedDrop seed = state.getSeedDropAt(row, col);
        if (seed == null) {
            return false;
        }
        sessionContext.addHeldSeed(seed.plantType);
        state.seedDrops.remove(seed);
        eventBus.publish(new SeedCollectedEvent(seed));
        return true;
    }

    public boolean placeZombie(int row, int col, ZombieType type) {
        if (!ruleEngine.canPlaceZombies()) {
            return false;
        }
        if (type == null || !IZombieShop.isPurchasable(type)) {
            return false;
        }
        if (!ruleEngine.canPlaceZombie(type, row, col, state, sessionContext)) {
            return false;
        }
        int cost = IZombieShop.getCost(type);
        if (state.sunAmount < cost) {
            return false;
        }

        Zombie zombie = new Zombie(
                type,
                row,
                col,
                new Position(
                        col * GameState.CELL_WIDTH + GameState.CELL_WIDTH / 2f,
                        row * GameState.CELL_HEIGHT + GameState.CELL_HEIGHT / 2f),
                eventBus);
        state.sunAmount -= cost;
        state.addZombie(zombie);
        eventBus.publish(new ZombieSpawnedEvent(zombie));
        return true;
    }

    public boolean pluckPlant(int row, int col) {
        Tile tile = state.getBoard().getTile(row, col);
        Plant plant = state.getPlantAt(row, col);
        if (plant == null) {
            return false;
        }
        state.removePlant(plant);
        return true;
    }

    public boolean feedPlant(int row, int col) {
        if (state.plantFoodAmount <= 0) {
            return false;
        }
        Plant plant = state.getPlantAt(row, col);
        if (plant == null) {
            return false;
        }
        plant.activatePlantFood(state, eventBus);
        state.plantFoodAmount--;
        return true;
    }

    public void addSun(int amount) {
        state.sunAmount += amount;
    }

    public void addPlantFood() {
        state.plantFoodAmount++;
    }

    public void removeCooldowns() {
        for (Plant plant : state.plants) {
            for (var ability : plant.abilities) {
                if (ability instanceof model.data.plant.abilities.runtime.PlantShootAbility shootAbility) {
                    shootAbility.resetCooldown();
                } else if (ability instanceof model.data.plant.abilities.runtime.PlantSunProduceAbility sunAbility) {
                    sunAbility.resetCooldown();
                }
            }
        }
    }

    public void releaseNuke() {
        state.zombies.clear();
    }
}