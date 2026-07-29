package model.data.plant;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import model.data.plant.abilities.config.AreaShape;
import model.data.plant.abilities.effects.DamageEffect;
import model.data.plant.abilities.effects.InstantKillEffect;
import model.data.plant.abilities.runtime.PlantMeleeAbility;
import model.data.plant.effects.runtime.PlantMeleeEffect;
import model.data.plant.upgrades.PlantLevelUpgrade;
import model.data.plant.upgrades.PlantLevelUpgrades;
import model.data.plant.upgrades.PlantStatBonus;

final class PlantTypeMeleeDefs {

        private PlantTypeMeleeDefs() {
        }

        static PlantTypeSpec bonkChoy() {
                return new PlantTypeSpec(39, "Bonk Choy", PlantCategory.MELEE, null,
                                new PlantBaseStats(150, 300, 15, 0.25f, 5),
                                Arrays.asList(
                                                new PlantMeleeAbility(AreaShape.FRONT_OR_BACK, 1, 0.25f,
                                                                List.of(new DamageEffect(15)), 0)

                                ),
                                new PlantMeleeEffect(3, 8, List.of(
                                                new PlantMeleeAbility(AreaShape.RADIUS_3x3, 1, 0f,
                                                                List.of(new DamageEffect(15)), 0))),
                                new PlantLevelUpgrades(
                                                PlantLevelUpgrade.atLevel(2, PlantStatBonus.DAMAGE, 5),
                                                PlantLevelUpgrade.atLevel(3, PlantStatBonus.ATTACK_SPEED, 10),
                                                PlantLevelUpgrade.atLevel(4, PlantStatBonus.HP, 200)));
        }

        static PlantTypeSpec phatBeet() {
                return new PlantTypeSpec(40, "Phat Beet", PlantCategory.MELEE, EnumSet.of(PlantTag.AOE),
                                new PlantBaseStats(150, 300, 15, 2, 5),
                                Arrays.asList(
                                                new PlantMeleeAbility(AreaShape.RADIUS_3x3, 1, 2,
                                                                List.of(new DamageEffect(15)), 0)),
                                new PlantMeleeEffect(0, 0, List.of(
                                                new PlantMeleeAbility(AreaShape.FULL_BOARD, -1, 0f,
                                                                List.of(new DamageEffect(45)), 0))),
                                new PlantLevelUpgrades(
                                                PlantLevelUpgrade.atLevel(2, PlantStatBonus.DAMAGE, 10),
                                                PlantLevelUpgrade.atLevel(3, PlantStatBonus.ATTACK_SPEED, 10),
                                                PlantLevelUpgrade.atLevel(4, PlantStatBonus.HP, 200)

                                ));
        }

        static PlantTypeSpec chomper() {
                return new PlantTypeSpec(41, "Chomper", PlantCategory.MELEE, null,
                                new PlantBaseStats(150, 300, 0, 40, 5),
                                Arrays.asList(
                                                new PlantMeleeAbility(AreaShape.ADJACENT, 1, 0,
                                                                List.of(new InstantKillEffect()), 400)

                                ),
                                new PlantMeleeEffect(0, 0, List.of(
                                                new PlantMeleeAbility(AreaShape.ROW, 3, 0f,
                                                                List.of(new InstantKillEffect()), 400))),
                                new PlantLevelUpgrades(
                                                PlantLevelUpgrade.atLevel(2, PlantStatBonus.COOLDOWN, -2),
                                                PlantLevelUpgrade.atLevel(3, PlantStatBonus.HP, 200),
                                                PlantLevelUpgrade.atLevel(4, PlantStatBonus.COOLDOWN, -3)

                                ));
        }

        static PlantTypeSpec wasabiWhip() {
                return new PlantTypeSpec(42, "Wasabi Whip", PlantCategory.MELEE, EnumSet.of(PlantTag.FIRE),
                                new PlantBaseStats(150, 300, 40, 2, 5),
                                Arrays.asList(
                                                new PlantMeleeAbility(AreaShape.FRONT_OR_BACK, -1, 2,
                                                                List.of(new DamageEffect(40)), 0)

                                ),
                                new PlantMeleeEffect(0, 0, List.of(
                                                new PlantMeleeAbility(AreaShape.RADIUS_3x3, -1, 0f,
                                                                List.of(new DamageEffect(40)), 0))),
                                new PlantLevelUpgrades(
                                                PlantLevelUpgrade.atLevel(2, PlantStatBonus.DAMAGE, 10),
                                                PlantLevelUpgrade.atLevel(3, PlantStatBonus.RANGE, 1),
                                                PlantLevelUpgrade.atLevel(4, PlantStatBonus.HP, 200)

                                ));
        }
}
