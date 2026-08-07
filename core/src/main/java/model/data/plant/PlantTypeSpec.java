package model.data.plant;

import java.util.List;
import java.util.Set;

import model.data.plant.abilities.config.PlantAbilityConfig;
import model.data.plant.effects.config.PlantEffectConfig;
import model.data.plant.upgrades.PlantLevelUpgrades;

public final class PlantTypeSpec {
    final int id;
    final String name;
    final PlantCategory category;
    final Set<PlantTag> tags;
    final PlantBaseStats baseStats;
    final List<PlantAbilityConfig> abilities;
    final PlantEffectConfig plantFoodEffect;
    final PlantLevelUpgrades levelUpgrades;

    public PlantTypeSpec(int id, String name, PlantCategory category, Set<PlantTag> tags,
            PlantBaseStats baseStats, List<PlantAbilityConfig> abilities,
            PlantEffectConfig plantFoodEffect, PlantLevelUpgrades levelUpgrades) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.tags = tags;
        this.baseStats = baseStats;
        this.abilities = abilities;
        this.plantFoodEffect = plantFoodEffect;
        this.levelUpgrades = levelUpgrades;
    }
}
