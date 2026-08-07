package model.rule.rules.minigame;

import java.util.ArrayList;
import java.util.List;

import model.core.EventBus;
import model.core.GameState;
import model.data.plant.PlantType;
import model.event.events.MinigameStartedEvent;
import model.event.events.PlantOfferedEvent;
import model.rule.ConveyorState;
import model.rule.LevelRule;
import model.rule.SessionContext;

public class WallnutBowlingRules implements LevelRule {
    private static final int MAX_PLANT_COL = 2;

    private static final List<PlantType> CONVEYOR_POOL = buildWeightedPool();

    private static List<PlantType> buildWeightedPool() {
        List<PlantType> pool = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            pool.add(PlantType.Bowling_Wall_nut);
        }
        for (int i = 0; i < 2; i++) {
            pool.add(PlantType.Bowling_Explode_o_nut);
        }
        pool.add(PlantType.Giant_Bowling_Wall_nut);
        return List.copyOf(pool);
    }

    @Override
    public boolean skipsPlantSelection() {
        return true;
    }

    @Override
    public boolean shouldDropSkySun() {
        return false;
    }

    @Override
    public boolean shouldSpawnWaves() {
        return true;
    }

    @Override
    public boolean lawnMowersEnabled() {
        return true;
    }

    @Override
    public boolean usesSunCurrency() {
        return false;
    }

    @Override
    public void onSessionStart(SessionContext context, GameState state, EventBus bus) {
        context.initializeConveyor(CONVEYOR_POOL);
        bus.publish(new MinigameStartedEvent(context.getConfig().miniGameType));
    }

    @Override
    public void preTick(SessionContext context, GameState state, EventBus bus) {
        if (!context.isConveyorMode()) {
            return;
        }
        ConveyorState conveyor = context.getConveyorState();
        if (conveyor == null || !conveyor.isActive()) {
            return;
        }
        PlantType offered = conveyor.tickTimer();
        if (offered != null) {
            bus.publish(new PlantOfferedEvent(offered));
        }
    }

    @Override
    public boolean canPlant(PlantType type, int row, int col, GameState state, SessionContext context) {
        return col <= MAX_PLANT_COL;
    }
}
