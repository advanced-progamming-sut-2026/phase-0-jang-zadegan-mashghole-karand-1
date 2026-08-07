package model.rule.rules.specialLevel;

import java.util.List;

import model.core.EventBus;
import model.core.GameState;
import model.data.plant.PlantCategory;
import model.data.plant.PlantTag;
import model.data.plant.PlantType;
import model.rule.LevelRule;
import model.rule.SessionContext;

public class PlantWhatYouGetRules implements LevelRule {
    private boolean wavesStarted;

    @Override
    public void onSessionStart(SessionContext context, GameState state, EventBus bus) {
        wavesStarted = false;
    }

    @Override
    public boolean shouldDropSkySun() {
        return false;
    }

    @Override
    public boolean shouldSpawnWaves() {
        return wavesStarted;
    }

    @Override
    public boolean canPlant(PlantType type, int row, int col, GameState state, SessionContext context) {
        return !wavesStarted;
    }

    @Override
    public boolean canSelectPlant(PlantType type, List<PlantType> alreadySelected) {
        if (type == null) {
            return false;
        }
        if (type.category == PlantCategory.SUN_PRODUCER) {
            return false;
        }
        return !type.hasTag(PlantTag.SUN);
    }

    @Override
    public boolean startDeferredWaves() {
        if (wavesStarted) {
            return false;
        }
        wavesStarted = true;
        return true;
    }

    public boolean hasWavesStarted() {
        return wavesStarted;
    }
}
