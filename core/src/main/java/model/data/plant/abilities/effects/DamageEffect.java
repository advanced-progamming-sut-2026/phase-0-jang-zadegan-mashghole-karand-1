package model.data.plant.abilities.effects;

import model.core.EventBus;
import model.core.GameState;
import model.data.plant.PlantType;
import model.data.zombie.Zombie;

public class DamageEffect implements HitEffect {
    private final int amount;

    public DamageEffect(int amount) {
        this.amount = amount;
    }

    @Override
    public void apply(Zombie zombie, GameState state, EventBus event, PlantType sourceType) {
        if (sourceType != null) {
            zombie.lastHitBy = sourceType;
        }
        zombie.takeDamage(amount);
    }

    public int getAmount() {
        return amount;
    }
}
