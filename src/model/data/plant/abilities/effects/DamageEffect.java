package model.data.plant.abilities.effects;

import model.core.EventBus;
import model.core.GameState;
import model.data.plant.Plant;
import model.data.plant.PlantType;
import model.data.zombie.Zombie;
import model.events.ZombieDiedEvent;

public class DamageEffect  implements HitEffect{
    private final int amount;
    public DamageEffect(int amount) {
        this.amount = amount;
    }
    @Override
    public void apply(Zombie zombie, GameState state, EventBus event, PlantType sourceType) {
        zombie.takeDamage(amount);
    }
}
