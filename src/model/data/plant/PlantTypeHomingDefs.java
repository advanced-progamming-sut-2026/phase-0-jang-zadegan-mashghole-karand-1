package model.data.plant;

import java.util.Arrays;
import java.util.EnumSet;

import model.data.plant.abilities.config.TargetStrategy;
import model.data.plant.abilities.runtime.PlantHomingAbility;
import model.data.plant.abilities.runtime.PlantMagnetAbility;
import model.data.plant.effects.runtime.PlantHomingEffect;
import model.data.plant.effects.runtime.PlantMagnetEffect;
import model.data.plant.upgrades.PlantLevelUpgrade;
import model.data.plant.upgrades.PlantLevelUpgrades;
import model.data.plant.upgrades.PlantStatBonus;
import model.data.projectile.ProjectileType;

final class PlantTypeHomingDefs {

        private PlantTypeHomingDefs() {
        }

        static PlantTypeSpec caulipower() {
                return new PlantTypeSpec(14, "Caulipower", PlantCategory.HOMING,
                                EnumSet.of(PlantTag.CHARGE, PlantTag.MAGIC),
                                new PlantBaseStats(250, 300, 0, 12, 15),
                                Arrays.asList(
                                                new PlantHomingAbility(0, 12, ProjectileType.PLASMA,
                                                                TargetStrategy.RANDOM)),
                                new PlantHomingEffect(5, 0, ProjectileType.PLASMA, TargetStrategy.RANDOM),
                                new PlantLevelUpgrades(
                                                new PlantLevelUpgrade(2, PlantStatBonus.COOLDOWN, -2),
                                                new PlantLevelUpgrade(3, PlantStatBonus.HP, 150),
                                                new PlantLevelUpgrade(4, PlantStatBonus.COST, -50)));
        }

        static PlantTypeSpec electricBlueberry() {
                return new PlantTypeSpec(15, "Electric Blueberry", PlantCategory.HOMING, EnumSet.of(PlantTag.CHARGE),
                                new PlantBaseStats(150, 300, 5000, 12, 15),
                                Arrays.asList(
                                                new PlantHomingAbility(5000, 12, ProjectileType.LASER,
                                                                TargetStrategy.RANDOM)),
                                new PlantHomingEffect(3, 0, ProjectileType.LASER, TargetStrategy.RANDOM),
                                new PlantLevelUpgrades(
                                                new PlantLevelUpgrade(2, PlantStatBonus.COOLDOWN, -2),
                                                new PlantLevelUpgrade(3, PlantStatBonus.TARGET_PRIORITY, 1),
                                                new PlantLevelUpgrade(4, PlantStatBonus.COST, -25)));
        }

        static PlantTypeSpec magnetShroom() {
                return new PlantTypeSpec(53, "Magnet-shroom", PlantCategory.HOMING,
                                EnumSet.of(PlantTag.SHROOM, PlantTag.MAGIC),
                                new PlantBaseStats(50, 1000, 0, 10, 20),
                                Arrays.asList(
                                                new PlantMagnetAbility()),
                                new PlantMagnetEffect(),
                                new PlantLevelUpgrades(
                                                PlantLevelUpgrade.atLevel(2, PlantStatBonus.RANGE, 1),
                                                PlantLevelUpgrade.atLevel(3, PlantStatBonus.COOLDOWN, -5),
                                                PlantLevelUpgrade.atLevel(4, PlantStatBonus.HP, 200)));
        }

        static PlantTypeSpec catTail() {
                return new PlantTypeSpec(55, "Cat-tail", PlantCategory.HOMING, null,
                                new PlantBaseStats(175, 300, 15, 1.5f, 20),
                                Arrays.asList(
                                                new PlantHomingAbility(15, 1.5f, ProjectileType.SPIKE,
                                                                TargetStrategy.CLOSEST)),
                                new PlantHomingEffect(1, 15, ProjectileType.SPIKE, TargetStrategy.CLOSEST),
                                new PlantLevelUpgrades(
                                                new PlantLevelUpgrade(2, PlantStatBonus.DAMAGE, 10),
                                                new PlantLevelUpgrade(3, PlantStatBonus.HP, 200),
                                                new PlantLevelUpgrade(4, PlantStatBonus.COST, -25)));
        }
}
