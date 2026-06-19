package model.data.plant;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import model.data.plant.abilities.config.PlantAbilityConfig;
import model.data.plant.abilities.runtime.PlantShootAbility;
import model.data.plant.abilities.runtime.PlantSunProduceAbility;
import model.data.plant.effects.config.PlantEffectConfig;
import model.data.plant.effects.runtime.PlantInstantSunEffect;
import model.data.plant.effects.runtime.PlantRapidFireEffect;
import model.data.plant.upgrades.PlantLevelUpgrade;
import model.data.plant.upgrades.PlantLevelUpgrades;
import model.data.plant.upgrades.PlantStatBonus;

public enum PlantType {

        SUNFLOWER(1, "Sunflower", PlantCategory.SUN_PRODUCER,
                        EnumSet.of(PlantTag.DAY),
                        new PlantBaseStats(50, 300, 0, 24, 5),
                        Arrays.asList(
                                        new PlantSunProduceAbility(50, 24)),
                        new PlantInstantSunEffect(150),
                        new PlantLevelUpgrades(
                                        PlantLevelUpgrade.atLevel(2, PlantStatBonus.COOLDOWN, -2),
                                        PlantLevelUpgrade.atLevel(3, PlantStatBonus.HP, 150),
                                        PlantLevelUpgrade.atLevel(4, PlantStatBonus.DOUBLE_SUN_CHANCE, true))),
        PeaShooter(2, "Peashooter", PlantCategory.SHOOTER, EnumSet.of(PlantTag.PEA),
                        new PlantBaseStats(100, 300, 20, 1.5f, 5),
                        Arrays.asList(new PlantShootAbility(20, 1.5f, PlantProjectileType.PEA)),
                        new PlantRapidFireEffect(3, 0.1f),
                        new PlantLevelUpgrades(
                                        PlantLevelUpgrade.atLevel(2, PlantStatBonus.DAMAGE, 10),
                                        PlantLevelUpgrade.atLevel(3, PlantStatBonus.HP, 150),
                                        PlantLevelUpgrade.atLevel(4, PlantStatBonus.COST, -25)));

        // ...

        public final int id;
        public final String name;
        public final PlantCategory category;
        public final Set<PlantTag> tags;
        public final PlantBaseStats baseStats;
        public final List<PlantAbilityConfig> abilities;
        public final PlantEffectConfig plantFoodEffect;
        public final PlantLevelUpgrades levelUpgrades;

        PlantType(int id, String name, PlantCategory category, Set<PlantTag> tags,
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

        public static PlantType fromName(String name) {
                if (name == null || name.isEmpty()) {
                        return null;
                }

                for (PlantType type : PlantType.values()) {
                        if (type.name.equalsIgnoreCase(name)) {
                                return type;
                        }
                }
                return null;
        }
}