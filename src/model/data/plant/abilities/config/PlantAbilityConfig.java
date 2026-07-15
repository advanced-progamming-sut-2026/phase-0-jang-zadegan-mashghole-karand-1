package model.data.plant.abilities.config;

import model.core.EventBus;
import model.core.GameState;
import model.data.plant.Plant;

public interface PlantAbilityConfig {
    public PlantAbilityConfig createInstance(Plant plant);

    public void onTick(Plant plant, GameState state, EventBus event);
    default void resetCooldown() {
    }
}

// should be moved to their own files

// public class IceShootConfig extends PlantAbilityConfig {
// public final int damage;
// public final float cooldownSeconds;
// public final int slowDurationTicks;

// public IceShootConfig(int damage, float cooldownSeconds, int
// slowDurationTicks) {
// this.damage = damage;
// this.cooldownSeconds = cooldownSeconds;
// this.slowDurationTicks = slowDurationTicks;
// }

// @Override
// public AbilityType getType() {
// return AbilityType.SHOOT_ICE;
// }
// }

// public class ExplodeConfig extends PlantAbilityConfig {
// public final int damage;
// public final float radiusTiles;
// public final int armTimeSeconds; // 0 = instant

// public ExplodeConfig(int damage, float radiusTiles, int armTimeSeconds) {
// this.damage = damage;
// this.radiusTiles = radiusTiles;
// this.armTimeSeconds = armTimeSeconds;
// }

// @Override
// public AbilityType getType() {
// return AbilityType.EXPLODE;
// }
// }

// public class ThornsConfig extends PlantAbilityConfig {
// public final int reflectDamage;

// public ThornsConfig(int reflectDamage) {
// this.reflectDamage = reflectDamage;
// }

// @Override
// public AbilityType getType() {
// return AbilityType.THORNS;
// }
// }

// public class WallConfig extends PlantAbilityConfig {
// @Override
// public AbilityType getType() {
// return AbilityType.WALL;
// }
// }

// public class MoveZombieConfig extends PlantAbilityConfig {
// @Override
// public AbilityType getType() {
// return AbilityType.MOVE_ZOMBIE;
// }
// }

// public class RemoveMetalConfig extends PlantAbilityConfig {
// public final int cooldownSeconds;
// public final float rangeTiles;

// public RemoveMetalConfig(int cooldownSeconds, float rangeTiles) {
// this.cooldownSeconds = cooldownSeconds;
// this.rangeTiles = rangeTiles;
// }

// @Override
// public AbilityType getType() {
// return AbilityType.REMOVE_METAL;
// }
// }

// public class ModifyProjectileConfig extends PlantAbilityConfig {
// public final float damageMultiplier;

// public ModifyProjectileConfig(float damageMultiplier) {
// this.damageMultiplier = damageMultiplier;
// }

// @Override
// public AbilityType getType() {
// return AbilityType.MODIFY_PROJECTILE;
// }
// }

// public class LobbedShootConfig extends PlantAbilityConfig {
// public final int damage;
// public final float cooldownSeconds;
// public final ProjectileType projectileType;

// public LobbedShootConfig(int damage, float cooldownSeconds, ProjectileType
// projectileType) {
// this.damage = damage;
// this.cooldownSeconds = cooldownSeconds;
// this.projectileType = projectileType;
// }

// @Override
// public AbilityType getType() {
// return AbilityType.SHOOT_LOBBED;
// }
// }

// public class HomingShootConfig extends PlantAbilityConfig {
// public final int damage;
// public final float cooldownSeconds;
// public final float rangeTiles;

// public HomingShootConfig(int damage, float cooldownSeconds, float rangeTiles)
// {
// this.damage = damage;
// this.cooldownSeconds = cooldownSeconds;
// this.rangeTiles = rangeTiles;
// }

// @Override
// public AbilityType getType() {
// return AbilityType.SHOOT_HOMING;
// }
// }

// public class PiercingShootConfig extends PlantAbilityConfig {
// public final int damage;
// public final float cooldownSeconds;
// public final ProjectileType projectileType;
// public final int pierceCount;

// public PiercingShootConfig(int damage, float cooldownSeconds, ProjectileType
// projectileType, int pierceCount) {
// this.damage = damage;
// this.cooldownSeconds = cooldownSeconds;
// this.projectileType = projectileType;
// this.pierceCount = pierceCount;
// }

// @Override
// public AbilityType getType() {
// return AbilityType.SHOOT_PIERCING;
// }
// }