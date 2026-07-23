package model.rule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import model.core.EventBus;
import model.core.GameState;
import model.data.plant.Plant;
import model.data.plant.PlantType;
import model.data.zombie.Zombie;

public class RuleEngine {
    private final List<LevelRule> activeRules = new ArrayList<>();

    public void addRule(LevelRule rule) {
        if (rule != null) {
            activeRules.add(rule);
        }
    }

    public void addRules(List<LevelRule> rules) {
        if (rules != null) {
            activeRules.addAll(rules);
        }
    }

    public void clearRules() {
        activeRules.clear();
    }

    public List<LevelRule> getActiveRules() {
        return Collections.unmodifiableList(activeRules);
    }

    public void onSessionStart(SessionContext context, GameState state, EventBus bus) {
        for (LevelRule rule : activeRules) {
            rule.onSessionStart(context, state, bus);
        }
    }

    public void preTick(SessionContext context, GameState state, EventBus bus) {
        for (LevelRule rule : activeRules) {
            rule.preTick(context, state, bus);
        }
    }

    public void postTick(SessionContext context, GameState state, EventBus bus) {
        for (LevelRule rule : activeRules) {
            rule.postTick(context, state, bus);
        }
    }

    public void onWaveStart(SessionContext context, GameState state, EventBus bus) {
        for (LevelRule rule : activeRules) {
            rule.onWaveStart(context, state, bus);
        }
    }

    public void onWaveEnd(SessionContext context, GameState state, EventBus bus) {
        for (LevelRule rule : activeRules) {
            rule.onWaveEnd(context, state, bus);
        }
    }

    public void onPlantPlaced(Plant plant, GameState state) {
        for (LevelRule rule : activeRules) {
            rule.onPlantPlaced(plant, state);
        }
    }

    public void onPlantDied(Plant plant, GameState state, EventBus bus) {
        for (LevelRule rule : activeRules) {
            rule.onPlantDied(plant, state, bus);
        }
    }

    public void onZombieSpawned(Zombie zombie, SessionContext context, GameState state) {
        for (LevelRule rule : activeRules) {
            rule.onZombieSpawned(zombie, context, state);
        }
    }

    public void onZombieDied(Zombie zombie, GameState state, EventBus bus) {
        for (LevelRule rule : activeRules) {
            rule.onZombieDied(zombie, state, bus);
        }
    }

    public void onSunCollected(model.data.sun.Sun sun, GameState state, EventBus bus) {
        for (LevelRule rule : activeRules) {
            rule.onSunCollected(sun, state, bus);
        }
    }

    public boolean shouldDropSkySun() {
        for (LevelRule rule : activeRules) {
            if (!rule.shouldDropSkySun()) {
                return false;
            }
        }
        return true;
    }

    public boolean freezeProjectilesEnabled() {
        for (LevelRule rule : activeRules) {
            if (!rule.freezeProjectilesEnabled()) {
                return false;
            }
        }
        return true;
    }

    public boolean skipsPlantSelection() {
        for (LevelRule rule : activeRules) {
            if (rule.skipsPlantSelection()) {
                return true;
            }
        }
        return false;
    }

    public boolean shouldSpawnWaves() {
        for (LevelRule rule : activeRules) {
            if (!rule.shouldSpawnWaves()) {
                return false;
            }
        }
        return true;
    }

    public boolean lawnMowersEnabled() {
        for (LevelRule rule : activeRules) {
            if (!rule.lawnMowersEnabled()) {
                return false;
            }
        }
        return true;
    }

    public boolean usesSunCurrency() {
        for (LevelRule rule : activeRules) {
            if (!rule.usesSunCurrency()) {
                return false;
            }
        }
        return true;
    }

    public boolean canPlaceZombies() {
        for (LevelRule rule : activeRules) {
            if (rule.canPlaceZombies()) {
                return true;
            }
        }
        return false;
    }

    public boolean canPlant(PlantType type, int row, int col, GameState state, SessionContext context) {
        for (LevelRule rule : activeRules) {
            if (!rule.canPlant(type, row, col, state, context)) {
                return false;
            }
        }
        return true;
    }

    public boolean canPlaceZombie(model.data.zombie.ZombieType type, int row, int col, GameState state,
            SessionContext context) {
        for (LevelRule rule : activeRules) {
            if (!rule.canPlaceZombie(type, row, col, state, context)) {
                return false;
            }
        }
        return true;
    }

    public boolean winsOnWaveClear() {
        for (LevelRule rule : activeRules) {
            if (!rule.winsOnWaveClear()) {
                return false;
            }
        }
        return true;
    }
}