package model.data.plant;
import java.util.*;

import model.data.plant.abilities.config.*;
import model.data.plant.abilities.effects.DamageEffect;
import model.data.plant.abilities.effects.FreezeEffect;
import model.data.plant.abilities.effects.InstantKillEffect;
import model.data.plant.abilities.runtime.*;
import model.data.plant.effects.config.EffectPhase;
import model.data.plant.effects.config.PlantEffectConfig;
import model.data.plant.effects.runtime.*;
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
                    new PlantLevelUpgrade(4, PlantStatBonus.COST, -25))),
    SplitPea(12, "Split Pea", PlantCategory.SHOOTER, EnumSet.of(PlantTag.PEA),
            new PlantBaseStats(125, 300, 20, 1.5f, 5),
            Arrays.asList(
                    new PlantShootAbility(20, 1.5f, ProjectileType.PEA, new ShootPattern(Direction.FORWARD, 0, 1)),
                    new PlantShootAbility(20, 1.5f, ProjectileType.PEA, new ShootPattern(Direction.BACK, 0, 2))
            ),
            new PlantRapidFireEffect(3, 0.1f,
                    Arrays.asList(
                            new PlantShootAbility(20, 0f, ProjectileType.PEA, new ShootPattern(Direction.FORWARD, 0, 1)),
                            new PlantShootAbility(20, 0f, ProjectileType.PEA, new ShootPattern(Direction.BACK, 0, 1))
                    )),
            new PlantLevelUpgrades(
                    new PlantLevelUpgrade(2, PlantStatBonus.DAMAGE, 10),
                    new PlantLevelUpgrade(3, PlantStatBonus.HP, 200),
                    new PlantLevelUpgrade(4, PlantStatBonus.COST, -25)
            )),
    Citron(13, "Citron", PlantCategory.SHOOTER, EnumSet.of(PlantTag.CHARGE),
            new PlantBaseStats(350, 300, 800, 9, 5),
            Arrays.asList(
                    new PlantShootAbility(800, 9f, ProjectileType.LASER, new ShootPattern(Direction.FORWARD, 0, 1))
            ),
            new PlantRapidFireEffect(3, 0.1f,
                    Arrays.asList(
                            new PlantShootAbility(8000, 0f, ProjectileType.PLASMA, new ShootPattern(Direction.FORWARD, 0, 1))
                    )),
            new PlantLevelUpgrades(
                    new PlantLevelUpgrade(2, PlantStatBonus.COOLDOWN, -1),
                    new PlantLevelUpgrade(3, PlantStatBonus.DAMAGE, 150),
                    new PlantLevelUpgrade(4, PlantStatBonus.COST, -50)
            )
    ),
    BowlingBulb(16, "Bowling Bulb", PlantCategory.SHOOTER, EnumSet.of(PlantTag.CHARGE),
            new PlantBaseStats(200, 300, 40, 2, 5),
            Arrays.asList(
                    new PlantShootAbility(40, 2, ProjectileType.BOUNCING_AQUA, new ShootPattern(Direction.FORWARD, 0, 1)),
                    new PlantShootAbility(80, 5, ProjectileType.BOUNCING_ORANGE, new ShootPattern(Direction.FORWARD, 0, 1)),
                    new PlantShootAbility(120, 10, ProjectileType.BOUNCING_BLUE, new ShootPattern(Direction.FORWARD, 0, 1))
            ),
            new PlantRapidFireEffect(1, 1f,
                    Arrays.asList(
                            new PlantShootAbility(600, 1.5f, ProjectileType.SUPER_BULB, new ShootPattern(Direction.FORWARD, 0, 1))
                    )),
            new PlantLevelUpgrades(
                    new PlantLevelUpgrade(2, PlantStatBonus.REGEN, -1),
                    new PlantLevelUpgrade(3, PlantStatBonus.DAMAGE, 15),
                    new PlantLevelUpgrade(4, PlantStatBonus.COST, -25)
            )
    ),
    FirePeashooter(18, "Fire Peashooter", PlantCategory.SHOOTER, EnumSet.of(PlantTag.PEA, PlantTag.FIRE),
            new PlantBaseStats(175, 300, 40, 1.5f, 5),
            Arrays.asList(
                    new PlantShootAbility(40, 1.5f, ProjectileType.FIRE, new ShootPattern(Direction.FORWARD, 0, 1))
            ),
            new PlantRapidFireEffect(3, 0.1f,
                    Arrays.asList(
                            new PlantShootAbility(40, 0, ProjectileType.FIRE, new ShootPattern(Direction.FORWARD, 0, 1))
                    )),
            new PlantLevelUpgrades(
                    new PlantLevelUpgrade(2, PlantStatBonus.DAMAGE, 10),
                    new PlantLevelUpgrade(3, PlantStatBonus.HP, 200),
                    new PlantLevelUpgrade(4, PlantStatBonus.COST, -25)
            )
    ),
    Starfruit(19, "Starfruit", PlantCategory.SHOOTER, null,
            new PlantBaseStats(150, 300, 20, 1.5f, 5),
            Arrays.asList(
                    new PlantShootAbility(20, 1.5f, ProjectileType.STAR, new ShootPattern(Direction.BACK, 0, 1)),
                    new PlantShootAbility(20, 1.5f, ProjectileType.STAR, new ShootPattern(Direction.UP, 0, 1)),
                    new PlantShootAbility(20, 1.5f, ProjectileType.STAR, new ShootPattern(Direction.DOWN, 0, 1)),
                    new PlantShootAbility(20, 1.5f, ProjectileType.STAR, new ShootPattern(Direction.UP_RIGHT, 0, 1)),
                    new PlantShootAbility(20, 1.5f, ProjectileType.STAR, new ShootPattern(Direction.DOWN_RIGHT, 0, 1))
            ),
            new PlantRapidFireEffect(3, 0.1f,
                    Arrays.asList(
                            new PlantShootAbility(20, 0, ProjectileType.STAR, new ShootPattern(Direction.BACK, 0, 1)),
                            new PlantShootAbility(20, 0, ProjectileType.STAR, new ShootPattern(Direction.UP, 0, 1)),
                            new PlantShootAbility(20, 0, ProjectileType.STAR, new ShootPattern(Direction.DOWN, 0, 1)),
                            new PlantShootAbility(20, 0, ProjectileType.STAR, new ShootPattern(Direction.UP_RIGHT, 0, 1)),
                            new PlantShootAbility(20, 0, ProjectileType.STAR, new ShootPattern(Direction.DOWN_RIGHT, 0, 1)),
                            new PlantShootAbility(20, 0, ProjectileType.STAR, new ShootPattern(Direction.DOWN_LEFT, 0, 1)),
                            new PlantShootAbility(20, 0, ProjectileType.STAR, new ShootPattern(Direction.UP_LEFT, 0, 1))
                    )),
            new PlantLevelUpgrades(
                    new PlantLevelUpgrade(2, PlantStatBonus.ATTACK_SPEED, 10),
                    new PlantLevelUpgrade(3, PlantStatBonus.DAMAGE, 10),
                    new PlantLevelUpgrade(4, PlantStatBonus.COST, -25)
            )
    ),
    GooPeashooter(20, "Goo Peashooter", PlantCategory.SHOOTER, EnumSet.of(PlantTag.POISON),
            new PlantBaseStats(125, 300, 20, 1.5f, 5),
            Arrays.asList(
                    new PlantShootAbility(20, 1.5f, ProjectileType.PEA, new ShootPattern(Direction.FORWARD, 0, 1))
            ),
            new PlantRapidFireEffect(3, 0.1f,
                    Arrays.asList(
                            new PlantShootAbility(20, 0f, ProjectileType.POISON, new ShootPattern(Direction.FORWARD, 0, 1))
                    )
            ),
            new PlantLevelUpgrades(
                    new PlantLevelUpgrade(2, PlantStatBonus.DAMAGE_PER_TICK, 5),
                    new PlantLevelUpgrade(3, PlantStatBonus.HP, 150),
                    new PlantLevelUpgrade(4, PlantStatBonus.COST, -25)
            )
    ),
    MegaGatlingPea(21, "Mega Gatling Pea", PlantCategory.SHOOTER, EnumSet.of(PlantTag.PEA),
            new PlantBaseStats(400, 300, 20, 1.5f, 5),
            Arrays.asList(
                    new PlantShootAbility(20, 1.5f, ProjectileType.PEA,
                            new ShootPattern(Direction.FORWARD, 0, 4))
            ),
            new PlantRapidFireEffect(3, 0.1f,
                    Arrays.asList(
                            new PlantShootAbility(20, 0f, ProjectileType.PEA,
                                    new ShootPattern(Direction.FORWARD, 0, 1)),
                            new PlantShootAbility(400, ProjectileType.PEA, EffectPhase.END),
                            new PlantShootAbility(400, ProjectileType.PEA, EffectPhase.END),
                            new PlantShootAbility(400, ProjectileType.PEA, EffectPhase.END),
                            new PlantShootAbility(400, ProjectileType.PEA, EffectPhase.END)
                    )),
            new PlantLevelUpgrades(
                    PlantLevelUpgrade.atLevel(2, PlantStatBonus.DAMAGE, 10),
                    PlantLevelUpgrade.atLevel(3, PlantStatBonus.SPECIAL_CHANGE, 5),
                    PlantLevelUpgrade.atLevel(4, PlantStatBonus.COST, -50)
            )
    ),
    SeaShroom(22, "Sea-shroom", PlantCategory.SHOOTER,
            EnumSet.of(PlantTag.SHROOM, PlantTag.WATER),
            new PlantBaseStats(0, 300, 20, 1.5f, 15),
            Arrays.asList(
                    new PlantShootAbility(20, 1.5f, ProjectileType.FUME,
                            new ShootPattern(Direction.FORWARD, 0, 1))
            ),
            new PlantRapidFireEffect(3, 0.1f,
                    Arrays.asList(
                            new PlantShootAbility(20, 0f, ProjectileType.FUME,
                                    new ShootPattern(Direction.FORWARD, 0, 1))
                    )),
            new PlantLevelUpgrades(
                    PlantLevelUpgrade.atLevel(2, PlantStatBonus.RANGE, 1),
                    PlantLevelUpgrade.atLevel(3, PlantStatBonus.DAMAGE, 5),
                    PlantLevelUpgrade.atLevel(4, PlantStatBonus.LIFE_SPAN, 10)
            )
    ),
    PuffShroom(23, "Puff-shroom", PlantCategory.SHOOTER, EnumSet.of(PlantTag.SHROOM),
            new PlantBaseStats(0, 300, 20, 1.5f, 5),
            Arrays.asList(
                    new PlantShootAbility(20, 1.5f, ProjectileType.FUME,
                            new ShootPattern(Direction.FORWARD, 0, 1))
            ),
            new PlantRapidFireEffect(3, 0.1f,
                    Arrays.asList(
                            new PlantShootAbility(20, 0f, ProjectileType.FUME,
                                    new ShootPattern(Direction.FORWARD, 0, 1))
                    )),
            new PlantLevelUpgrades(
                    PlantLevelUpgrade.atLevel(2, PlantStatBonus.LIFE_SPAN, 10),
                    PlantLevelUpgrade.atLevel(3, PlantStatBonus.DAMAGE, 10),
                    PlantLevelUpgrade.atLevel(4, PlantStatBonus.RANGE, 1)
            )
    ),
    Cabbage_pult(25, "Cabbage-pult", PlantCategory.LOBBER, null,
            new PlantBaseStats(100, 300, 40, 2.9f, 5),
            Arrays.asList(
                    new PlantLobAbility(40, 2.9f, ProjectileType.CABBAGE,
                            new ShootPattern(Direction.FORWARD, 0, 1))
            ),
            new PlantRapidLobEffect(5, 0.1f,
                    Arrays.asList(
                            new PlantLobAbility(40, 0f, ProjectileType.CABBAGE,
                                    new ShootPattern(Direction.FORWARD, 0, 5, true))
                    )),
            new PlantLevelUpgrades(
                    PlantLevelUpgrade.atLevel(2, PlantStatBonus.DAMAGE, 10),
                    PlantLevelUpgrade.atLevel(3, PlantStatBonus.ATTACK_SPEED, 15),
                    PlantLevelUpgrade.atLevel(4, PlantStatBonus.HP, 150)
            )
    ),
    Kernel_pult(26, "Kernel-pult", PlantCategory.LOBBER, null,
            new PlantBaseStats(100, 300, 20, 2.9f, 5),
            Arrays.asList(
                    new PlantLobAbility(20, 2.9f, ProjectileType.KERNEL,
                            new ShootPattern(Direction.FORWARD, 0, 1),
                            0.05f, 40, 0)
            ),
            new PlantRapidLobEffect(5, 0.1f,
                    Arrays.asList(
                            new PlantLobAbility(40, 0f, ProjectileType.BUTTER,
                                    new ShootPattern(Direction.FORWARD, 0, 5, true),
                                    1f, 40, 0)
                    )),
            new PlantLevelUpgrades(
                    PlantLevelUpgrade.atLevel(2, PlantStatBonus.SPECIAL_CHANGE, 5),
                    PlantLevelUpgrade.atLevel(3, PlantStatBonus.DAMAGE, 10),
                    PlantLevelUpgrade.atLevel(4, PlantStatBonus.HP, 150)
            )
    ),
    Melon_pult(27, "Melon-pult", PlantCategory.LOBBER, EnumSet.of(PlantTag.AOE),
            new PlantBaseStats(325, 300, 80, 2.9f, 5),
            Arrays.asList(
                    new PlantLobAbility(80, 2.9f, ProjectileType.MELON,
                            new ShootPattern(Direction.FORWARD, 0, 1)
                            , 0f, 0, 1)
            ),
            new PlantRapidLobEffect(5, 0.1f,
                    Arrays.asList(
                            new PlantLobAbility(80, 0f, ProjectileType.MELON,
                                    new ShootPattern(Direction.FORWARD, 0, 5, true)
                                    , 0f, 0, 1)
                    )),
            new PlantLevelUpgrades(
                    PlantLevelUpgrade.atLevel(2, PlantStatBonus.COST, -25),
                    PlantLevelUpgrade.atLevel(3, PlantStatBonus.AOE_DAMAGE, 15),
                    PlantLevelUpgrade.atLevel(4, PlantStatBonus.DAMAGE, 30)
            )
    ),
    Winter_Melon(28, "Winter-Melon", PlantCategory.LOBBER, EnumSet.of(PlantTag.ICE, PlantTag.AOE),
            new PlantBaseStats(500, 300, 80, 2.9f, 5),
            Arrays.asList(
                    new PlantLobAbility(80, 2.9f, ProjectileType.ICE_MELON,
                            new ShootPattern(Direction.FORWARD, 0, 1),
                            0f, 0, 1)
            ),
            new PlantRapidLobEffect(5, 0.1f,
                    Arrays.asList(
                            new PlantLobAbility(80, 0f, ProjectileType.ICE_MELON,
                                    new ShootPattern(Direction.FORWARD, 0, 5, true),
                                    0f, 0, 1)
                    )),
            new PlantLevelUpgrades(
                    PlantLevelUpgrade.atLevel(2, PlantStatBonus.COST, -50),
                    PlantLevelUpgrade.atLevel(3, PlantStatBonus.AOE_DAMAGE, 15),
                    PlantLevelUpgrade.atLevel(4, PlantStatBonus.COST, -25)
            )
    ),
    Pepper_pult(29, "Pepper-pult", PlantCategory.LOBBER, EnumSet.of(PlantTag.AOE, PlantTag.FIRE),
            new PlantBaseStats(200, 300, 50, 2.9f, 5),
            Arrays.asList(
                    new PlantLobAbility(50, 2.9f, ProjectileType.PEPPER,
                            new ShootPattern(Direction.FORWARD, 0, 1),
                            0f, 0, 1)
            ),
            new PlantRapidLobEffect(5, 0.1f,
                    Arrays.asList(
                            new PlantLobAbility(50, 0f, ProjectileType.PEPPER,
                                    new ShootPattern(Direction.FORWARD, 0, 3, true),
                                    0f, 0, 1)
                    )),
            new PlantLevelUpgrades(
                    PlantLevelUpgrade.atLevel(2, PlantStatBonus.DAMAGE, 15),
                    PlantLevelUpgrade.atLevel(3, PlantStatBonus.RADIUS, 1),
                    PlantLevelUpgrade.atLevel(4, PlantStatBonus.COST, -25)
            )
    ),
    Potato_Mine(30, "Potato-Mine", PlantCategory.EXPLOSIVE,
            EnumSet.of(PlantTag.TRAP, PlantTag.CHARGE),
            new PlantBaseStats(25, 300, 1800, 0f, 25),
            Arrays.asList(
                    new PlantExplodeAbility(ExplodeTrigger.ON_ZOMBIE_ENTER, AreaShape.SINGLE_TILE,
                            1, 150, false, List.of(new DamageEffect(1800)))
            ),
            new PlantExplodeEffect(null, 0, List.of(), true, 2),
            new PlantLevelUpgrades(
                    PlantLevelUpgrade.atLevel(2, PlantStatBonus.COOLDOWN, -3),
                    PlantLevelUpgrade.atLevel(3, PlantStatBonus.COOLDOWN, -5),
                    PlantLevelUpgrade.atLevel(4, PlantStatBonus.DAMAGE, 600)


            )
    ),
    Primal_Potato_Mine(31, "Primal_Potato-Mine", PlantCategory.EXPLOSIVE,
            EnumSet.of(PlantTag.TRAP, PlantTag.CHARGE),
            new PlantBaseStats(50, 300, 2400, 0f, 5),
            Arrays.asList(
                    new PlantExplodeAbility(ExplodeTrigger.ON_ZOMBIE_ENTER, AreaShape.RADIUS_3x3,
                            -1, 50, false, List.of(new DamageEffect(2400)))
            ),
            new PlantExplodeEffect(null, 0, List.of(), true, 2),
            new PlantLevelUpgrades(
                    PlantLevelUpgrade.atLevel(2, PlantStatBonus.COOLDOWN, -1),
                    PlantLevelUpgrade.atLevel(3, PlantStatBonus.COOLDOWN, -3),
                    PlantLevelUpgrade.atLevel(4, PlantStatBonus.DAMAGE, 400)


            )
    ),
    Cherry_Bomb(32, "Cherry Bomb", PlantCategory.EXPLOSIVE, null,
            new PlantBaseStats(150, 0, 1800, 0f, 35),
            Arrays.asList(
                    new PlantExplodeAbility(ExplodeTrigger.INSTANT, AreaShape.RADIUS_3x3, -1,
                            0, false, List.of(new DamageEffect(1800)))
            ),
            null,
            new PlantLevelUpgrades(
                    PlantLevelUpgrade.atLevel(2, PlantStatBonus.COOLDOWN, -5),
                    PlantLevelUpgrade.atLevel(3, PlantStatBonus.DAMAGE, 600),
                    PlantLevelUpgrade.atLevel(4, PlantStatBonus.COST, -25)
            )),
    Squash(33, "Squash", PlantCategory.EXPLOSIVE, EnumSet.of(PlantTag.TRAP),
            new PlantBaseStats(50, 300, 1800, 0f, 20),
            Arrays.asList(
                    new PlantExplodeAbility(ExplodeTrigger.ON_ADJACENT_ZOMBIE, AreaShape.ADJACENT,
                            1, 0, false, List.of(new DamageEffect(1800)))
            ),
            new PlantExplodeEffect(AreaShape.FULL_BOARD, 2,
                    List.of(new DamageEffect(1800)), false, 0),
            new PlantLevelUpgrades(
                    PlantLevelUpgrade.atLevel(2, PlantStatBonus.COOLDOWN, -3),
                    PlantLevelUpgrade.atLevel(3, PlantStatBonus.DAMAGE, 600),
                    PlantLevelUpgrade.atLevel(4, PlantStatBonus.DOUBLE_CRUSH, 2)
            )
    ),
    Jalapeno(35, "Jalapeno", PlantCategory.EXPLOSIVE, EnumSet.of(PlantTag.FIRE),
            new PlantBaseStats(125, 0, 1800, 0f, 35),
            Arrays.asList(
                    new PlantExplodeAbility(ExplodeTrigger.INSTANT, AreaShape.ROW, -1,
                            0, false, List.of(new DamageEffect(1800)))
            ),
            null,
            new PlantLevelUpgrades(
                    PlantLevelUpgrade.atLevel(2, PlantStatBonus.COOLDOWN, -5),
                    PlantLevelUpgrade.atLevel(3, PlantStatBonus.DAMAGE, 600),
                    PlantLevelUpgrade.atLevel(4, PlantStatBonus.COST, -25)
            )),
    Doom_shroom(36, "Doom-shroom", PlantCategory.EXPLOSIVE, EnumSet.of(PlantTag.SHROOM),
            new PlantBaseStats(125, 0, 1800, 0f, 35),
            Arrays.asList(
                    new PlantExplodeAbility(ExplodeTrigger.INSTANT, AreaShape.FULL_BOARD, -1,
                            0, false, List.of(new DamageEffect(1800)))
            ),
            null,
            new PlantLevelUpgrades(
                    PlantLevelUpgrade.atLevel(2, PlantStatBonus.COOLDOWN, -5),
                    PlantLevelUpgrade.atLevel(3, PlantStatBonus.DAMAGE, 800),
                    PlantLevelUpgrade.atLevel(4, PlantStatBonus.COST, -50)
            )),
    Tangle_Kelp(37, "Tangle Kelp", PlantCategory.EXPLOSIVE, EnumSet.of(PlantTag.TRAP, PlantTag.WATER),
            new PlantBaseStats(25, 300, 0, 0f, 15),
            Arrays.asList(
                    new PlantExplodeAbility(ExplodeTrigger.ON_ZOMBIE_ENTER, AreaShape.SINGLE_TILE
                            , 1, 0, true, List.of(new InstantKillEffect()))
            ),
            new PlantExplodeEffect(AreaShape.FULL_BOARD, 3,
                    List.of(new InstantKillEffect()), false, 0)
            ,
            new PlantLevelUpgrades(
                    PlantLevelUpgrade.atLevel(2, PlantStatBonus.COOLDOWN, -5),
                    PlantLevelUpgrade.atLevel(3, PlantStatBonus.TARGET_PRIORITY, 1),
                    PlantLevelUpgrade.atLevel(4, PlantStatBonus.COST, -25)
            )),
    Iceberg_Lettuce(38, "Iceberg Lettuce", PlantCategory.EXPLOSIVE, EnumSet.of(PlantTag.TRAP, PlantTag.ICE),
            new PlantBaseStats(0, 300, 0, 0f, 20),
            Arrays.asList(
                    new PlantExplodeAbility(ExplodeTrigger.ON_ZOMBIE_ENTER, AreaShape.SINGLE_TILE
                            , 1, 0, false, List.of(new FreezeEffect(100)))
            ),
            new PlantExplodeEffect(AreaShape.FULL_BOARD, -1,
                    List.of(new FreezeEffect(10)), false, 0)
            ,
            new PlantLevelUpgrades(
                    PlantLevelUpgrade.atLevel(2, PlantStatBonus.COOLDOWN, -2),
                    PlantLevelUpgrade.atLevel(3, PlantStatBonus.EFFECT_DURATION, 2),
                    PlantLevelUpgrade.atLevel(4, PlantStatBonus.COST, -25)
            )),
    Bonk_Choy(39, "Bonk Choy", PlantCategory.MELEE, null,
            new PlantBaseStats(150, 300, 15, 0.25f, 5),
            Arrays.asList(
                    new PlantMeleeAbility(AreaShape.FRONT_OR_BACK, 1, 0.25f,
                            List.of(new DamageEffect(15)), 0)

            ),
            new PlantMeleeEffect(3, 8, List.of(
                    new PlantMeleeAbility(AreaShape.RADIUS_3x3, 1, 0f,
                            List.of(new DamageEffect(15)), 0)
            )),
            new PlantLevelUpgrades(
                    PlantLevelUpgrade.atLevel(2, PlantStatBonus.DAMAGE, 5),
                    PlantLevelUpgrade.atLevel(3, PlantStatBonus.ATTACK_SPEED, 10),
                    PlantLevelUpgrade.atLevel(4, PlantStatBonus.HP, 200)
            )
    ),
    Phat_Beet(40, "Phat Beet", PlantCategory.MELEE, EnumSet.of(PlantTag.AOE),
            new PlantBaseStats(150, 300, 15, 2, 5),
            Arrays.asList(
                    new PlantMeleeAbility(AreaShape.RADIUS_3x3, 1, 2,
                            List.of(new DamageEffect(15)), 0)
            ),
            new PlantMeleeEffect(0, 0, List.of(
                    new PlantMeleeAbility(AreaShape.FULL_BOARD, -1, 0f,
                            List.of(new DamageEffect(45)), 0)
            )),
            new PlantLevelUpgrades(
                    PlantLevelUpgrade.atLevel(2, PlantStatBonus.DAMAGE, 10),
                    PlantLevelUpgrade.atLevel(3, PlantStatBonus.ATTACK_SPEED, 10),
                    PlantLevelUpgrade.atLevel(4, PlantStatBonus.HP, 200)

            )
    ),
    Chomper(41, "Chomper", PlantCategory.MELEE, null,
            new PlantBaseStats(150, 300, 0, 40, 5),
            Arrays.asList(
                    new PlantMeleeAbility(AreaShape.ADJACENT, 1, 0, List.of(new InstantKillEffect()), 400)

            ),
            new PlantMeleeEffect(0, 0, List.of(
                    new PlantMeleeAbility(AreaShape.ROW, 3, 0f, List.of(new InstantKillEffect()), 400)
            )),
            new PlantLevelUpgrades(
                    PlantLevelUpgrade.atLevel(2, PlantStatBonus.COOLDOWN, -2),
                    PlantLevelUpgrade.atLevel(3, PlantStatBonus.HP, 200),
                    PlantLevelUpgrade.atLevel(4, PlantStatBonus.COOLDOWN, -3)

            )
    ),
    Wasabi_Whip(42, "Wasabi Whip", PlantCategory.MELEE, EnumSet.of(PlantTag.FIRE),
            new PlantBaseStats(150, 300, 40, 2, 5),
            Arrays.asList(
                    new PlantMeleeAbility(AreaShape.FRONT_OR_BACK, -1, 2, List.of(new DamageEffect(40)), 0)

            ),
            new PlantMeleeEffect(0, 0, List.of(
                    new PlantMeleeAbility(AreaShape.RADIUS_3x3, -1, 0f, List.of(new DamageEffect(40)), 0)
            )),
            new PlantLevelUpgrades(
                    PlantLevelUpgrade.atLevel(2, PlantStatBonus.DAMAGE, 10),
                    PlantLevelUpgrade.atLevel(3, PlantStatBonus.RANGE, 1),
                    PlantLevelUpgrade.atLevel(4, PlantStatBonus.HP, 200)

            )

    ),
    Wall_nut(44,"Wall-nut" , PlantCategory.DEFENDER, null,
            new PlantBaseStats(50,4000,0,0,20),
            Arrays.asList(
                    new PlantDefenderAbility.Builder().build()
            ),
            new PlantDefenderEffect.Builder().addArmor(4000).build() ,
            new PlantLevelUpgrades(
                    PlantLevelUpgrade.atLevel(2,PlantStatBonus.HP,1000),
                    PlantLevelUpgrade.atLevel(3,PlantStatBonus.COOLDOWN,-5),
                    PlantLevelUpgrade.atLevel(4,PlantStatBonus.HP,1500)


                    )),
    Tall_nut(45,"Tall-nut" , PlantCategory.DEFENDER, null,
            new PlantBaseStats(125,8000,0,0,20),
            Arrays.asList(
                    new PlantDefenderAbility.Builder()
                            .blockJump()
                            .build()
            ),
            new PlantDefenderEffect.Builder().addArmor(8000).build() ,
            new PlantLevelUpgrades(
                    PlantLevelUpgrade.atLevel(2,PlantStatBonus.HP,2000),
                    PlantLevelUpgrade.atLevel(3,PlantStatBonus.COOLDOWN,-5),
                    PlantLevelUpgrade.atLevel(4,PlantStatBonus.HP,3000)


            )),
    Endurian(46,"Endurian" , PlantCategory.DEFENDER, null,
            new PlantBaseStats(100,3000,20,0,15),
            Arrays.asList(
                    new PlantDefenderAbility.Builder()
                            .reflectDamage(20)
                            .build()
            ),
            new PlantDefenderEffect.Builder()
                    .addArmor(3000)
                    .increaseReflect(20)
                    .build() ,
            new PlantLevelUpgrades(
                    PlantLevelUpgrade.atLevel(2,PlantStatBonus.DAMAGE,5),
                    PlantLevelUpgrade.atLevel(3,PlantStatBonus.HP,1000),
                    PlantLevelUpgrade.atLevel(4,PlantStatBonus.COST,-25)


            )),
    Garlic(47,"Garlic" , PlantCategory.DEFENDER, EnumSet.of(PlantTag.MOVE_ZOMBIES),
            new PlantBaseStats(50,300,0,0,20),
            Arrays.asList(
                    new PlantDefenderAbility.Builder()
                            .moveZombie()
                            .build()
            ),
            new PlantDefenderEffect.Builder()
                    .moveAllZombies()
                    .build() ,
            new PlantLevelUpgrades(
                    PlantLevelUpgrade.atLevel(2,PlantStatBonus.HP,150),
                    PlantLevelUpgrade.atLevel(3,PlantStatBonus.COOLDOWN,-3),
                    PlantLevelUpgrade.atLevel(4,PlantStatBonus.HP,250)


            )),
    Sweet_Potato(48,"Sweet Potato" , PlantCategory.DEFENDER, EnumSet.of(PlantTag.MOVE_ZOMBIES),
            new PlantBaseStats(150,3000,0,0,20),
            Arrays.asList(
                    new PlantDefenderAbility.Builder()
                            .attractZombies()
                            .build()
            ),
            new PlantDefenderEffect.Builder()
                    .attractAll()
                    .healFully()
                    .build(),

            new PlantLevelUpgrades(
                    PlantLevelUpgrade.atLevel(2,PlantStatBonus.HP,1000),
                    PlantLevelUpgrade.atLevel(3,PlantStatBonus.COOLDOWN,-5),
                    PlantLevelUpgrade.atLevel(4,PlantStatBonus.HP,1500)


            )),
    Explode_o_nut(49,"Explode-o-nut" , PlantCategory.DEFENDER, EnumSet.of(PlantTag.EXPLOSIVE),
            new PlantBaseStats(50,4000,1800,0,20),
            Arrays.asList(
                    new PlantDefenderAbility.Builder()
                            .explodeOnDeath(1800)
                            .build()
            ),
            new PlantDefenderEffect.Builder()
                    .addArmor(4000)
                    .explodeOnArmorBreak(1800)
                    .build(),

            new PlantLevelUpgrades(
                    PlantLevelUpgrade.atLevel(2,PlantStatBonus.HP,1000),
                    PlantLevelUpgrade.atLevel(3,PlantStatBonus.DAMAGE,200),
                    PlantLevelUpgrade.atLevel(4,PlantStatBonus.COST,-25)


            )),
    Pumpkin(50,"Pumpkin" , PlantCategory.DEFENDER, EnumSet.of(PlantTag.STACK),
            new PlantBaseStats(150,4000,0,0,20),
            null,
            new PlantDefenderEffect.Builder()
                    .addArmor(4000)
                    .build(),

            new PlantLevelUpgrades(
                    PlantLevelUpgrade.atLevel(2,PlantStatBonus.HP,1000),
                    PlantLevelUpgrade.atLevel(3,PlantStatBonus.COOLDOWN,-5),
                    PlantLevelUpgrade.atLevel(4,PlantStatBonus.HP,1500)


            )),
    Sun_Bean(51,"Sun Bean" , PlantCategory.DEFENDER, EnumSet.of(PlantTag.SUN),
            new PlantBaseStats(50,1000,0,0,20),
            Arrays.asList(
                 new   PlantDefenderAbility.Builder()
                         .produceSunOnHit(5)
                         .build()
            ),
            new PlantDefenderEffect.Builder()
                    .addArmor(1000)
                    .build(),

            new PlantLevelUpgrades(
                    PlantLevelUpgrade.atLevel(2,PlantStatBonus.SUN_DROP,5),
                    PlantLevelUpgrade.atLevel(3,PlantStatBonus.HP,150),
                    PlantLevelUpgrade.atLevel(4,PlantStatBonus.COST,-25)


            )),
    Ice_shroom(57, "Ice_shroom", PlantCategory.EXPLOSIVE, EnumSet.of(PlantTag.SHROOM,PlantTag.ICE),
            new PlantBaseStats(75, 0, 0, 0f, 50),
            Arrays.asList(
                    new PlantExplodeAbility(ExplodeTrigger.INSTANT, AreaShape.FULL_BOARD, -1,
                            0, false, List.of(new FreezeEffect(100)))
            ),
            null,
            new PlantLevelUpgrades(
                    PlantLevelUpgrade.atLevel(2, PlantStatBonus.EFFECT_DURATION, 2),
                    PlantLevelUpgrade.atLevel(3, PlantStatBonus.COOLDOWN, -5),
                    PlantLevelUpgrade.atLevel(4, PlantStatBonus.DAMAGE, 50)
            )),
    Enlighten_mint(61,"Enlighten-mint",PlantCategory.MINT,null,
            new PlantBaseStats(0,0,0,0,85),
            Arrays.asList(
                    new PlantMintAbility(PlantCategory.SUN_PRODUCER,5)
            ),
            null,
            new PlantLevelUpgrades(
                    PlantLevelUpgrade.atLevel(2, PlantStatBonus.EFFECT_DURATION, 1),
                    PlantLevelUpgrade.atLevel(3, PlantStatBonus.COOLDOWN, -5),
                    PlantLevelUpgrade.atLevel(4, PlantStatBonus.RESET_FAMILY_COOLDOWN,true)
            )),
    Appease_mint(62,"Appease-mint",PlantCategory.MINT,null,
            new PlantBaseStats(0,0,0,0,85),
            Arrays.asList(
                    new PlantMintAbility(PlantCategory.SHOOTER,6)
            ),
            null,
            new PlantLevelUpgrades(
                    PlantLevelUpgrade.atLevel(2, PlantStatBonus.EFFECT_DURATION, 1),
                    PlantLevelUpgrade.atLevel(3, PlantStatBonus.COOLDOWN, -5),
                    PlantLevelUpgrade.atLevel(4, PlantStatBonus.RESET_FAMILY_COOLDOWN,true)
            )),
    Arma_mint(63,"Arma-mint",PlantCategory.MINT,null,
            new PlantBaseStats(0,0,0,0,85),
            Arrays.asList(
                    new PlantMintAbility(PlantCategory.LOBBER,6)
            ),
            null,
            new PlantLevelUpgrades(
                    PlantLevelUpgrade.atLevel(2, PlantStatBonus.EFFECT_DURATION, 1),
                    PlantLevelUpgrade.atLevel(3, PlantStatBonus.COOLDOWN, -5),
                    PlantLevelUpgrade.atLevel(4, PlantStatBonus.RESET_FAMILY_COOLDOWN,true)
            )),
    Bombard_mint(64,"Bombard-mint",PlantCategory.MINT,null,
            new PlantBaseStats(0,0,0,0,85),
            Arrays.asList(
                    new PlantMintAbility(PlantCategory.LOBBER,6)
            ),
            null,
            new PlantLevelUpgrades(
                    PlantLevelUpgrade.atLevel(2, PlantStatBonus.EFFECT_DURATION, 1),
                    PlantLevelUpgrade.atLevel(3, PlantStatBonus.COOLDOWN, -5),
                    PlantLevelUpgrade.atLevel(4, PlantStatBonus.RESET_FAMILY_COOLDOWN,true)
            )),
    Enforce_mint(65,"Enforce-mint",PlantCategory.MINT,null,
            new PlantBaseStats(0,0,0,0,85),
            Arrays.asList(
                    new PlantMintAbility(PlantCategory.EXPLOSIVE,8)
            ),
            null,
            new PlantLevelUpgrades(
                    PlantLevelUpgrade.atLevel(2, PlantStatBonus.EFFECT_DURATION, 1),
                    PlantLevelUpgrade.atLevel(3, PlantStatBonus.COOLDOWN, -5),
                    PlantLevelUpgrade.atLevel(4, PlantStatBonus.RESET_FAMILY_COOLDOWN,true)
            )),
    Reinforce_mint(66,"Reinforce-mint",PlantCategory.MINT,null,
            new PlantBaseStats(0,0,0,0,85),
            Arrays.asList(
                    new PlantMintAbility(PlantCategory.DEFENDER,8)
            ),
            null,
            new PlantLevelUpgrades(
                    PlantLevelUpgrade.atLevel(2, PlantStatBonus.EFFECT_DURATION, 1),
                    PlantLevelUpgrade.atLevel(3, PlantStatBonus.COOLDOWN, -5),
                    PlantLevelUpgrade.atLevel(4, PlantStatBonus.RESET_FAMILY_COOLDOWN,true)
            )),
    Enchant_mint(67,"Enchant-mint",PlantCategory.MINT,null,
            new PlantBaseStats(0,0,0,0,85),
            Arrays.asList(
                    new PlantMintAbility(PlantCategory.MODIFIER,8)
            ),
            null,
            new PlantLevelUpgrades(
                    PlantLevelUpgrade.atLevel(2, PlantStatBonus.EFFECT_DURATION, 1),
                    PlantLevelUpgrade.atLevel(3, PlantStatBonus.COOLDOWN, -5),
                    PlantLevelUpgrade.atLevel(4, PlantStatBonus.RESET_FAMILY_COOLDOWN,true)
            )),
    Pierce_mint(68,"Pierce-mint",PlantCategory.MINT,null,
            new PlantBaseStats(0,0,0,0,85),
            Arrays.asList(
                    new PlantMintAbility(PlantCategory.STRIKE_THROUGH,8)
            ),
            null,
            new PlantLevelUpgrades(
                    PlantLevelUpgrade.atLevel(2, PlantStatBonus.EFFECT_DURATION, 1),
                    PlantLevelUpgrade.atLevel(3, PlantStatBonus.COOLDOWN, -5),
                    PlantLevelUpgrade.atLevel(4, PlantStatBonus.RESET_FAMILY_COOLDOWN,true)
            )),
    catTail_mint(69,"catTail-mint",PlantCategory.MINT,null,
            new PlantBaseStats(0,0,0,0,85),
            Arrays.asList(
                    new PlantMintAbility(PlantCategory.HOMING,8)
            ),
            null,
            new PlantLevelUpgrades(
                    PlantLevelUpgrade.atLevel(2, PlantStatBonus.EFFECT_DURATION, 1),
                    PlantLevelUpgrade.atLevel(3, PlantStatBonus.COOLDOWN, -5),
                    PlantLevelUpgrade.atLevel(4, PlantStatBonus.RESET_FAMILY_COOLDOWN,true)
            ));








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