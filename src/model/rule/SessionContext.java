package model.rule;

import model.data.content.specialLevel.SpecialLevelType;
import model.data.plant.PlantType;
import model.systems.WaveManager;

import java.util.List;

public class SessionContext {
    private final SessionConfig config;
    private final RuleEngine ruleEngine;
    private ConveyorState conveyorState = null;
    private final WaveManager waveManager;

    public SessionContext(SessionConfig config, RuleEngine ruleEngine, WaveManager waveManager) {
        this.config = config;
        this.ruleEngine = ruleEngine;
        this.waveManager = waveManager;
    }

    public SessionConfig getConfig() {
        return config;
    }

    public RuleEngine getRuleEngine() {
        return ruleEngine;
    }

    public void initializeConveyor(List<PlantType> availablePlants) {
        this.conveyorState = new ConveyorState(availablePlants);
    }

    public boolean isConveyorMode() {
        return config.isSpecial()
                && config.specialLevelType == SpecialLevelType.CONVEYOR_BELT;
    }

    public ConveyorState getConveyorState() {
        return conveyorState;
    }

    public PlantType getConveyorOffer() {
        return conveyorState != null ? conveyorState.getCurrentOffer() : null;
    }

    public void consumeConveyorOffer() {
        if (conveyorState != null) {
            conveyorState.consumeOffer();
        }
    }

    public boolean hasConveyorOffer() {
        return conveyorState != null && conveyorState.hasOffer();
    }

    public int getConveyorRemaining() {
        return conveyorState != null ? conveyorState.getRemainingPlants() : 0;
    }

    public WaveManager getWaveManager() {
        return waveManager;
    }
}
