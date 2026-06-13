package model.data.plant.upgrades;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlantLevelUpgrades {
    private final Map<Integer, List<PlantLevelUpgrade>> upgrades = new HashMap<>();

    public PlantLevelUpgrades(PlantLevelUpgrade... upgrades) {
        for (PlantLevelUpgrade upgrade : upgrades) {
            this.upgrades.computeIfAbsent(upgrade.level, k -> new ArrayList<>()).add(upgrade);
        }
    }

    public List<PlantLevelUpgrade> getForLevel(int level) {
        List<PlantLevelUpgrade> result = new ArrayList<>();
        for (int lvl = 2; lvl <= Math.min(level, 4); lvl++) {
            if (upgrades.containsKey(lvl)) {
                result.addAll(upgrades.get(lvl));
            }
        }
        return result;
    }

    // public boolean hasUpgrades(int level) {
    // return !getForLevel(level).isEmpty();
    // }
}
