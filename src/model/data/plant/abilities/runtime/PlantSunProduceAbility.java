package model.data.plant.abilities.runtime;

import model.core.EventBus;
import model.core.GameLoop;
import model.core.GameState;
import model.core.Position;
import model.data.plant.Plant;
import model.data.plant.abilities.config.PlantAbilityConfig;
import model.data.sun.Sun;
import model.events.SunProducedEvent;

public class PlantSunProduceAbility implements PlantAbilityConfig {
    private final int amount;
    private final float cooldownSeconds;
    private final int cooldownTicks;
    private int currentCooldown = 0;
    private boolean waitingForCollection = false;
    private int doubleSunDropChance = 0;

    public PlantSunProduceAbility(int amount, float cooldownSeconds, int doubleSunChance) {
        this.amount = amount;
        this.cooldownSeconds = cooldownSeconds;
        this.cooldownTicks = (int) cooldownSeconds * GameLoop.TICKS_PER_SECOND;
        this.doubleSunDropChance = doubleSunChance;
    }

    public PlantSunProduceAbility createInstance(Plant plant) {
        return new PlantSunProduceAbility(amount, plant.actionInterval, plant.doubleSunChance);
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

        int baseAmount = amount;
        if (plant.doubleSunChance > 0 && Math.random() * 100 < doubleSunDropChance) {
            baseAmount *= 2;
        }

        Sun sun = new Sun(plant.row, new Position(plant.getX(), plant.getY()), baseAmount, plant);
        state.sunDrops.add(sun);
        waitingForCollection = true;

        bus.publish(new SunProducedEvent(plant, sun));
    }

    public void onSunCollected() {
        waitingForCollection = false;
        currentCooldown = cooldownTicks;
    }

    public void setDoubleSunChance(int change) {
        this.doubleSunDropChance = change;
    }
}
