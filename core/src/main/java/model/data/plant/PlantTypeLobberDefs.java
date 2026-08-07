package model.data.plant;

import java.util.Arrays;
import java.util.EnumSet;

import model.data.plant.abilities.config.Direction;
import model.data.plant.abilities.config.ShootPattern;
import model.data.plant.abilities.runtime.PlantLobAbility;
import model.data.plant.effects.runtime.PlantRapidLobEffect;
import model.data.plant.upgrades.PlantLevelUpgrade;
import model.data.plant.upgrades.PlantLevelUpgrades;
import model.data.plant.upgrades.PlantStatBonus;
import model.data.projectile.ProjectileType;

final class PlantTypeLobberDefs {

        private PlantTypeLobberDefs() {
        }

        static PlantTypeSpec cabbagePult() {
                return new PlantTypeSpec(25, "Cabbage-pult", PlantCategory.LOBBER, null,
                                new PlantBaseStats(100, 300, 40, 2.9f, 5),
                                Arrays.asList(
                                                new PlantLobAbility(40, 2.9f, ProjectileType.CABBAGE,
                                                                new ShootPattern(Direction.FORWARD, 0, 1))),
                                new PlantRapidLobEffect(5, 0.1f,
                                                Arrays.asList(
                                                                new PlantLobAbility(40, 0f, ProjectileType.CABBAGE,
                                                                                new ShootPattern(Direction.FORWARD, 0,
                                                                                                5, true)))),
                                new PlantLevelUpgrades(
                                                PlantLevelUpgrade.atLevel(2, PlantStatBonus.DAMAGE, 10),
                                                PlantLevelUpgrade.atLevel(3, PlantStatBonus.ATTACK_SPEED, 15),
                                                PlantLevelUpgrade.atLevel(4, PlantStatBonus.HP, 150)));
        }

        static PlantTypeSpec kernelPult() {
                return new PlantTypeSpec(26, "Kernel-pult", PlantCategory.LOBBER, null,
                                new PlantBaseStats(100, 300, 20, 2.9f, 5),
                                Arrays.asList(
                                                new PlantLobAbility(20, 2.9f, ProjectileType.KERNEL,
                                                                new ShootPattern(Direction.FORWARD, 0, 1),
                                                                0.05f, 40, 0)),
                                new PlantRapidLobEffect(5, 0.1f,
                                                Arrays.asList(
                                                                new PlantLobAbility(40, 0f, ProjectileType.BUTTER,
                                                                                new ShootPattern(Direction.FORWARD, 0,
                                                                                                5, true),
                                                                                1f, 40, 0))),
                                new PlantLevelUpgrades(
                                                PlantLevelUpgrade.atLevel(2, PlantStatBonus.SPECIAL_CHANGE, 5),
                                                PlantLevelUpgrade.atLevel(3, PlantStatBonus.DAMAGE, 10),
                                                PlantLevelUpgrade.atLevel(4, PlantStatBonus.HP, 150)));
        }

        static PlantTypeSpec melonPult() {
                return new PlantTypeSpec(27, "Melon-pult", PlantCategory.LOBBER, EnumSet.of(PlantTag.AOE),
                                new PlantBaseStats(325, 300, 80, 2.9f, 5),
                                Arrays.asList(
                                                new PlantLobAbility(80, 2.9f, ProjectileType.MELON,
                                                                new ShootPattern(Direction.FORWARD, 0, 1), 0f, 0, 1)),
                                new PlantRapidLobEffect(5, 0.1f,
                                                Arrays.asList(
                                                                new PlantLobAbility(80, 0f, ProjectileType.MELON,
                                                                                new ShootPattern(Direction.FORWARD, 0,
                                                                                                5, true),
                                                                                0f, 0, 1))),
                                new PlantLevelUpgrades(
                                                PlantLevelUpgrade.atLevel(2, PlantStatBonus.COST, -25),
                                                PlantLevelUpgrade.atLevel(3, PlantStatBonus.AOE_DAMAGE, 15),
                                                PlantLevelUpgrade.atLevel(4, PlantStatBonus.DAMAGE, 30)));
        }

        static PlantTypeSpec winterMelon() {
                return new PlantTypeSpec(28, "Winter-Melon", PlantCategory.LOBBER,
                                EnumSet.of(PlantTag.ICE, PlantTag.AOE),
                                new PlantBaseStats(500, 300, 80, 2.9f, 5),
                                Arrays.asList(
                                                new PlantLobAbility(80, 2.9f, ProjectileType.ICE_MELON,
                                                                new ShootPattern(Direction.FORWARD, 0, 1),
                                                                0f, 0, 1)),
                                new PlantRapidLobEffect(5, 0.1f,
                                                Arrays.asList(
                                                                new PlantLobAbility(80, 0f, ProjectileType.ICE_MELON,
                                                                                new ShootPattern(Direction.FORWARD, 0,
                                                                                                5, true),
                                                                                0f, 0, 1))),
                                new PlantLevelUpgrades(
                                                PlantLevelUpgrade.atLevel(2, PlantStatBonus.COST, -50),
                                                PlantLevelUpgrade.atLevel(3, PlantStatBonus.AOE_DAMAGE, 15),
                                                PlantLevelUpgrade.atLevel(4, PlantStatBonus.COST, -25)));
        }

        static PlantTypeSpec pepperPult() {
                return new PlantTypeSpec(29, "Pepper-pult", PlantCategory.LOBBER,
                                EnumSet.of(PlantTag.AOE, PlantTag.FIRE),
                                new PlantBaseStats(200, 300, 50, 2.9f, 5),
                                Arrays.asList(
                                                new PlantLobAbility(50, 2.9f, ProjectileType.PEPPER,
                                                                new ShootPattern(Direction.FORWARD, 0, 1),
                                                                0f, 0, 1)),
                                new PlantRapidLobEffect(5, 0.1f,
                                                Arrays.asList(
                                                                new PlantLobAbility(50, 0f, ProjectileType.PEPPER,
                                                                                new ShootPattern(Direction.FORWARD, 0,
                                                                                                3, true),
                                                                                0f, 0, 1))),
                                new PlantLevelUpgrades(
                                                PlantLevelUpgrade.atLevel(2, PlantStatBonus.DAMAGE, 15),
                                                PlantLevelUpgrade.atLevel(3, PlantStatBonus.RADIUS, 1),
                                                PlantLevelUpgrade.atLevel(4, PlantStatBonus.COST, -25)));
        }
}
