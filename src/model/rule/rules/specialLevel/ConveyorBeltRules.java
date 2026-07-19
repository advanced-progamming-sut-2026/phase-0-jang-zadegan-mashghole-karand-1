package model.rule.rules.specialLevel;

import model.core.EventBus;
import model.core.GameState;
import model.data.plant.PlantType;
import model.events.PlantOfferedEvent;
import model.rule.ConveyorState;
import model.rule.LevelRule;
import model.rule.SessionContext;

import java.util.List;

public class ConveyorBeltRules implements LevelRule {

    @Override
    public void onSessionStart(SessionContext context, GameState state, EventBus bus) {
        List<PlantType> pool = context.getConfig().selectedPlants;
        context.initializeConveyor(pool);
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
}
