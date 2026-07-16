package model.rule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import model.core.EventBus;
import model.core.GameState;
import model.data.Grave.Grave;
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

    public void onZombieSpawned(Zombie zombie, GameState state) {
        for (LevelRule rule : activeRules) {
            rule.onZombieSpawned(zombie, state);
        }
    }

    public void onZombieDied(Zombie zombie, GameState state, EventBus bus) {
        for (LevelRule rule : activeRules) {
            rule.onZombieDied(zombie, state, bus);
        }
    }

    public void onGraveDestroyed(Grave grave, GameState state, EventBus bus) {
        for (LevelRule rule : activeRules) {
            rule.onGraveDestroyed(grave, state, bus);
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

    public boolean canPlant(PlantType type, int row, int col, GameState state) {
        for (LevelRule rule : activeRules) {
            if (!rule.canPlant(type, row, col, state)) {
                return false;
            }
        }
        return true;
    }

    public int getSpawnOffset(Zombie zombie) {
        int totalOffset = 0;
        for (LevelRule rule : activeRules) {
            totalOffset += rule.getSpawnOffset(zombie);
        }
        return totalOffset;
    }

    public float getDifficultyMultiplier() {
        float multiplier = 1.0f;
        for (LevelRule rule : activeRules) {
            multiplier *= rule.getDifficultyMultiplier();
        }
        return multiplier;
    }

    public boolean isProjectileBlocked(int row, int col, GameState state) {
        for (LevelRule rule : activeRules) {
            if (rule.isProjectileBlocked(row, col, state)) {
                return true;
            }
        }
        return false;
    }

    public boolean shouldSpawnDynamicGraves() {
        for (LevelRule rule : activeRules) {
            if (rule.shouldSpawnDynamicGraves()) {
                return true;
            }
        }
        return false;
    }
}