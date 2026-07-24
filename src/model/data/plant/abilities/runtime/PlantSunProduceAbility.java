package model.data.plant.abilities.runtime;

import model.core.EventBus;
import model.core.GameLoop;
import model.core.GameState;
import model.core.Position;
import model.data.plant.Plant;
import model.data.plant.PlantTag;
import model.data.plant.PlantType;
import model.data.plant.abilities.config.PlantAbilityConfig;
import model.data.sun.Sun;
import model.event.events.SunProducedEvent;

public class PlantSunProduceAbility implements PlantAbilityConfig {
    private static final int[] AMOUNTS = {25, 50, 75};
    private static final int[] GROW_TICKS = {
            24 * GameLoop.TICKS_PER_SECOND,  // stage 0 → 1
            72 * GameLoop.TICKS_PER_SECOND   // stage 1 → 2
    };
    private final boolean rampUp;
    private int stage = 0;
    private int growTicksRemaining;
    private final int amount;
    private final float cooldownSeconds;
    private final int cooldownTicks;
    private int currentCooldown = 0;
    private boolean waitingForCollection = false;
    private int doubleSunDropChance = 0;

    public PlantSunProduceAbility(int amount, float cooldownSeconds, int doubleSunChance) {
        this(amount,cooldownSeconds,doubleSunChance,false);
    }
    public PlantSunProduceAbility(int amount, float cooldownSeconds, int doubleSunChance, boolean rampUp) {
        this.amount = amount;
        this.cooldownSeconds = cooldownSeconds;
        this.cooldownTicks = (int) (cooldownSeconds * GameLoop.TICKS_PER_SECOND);
        this.doubleSunDropChance = doubleSunChance;
        this.rampUp = rampUp;
        this.growTicksRemaining = rampUp ? GROW_TICKS[0] : 0;
    }

    public PlantSunProduceAbility createInstance(Plant plant) {
        boolean ramp = plant.type.tags != null
                && plant.type.tags.contains(PlantTag.WRAMP_UP);
        int finalAmount = amount + plant.upgradeState.sunDropBonus;
        return new PlantSunProduceAbility(finalAmount, plant.actionInterval, plant.doubleSunChance , ramp );
    }

    @Override
    public void onTick(Plant plant, GameState state, EventBus bus) {
        if (rampUp && stage < AMOUNTS.length - 1) {
            if (growTicksRemaining > 0) {
                growTicksRemaining--;
            } else {
                stage++;
                if (stage < AMOUNTS.length - 1) {
                    growTicksRemaining = GROW_TICKS[stage];
                }
            }
        }
        if (waitingForCollection) return;
        if (currentCooldown > 0) {
            currentCooldown--;
            return;
        }
        int baseAmount = rampUp ? AMOUNTS[stage] : amount;
        if (plant.doubleSunChance > 0 && Math.random() * 100 < doubleSunDropChance) {
            baseAmount *= 2;
        }
        Sun sun = new Sun(plant.row, new Position(plant.getX(), plant.getY()), baseAmount, plant);
        state.sunDrops.add(sun);
        waitingForCollection = true;
        if (plant.type == PlantType.GoldBloom){
            plant.isAlive = false;
        }
        bus.publish(new SunProducedEvent(plant, sun));
    }


    public void onSunCollected() {
        waitingForCollection = false;
        currentCooldown = cooldownTicks;
    }

    public void resetCooldown() {
        currentCooldown = 0;
        waitingForCollection = false;
    }

    public void setDoubleSunChance(int change) {
        this.doubleSunDropChance = change;
    }
}
