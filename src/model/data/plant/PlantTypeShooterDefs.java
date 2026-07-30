package model.data.plant;

import java.util.Arrays;
import java.util.EnumSet;

import model.data.plant.abilities.config.Direction;
import model.data.plant.abilities.config.ShootPattern;
import model.data.plant.abilities.runtime.PlantShootAbility;
import model.data.plant.effects.config.EffectPhase;
import model.data.plant.effects.runtime.PlantRapidFireEffect;
import model.data.plant.upgrades.PlantLevelUpgrade;
import model.data.plant.upgrades.PlantLevelUpgrades;
import model.data.plant.upgrades.PlantStatBonus;
import model.data.projectile.ProjectileType;

final class PlantTypeShooterDefs {

        private PlantTypeShooterDefs() {
        }

        static PlantTypeSpec peaShooter() {
                return new PlantTypeSpec(6, "Peashooter", PlantCategory.SHOOTER, EnumSet.of(PlantTag.PEA),
                                new PlantBaseStats(100, 300, 20, 1.5f, 5),
                                Arrays.asList(PlantShootAbility.builder().damage(20).cooldown(1.5f)
                                                .projectile(ProjectileType.PEA)
                                                .pattern(new ShootPattern(Direction.FORWARD, 0, 1)).build()),
                                new PlantRapidFireEffect(3, 0.1f, Arrays.asList(
                                                PlantShootAbility.builder().damage(20).cooldown(0f)
                                                                .projectile(ProjectileType.PEA)
                                                                .phase(EffectPhase.ALWAYS).build())),
                                new PlantLevelUpgrades(
                                                PlantLevelUpgrade.atLevel(2, PlantStatBonus.DAMAGE, 10),
                                                PlantLevelUpgrade.atLevel(3, PlantStatBonus.HP, 150),
                                                PlantLevelUpgrade.atLevel(4, PlantStatBonus.COST, -25)));
        }

        static PlantTypeSpec repeater() {
                return new PlantTypeSpec(7, "Repeater", PlantCategory.SHOOTER, EnumSet.of(PlantTag.PEA),
                                new PlantBaseStats(200, 300, 20, 1.5f, 5),
                                Arrays.asList(PlantShootAbility.builder().damage(20).cooldown(1.5f)
                                                .projectile(ProjectileType.PEA)
                                                .pattern(new ShootPattern(Direction.FORWARD, 0, 2)).build()),
                                new PlantRapidFireEffect(3, 0.1f, Arrays.asList(
                                                PlantShootAbility.builder().damage(20).cooldown(0f)
                                                                .projectile(ProjectileType.PEA)
                                                                .phase(EffectPhase.ALWAYS).build(),
                                                PlantShootAbility.builder().damage(400).cooldown(0f)
                                                                .projectile(ProjectileType.PEA).phase(EffectPhase.END)
                                                                .build())),
                                new PlantLevelUpgrades(
                                                PlantLevelUpgrade.atLevel(2, PlantStatBonus.DAMAGE, 10),
                                                PlantLevelUpgrade.atLevel(3, PlantStatBonus.HP, 200),
                                                PlantLevelUpgrade.atLevel(4, PlantStatBonus.COST, -25)));
        }

        static PlantTypeSpec threepeater() {
                return new PlantTypeSpec(8, "Threepeater", PlantCategory.SHOOTER, EnumSet.of(PlantTag.PEA),
                                new PlantBaseStats(300, 300, 20, 1.5f, 5),
                                Arrays.asList(
                                                PlantShootAbility.builder().damage(20).cooldown(1.5f)
                                                                .projectile(ProjectileType.PEA)
                                                                .pattern(new ShootPattern(Direction.FORWARD, 1, 1))
                                                                .build(),
                                                PlantShootAbility.builder().damage(20).cooldown(1.5f)
                                                                .projectile(ProjectileType.PEA)
                                                                .pattern(new ShootPattern(Direction.FORWARD, 0, 1))
                                                                .build(),
                                                PlantShootAbility.builder().damage(20).cooldown(1.5f)
                                                                .projectile(ProjectileType.PEA)
                                                                .pattern(new ShootPattern(Direction.FORWARD, -1, 1))
                                                                .build()),
                                new PlantRapidFireEffect(3, 0.1f,
                                                Arrays.asList(
                                                                PlantShootAbility.builder().damage(20).cooldown(0f)
                                                                                .projectile(ProjectileType.PEA)
                                                                                .pattern(new ShootPattern(
                                                                                                Direction.FORWARD, 2,
                                                                                                1))
                                                                                .build(),
                                                                PlantShootAbility.builder().damage(20).cooldown(0f)
                                                                                .projectile(ProjectileType.PEA)
                                                                                .pattern(new ShootPattern(
                                                                                                Direction.FORWARD, 1,
                                                                                                1))
                                                                                .build(),
                                                                PlantShootAbility.builder().damage(20).cooldown(0f)
                                                                                .projectile(ProjectileType.PEA)
                                                                                .pattern(new ShootPattern(
                                                                                                Direction.FORWARD, 0,
                                                                                                1))
                                                                                .build(),
                                                                PlantShootAbility.builder().damage(20).cooldown(0f)
                                                                                .projectile(ProjectileType.PEA)
                                                                                .pattern(new ShootPattern(
                                                                                                Direction.FORWARD, -1,
                                                                                                1))
                                                                                .build(),
                                                                PlantShootAbility.builder().damage(20).cooldown(0f)
                                                                                .projectile(ProjectileType.PEA)
                                                                                .pattern(new ShootPattern(
                                                                                                Direction.FORWARD, -2,
                                                                                                1))
                                                                                .build())),
                                new PlantLevelUpgrades(
                                                PlantLevelUpgrade.atLevel(2, PlantStatBonus.COST, -25),
                                                PlantLevelUpgrade.atLevel(3, PlantStatBonus.DAMAGE, 10),
                                                PlantLevelUpgrade.atLevel(4, PlantStatBonus.HP, 200)));
        }

        static PlantTypeSpec snowPea() {
                return new PlantTypeSpec(9, "Snow Pea", PlantCategory.SHOOTER, EnumSet.of(PlantTag.PEA, PlantTag.ICE),
                                new PlantBaseStats(150, 300, 20, 1.5f, 5),
                                Arrays.asList(
                                                PlantShootAbility.builder().damage(20).cooldown(1.5f)
                                                                .projectile(ProjectileType.ICE)
                                                                .pattern(new ShootPattern(Direction.FORWARD, 0, 1))
                                                                .build()),
                                new PlantRapidFireEffect(3, 0.1f, Arrays.asList(
                                                PlantShootAbility.builder().damage(20).cooldown(0f)
                                                                .projectile(ProjectileType.ICE)
                                                                .phase(EffectPhase.ALWAYS).build(),
                                                PlantShootAbility.builder().damage(0).cooldown(0f)
                                                                .projectile(ProjectileType.FREEZE_LINE)
                                                                .phase(EffectPhase.START).build())),
                                new PlantLevelUpgrades(
                                                PlantLevelUpgrade.atLevel(2, PlantStatBonus.DAMAGE, 10),
                                                PlantLevelUpgrade.atLevel(3, PlantStatBonus.EFFECT_DURATION, 2),
                                                PlantLevelUpgrade.atLevel(4, PlantStatBonus.COST, -25)));
        }

        static PlantTypeSpec rotobaga() {
                return new PlantTypeSpec(10, "Rotobaga", PlantCategory.SHOOTER, null,
                                new PlantBaseStats(150, 300, 10, 1.5f, 5),
                                Arrays.asList(
                                                PlantShootAbility.builder().damage(10).cooldown(1.5f)
                                                                .projectile(ProjectileType.ROTO_SEED)
                                                                .pattern(new ShootPattern(Direction.UP_LEFT, 0, 3))
                                                                .build(),
                                                PlantShootAbility.builder().damage(10).cooldown(1.5f)
                                                                .projectile(ProjectileType.ROTO_SEED)
                                                                .pattern(new ShootPattern(Direction.UP_RIGHT, 0, 3))
                                                                .build(),
                                                PlantShootAbility.builder().damage(10).cooldown(1.5f)
                                                                .projectile(ProjectileType.ROTO_SEED)
                                                                .pattern(new ShootPattern(Direction.DOWN_LEFT, 0, 3))
                                                                .build(),
                                                PlantShootAbility.builder().damage(10).cooldown(1.5f)
                                                                .projectile(ProjectileType.ROTO_SEED)
                                                                .pattern(new ShootPattern(Direction.DOWN_RIGHT, 0, 3))
                                                                .build()),
                                new PlantRapidFireEffect(3, 0.1f, Arrays.asList(
                                                PlantShootAbility.builder().damage(10).cooldown(0f)
                                                                .projectile(ProjectileType.ROTO_SEED)
                                                                .pattern(new ShootPattern(Direction.UP_RIGHT, 0, 1))
                                                                .build(),
                                                PlantShootAbility.builder().damage(10).cooldown(0f)
                                                                .projectile(ProjectileType.ROTO_SEED)
                                                                .pattern(new ShootPattern(Direction.UP_LEFT, 0, 1))
                                                                .build(),
                                                PlantShootAbility.builder().damage(10).cooldown(0f)
                                                                .projectile(ProjectileType.ROTO_SEED)
                                                                .pattern(new ShootPattern(Direction.DOWN_RIGHT, 0, 1))
                                                                .build(),
                                                PlantShootAbility.builder().damage(10).cooldown(0f)
                                                                .projectile(ProjectileType.ROTO_SEED)
                                                                .pattern(new ShootPattern(Direction.DOWN_LEFT, 0, 1))
                                                                .build())),
                                new PlantLevelUpgrades(
                                                new PlantLevelUpgrade(2, PlantStatBonus.DAMAGE, 10),
                                                new PlantLevelUpgrade(3, PlantStatBonus.HP, 150),
                                                new PlantLevelUpgrade(4, PlantStatBonus.COST, -25)));
        }

        static PlantTypeSpec splitPea() {
                return new PlantTypeSpec(12, "Split Pea", PlantCategory.SHOOTER, EnumSet.of(PlantTag.PEA),
                                new PlantBaseStats(125, 300, 20, 1.5f, 5),
                                Arrays.asList(
                                                PlantShootAbility.builder().damage(20).cooldown(1.5f)
                                                                .projectile(ProjectileType.PEA)
                                                                .pattern(new ShootPattern(Direction.FORWARD, 0, 1))
                                                                .build(),
                                                PlantShootAbility.builder().damage(20).cooldown(1.5f)
                                                                .projectile(ProjectileType.PEA)
                                                                .pattern(new ShootPattern(Direction.BACK, 0, 2))
                                                                .build()),
                                new PlantRapidFireEffect(3, 0.1f, Arrays.asList(
                                                PlantShootAbility.builder().damage(20).cooldown(0f)
                                                                .projectile(ProjectileType.PEA)
                                                                .pattern(new ShootPattern(Direction.FORWARD, 0, 1))
                                                                .build(),
                                                PlantShootAbility.builder().damage(20).cooldown(0f)
                                                                .projectile(ProjectileType.PEA)
                                                                .pattern(new ShootPattern(Direction.BACK, 0, 1))
                                                                .build())),
                                new PlantLevelUpgrades(
                                                new PlantLevelUpgrade(2, PlantStatBonus.DAMAGE, 10),
                                                new PlantLevelUpgrade(3, PlantStatBonus.HP, 200),
                                                new PlantLevelUpgrade(4, PlantStatBonus.COST, -25)));
        }

        static PlantTypeSpec citron() {
                return new PlantTypeSpec(13, "Citron", PlantCategory.SHOOTER, EnumSet.of(PlantTag.CHARGE),
                                new PlantBaseStats(350, 300, 800, 9, 5),
                                Arrays.asList(
                                                PlantShootAbility.builder().damage(800).cooldown(9f)
                                                                .projectile(ProjectileType.LASER)
                                                                .pattern(new ShootPattern(Direction.FORWARD, 0, 1))
                                                                .build()),
                                new PlantRapidFireEffect(3, 0.1f, Arrays.asList(
                                                PlantShootAbility.builder().damage(8000).cooldown(0f)
                                                                .projectile(ProjectileType.PLASMA)
                                                                .pattern(new ShootPattern(Direction.FORWARD, 0, 1))
                                                                .build())),
                                new PlantLevelUpgrades(
                                                new PlantLevelUpgrade(2, PlantStatBonus.COOLDOWN, -1),
                                                new PlantLevelUpgrade(3, PlantStatBonus.DAMAGE, 150),
                                                new PlantLevelUpgrade(4, PlantStatBonus.COST, -50)));
        }

        static PlantTypeSpec bowlingBulb() {
                return new PlantTypeSpec(16, "Bowling Bulb", PlantCategory.SHOOTER, EnumSet.of(PlantTag.CHARGE),
                                new PlantBaseStats(200, 300, 40, 2, 5),
                                Arrays.asList(
                                                PlantShootAbility.builder().damage(40).cooldown(2f)
                                                                .projectile(ProjectileType.BOUNCING_AQUA)
                                                                .pattern(new ShootPattern(Direction.FORWARD, 0, 1))
                                                                .build()
                                                ),
                                new PlantRapidFireEffect(3, 1f, Arrays.asList(
                                                PlantShootAbility.builder().damage(600).cooldown(1.5f)
                                                                .projectile(ProjectileType.SUPER_BULB)
                                                                .pattern(new ShootPattern(Direction.FORWARD, 0, 3))
                                                                .build())),
                                new PlantLevelUpgrades(
                                                new PlantLevelUpgrade(2, PlantStatBonus.REGEN, -1),
                                                new PlantLevelUpgrade(3, PlantStatBonus.DAMAGE, 15),
                                                new PlantLevelUpgrade(4, PlantStatBonus.COST, -25)));
        }

        static PlantTypeSpec cactus() {
                return new PlantTypeSpec(17, "Cactus", PlantCategory.STRIKE_THROUGH, null,
                                new PlantBaseStats(175, 300, 30, 1.5f, 5),
                                Arrays.asList(
                                                PlantShootAbility.builder().damage(30).cooldown(1.5f)
                                                                .projectile(ProjectileType.SPIKE)
                                                                .pattern(new ShootPattern(Direction.FORWARD, 0, 1))
                                                                .pierce(3).build()),
                                new PlantRapidFireEffect(3, 0.1f, Arrays.asList(
                                                PlantShootAbility.builder().damage(60).cooldown(0f)
                                                                .projectile(ProjectileType.SPIKE)
                                                                .pattern(new ShootPattern(Direction.FORWARD, 0, 1))
                                                                .pierce(-1).build())),
                                new PlantLevelUpgrades(
                                                new PlantLevelUpgrade(2, PlantStatBonus.PIERCE_COUNT, 1),
                                                new PlantLevelUpgrade(3, PlantStatBonus.DAMAGE, 10),
                                                new PlantLevelUpgrade(4, PlantStatBonus.COST, -25)));
        }

        static PlantTypeSpec firePeashooter() {
                return new PlantTypeSpec(18, "Fire Peashooter", PlantCategory.SHOOTER,
                                EnumSet.of(PlantTag.PEA, PlantTag.FIRE),
                                new PlantBaseStats(175, 300, 40, 1.5f, 5),
                                Arrays.asList(
                                                PlantShootAbility.builder().damage(40).cooldown(1.5f)
                                                                .projectile(ProjectileType.FIRE)
                                                                .pattern(new ShootPattern(Direction.FORWARD, 0, 1))
                                                                .build()),
                                new PlantRapidFireEffect(3, 0.1f, Arrays.asList(
                                                PlantShootAbility.builder().damage(40).cooldown(0f)
                                                                .projectile(ProjectileType.FIRE)
                                                                .pattern(new ShootPattern(Direction.FORWARD, 0, 1))
                                                                .build())),
                                new PlantLevelUpgrades(
                                                new PlantLevelUpgrade(2, PlantStatBonus.DAMAGE, 10),
                                                new PlantLevelUpgrade(3, PlantStatBonus.HP, 200),
                                                new PlantLevelUpgrade(4, PlantStatBonus.COST, -25)));
        }

        static PlantTypeSpec starfruit() {
                return new PlantTypeSpec(19, "Starfruit", PlantCategory.SHOOTER, null,
                                new PlantBaseStats(150, 300, 20, 1.5f, 5),
                                Arrays.asList(
                                                PlantShootAbility.builder().damage(20).cooldown(1.5f)
                                                                .projectile(ProjectileType.STAR)
                                                                .pattern(new ShootPattern(Direction.BACK, 0, 1))
                                                                .build(),
                                                PlantShootAbility.builder().damage(20).cooldown(1.5f)
                                                                .projectile(ProjectileType.STAR)
                                                                .pattern(new ShootPattern(Direction.UP, 0, 1)).build(),
                                                PlantShootAbility.builder().damage(20).cooldown(1.5f)
                                                                .projectile(ProjectileType.STAR)
                                                                .pattern(new ShootPattern(Direction.DOWN, 0, 1))
                                                                .build(),
                                                PlantShootAbility.builder().damage(20).cooldown(1.5f)
                                                                .projectile(ProjectileType.STAR)
                                                                .pattern(new ShootPattern(Direction.UP_RIGHT, 0, 1))
                                                                .build(),
                                                PlantShootAbility.builder().damage(20).cooldown(1.5f)
                                                                .projectile(ProjectileType.STAR)
                                                                .pattern(new ShootPattern(Direction.DOWN_RIGHT, 0, 1))
                                                                .build()),
                                new PlantRapidFireEffect(3, 0.1f, Arrays.asList(
                                                PlantShootAbility.builder().damage(20).cooldown(0f)
                                                                .projectile(ProjectileType.STAR)
                                                                .pattern(new ShootPattern(Direction.BACK, 0, 1))
                                                                .build(),
                                                PlantShootAbility.builder().damage(20).cooldown(0f)
                                                                .projectile(ProjectileType.STAR)
                                                                .pattern(new ShootPattern(Direction.UP, 0, 1)).build(),
                                                PlantShootAbility.builder().damage(20).cooldown(0f)
                                                                .projectile(ProjectileType.STAR)
                                                                .pattern(new ShootPattern(Direction.DOWN, 0, 1))
                                                                .build(),
                                                PlantShootAbility.builder().damage(20).cooldown(0f)
                                                                .projectile(ProjectileType.STAR)
                                                                .pattern(new ShootPattern(Direction.UP_RIGHT, 0, 1))
                                                                .build(),
                                                PlantShootAbility.builder().damage(20).cooldown(0f)
                                                                .projectile(ProjectileType.STAR)
                                                                .pattern(new ShootPattern(Direction.DOWN_RIGHT, 0, 1))
                                                                .build(),
                                                PlantShootAbility.builder().damage(20).cooldown(0f)
                                                                .projectile(ProjectileType.STAR)
                                                                .pattern(new ShootPattern(Direction.DOWN_LEFT, 0, 1))
                                                                .build(),
                                                PlantShootAbility.builder().damage(20).cooldown(0f)
                                                                .projectile(ProjectileType.STAR)
                                                                .pattern(new ShootPattern(Direction.UP_LEFT, 0, 1))
                                                                .build())),
                                new PlantLevelUpgrades(
                                                new PlantLevelUpgrade(2, PlantStatBonus.ATTACK_SPEED, 10),
                                                new PlantLevelUpgrade(3, PlantStatBonus.DAMAGE, 10),
                                                new PlantLevelUpgrade(4, PlantStatBonus.COST, -25)));
        }

        static PlantTypeSpec gooPeashooter() {
                return new PlantTypeSpec(20, "Goo Peashooter", PlantCategory.SHOOTER, EnumSet.of(PlantTag.POISON),
                                new PlantBaseStats(125, 300, 20, 1.5f, 5),
                                Arrays.asList(
                                                PlantShootAbility.builder().damage(20).cooldown(1.5f)
                                                                .projectile(ProjectileType.POISON)
                                                                .pattern(new ShootPattern(Direction.FORWARD, 0, 1))
                                                                .build()),
                                new PlantRapidFireEffect(3, 0.1f, Arrays.asList(
                                                PlantShootAbility.builder().damage(20).cooldown(0f)
                                                                .projectile(ProjectileType.POISON)
                                                                .pattern(new ShootPattern(Direction.FORWARD, 0, 1))
                                                                .build())),
                                new PlantLevelUpgrades(
                                                new PlantLevelUpgrade(2, PlantStatBonus.DAMAGE_PER_TICK, 5),
                                                new PlantLevelUpgrade(3, PlantStatBonus.HP, 150),
                                                new PlantLevelUpgrade(4, PlantStatBonus.COST, -25)));
        }

        static PlantTypeSpec megaGatlingPea() {
                return new PlantTypeSpec(21, "Mega Gatling Pea", PlantCategory.SHOOTER, EnumSet.of(PlantTag.PEA),
                                new PlantBaseStats(400, 300, 20, 1.5f, 5),
                                Arrays.asList(
                                                PlantShootAbility.builder().damage(20).cooldown(1.5f)
                                                                .projectile(ProjectileType.PEA)
                                                                .pattern(new ShootPattern(Direction.FORWARD, 0, 4))
                                                                .build()),
                                new PlantRapidFireEffect(3, 0.1f,
                                                Arrays.asList(
                                                                PlantShootAbility.builder().damage(20).cooldown(0f)
                                                                                .projectile(ProjectileType.PEA)
                                                                                .pattern(new ShootPattern(
                                                                                                Direction.FORWARD, 0,
                                                                                                1))
                                                                                .build(),
                                                                PlantShootAbility.builder().damage(400).cooldown(0f)
                                                                                .projectile(ProjectileType.PEA)
                                                                                .phase(EffectPhase.END).build(),
                                                                PlantShootAbility.builder().damage(400).cooldown(0f)
                                                                                .projectile(ProjectileType.PEA)
                                                                                .phase(EffectPhase.END).build(),
                                                                PlantShootAbility.builder().damage(400).cooldown(0f)
                                                                                .projectile(ProjectileType.PEA)
                                                                                .phase(EffectPhase.END).build())),
                                new PlantLevelUpgrades(
                                                PlantLevelUpgrade.atLevel(2, PlantStatBonus.DAMAGE, 10),
                                                PlantLevelUpgrade.atLevel(3, PlantStatBonus.SPECIAL_CHANGE, 5),
                                                PlantLevelUpgrade.atLevel(4, PlantStatBonus.COST, -50)));
        }

        static PlantTypeSpec seaShroom() {
                return new PlantTypeSpec(22, "Sea-shroom", PlantCategory.SHOOTER,
                                EnumSet.of(PlantTag.SHROOM, PlantTag.WATER),
                                new PlantBaseStats(0, 300, 20, 1.5f, 15),
                                Arrays.asList(
                                                PlantShootAbility.builder().damage(20).cooldown(1.5f)
                                                                .projectile(ProjectileType.FUME)
                                                                .lifespan(60)
                                                                .pattern(new ShootPattern(Direction.FORWARD, 0, 1))
                                                                .maxRange(3).build()),
                                new PlantRapidFireEffect(3, 0.1f,
                                                Arrays.asList(
                                                                PlantShootAbility.builder().damage(20).cooldown(0f)
                                                                                .projectile(ProjectileType.FUME)
                                                                                .pattern(new ShootPattern(
                                                                                                Direction.FORWARD, 0,
                                                                                                1))
                                                                                .pierce(-1).maxRange(3)
                                                                                .build())),
                                new PlantLevelUpgrades(
                                                PlantLevelUpgrade.atLevel(2, PlantStatBonus.RANGE, 1),
                                                PlantLevelUpgrade.atLevel(3, PlantStatBonus.DAMAGE, 5),
                                                PlantLevelUpgrade.atLevel(4, PlantStatBonus.LIFE_SPAN, 10)));
        }

        static PlantTypeSpec puffShroom() {
                return new PlantTypeSpec(23, "Puff-shroom", PlantCategory.SHOOTER, EnumSet.of(PlantTag.SHROOM),
                                new PlantBaseStats(0, 300, 20, 1.5f, 5),
                                Arrays.asList(
                                                PlantShootAbility.builder().damage(20).cooldown(1.5f)
                                                                .projectile(ProjectileType.FUME)
                                                                .lifespan(60)
                                                                .pattern(new ShootPattern(Direction.FORWARD, 0, 1))
                                                                .pierce(-1).maxRange(3).build()),
                                new PlantRapidFireEffect(3, 0.1f,
                                                Arrays.asList(
                                                                PlantShootAbility.builder().damage(20).cooldown(0f)
                                                                                .projectile(ProjectileType.FUME)
                                                                                .pattern(new ShootPattern(
                                                                                                Direction.FORWARD, 0,
                                                                                                1))
                                                                                .build())),
                                new PlantLevelUpgrades(
                                                PlantLevelUpgrade.atLevel(2, PlantStatBonus.LIFE_SPAN, 10),
                                                PlantLevelUpgrade.atLevel(3, PlantStatBonus.DAMAGE, 10),
                                                PlantLevelUpgrade.atLevel(4, PlantStatBonus.RANGE, 1)));
        }

        static PlantTypeSpec fumeShroom() {
                return new PlantTypeSpec(24, "Fume-shroom", PlantCategory.STRIKE_THROUGH, EnumSet.of(PlantTag.SHROOM),
                                new PlantBaseStats(125, 300, 20, 1.5f, 5),
                                Arrays.asList(
                                                PlantShootAbility.builder().damage(20).cooldown(1.5f)
                                                                .projectile(ProjectileType.FUME)
                                                                .pattern(new ShootPattern(Direction.FORWARD, 0, 1))
                                                                .pierce(-1).maxRange(4).build()),
                                new PlantRapidFireEffect(3, 0.1f,
                                                Arrays.asList(
                                                                PlantShootAbility.builder().damage(20).cooldown(0f)
                                                                                .projectile(ProjectileType.FUME)
                                                                                .pattern(new ShootPattern(
                                                                                                Direction.FORWARD, 0,
                                                                                                1))
                                                                                .pierce(-1).maxRange(5).knockBack(30)
                                                                                .build())),
                                new PlantLevelUpgrades(
                                                PlantLevelUpgrade.atLevel(2, PlantStatBonus.RANGE, 1),
                                                PlantLevelUpgrade.atLevel(3, PlantStatBonus.DAMAGE, 10),
                                                PlantLevelUpgrade.atLevel(4, PlantStatBonus.COST, -25)));
        }
}
