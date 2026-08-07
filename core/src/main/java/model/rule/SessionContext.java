package model.rule;

import model.data.plant.PlantType;
import model.systems.WaveManager;

import java.util.*;

public class SessionContext {
    private final SessionConfig config;
    private final RuleEngine ruleEngine;
    private ConveyorState conveyorState = null;
    private final WaveManager waveManager;
    private final Map<PlantType, Integer> heldSeeds = new EnumMap<>(PlantType.class);
    private final Set<PlantType> boostedPlants = EnumSet.noneOf(PlantType.class);
    private final Map<PlantType, Integer> plantingCooldownTicks = new EnumMap<>(PlantType.class);

    public SessionContext(SessionConfig config, RuleEngine ruleEngine, WaveManager waveManager) {
        this.config = config;
        this.ruleEngine = ruleEngine;
        this.waveManager = waveManager;
        if (config.boostedPlants != null) {
            boostedPlants.addAll(config.boostedPlants);
        }
    }

    public boolean isBoosted(PlantType type) {
        return type != null && boostedPlants.contains(type);
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
        return conveyorState != null;
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

    public void addHeldSeed(PlantType type) {
        if (type == null) {
            return;
        }
        heldSeeds.merge(type, 1, Integer::sum);
    }

    public boolean hasHeldSeed(PlantType type) {
        return type != null && heldSeeds.getOrDefault(type, 0) > 0;
    }

    public boolean consumeHeldSeed(PlantType type) {
        if (!hasHeldSeed(type)) {
            return false;
        }
        int remaining = heldSeeds.get(type) - 1;
        if (remaining <= 0) {
            heldSeeds.remove(type);
        } else {
            heldSeeds.put(type, remaining);
        }
        return true;
    }

    public Map<PlantType, Integer> getHeldSeeds() {
        return Collections.unmodifiableMap(heldSeeds);
    }

    public void tickPlantingCooldowns() {
        if (plantingCooldownTicks.isEmpty()) {
            return;
        }
        var iterator = plantingCooldownTicks.entrySet().iterator();
        while (iterator.hasNext()) {
            var entry = iterator.next();
            int next = entry.getValue() - 1;
            if (next <= 0) {
                iterator.remove();
            } else {
                entry.setValue(next);
            }
        }
    }

    public boolean isPlantOnCooldown(PlantType type) {
        return type != null && plantingCooldownTicks.getOrDefault(type, 0) > 0;
    }

    public int getPlantingCooldownTicks(PlantType type) {
        if (type == null) {
            return 0;
        }
        return plantingCooldownTicks.getOrDefault(type, 0);
    }

    public void startPlantingCooldown(PlantType type, int ticks) {
        if (type == null || ticks <= 0) {
            return;
        }
        plantingCooldownTicks.put(type, ticks);
    }

    public void clearPlantingCooldowns() {
        plantingCooldownTicks.clear();
    }
}
