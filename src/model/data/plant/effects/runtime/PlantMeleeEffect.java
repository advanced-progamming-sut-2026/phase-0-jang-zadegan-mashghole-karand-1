package model.data.plant.effects.runtime;

import model.core.EventBus;
import model.core.GameState;
import model.data.plant.Plant;
import model.data.plant.abilities.runtime.PlantMeleeAbility;
import model.data.plant.effects.config.PlantEffectConfig;

import java.util.List;

import static model.core.GameLoop.TICKS_PER_SECOND;

public class PlantMeleeEffect implements PlantEffectConfig {
    private final float durationSeconds;
    private final float attacksPerSec;
    private final List<PlantMeleeAbility> abilities ;
    private int remainingTicks;
    private int attackCooldown;
    private List<PlantMeleeAbility> runtimeAbilities;

    public PlantMeleeEffect(float durationSeconds, float attacksPerSec, List<PlantMeleeAbility> abilities) {
        this.durationSeconds = durationSeconds;
        this.attacksPerSec = attacksPerSec;
        this.abilities = abilities;
    }

    @Override
    public PlantEffectConfig createInstance(Plant plant) {
        return new PlantMeleeEffect(durationSeconds,attacksPerSec,abilities);
    }
    public void onActivate(Plant plant, GameState state, EventBus event) {
        runtimeAbilities = abilities.stream()
                .map(a -> (PlantMeleeAbility) a.createInstance(plant))
                .toList();

        if (durationSeconds <= 0) {
            for (PlantMeleeAbility a : runtimeAbilities)
                a.forceAttack(plant, state, event);
            return;
        }

        remainingTicks = (int) (durationSeconds * TICKS_PER_SECOND);
        attackCooldown = 0;
    }
    public void onTick(Plant plant, GameState state, EventBus event) {
        if (remainingTicks <= 0) return;
        remainingTicks--;

        if (attackCooldown > 0) { attackCooldown--; return; }

        for (PlantMeleeAbility a : runtimeAbilities)
            a.forceAttack(plant, state, event);

        attackCooldown = (int) (TICKS_PER_SECOND / attacksPerSec);
    }
}
