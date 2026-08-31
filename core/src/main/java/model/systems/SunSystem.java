package model.systems;

import model.core.EventBus;
import model.core.GameLoop;
import model.core.GameState;
import model.data.plant.Plant;
import model.data.plant.abilities.config.PlantAbilityConfig;
import model.data.plant.abilities.runtime.PlantSunProduceAbility;
import model.data.sun.Sun;
import model.data.sun.SunType;
import model.event.events.RadioactiveExplosionEvent;
import model.event.events.SunCollectedEvent;
import model.event.events.SunLandedEvent;

public class SunSystem {
    private static final int FALL_DURATION_SEC = 5;
    private static final int FALL_DURATION_TICKS = FALL_DURATION_SEC * GameLoop.TICKS_PER_SECOND;
    private EventBus eventBus;

    public SunSystem(EventBus bus) {
        this.eventBus = bus;
    }

    public void update(GameState state) {
        for (Sun sun : state.sunDrops) {
            if (sun.isFalling) {
                sun.age++;
                float progress = Math.min(1.0f, sun.age / (float) FALL_DURATION_TICKS);
                sun.position.y = sun.targetY * progress;

                if (sun.age >= FALL_DURATION_TICKS) {
                    sun.isFalling = false;
                    sun.position.y = sun.targetY;
                    sun.groundAge = 0;

                    if (sun.type == SunType.RADIO_ACTIVE) {
                        sun.type = SunType.NORMAL;
                        sun.amount = SunType.NORMAL.amount;
                    }
                    eventBus.publish(new SunLandedEvent(sun));
                }
            } else {
                sun.groundAge++;
            }
        }
        state.sunDrops.removeIf(Sun::isExpired);
    }

    public boolean collectSun(GameState state, EventBus bus, int index) {
        if (index < 0 || index >= state.sunDrops.size()) {
            return false;
        }
        Sun target = state.sunDrops.get(index);

        if (target == null)
            return false;

        if (target.type == SunType.RADIO_ACTIVE && target.isFalling) {
            explodeRadioactiveSun(state, bus, target);
            state.sunDrops.remove(target);
            return false;
        }

        if (target.generatorPlant != null) {
            notifyPlantSunCollected(state, target);
        }

        if (!(state.dualSunMode && state.networkSunAuthority)) {
            if (state.dualSunMode) {
                if (target.generatorPlant != null) {
                    state.plantSun += target.amount;
                } else {
                    state.zombieSun += target.amount;
                }
            } else {
                state.sunAmount += target.amount;
            }
        }
        state.sunDrops.remove(target);
        bus.publish(new SunCollectedEvent(target));
        return true;
    }

    public boolean collectSunAt(GameState state, EventBus bus, int row, int col) {
        Sun sun = findSunInCell(state, row, col);
        if (sun == null) {
            return false;
        }
        int index = state.sunDrops.indexOf(sun);
        if (index < 0) {
            return false;
        }
        return collectSun(state, bus, index);
    }

    public boolean collectSunAtPosition(GameState state, EventBus bus, float modelX, float modelY, float hitRadius) {
        int bestIndex = -1;
        float bestDistSq = hitRadius * hitRadius;
        for (int i = 0; i < state.sunDrops.size(); i++) {
            Sun sun = state.sunDrops.get(i);
            float dx = sun.position.x - modelX;
            float dy = sun.position.y - modelY;
            float distSq = dx * dx + dy * dy;
            if (distSq <= bestDistSq) {
                bestIndex = i;
                bestDistSq = distSq;
            }
        }
        if (bestIndex < 0) {
            return false;
        }
        return collectSun(state, bus, bestIndex);
    }

    private void explodeRadioactiveSun(GameState state, EventBus bus, Sun sun) {
        bus.publish(new RadioactiveExplosionEvent(sun));
    }

    private Sun findSunInCell(GameState state, int row, int col) {
        int cellStartX = col * GameState.CELL_WIDTH;
        int cellEndX = (col + 1) * GameState.CELL_WIDTH;
        return state.sunDrops.stream()
                .filter(s -> s.row == row
                        && !s.isFalling
                        && s.position.x >= cellStartX
                        && s.position.x < cellEndX)
                .findFirst()
                .orElse(null);
    }

    private void notifyPlantSunCollected(GameState state, Sun sun) {
        Plant plant = sun.generatorPlant;
        if (plant == null) {
            return;
        }

        for (PlantAbilityConfig ability : plant.abilities) {
            if (ability instanceof PlantSunProduceAbility) {
                ((PlantSunProduceAbility) ability).onSunCollected();
                break;
            }
        }
    }
}
