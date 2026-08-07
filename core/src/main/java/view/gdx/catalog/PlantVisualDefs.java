package view.gdx.catalog;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import model.data.plant.PlantType;

public final class PlantVisualDefs {
    private PlantVisualDefs() {
    }

    public static Map<PlantType, PlantVisualDef> create() {
        Map<PlantType, PlantVisualDef> plants = new EnumMap<>(PlantType.class);

        plants.put(PlantType.PeaShooter, new PlantVisualDef(
                "768/INITIAL/PLANT/PEASHOOTER/PEASHOOTER.PAM", "idle", "attack"));
        plants.put(PlantType.Sunflower, new PlantVisualDef(
                "768/INITIAL/PLANT/SUNFLOWER/SUNFLOWER.PAM", "idle", "special"));

        return Collections.unmodifiableMap(plants);
    }
}
