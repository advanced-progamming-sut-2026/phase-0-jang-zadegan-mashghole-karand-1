package model.data.plant;

import java.util.Arrays;
import java.util.EnumSet;

import model.data.plant.abilities.runtime.BowlingNutMode;
import model.data.plant.abilities.runtime.PlantBowlAbility;
import model.data.plant.abilities.runtime.PlantDefenderAbility;
import model.data.plant.effects.runtime.PlantDefenderEffect;
import model.data.plant.upgrades.PlantLevelUpgrade;
import model.data.plant.upgrades.PlantLevelUpgrades;
import model.data.plant.upgrades.PlantStatBonus;

final class PlantTypeDefenderDefs {

        private PlantTypeDefenderDefs() {
        }

        static PlantTypeSpec wallNut() {
                return new PlantTypeSpec(44, "Wall-nut", PlantCategory.DEFENDER, null,
                                new PlantBaseStats(50, 4000, 0, 0, 20),
                                Arrays.asList(
                                                new PlantDefenderAbility.Builder().build()),
                                new PlantDefenderEffect.Builder().addArmor(4000).build(),
                                new PlantLevelUpgrades(
                                                PlantLevelUpgrade.atLevel(2, PlantStatBonus.HP, 1000),
                                                PlantLevelUpgrade.atLevel(3, PlantStatBonus.COOLDOWN, -5),
                                                PlantLevelUpgrade.atLevel(4, PlantStatBonus.HP, 1500)));
        }

        static PlantTypeSpec tallNut() {
                return new PlantTypeSpec(45, "Tall-nut", PlantCategory.DEFENDER, null,
                                new PlantBaseStats(125, 8000, 0, 0, 20),
                                Arrays.asList(
                                                new PlantDefenderAbility.Builder()
                                                                .blockJump()
                                                                .build()),
                                new PlantDefenderEffect.Builder().addArmor(8000).build(),
                                new PlantLevelUpgrades(
                                                PlantLevelUpgrade.atLevel(2, PlantStatBonus.HP, 2000),
                                                PlantLevelUpgrade.atLevel(3, PlantStatBonus.COOLDOWN, -5),
                                                PlantLevelUpgrade.atLevel(4, PlantStatBonus.HP, 3000)));
        }

        static PlantTypeSpec endurian() {
                return new PlantTypeSpec(46, "Endurian", PlantCategory.DEFENDER, null,
                                new PlantBaseStats(100, 3000, 20, 0, 15),
                                Arrays.asList(
                                                new PlantDefenderAbility.Builder()
                                                                .reflectDamage(20)
                                                                .build()),
                                new PlantDefenderEffect.Builder()
                                                .addArmor(3000)
                                                .increaseReflect(20)
                                                .build(),
                                new PlantLevelUpgrades(
                                                PlantLevelUpgrade.atLevel(2, PlantStatBonus.DAMAGE, 5),
                                                PlantLevelUpgrade.atLevel(3, PlantStatBonus.HP, 1000),
                                                PlantLevelUpgrade.atLevel(4, PlantStatBonus.COST, -25)));
        }

        static PlantTypeSpec garlic() {
                return new PlantTypeSpec(47, "Garlic", PlantCategory.DEFENDER, EnumSet.of(PlantTag.MOVE_ZOMBIES),
                                new PlantBaseStats(50, 300, 0, 0, 20),
                                Arrays.asList(
                                                new PlantDefenderAbility.Builder()
                                                                .moveZombie()
                                                                .build()),
                                new PlantDefenderEffect.Builder()
                                                .moveAllZombies()
                                                .build(),
                                new PlantLevelUpgrades(
                                                PlantLevelUpgrade.atLevel(2, PlantStatBonus.HP, 150),
                                                PlantLevelUpgrade.atLevel(3, PlantStatBonus.COOLDOWN, -3),
                                                PlantLevelUpgrade.atLevel(4, PlantStatBonus.HP, 250)));
        }

        static PlantTypeSpec sweetPotato() {
                return new PlantTypeSpec(48, "Sweet Potato", PlantCategory.DEFENDER, EnumSet.of(PlantTag.MOVE_ZOMBIES),
                                new PlantBaseStats(150, 3000, 0, 0, 20),
                                Arrays.asList(
                                                new PlantDefenderAbility.Builder()
                                                                .attractZombies()
                                                                .build()),
                                new PlantDefenderEffect.Builder()
                                                .attractAll()
                                                .healFully()
                                                .build(),
                                new PlantLevelUpgrades(
                                                PlantLevelUpgrade.atLevel(2, PlantStatBonus.HP, 1000),
                                                PlantLevelUpgrade.atLevel(3, PlantStatBonus.COOLDOWN, -5),
                                                PlantLevelUpgrade.atLevel(4, PlantStatBonus.HP, 1500)));
        }

        static PlantTypeSpec explodeONut() {
                return new PlantTypeSpec(49, "Explode-o-nut", PlantCategory.DEFENDER, EnumSet.of(PlantTag.EXPLOSIVE),
                                new PlantBaseStats(50, 4000, 1800, 0, 20),
                                Arrays.asList(
                                                new PlantDefenderAbility.Builder()
                                                                .explodeOnDeath(1800)
                                                                .build()),
                                new PlantDefenderEffect.Builder()
                                                .addArmor(4000)
                                                .explodeOnArmorBreak(1800)
                                                .build(),
                                new PlantLevelUpgrades(
                                                PlantLevelUpgrade.atLevel(2, PlantStatBonus.HP, 1000),
                                                PlantLevelUpgrade.atLevel(3, PlantStatBonus.DAMAGE, 200),
                                                PlantLevelUpgrade.atLevel(4, PlantStatBonus.COST, -25)));
        }

        static PlantTypeSpec pumpkin() {
                return new PlantTypeSpec(50, "Pumpkin", PlantCategory.DEFENDER, EnumSet.of(PlantTag.STACK),
                                new PlantBaseStats(150, 4000, 0, 0, 20),
                                Arrays.asList(
                                                new PlantDefenderAbility.Builder()
                                                                .build()),
                                new PlantDefenderEffect.Builder()
                                                .addArmor(4000)
                                                .build(),
                                new PlantLevelUpgrades(
                                                PlantLevelUpgrade.atLevel(2, PlantStatBonus.HP, 1000),
                                                PlantLevelUpgrade.atLevel(3, PlantStatBonus.COOLDOWN, -5),
                                                PlantLevelUpgrade.atLevel(4, PlantStatBonus.HP, 1500)));
        }

        static PlantTypeSpec sunBean() {
                return new PlantTypeSpec(51, "Sun Bean", PlantCategory.DEFENDER, EnumSet.of(PlantTag.SUN),
                                new PlantBaseStats(50, 1000, 0, 0, 20),
                                Arrays.asList(
                                                new PlantDefenderAbility.Builder()
                                                                .produceSunOnHit(5)
                                                                .build()),
                                new PlantDefenderEffect.Builder()
                                                .addArmor(1000)
                                                .build(),
                                new PlantLevelUpgrades(
                                                PlantLevelUpgrade.atLevel(2, PlantStatBonus.SUN_DROP, 5),
                                                PlantLevelUpgrade.atLevel(3, PlantStatBonus.HP, 150),
                                                PlantLevelUpgrade.atLevel(4, PlantStatBonus.COST, -25)));
        }

        static PlantTypeSpec bowlingWallNut() {
                return new PlantTypeSpec(70, "Bowling Wall-nut", PlantCategory.DEFENDER, EnumSet.of(PlantTag.BOWLING),
                                new PlantBaseStats(0, 300, 1800, 0, 0),
                                Arrays.asList(
                                                new PlantBowlAbility(BowlingNutMode.NORMAL)),
                                null,
                                new PlantLevelUpgrades());
        }

        static PlantTypeSpec bowlingExplodeONut() {
                return new PlantTypeSpec(71, "Bowling Explode-o-nut", PlantCategory.DEFENDER,
                                EnumSet.of(PlantTag.BOWLING, PlantTag.EXPLOSIVE),
                                new PlantBaseStats(0, 300, 1800, 0, 0),
                                Arrays.asList(
                                                new PlantBowlAbility(BowlingNutMode.EXPLODE)),
                                null,
                                new PlantLevelUpgrades());
        }

        static PlantTypeSpec giantBowlingWallNut() {
                return new PlantTypeSpec(72, "Giant Bowling Wall-nut", PlantCategory.DEFENDER,
                                EnumSet.of(PlantTag.BOWLING),
                                new PlantBaseStats(0, 300, 1800, 0, 0),
                                Arrays.asList(
                                                new PlantBowlAbility(BowlingNutMode.GIANT)),
                                null,
                                new PlantLevelUpgrades());
        }
}
