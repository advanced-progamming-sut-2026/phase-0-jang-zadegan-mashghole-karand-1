package model.data.plant.effects.runtime;

import model.core.EventBus;
import model.core.GameState;
import model.core.Position;
import model.data.plant.Plant;
import model.data.plant.effects.config.PlantEffectConfig;
import model.data.sun.Sun;
import model.events.SunProducedEvent;

public class PlantInstantSunEffect implements PlantEffectConfig {
    public final int amount;

    public PlantInstantSunEffect(int amount) {
        this.amount = amount;
    }

    public PlantInstantSunEffect createInstance(Plant plant) {
        return new PlantInstantSunEffect(amount);
    }
    @Override
    public void onActivate(Plant plant, GameState state, EventBus event) {
        Sun sun = new Sun(plant.row, new Position(plant.getX(), plant.getY()), amount, plant);
        state.sunDrops.add(sun);
        event.publish(new SunProducedEvent(plant, sun));
    }
}
