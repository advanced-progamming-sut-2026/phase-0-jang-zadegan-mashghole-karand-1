package model.rule.rules.specialLevel;

import java.util.List;

import model.data.content.specialLevel.LockedPlantsConfig;
import model.data.content.specialLevel.LockedPlantsMode;
import model.data.plant.PlantType;
import model.rule.LevelRule;

public class LockedPlantsRules implements LevelRule {
    private final LockedPlantsConfig config;

    public LockedPlantsRules(LockedPlantsConfig config) {
        this.config = config != null ? config : LockedPlantsConfig.onePerFamily();
    }

    @Override
    public boolean skipsPlantSelection() {
        return config.mode == LockedPlantsMode.PRESET;
    }

    @Override
    public boolean canSelectPlant(PlantType type, List<PlantType> alreadySelected) {
        if (type == null) {
            return false;
        }
        if (config.mode != LockedPlantsMode.ONE_PER_FAMILY) {
            return true;
        }
        if (alreadySelected == null || alreadySelected.isEmpty()) {
            return true;
        }
        for (PlantType selected : alreadySelected) {
            if (selected != null && selected.category == type.category) {
                return false;
            }
        }
        return true;
    }
}
