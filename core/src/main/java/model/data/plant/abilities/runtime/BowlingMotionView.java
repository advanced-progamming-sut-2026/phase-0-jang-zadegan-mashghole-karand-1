package model.data.plant.abilities.runtime;

import model.data.plant.Plant;
import model.data.plant.abilities.config.PlantAbilityConfig;

public interface BowlingMotionView {
    float modelX();

    float modelY();

    float rotationRadians();

    static BowlingMotionView of(Plant plant) {
        if (plant == null || plant.abilities == null) {
            return null;
        }
        for (PlantAbilityConfig ability : plant.abilities) {
            if (ability instanceof PlantBowlAbility bowl) {
                return bowl;
            }
        }
        return null;
    }
}
