package model.data.plant;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import model.data.plant.abilities.runtime.PlantHypnotizeAbility;
import model.data.plant.abilities.runtime.PlantTorchwoodAbility;
import model.data.plant.abilities.runtime.PlantTransformAbility;
import model.data.plant.effects.runtime.PlantHypnoGargEffect;
import model.data.plant.effects.runtime.PlantSpawnCopiesEffect;
import model.data.plant.effects.runtime.PlantTorchEffect;
import model.data.plant.upgrades.PlantLevelUpgrade;
import model.data.plant.upgrades.PlantLevelUpgrades;
import model.data.plant.upgrades.PlantStatBonus;

final class PlantTypeModifierDefs {

        private PlantTypeModifierDefs() {
        }

        static PlantTypeSpec torchwood() {
                return new PlantTypeSpec(52, "Torchwood", PlantCategory.MODIFIER, EnumSet.of(PlantTag.FIRE),
                                new PlantBaseStats(175, 300, 0, 15f, 15),
                                Arrays.asList(
                                                new PlantTorchwoodAbility()),
                                new PlantTorchEffect(),
                                new PlantLevelUpgrades(
                                                PlantLevelUpgrade.atLevel(2, PlantStatBonus.HP, 300),
                                                PlantLevelUpgrade.atLevel(3, PlantStatBonus.AOE_ON_DEATH, true),
                                                PlantLevelUpgrade.atLevel(4, PlantStatBonus.COST, -25)));
        }

        static PlantTypeSpec hypnoShroom() {
                return new PlantTypeSpec(54, "Hypno-shroom", PlantCategory.MODIFIER,
                                EnumSet.of(PlantTag.SHROOM, PlantTag.MAGIC),
                                new PlantBaseStats(125, 300, 0, 0f, 20),
                                Arrays.asList(
                                                new PlantHypnotizeAbility()),
                                new PlantHypnoGargEffect(),
                                new PlantLevelUpgrades(
                                                PlantLevelUpgrade.atLevel(2, PlantStatBonus.COST, -25),
                                                PlantLevelUpgrade.atLevel(3, PlantStatBonus.ZOMBIE_HP_BUFF, true),
                                                PlantLevelUpgrade.atLevel(4, PlantStatBonus.ZOMBIE_DAMAGE_BUFF, true)));
        }

        static PlantTypeSpec imitater() {
                return new PlantTypeSpec(56, "Imitater", PlantCategory.MODIFIER, null,
                                new PlantBaseStats(0, 0, 0, 0f, 0),
                                Arrays.asList(
                                                new PlantTransformAbility()),
                                null,
                                new PlantLevelUpgrades(
                                                PlantLevelUpgrade.atLevel(2, PlantStatBonus.COOLDOWN, -2),
                                                PlantLevelUpgrade.atLevel(3, PlantStatBonus.COST, -25),
                                                PlantLevelUpgrade.atLevel(4, PlantStatBonus.FOOD_ON_ENTRANCE, true)));
        }

        static PlantTypeSpec lilyPad() {
                return new PlantTypeSpec(58, "Lily Pad", PlantCategory.MODIFIER,
                                EnumSet.of(PlantTag.WATER, PlantTag.STACK),
                                new PlantBaseStats(25, 300, 0, 0f, 5),
                                List.of(),
                                new PlantSpawnCopiesEffect(),
                                new PlantLevelUpgrades(
                                                PlantLevelUpgrade.atLevel(2, PlantStatBonus.COST, -25),
                                                PlantLevelUpgrade.atLevel(3, PlantStatBonus.HP, 200),
                                                PlantLevelUpgrade.atLevel(4, PlantStatBonus.COOLDOWN, -2)));
        }
}
