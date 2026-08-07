package model.data.plant;

import java.util.Arrays;
import java.util.EnumSet;

import model.data.plant.abilities.runtime.PlantSunProduceAbility;
import model.data.plant.effects.runtime.PlantInstantSunEffect;
import model.data.plant.upgrades.PlantLevelUpgrade;
import model.data.plant.upgrades.PlantLevelUpgrades;
import model.data.plant.upgrades.PlantStatBonus;

final class PlantTypeSunDefs {

        private PlantTypeSunDefs() {
        }

        static PlantTypeSpec sunflower() {
                return new PlantTypeSpec(1, "Sunflower", PlantCategory.SUN_PRODUCER,
                                EnumSet.of(PlantTag.DAY),
                                new PlantBaseStats(50, 300, 0, 24, 5),
                                Arrays.asList(
                                                new PlantSunProduceAbility(50, 24, 0)),
                                new PlantInstantSunEffect(150),
                                new PlantLevelUpgrades(
                                                PlantLevelUpgrade.atLevel(2, PlantStatBonus.COOLDOWN, -2),
                                                PlantLevelUpgrade.atLevel(3, PlantStatBonus.HP, 150),
                                                PlantLevelUpgrade.atLevel(4, PlantStatBonus.DOUBLE_SUN_CHANCE, 25)));
        }

        static PlantTypeSpec twinSunflower() {
                return new PlantTypeSpec(2, "Twin Sunflower", PlantCategory.SUN_PRODUCER,
                                EnumSet.of(PlantTag.DAY),
                                new PlantBaseStats(125, 300, 0, 24, 15),
                                Arrays.asList(
                                                new PlantSunProduceAbility(100, 24, 0)),
                                new PlantInstantSunEffect(250),
                                new PlantLevelUpgrades(
                                                PlantLevelUpgrade.atLevel(2, PlantStatBonus.COOLDOWN, -2),
                                                PlantLevelUpgrade.atLevel(3, PlantStatBonus.HP, 150),
                                                PlantLevelUpgrade.atLevel(4, PlantStatBonus.COST, -25)));
        }

        static PlantTypeSpec sunShroom() {
                return new PlantTypeSpec(3, "Sun-shroom", PlantCategory.SUN_PRODUCER,
                                EnumSet.of(PlantTag.SHROOM, PlantTag.WRAMP_UP, PlantTag.NIGHT),
                                new PlantBaseStats(25, 300, 0, 24, 5),
                                Arrays.asList(
                                                new PlantSunProduceAbility(25, 24, 0, true)),
                                new PlantInstantSunEffect(225),
                                new PlantLevelUpgrades(
                                                PlantLevelUpgrade.atLevel(2, PlantStatBonus.COOLDOWN, -5),
                                                PlantLevelUpgrade.atLevel(3, PlantStatBonus.HP, 150),
                                                PlantLevelUpgrade.atLevel(4, PlantStatBonus.DOUBLE_SUN_CHANCE, 25)));
        }

        static PlantTypeSpec primalSunflower() {
                return new PlantTypeSpec(4, "Primal Sunflower", PlantCategory.SUN_PRODUCER,
                                null,
                                new PlantBaseStats(75, 300, 0, 24, 5),
                                Arrays.asList(
                                                new PlantSunProduceAbility(75, 24, 0)),
                                new PlantInstantSunEffect(225),
                                new PlantLevelUpgrades(
                                                new PlantLevelUpgrade(2, PlantStatBonus.COOLDOWN, -2),
                                                new PlantLevelUpgrade(3, PlantStatBonus.HP, 150),
                                                new PlantLevelUpgrade(4, PlantStatBonus.COST, -25)));
        }

        static PlantTypeSpec goldBloom() {
                return new PlantTypeSpec(5, "Gold Bloom", PlantCategory.SUN_PRODUCER,
                                EnumSet.noneOf(PlantTag.class),
                                new PlantBaseStats(0, 0, 0, 0, 75),
                                Arrays.asList(new PlantSunProduceAbility(375, 0, 0)),
                                null,
                                new PlantLevelUpgrades(
                                                new PlantLevelUpgrade(2, PlantStatBonus.COOLDOWN, -5),
                                                new PlantLevelUpgrade(3, PlantStatBonus.SUN_DROP, 50),
                                                new PlantLevelUpgrade(4, PlantStatBonus.COST, -25)));
        }
}
