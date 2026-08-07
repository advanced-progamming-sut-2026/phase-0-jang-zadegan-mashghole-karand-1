package model.data.plant;

import java.util.Arrays;

import model.data.plant.abilities.runtime.PlantMintAbility;
import model.data.plant.upgrades.PlantLevelUpgrade;
import model.data.plant.upgrades.PlantLevelUpgrades;
import model.data.plant.upgrades.PlantStatBonus;

final class PlantTypeMintDefs {

        private PlantTypeMintDefs() {
        }

        private static PlantLevelUpgrades standardMintUpgrades() {
                return new PlantLevelUpgrades(
                                PlantLevelUpgrade.atLevel(2, PlantStatBonus.EFFECT_DURATION, 1),
                                PlantLevelUpgrade.atLevel(3, PlantStatBonus.COOLDOWN, -5),
                                PlantLevelUpgrade.atLevel(4, PlantStatBonus.RESET_FAMILY_COOLDOWN, true));
        }

        static PlantTypeSpec enlightenMint() {
                return new PlantTypeSpec(61, "Enlighten-mint", PlantCategory.MINT, null,
                                new PlantBaseStats(0, 0, 0, 0, 85),
                                Arrays.asList(
                                                new PlantMintAbility(PlantCategory.SUN_PRODUCER, 5)),
                                null,
                                standardMintUpgrades());
        }

        static PlantTypeSpec appeaseMint() {
                return new PlantTypeSpec(62, "Appease-mint", PlantCategory.MINT, null,
                                new PlantBaseStats(0, 0, 0, 0, 85),
                                Arrays.asList(
                                                new PlantMintAbility(PlantCategory.SHOOTER, 6)),
                                null,
                                standardMintUpgrades());
        }

        static PlantTypeSpec armaMint() {
                return new PlantTypeSpec(63, "Arma-mint", PlantCategory.MINT, null,
                                new PlantBaseStats(0, 0, 0, 0, 85),
                                Arrays.asList(
                                                new PlantMintAbility(PlantCategory.LOBBER, 6)),
                                null,
                                standardMintUpgrades());
        }

        static PlantTypeSpec bombardMint() {
                return new PlantTypeSpec(64, "Bombard-mint", PlantCategory.MINT, null,
                                new PlantBaseStats(0, 0, 0, 0, 85),
                                Arrays.asList(
                                                new PlantMintAbility(PlantCategory.EXPLOSIVE, 6)),
                                null,
                                standardMintUpgrades());
        }

        static PlantTypeSpec enforceMint() {
                return new PlantTypeSpec(65, "Enforce-mint", PlantCategory.MINT, null,
                                new PlantBaseStats(0, 0, 0, 0, 85),
                                Arrays.asList(
                                                new PlantMintAbility(PlantCategory.MELEE, 8)),
                                null,
                                standardMintUpgrades());
        }

        static PlantTypeSpec reinforceMint() {
                return new PlantTypeSpec(66, "Reinforce-mint", PlantCategory.MINT, null,
                                new PlantBaseStats(0, 0, 0, 0, 85),
                                Arrays.asList(
                                                new PlantMintAbility(PlantCategory.DEFENDER, 8)),
                                null,
                                standardMintUpgrades());
        }

        static PlantTypeSpec enchantMint() {
                return new PlantTypeSpec(67, "Enchant-mint", PlantCategory.MINT, null,
                                new PlantBaseStats(0, 0, 0, 0, 85),
                                Arrays.asList(
                                                new PlantMintAbility(PlantCategory.MODIFIER, 8)),
                                null,
                                standardMintUpgrades());
        }

        static PlantTypeSpec pierceMint() {
                return new PlantTypeSpec(68, "Pierce-mint", PlantCategory.MINT, null,
                                new PlantBaseStats(0, 0, 0, 0, 85),
                                Arrays.asList(
                                                new PlantMintAbility(PlantCategory.STRIKE_THROUGH, 8)),
                                null,
                                standardMintUpgrades());
        }

        static PlantTypeSpec catTailMint() {
                return new PlantTypeSpec(69, "catTail-mint", PlantCategory.MINT, null,
                                new PlantBaseStats(0, 0, 0, 0, 85),
                                Arrays.asList(
                                                new PlantMintAbility(PlantCategory.HOMING, 8)),
                                null,
                                standardMintUpgrades());
        }
}
