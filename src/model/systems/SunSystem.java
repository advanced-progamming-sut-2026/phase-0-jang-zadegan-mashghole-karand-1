package model.systems;

import model.core.EventBus;
import model.core.GameLoop;
import model.core.GameState;
import model.data.plant.Plant;
import model.data.plant.abilities.config.PlantAbilityConfig;
import model.data.plant.abilities.runtime.PlantSunProduceAbility;
import model.data.sun.Sun;
import model.data.sun.SunType;
import model.events.RadioactiveExplosionEvent;
import model.events.SunCollectedEvent;
import model.events.SunLandedEvent;

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

                    if (sun.type == SunType.RADIO_ACTIVE) {
                        sun.type = SunType.NORMAL;
                        sun.amount = SunType.NORMAL.amount;
                    }
                    eventBus.publish(new SunLandedEvent(sun));
                }
            }
        }
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

        state.sunAmount += target.amount;
        state.sunDrops.remove(target);
        bus.publish(new SunCollectedEvent(target));
        return true;
    }

    private void explodeRadioactiveSun(GameState state, EventBus bus, Sun sun) {
        bus.publish(new RadioactiveExplosionEvent(sun));
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
