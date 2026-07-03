package model.data.plant;

import java.lang.reflect.Array;
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

    SUNFLOWER(1, "Sunflower", PlantCategory.SUN_PRODUCER,
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
                    new PlantSunProduceAbility(75, 24, 0)
            ),
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
            Arrays.asList(new PlantShootAbility(20, 1.5f, PlantProjectileType.PEA,
                    new ShootPattern(Direction.FORWARD, 0, 1))),
            new PlantRapidFireEffect(3, 0.1f, Arrays.asList(
                    new PlantShootAbility(20, PlantProjectileType.PEA, EffectPhase.ALWAYS)
            )),
            new PlantLevelUpgrades(
                    PlantLevelUpgrade.atLevel(2, PlantStatBonus.DAMAGE, 10),
                    PlantLevelUpgrade.atLevel(3, PlantStatBonus.HP, 150),
                    PlantLevelUpgrade.atLevel(4, PlantStatBonus.COST, -25))),
    Repeater(7, "Repeater", PlantCategory.SHOOTER, EnumSet.of(PlantTag.PEA),
            new PlantBaseStats(200, 300, 20, 1.5f, 5),
            Arrays.asList(new PlantShootAbility(20, 1.5f, PlantProjectileType.PEA,
                    new ShootPattern(Direction.FORWARD, 0, 2))),
            new PlantRapidFireEffect(3, 0.1f, Arrays.asList(
                    new PlantShootAbility(20, PlantProjectileType.PEA, EffectPhase.ALWAYS),
                    new PlantShootAbility(400, PlantProjectileType.PEA, EffectPhase.END)
            )),
            new PlantLevelUpgrades(
                    PlantLevelUpgrade.atLevel(2, PlantStatBonus.DAMAGE, 10),
                    PlantLevelUpgrade.atLevel(3, PlantStatBonus.HP, 200),
                    PlantLevelUpgrade.atLevel(4, PlantStatBonus.COST, -25))),
    Threepeater(8, "Threepeater", PlantCategory.SHOOTER, EnumSet.of(PlantTag.PEA),
            new PlantBaseStats(300, 300, 20, 1.5f, 5),
            Arrays.asList(
                    new PlantShootAbility(20, 1.5f, PlantProjectileType.PEA, new ShootPattern(Direction.FORWARD, 1, 1)),
                    new PlantShootAbility(20, 1.5f, PlantProjectileType.PEA, new ShootPattern(Direction.FORWARD, 0, 1)),
                    new PlantShootAbility(20, 1.5f, PlantProjectileType.PEA, new ShootPattern(Direction.FORWARD, -1, 1))
            ),
            new PlantRapidFireEffect(3, 0.1f,
                    Arrays.asList(
                            new PlantShootAbility(20, 0f, PlantProjectileType.PEA, new ShootPattern(Direction.FORWARD, 2, 1)),
                            new PlantShootAbility(20, 0f, PlantProjectileType.PEA, new ShootPattern(Direction.FORWARD, 1, 1)),
                            new PlantShootAbility(20, 0f, PlantProjectileType.PEA, new ShootPattern(Direction.FORWARD, 0, 1)),
                            new PlantShootAbility(20, 0f, PlantProjectileType.PEA, new ShootPattern(Direction.FORWARD, -1, 1)),
                            new PlantShootAbility(20, 0f, PlantProjectileType.PEA, new ShootPattern(Direction.FORWARD, -2, 1))

                    )),
            new PlantLevelUpgrades(
                    PlantLevelUpgrade.atLevel(2, PlantStatBonus.COST, -25),
                    PlantLevelUpgrade.atLevel(2, PlantStatBonus.DAMAGE, 10),
                    PlantLevelUpgrade.atLevel(2, PlantStatBonus.HP, 200)
            )),
    SnowPea(9, "Snow Pea", PlantCategory.SHOOTER, EnumSet.of(PlantTag.PEA, PlantTag.ICE),
            new PlantBaseStats(150, 300, 20, 1.5f, 5),
            Arrays.asList(new PlantShootAbility(20, 1.5f, PlantProjectileType.ICE,
                    new ShootPattern(Direction.FORWARD, 0, 1))),
            new PlantRapidFireEffect(3, 0.1f, Arrays.asList(
                    new PlantShootAbility(20, PlantProjectileType.ICE, EffectPhase.ALWAYS),
                    new PlantShootAbility(0, PlantProjectileType.FREEZE_LINE, EffectPhase.START)
            )),
            new PlantLevelUpgrades(
                    PlantLevelUpgrade.atLevel(2, PlantStatBonus.DAMAGE, 10),
                    PlantLevelUpgrade.atLevel(3, PlantStatBonus.EFFECT_DURATION, 2),
                    PlantLevelUpgrade.atLevel(4, PlantStatBonus.COST, -25))),
    Rotobaga(10, "Rotobaga", PlantCategory.SHOOTER, null,
            new PlantBaseStats(150, 300, 10, 1.5f, 5),
            Arrays.asList(
                    new PlantShootAbility(10, 1.5f, PlantProjectileType.ROTO_SEED, new ShootPattern(Direction.UP_LEFT, 0, 3)),
                    new PlantShootAbility(10, 1.5f, PlantProjectileType.ROTO_SEED, new ShootPattern(Direction.UP_RIGHT, 0, 3)),
                    new PlantShootAbility(10, 1.5f, PlantProjectileType.ROTO_SEED, new ShootPattern(Direction.DOWN_LEFT, 0, 3)),
                    new PlantShootAbility(10, 1.5f, PlantProjectileType.ROTO_SEED, new ShootPattern(Direction.DOWN_RIGHT, 0, 3))
            ),
            new PlantRapidFireEffect(3, 0.1f,
                    Arrays.asList(
                            new PlantShootAbility(10, 0f, PlantProjectileType.ROTO_SEED, new ShootPattern(Direction.UP_RIGHT, 0, 1)),
                            new PlantShootAbility(10, 0f, PlantProjectileType.ROTO_SEED, new ShootPattern(Direction.UP_LEFT, 0, 1)),
                            new PlantShootAbility(10, 0f, PlantProjectileType.ROTO_SEED, new ShootPattern(Direction.DOWN_RIGHT, 0, 1)),
                            new PlantShootAbility(10, 0f, PlantProjectileType.ROTO_SEED, new ShootPattern(Direction.DOWN_LEFT, 0, 1))
                    )),
            new PlantLevelUpgrades(
                    new PlantLevelUpgrade(2, PlantStatBonus.DAMAGE, 10),
                    new PlantLevelUpgrade(3, PlantStatBonus.HP, 150),
                    new PlantLevelUpgrade(4, PlantStatBonus.COST, -25)
            )),
    SplitPea(12, "Split Pea", PlantCategory.SHOOTER, EnumSet.of(PlantTag.PEA),
            new PlantBaseStats(125, 300, 20, 1.5f, 5),
            Arrays.asList(
                    new PlantShootAbility(20, 1.5f, PlantProjectileType.PEA, new ShootPattern(Direction.FORWARD, 0, 1)),
                    new PlantShootAbility(20, 1.5f, PlantProjectileType.PEA, new ShootPattern(Direction.BACK, 0, 2))
            ),
            new PlantRapidFireEffect(3, 0.1f,
                    Arrays.asList(
                            new PlantShootAbility(20, 0f, PlantProjectileType.PEA, new ShootPattern(Direction.FORWARD, 0, 1)),
                            new PlantShootAbility(20, 0f, PlantProjectileType.PEA, new ShootPattern(Direction.BACK, 0, 1))
                    )),
            new PlantLevelUpgrades(
                    new PlantLevelUpgrade(2, PlantStatBonus.DAMAGE, 10),
                    new PlantLevelUpgrade(3, PlantStatBonus.HP, 200),
                    new PlantLevelUpgrade(4, PlantStatBonus.COST, -25)
            )),
    Citron(13, "Citron", PlantCategory.SHOOTER, EnumSet.of(PlantTag.CHARGE),
            new PlantBaseStats(350, 300, 800, 9, 5),
            Arrays.asList(
                    new PlantShootAbility(800, 9f, PlantProjectileType.LASER, new ShootPattern(Direction.FORWARD, 0, 1))
            ),
            new PlantRapidFireEffect(3, 0.1f,
                    Arrays.asList(
                            new PlantShootAbility(8000, 0f, PlantProjectileType.PLASMA, new ShootPattern(Direction.FORWARD, 0, 1))
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
                    new PlantShootAbility(40, 2, PlantProjectileType.BOUNCING_AQUA, new ShootPattern(Direction.FORWARD, 0, 1)),
                    new PlantShootAbility(80, 5, PlantProjectileType.BOUNCING_ORANGE, new ShootPattern(Direction.FORWARD, 0, 1)),
                    new PlantShootAbility(120, 10, PlantProjectileType.BOUNCING_BLUE, new ShootPattern(Direction.FORWARD, 0, 1))
            ),
            new PlantRapidFireEffect(1, 1f,
                    Arrays.asList(
                            new PlantShootAbility(600, 1.5f, PlantProjectileType.SUPER_BULB, new ShootPattern(Direction.FORWARD, 0, 1))
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
                    new PlantShootAbility(40, 1.5f, PlantProjectileType.FIRE, new ShootPattern(Direction.FORWARD, 0, 1))
            ),
            new PlantRapidFireEffect(3, 0.1f,
                    Arrays.asList(
                            new PlantShootAbility(40, 0, PlantProjectileType.FIRE, new ShootPattern(Direction.FORWARD, 0, 1))
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
                    new PlantShootAbility(20, 1.5f, PlantProjectileType.STAR, new ShootPattern(Direction.BACK, 0, 1)),
                    new PlantShootAbility(20, 1.5f, PlantProjectileType.STAR, new ShootPattern(Direction.UP, 0, 1)),
                    new PlantShootAbility(20, 1.5f, PlantProjectileType.STAR, new ShootPattern(Direction.DOWN, 0, 1)),
                    new PlantShootAbility(20, 1.5f, PlantProjectileType.STAR, new ShootPattern(Direction.UP_RIGHT, 0, 1)),
                    new PlantShootAbility(20, 1.5f, PlantProjectileType.STAR, new ShootPattern(Direction.DOWN_RIGHT, 0, 1))
            ),
            new PlantRapidFireEffect(3, 0.1f,
                    Arrays.asList(
                            new PlantShootAbility(20, 0, PlantProjectileType.STAR, new ShootPattern(Direction.BACK, 0, 1)),
                            new PlantShootAbility(20, 0, PlantProjectileType.STAR, new ShootPattern(Direction.UP, 0, 1)),
                            new PlantShootAbility(20, 0, PlantProjectileType.STAR, new ShootPattern(Direction.DOWN, 0, 1)),
                            new PlantShootAbility(20, 0, PlantProjectileType.STAR, new ShootPattern(Direction.UP_RIGHT, 0, 1)),
                            new PlantShootAbility(20, 0, PlantProjectileType.STAR, new ShootPattern(Direction.DOWN_RIGHT, 0, 1)),
                            new PlantShootAbility(20, 0, PlantProjectileType.STAR, new ShootPattern(Direction.DOWN_LEFT, 0, 1)),
                            new PlantShootAbility(20, 0, PlantProjectileType.STAR, new ShootPattern(Direction.UP_LEFT, 0, 1))
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
                    new PlantShootAbility(20, 1.5f, PlantProjectileType.PEA, new ShootPattern(Direction.FORWARD, 0, 1))
            ),
            new PlantRapidFireEffect(3, 0.1f,
                    Arrays.asList(
                            new PlantShootAbility(20, 0f, PlantProjectileType.POISON, new ShootPattern(Direction.FORWARD, 0, 1))
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
                    new PlantShootAbility(20, 1.5f, PlantProjectileType.PEA,
                            new ShootPattern(Direction.FORWARD, 0, 4))
            ),
            new PlantRapidFireEffect(3, 0.1f,
                    Arrays.asList(
                            new PlantShootAbility(20, 0f, PlantProjectileType.PEA,
                                    new ShootPattern(Direction.FORWARD, 0, 1)),
                            new PlantShootAbility(400, PlantProjectileType.PEA, EffectPhase.END),
                            new PlantShootAbility(400, PlantProjectileType.PEA, EffectPhase.END),
                            new PlantShootAbility(400, PlantProjectileType.PEA, EffectPhase.END),
                            new PlantShootAbility(400, PlantProjectileType.PEA, EffectPhase.END)
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
                    new PlantShootAbility(20, 1.5f, PlantProjectileType.FUME,
                            new ShootPattern(Direction.FORWARD, 0, 1))
            ),
            new PlantRapidFireEffect(3, 0.1f,
                    Arrays.asList(
                            new PlantShootAbility(20, 0f, PlantProjectileType.FUME,
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
                    new PlantShootAbility(20, 1.5f, PlantProjectileType.FUME,
                            new ShootPattern(Direction.FORWARD, 0, 1))
            ),
            new PlantRapidFireEffect(3, 0.1f,
                    Arrays.asList(
                            new PlantShootAbility(20, 0f, PlantProjectileType.FUME,
                                    new ShootPattern(Direction.FORWARD, 0, 1))
                    )),
            new PlantLevelUpgrades(
                    PlantLevelUpgrade.atLevel(2, PlantStatBonus.LIFE_SPAN, 10),
                    PlantLevelUpgrade.atLevel(3, PlantStatBonus.DAMAGE, 10),
                    PlantLevelUpgrade.atLevel(4, PlantStatBonus.RANGE, 1)
            )
    );


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