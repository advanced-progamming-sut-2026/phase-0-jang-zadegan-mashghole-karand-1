package model.data.plant;

import java.util.*;

import model.data.plant.abilities.config.Direction;
import model.data.plant.abilities.config.PlantAbilityConfig;
import model.data.plant.abilities.config.ShootPattern;
import model.data.plant.abilities.runtime.PlantShootAbility;
import model.data.plant.abilities.runtime.PlantSunProduceAbility;
import model.data.plant.effects.config.EffectPhase;
import model.data.plant.effects.config.PlantEffectConfig;
import model.data.plant.effects.runtime.PlantInstantSunEffect;
import model.data.plant.effects.runtime.PlantRapidFireEffect;
import model.data.plant.upgrades.PlantLevelUpgrade;
import model.data.plant.upgrades.PlantLevelUpgrades;
import model.data.plant.upgrades.PlantStatBonus;

public enum PlantType {

        Sunflower(1, "Sunflower", PlantCategory.SUN_PRODUCER,
                        EnumSet.of(PlantTag.DAY),
                        new PlantBaseStats(50, 300, 0, 24, 5),
                        Arrays.asList(
                                        new PlantSunProduceAbility(50, 24, 0)),
                        new PlantInstantSunEffect(150),
                        new PlantLevelUpgrades(
                                        PlantLevelUpgrade.atLevel(2, PlantStatBonus.COOLDOWN, -2),
                                        PlantLevelUpgrade.atLevel(3, PlantStatBonus.HP, 150),
                                        PlantLevelUpgrade.atLevel(4, PlantStatBonus.DOUBLE_SUN_CHANCE, 25))),
        TwinSunflower(2, "Twin Sunflower", PlantCategory.SUN_PRODUCER,
                        EnumSet.of(PlantTag.DAY),
                        new PlantBaseStats(125, 300, 0, 24, 15),
                        Arrays.asList(
                                        new PlantSunProduceAbility(100, 24, 0)),
                        new PlantInstantSunEffect(250),
                        new PlantLevelUpgrades(
                                        PlantLevelUpgrade.atLevel(2, PlantStatBonus.COOLDOWN, -2),
                                        PlantLevelUpgrade.atLevel(3, PlantStatBonus.HP, 150),
                                        PlantLevelUpgrade.atLevel(4, PlantStatBonus.COST, -25))),
        SunShroom(3, "Sun-shroom", PlantCategory.SUN_PRODUCER,
                        EnumSet.of(PlantTag.SHROOM, PlantTag.WRAMP_UP, PlantTag.NIGHT),
                        new PlantBaseStats(25, 300, 0, 24, 5),
                        Arrays.asList(
                                        new PlantSunProduceAbility(25, 24, 0),
                                        new PlantSunProduceAbility(50, 24, 0),
                                        new PlantSunProduceAbility(75, 24, 0)),
                        new PlantInstantSunEffect(225),
                        new PlantLevelUpgrades(
                                        PlantLevelUpgrade.atLevel(2, PlantStatBonus.COOLDOWN, -5),
                                        PlantLevelUpgrade.atLevel(3, PlantStatBonus.HP, 150),
                                        PlantLevelUpgrade.atLevel(4, PlantStatBonus.DOUBLE_SUN_CHANCE, 25))),
        PrimalSunflower(4, "Primal Sunflower", PlantCategory.SUN_PRODUCER,
                        null,
                        new PlantBaseStats(75, 300, 0, 24, 5),
                        Arrays.asList(
                                        new PlantSunProduceAbility(75, 24, 0)),
                        new PlantInstantSunEffect(225),
                        new PlantLevelUpgrades(
                                        new PlantLevelUpgrade(2, PlantStatBonus.COOLDOWN, -2),
                                        new PlantLevelUpgrade(3, PlantStatBonus.HP, 150),
                                        new PlantLevelUpgrade(4, PlantStatBonus.COST, -25))),
        GoldBloom(5, "Gold Bloom", PlantCategory.SUN_PRODUCER,
                        EnumSet.noneOf(PlantTag.class),
                        new PlantBaseStats(0, 0, 0, 0, 75),
                        Arrays.asList(new PlantSunProduceAbility(375, 0, 0)),
                        null,
                        new PlantLevelUpgrades(
                                        new PlantLevelUpgrade(2, PlantStatBonus.COOLDOWN, -5),
                                        new PlantLevelUpgrade(3, PlantStatBonus.SUN_DROP, 50),
                                        new PlantLevelUpgrade(4, PlantStatBonus.COST, -25))),
        PeaShooter(6, "Peashooter", PlantCategory.SHOOTER, EnumSet.of(PlantTag.PEA),
                        new PlantBaseStats(100, 300, 20, 1.5f, 5),
                        Arrays.asList(new PlantShootAbility(20, 1.5f, ProjectileType.PEA,
                                        new ShootPattern(Direction.FORWARD, 0, 1))),
                        new PlantRapidFireEffect(3, 0.1f, Arrays.asList(
                                        new PlantShootAbility(20, ProjectileType.PEA, EffectPhase.ALWAYS))),
                        new PlantLevelUpgrades(
                                        PlantLevelUpgrade.atLevel(2, PlantStatBonus.DAMAGE, 10),
                                        PlantLevelUpgrade.atLevel(3, PlantStatBonus.HP, 150),
                                        PlantLevelUpgrade.atLevel(4, PlantStatBonus.COST, -25))),
        Repeater(7, "Repeater", PlantCategory.SHOOTER, EnumSet.of(PlantTag.PEA),
                        new PlantBaseStats(200, 300, 20, 1.5f, 5),
                        Arrays.asList(new PlantShootAbility(20, 1.5f, ProjectileType.PEA,
                                        new ShootPattern(Direction.FORWARD, 0, 2))),
                        new PlantRapidFireEffect(3, 0.1f, Arrays.asList(
                                        new PlantShootAbility(20, ProjectileType.PEA, EffectPhase.ALWAYS),
                                        new PlantShootAbility(400, ProjectileType.PEA, EffectPhase.END))),
                        new PlantLevelUpgrades(
                                        PlantLevelUpgrade.atLevel(2, PlantStatBonus.DAMAGE, 10),
                                        PlantLevelUpgrade.atLevel(3, PlantStatBonus.HP, 200),
                                        PlantLevelUpgrade.atLevel(4, PlantStatBonus.COST, -25))),
        Threepeater(8, "Threepeater", PlantCategory.SHOOTER, EnumSet.of(PlantTag.PEA),
                        new PlantBaseStats(300, 300, 20, 1.5f, 5),
                        Arrays.asList(
                                        new PlantShootAbility(20, 1.5f, ProjectileType.PEA,
                                                        new ShootPattern(Direction.FORWARD, 1, 1)),
                                        new PlantShootAbility(20, 1.5f, ProjectileType.PEA,
                                                        new ShootPattern(Direction.FORWARD, 0, 1)),
                                        new PlantShootAbility(20, 1.5f, ProjectileType.PEA,
                                                        new ShootPattern(Direction.FORWARD, -1, 1))),
                        new PlantRapidFireEffect(3, 0.1f,
                                        Arrays.asList(
                                                        new PlantShootAbility(20, 0f, ProjectileType.PEA,
                                                                        new ShootPattern(Direction.FORWARD, 2, 1)),
                                                        new PlantShootAbility(20, 0f, ProjectileType.PEA,
                                                                        new ShootPattern(Direction.FORWARD, 1, 1)),
                                                        new PlantShootAbility(20, 0f, ProjectileType.PEA,
                                                                        new ShootPattern(Direction.FORWARD, 0, 1)),
                                                        new PlantShootAbility(20, 0f, ProjectileType.PEA,
                                                                        new ShootPattern(Direction.FORWARD, -1, 1)),
                                                        new PlantShootAbility(20, 0f, ProjectileType.PEA,
                                                                        new ShootPattern(Direction.FORWARD, -2, 1))

                                        )),
                        new PlantLevelUpgrades(
                                        PlantLevelUpgrade.atLevel(2, PlantStatBonus.COST, -25),
                                        PlantLevelUpgrade.atLevel(2, PlantStatBonus.DAMAGE, 10),
                                        PlantLevelUpgrade.atLevel(2, PlantStatBonus.HP, 200))),
        SnowPea(9, "Snow Pea", PlantCategory.SHOOTER, EnumSet.of(PlantTag.PEA, PlantTag.ICE),
                        new PlantBaseStats(150, 300, 20, 1.5f, 5),
                        Arrays.asList(new PlantShootAbility(20, 1.5f, ProjectileType.ICE,
                                        new ShootPattern(Direction.FORWARD, 0, 1))),
                        new PlantRapidFireEffect(3, 0.1f, Arrays.asList(
                                        new PlantShootAbility(20, ProjectileType.ICE, EffectPhase.ALWAYS),
                                        new PlantShootAbility(0, ProjectileType.FREEZE_LINE, EffectPhase.START))),
                        new PlantLevelUpgrades(
                                        PlantLevelUpgrade.atLevel(2, PlantStatBonus.DAMAGE, 10),
                                        PlantLevelUpgrade.atLevel(3, PlantStatBonus.EFFECT_DURATION, 2),
                                        PlantLevelUpgrade.atLevel(4, PlantStatBonus.COST, -25))),
        Rotobaga(10, "Rotobaga", PlantCategory.SHOOTER, null,
                        new PlantBaseStats(150, 300, 10, 1.5f, 5),
                        Arrays.asList(
                                        new PlantShootAbility(10, 1.5f, ProjectileType.ROTO_SEED,
                                                        new ShootPattern(Direction.UP_LEFT, 0, 3)),
                                        new PlantShootAbility(10, 1.5f, ProjectileType.ROTO_SEED,
                                                        new ShootPattern(Direction.UP_RIGHT, 0, 3)),
                                        new PlantShootAbility(10, 1.5f, ProjectileType.ROTO_SEED,
                                                        new ShootPattern(Direction.DOWN_LEFT, 0, 3)),
                                        new PlantShootAbility(10, 1.5f, ProjectileType.ROTO_SEED,
                                                        new ShootPattern(Direction.DOWN_RIGHT, 0, 3))),
                        new PlantRapidFireEffect(3, 0.1f,
                                        Arrays.asList(
                                                        new PlantShootAbility(10, 0f, ProjectileType.ROTO_SEED,
                                                                        new ShootPattern(Direction.UP_RIGHT, 0, 1)),
                                                        new PlantShootAbility(10, 0f, ProjectileType.ROTO_SEED,
                                                                        new ShootPattern(Direction.UP_LEFT, 0, 1)),
                                                        new PlantShootAbility(10, 0f, ProjectileType.ROTO_SEED,
                                                                        new ShootPattern(Direction.DOWN_RIGHT, 0, 1)),
                                                        new PlantShootAbility(10, 0f, ProjectileType.ROTO_SEED,
                                                                        new ShootPattern(Direction.DOWN_LEFT, 0, 1)))),
                        new PlantLevelUpgrades(
                                        new PlantLevelUpgrade(2, PlantStatBonus.DAMAGE, 10),
                                        new PlantLevelUpgrade(3, PlantStatBonus.HP, 150),
                                        new PlantLevelUpgrade(4, PlantStatBonus.COST, -25)));

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