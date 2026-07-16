package model;

import java.util.List;

import model.board.Tile;
import model.core.EventBus;
import model.core.GameState;
import model.core.ReadOnlyGameState;
import model.data.plant.Plant;
import model.data.plant.PlantType;
import model.events.PlantPlacedEvent;
import model.rule.LevelRule;
import model.rule.RuleEngine;
import model.rule.SessionConfig;
import model.rule.SessionContext;
import model.rule.rules.ChapterRules;
import model.rule.rules.MiniGameRules;
import model.rule.rules.SpecialLevelRules;
import model.storage.StorageManager;
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
    private final ZombieAbilitySystem  zombieAbilitySystem;
    private final SunSpawnSystem sunSpawnSystem;
    private final SunSystem sunSystem;

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
    }

    public void tick() {
        if (state.gameOver) {
            return;
        }

        ruleEngine.preTick(sessionContext, state, eventBus);

        plantAbilitySystem.update(state, eventBus);
        zombieAbilitySystem.update(state);
        sunSpawnSystem.update(state);
        sunSystem.update(state);
        movementSystem.update(state);
        combatSystem.update(state, eventBus);
        waveManager.update(state, eventBus);

        ruleEngine.postTick(sessionContext, state, eventBus);
    }

    public void startSession(SessionConfig config) {
        state.reset();
        state.sunAmount = config.levelConfig.startingSun;
        ruleEngine.clearRules();

        List<LevelRule> chapterRules = ChapterRules.forChapter(config.levelConfig.chapterType);
        ruleEngine.addRules(chapterRules);

        if (config.isSpecial()) {
            List<LevelRule> specialRules = SpecialLevelRules.forSpecialLevel(config.specialLevelType);
            ruleEngine.addRules(specialRules);
        }

        if (config.isMinigame()) {
            List<LevelRule> minigameRules = MiniGameRules.forMiniGame(config.miniGameType);
            ruleEngine.addRules(minigameRules);
        }

        this.sessionContext = new SessionContext(config, ruleEngine);

        waveManager.initialize(config.levelConfig);

        ruleEngine.onSessionStart(sessionContext, state, eventBus);
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
        return ruleEngine.canPlant(type, row, col, state);
    }

    public boolean placePlant(int row, int col, PlantType plantType, int level) {
        Tile tile = state.getBoard().getTile(row, col);
        if (tile == null)
            return false;
        if (!tile.isPlantable(plantType))
            return false;

        if (!ruleEngine.canPlant(plantType, row, col, state))
            return false;

        if (state.sunAmount < plantType.baseStats.cost)
            return false;

        Plant plant = new Plant(plantType, row, col, level, eventBus);
        state.plants.add(plant);
        state.sunAmount -= plant.cost;

        eventBus.publish(new PlantPlacedEvent(plant));
        return true;
    }

    public boolean collectSun(int index) {
        return sunSystem.collectSun(state, eventBus, index);
    }

    public boolean collectSunAt(int row, int col) {
        return sunSystem.collectSunAt(state, eventBus, row, col);
    }

    public boolean pluckPlant(int row, int col) {
        Plant plant = state.getPlantAt(row, col);
        if (plant == null) {
            return false;
        }
        state.plants.remove(plant);
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
        plant.activatePlantFood(state , eventBus);
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