package model.data.content.specialLevel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import model.data.plant.PlantType;

public final class LockedPlantsConfig {
    public final LockedPlantsMode mode;
    public final List<PlantType> presetPlants;

    public LockedPlantsConfig(LockedPlantsMode mode, List<PlantType> presetPlants) {
        this.mode = mode;
        this.presetPlants = presetPlants != null
                ? Collections.unmodifiableList(new ArrayList<>(presetPlants))
                : List.of();
    }

    public static LockedPlantsConfig onePerFamily() {
        return new LockedPlantsConfig(LockedPlantsMode.ONE_PER_FAMILY, List.of());
    }

    public static LockedPlantsConfig preset(List<PlantType> plants) {
        return new LockedPlantsConfig(LockedPlantsMode.PRESET, plants);
    }
}
