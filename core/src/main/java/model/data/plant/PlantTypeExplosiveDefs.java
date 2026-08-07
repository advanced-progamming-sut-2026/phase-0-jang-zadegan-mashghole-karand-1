package model.data.plant;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import model.data.plant.abilities.config.AreaShape;
import model.data.plant.abilities.config.ExplodeTrigger;
import model.data.plant.abilities.effects.DamageEffect;
import model.data.plant.abilities.effects.FreezeEffect;
import model.data.plant.abilities.effects.InstantKillEffect;
import model.data.plant.abilities.runtime.ActionTarget;
import model.data.plant.abilities.runtime.PlantExplodeAbility;
import model.data.plant.abilities.runtime.PlantTileActionAbility;
import model.data.plant.effects.runtime.PlantExplodeEffect;
import model.data.plant.upgrades.PlantLevelUpgrade;
import model.data.plant.upgrades.PlantLevelUpgrades;
import model.data.plant.upgrades.PlantStatBonus;
import model.board.TileType;

final class PlantTypeExplosiveDefs {

        private PlantTypeExplosiveDefs() {
        }

        static PlantTypeSpec potatoMine() {
                return new PlantTypeSpec(30, "Potato-Mine", PlantCategory.EXPLOSIVE,
                                EnumSet.of(PlantTag.TRAP, PlantTag.CHARGE),
                                new PlantBaseStats(25, 300, 1800, 0f, 25),
                                Arrays.asList(
                                                new PlantExplodeAbility(ExplodeTrigger.ON_ZOMBIE_ENTER,
                                                                AreaShape.SINGLE_TILE,
                                                                1, 150, false, List.of(new DamageEffect(1800)))),
                                new PlantExplodeEffect(null, 0, List.of(), true, 2),
                                new PlantLevelUpgrades(
                                                PlantLevelUpgrade.atLevel(2, PlantStatBonus.COOLDOWN, -3),
                                                PlantLevelUpgrade.atLevel(3, PlantStatBonus.COOLDOWN, -5),
                                                PlantLevelUpgrade.atLevel(4, PlantStatBonus.DAMAGE, 600)));
        }

        static PlantTypeSpec primalPotatoMine() {
                return new PlantTypeSpec(31, "Primal_Potato-Mine", PlantCategory.EXPLOSIVE,
                                EnumSet.of(PlantTag.TRAP, PlantTag.CHARGE),
                                new PlantBaseStats(50, 300, 2400, 0f, 5),
                                Arrays.asList(
                                                new PlantExplodeAbility(ExplodeTrigger.ON_ZOMBIE_ENTER,
                                                                AreaShape.RADIUS_3x3,
                                                                -1, 50, false, List.of(new DamageEffect(2400)))),
                                new PlantExplodeEffect(null, 0, List.of(), true, 2),
                                new PlantLevelUpgrades(
                                                PlantLevelUpgrade.atLevel(2, PlantStatBonus.COOLDOWN, -1),
                                                PlantLevelUpgrade.atLevel(3, PlantStatBonus.COOLDOWN, -3),
                                                PlantLevelUpgrade.atLevel(4, PlantStatBonus.DAMAGE, 400)));
        }

        static PlantTypeSpec cherryBomb() {
                return new PlantTypeSpec(32, "Cherry Bomb", PlantCategory.EXPLOSIVE, null,
                                new PlantBaseStats(150, 0, 1800, 0f, 35),
                                Arrays.asList(
                                                new PlantExplodeAbility(ExplodeTrigger.INSTANT, AreaShape.RADIUS_3x3,
                                                                -1,
                                                                0, false, List.of(new DamageEffect(1800)))),
                                null,
                                new PlantLevelUpgrades(
                                                PlantLevelUpgrade.atLevel(2, PlantStatBonus.COOLDOWN, -5),
                                                PlantLevelUpgrade.atLevel(3, PlantStatBonus.DAMAGE, 600),
                                                PlantLevelUpgrade.atLevel(4, PlantStatBonus.COST, -25)));
        }

        static PlantTypeSpec squash() {
                return new PlantTypeSpec(33, "Squash", PlantCategory.EXPLOSIVE, EnumSet.of(PlantTag.TRAP),
                                new PlantBaseStats(50, 300, 1800, 0f, 20),
                                Arrays.asList(
                                                new PlantExplodeAbility(ExplodeTrigger.ON_ADJACENT_ZOMBIE,
                                                                AreaShape.ADJACENT,
                                                                1, 0, false, List.of(new DamageEffect(1800)))),
                                new PlantExplodeEffect(AreaShape.FULL_BOARD, 2,
                                                List.of(new DamageEffect(1800)), false, 0),
                                new PlantLevelUpgrades(
                                                PlantLevelUpgrade.atLevel(2, PlantStatBonus.COOLDOWN, -3),
                                                PlantLevelUpgrade.atLevel(3, PlantStatBonus.DAMAGE, 600),
                                                PlantLevelUpgrade.atLevel(4, PlantStatBonus.DOUBLE_CRUSH, 2)));
        }

        static PlantTypeSpec jalapeno() {
                return new PlantTypeSpec(35, "Jalapeno", PlantCategory.EXPLOSIVE, EnumSet.of(PlantTag.FIRE),
                                new PlantBaseStats(125, 0, 1800, 0f, 35),
                                Arrays.asList(
                                                new PlantExplodeAbility(ExplodeTrigger.INSTANT, AreaShape.ROW, -1,
                                                                0, false, List.of(new DamageEffect(1800)))),
                                null,
                                new PlantLevelUpgrades(
                                                PlantLevelUpgrade.atLevel(2, PlantStatBonus.COOLDOWN, -5),
                                                PlantLevelUpgrade.atLevel(3, PlantStatBonus.DAMAGE, 600),
                                                PlantLevelUpgrade.atLevel(4, PlantStatBonus.COST, -25)));
        }

        static PlantTypeSpec doomShroom() {
                return new PlantTypeSpec(36, "Doom-shroom", PlantCategory.EXPLOSIVE, EnumSet.of(PlantTag.SHROOM),
                                new PlantBaseStats(125, 0, 1800, 0f, 35),
                                Arrays.asList(
                                                new PlantExplodeAbility(ExplodeTrigger.INSTANT, AreaShape.FULL_BOARD,
                                                                -1,
                                                                0, false, List.of(new DamageEffect(1800)))),
                                null,
                                new PlantLevelUpgrades(
                                                PlantLevelUpgrade.atLevel(2, PlantStatBonus.COOLDOWN, -5),
                                                PlantLevelUpgrade.atLevel(3, PlantStatBonus.DAMAGE, 800),
                                                PlantLevelUpgrade.atLevel(4, PlantStatBonus.COST, -50)));
        }

        static PlantTypeSpec tangleKelp() {
                return new PlantTypeSpec(37, "Tangle Kelp", PlantCategory.EXPLOSIVE,
                                EnumSet.of(PlantTag.TRAP, PlantTag.WATER),
                                new PlantBaseStats(25, 300, 0, 0f, 15),
                                Arrays.asList(
                                                new PlantExplodeAbility(ExplodeTrigger.ON_ZOMBIE_ENTER,
                                                                AreaShape.SINGLE_TILE, 1, 0, true,
                                                                List.of(new InstantKillEffect()))),
                                new PlantExplodeEffect(AreaShape.FULL_BOARD, 3,
                                                List.of(new InstantKillEffect()), false, 0),
                                new PlantLevelUpgrades(
                                                PlantLevelUpgrade.atLevel(2, PlantStatBonus.COOLDOWN, -5),
                                                PlantLevelUpgrade.atLevel(3, PlantStatBonus.TARGET_PRIORITY, 1),
                                                PlantLevelUpgrade.atLevel(4, PlantStatBonus.COST, -25)));
        }

        static PlantTypeSpec icebergLettuce() {
                return new PlantTypeSpec(38, "Iceberg Lettuce", PlantCategory.EXPLOSIVE,
                                EnumSet.of(PlantTag.TRAP, PlantTag.ICE),
                                new PlantBaseStats(0, 300, 0, 0f, 20),
                                Arrays.asList(
                                                new PlantExplodeAbility(ExplodeTrigger.ON_ZOMBIE_ENTER,
                                                                AreaShape.SINGLE_TILE, 1, 0, false,
                                                                List.of(new FreezeEffect(100)))),
                                new PlantExplodeEffect(AreaShape.FULL_BOARD, -1,
                                                List.of(new FreezeEffect(10)), false, 0),
                                new PlantLevelUpgrades(
                                                PlantLevelUpgrade.atLevel(2, PlantStatBonus.COOLDOWN, -2),
                                                PlantLevelUpgrade.atLevel(3, PlantStatBonus.EFFECT_DURATION, 2),
                                                PlantLevelUpgrade.atLevel(4, PlantStatBonus.COST, -25)));
        }

        static PlantTypeSpec iceShroom() {
                return new PlantTypeSpec(57, "Ice_shroom", PlantCategory.EXPLOSIVE,
                                EnumSet.of(PlantTag.SHROOM, PlantTag.ICE),
                                new PlantBaseStats(75, 0, 0, 0f, 50),
                                Arrays.asList(
                                                new PlantExplodeAbility(ExplodeTrigger.INSTANT, AreaShape.FULL_BOARD,
                                                                -1,
                                                                0, false,
                                                                List.of(new FreezeEffect(100), new DamageEffect(0)))),
                                null,
                                new PlantLevelUpgrades(
                                                PlantLevelUpgrade.atLevel(2, PlantStatBonus.EFFECT_DURATION, 2),
                                                PlantLevelUpgrade.atLevel(3, PlantStatBonus.COOLDOWN, -5),
                                                PlantLevelUpgrade.atLevel(4, PlantStatBonus.DAMAGE, 50)));
        }

        static PlantTypeSpec hotPotato() {
                return new PlantTypeSpec(59, "Hot Potato", PlantCategory.EXPLOSIVE, EnumSet.of(PlantTag.FIRE),
                                new PlantBaseStats(0, 0, 0, 0f, 5),
                                Arrays.asList(
                                                new PlantTileActionAbility(ActionTarget.ICE, TileType.ICE,
                                                                AreaShape.SINGLE_TILE, 0)),
                                null,
                                new PlantLevelUpgrades(
                                                PlantLevelUpgrade.atLevel(2, PlantStatBonus.COOLDOWN, -2),
                                                PlantLevelUpgrade.atLevel(3, PlantStatBonus.MELT_AREA_3x3, true),
                                                PlantLevelUpgrade.atLevel(4, PlantStatBonus.EXPLODE_ON_FINISH, true)));
        }

        static PlantTypeSpec graveBuster() {
                return new PlantTypeSpec(60, "Grave Buster", PlantCategory.EXPLOSIVE, null,
                                new PlantBaseStats(0, 0, 0, 0f, 10),
                                Arrays.asList(
                                                new PlantTileActionAbility(ActionTarget.GRAVE, null,
                                                                AreaShape.SINGLE_TILE, 5)),
                                null,
                                new PlantLevelUpgrades(
                                                PlantLevelUpgrade.atLevel(2, PlantStatBonus.COOLDOWN, -1),
                                                PlantLevelUpgrade.atLevel(3, PlantStatBonus.COOLDOWN, -2),
                                                PlantLevelUpgrade.atLevel(4, PlantStatBonus.EXPLODE_ON_FINISH, true)));
        }
}
