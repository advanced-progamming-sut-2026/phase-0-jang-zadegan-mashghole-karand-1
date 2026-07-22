package model.data.plant.abilities.runtime;

import model.core.EventBus;
import model.core.GameLoop;
import model.core.GameState;
import model.data.plant.Plant;
import model.data.plant.PlantCategory;
import model.data.plant.abilities.config.PlantAbilityConfig;

import java.util.ArrayList;
import java.util.List;

public class PlantMintAbility implements PlantAbilityConfig {
    private final PlantCategory targetCategory;
    private final int durationTicks;
    private boolean activated = false;

    public PlantMintAbility(PlantCategory targetCategory, int durationTicks) {
        this.targetCategory = targetCategory;
        this.durationTicks = durationTicks;
    }

    @Override
    public PlantAbilityConfig createInstance(Plant plant) {
        return new PlantMintAbility(targetCategory, durationTicks);
    }

    @Override
    public void onTick(Plant plant, GameState state, EventBus event) {
        if (activated)
            return;
        activated = true;
        List<Plant> familyPlants = new ArrayList<>(state.plants);
        for (Plant target : familyPlants) {
            if (target == plant)
                continue;
            if (target.type.category != targetCategory)
                continue;

            if (target.plantFoodEffect != null) {
                int finalDuration = durationTicks
                        + plant.upgradeState.plantFoodDurationBonus * GameLoop.TICKS_PER_SECOND;
                target.activatePlantFood(
                        state,
                        event,
                        finalDuration);
            }
            if (plant.resetFamilyCooldowns) {
                resetCooldowns(target);
            }
        }

        plant.hp = 0;
    }

    private void resetCooldowns(Plant plant) {
        for (PlantAbilityConfig ability : plant.abilities) {
            ability.resetCooldown();
        }
    }
}
