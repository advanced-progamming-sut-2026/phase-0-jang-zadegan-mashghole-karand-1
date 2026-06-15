package model.data.plant.abilities.runtime;

import model.core.EventBus;
import model.core.GameState;
import model.core.Position;
import model.data.plant.Plant;
import model.data.plant.abilities.config.PlantAbilityConfig;
import model.data.sun.Sun;
import model.events.SunProducedEvent;

public class PlantSunProduceAbility implements PlantAbilityConfig {
    private final int amount;
    private final int cooldownTicks;
    private int currentCooldown = 0;
    private boolean waitingForCollection = false;

    public PlantSunProduceAbility(int amount, int cooldownTicks) {
        this.amount = amount;
        this.cooldownTicks = cooldownTicks;
    }

    public PlantSunProduceAbility createInstance(Plant plant) {
        // should implement upgrades effect here
        return new PlantSunProduceAbility(amount, cooldownTicks);
    }

    @Override
    public void onTick(Plant plant, GameState state, EventBus bus) {
        if (waitingForCollection) {
            return;
        }

        if (currentCooldown > 0) {
            currentCooldown--;
            return;
        }

        // implement sun target position
        Sun sun = new Sun(0, new Position(0, 0), amount, plant);
        state.sunDrops.add(sun);
        waitingForCollection = true;

        bus.publish(new SunProducedEvent(plant, sun));
    }

    public void onSunCollected() {
        waitingForCollection = false;
        currentCooldown = cooldownTicks;
    }
}
