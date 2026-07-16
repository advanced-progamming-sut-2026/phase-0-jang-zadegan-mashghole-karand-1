package model.data.plant.effects.runtime;

import model.data.plant.Plant;
import model.data.plant.abilities.config.DefenderFeature;
import model.data.plant.abilities.runtime.PlantDefenderAbility;
import model.data.plant.effects.config.PlantEffectConfig;

import java.util.EnumSet;

public class PlantDefenderEffect implements PlantEffectConfig {

    private final EnumSet<DefenderFeature> features;
    private final int armorHp;
    private final int reflectBonus;
    private final int armorExplosionDamage;

    private PlantDefenderEffect(
            EnumSet<DefenderFeature> features,
            int armorHp,
            int reflectBonus,
            int armorExplosionDamage) {

        this.features = features;
        this.armorHp = armorHp;
        this.reflectBonus = reflectBonus;
        this.armorExplosionDamage = armorExplosionDamage;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public PlantEffectConfig createInstance(Plant plant) {
        return new PlantDefenderEffect(
                EnumSet.copyOf(features),
                armorHp,
                reflectBonus,
                armorExplosionDamage
        );
    }

    public static class Builder {

        private final EnumSet<DefenderFeature> features =
                EnumSet.noneOf(DefenderFeature.class);

        private int armorHp;
        private int reflectBonus;
        private int armorExplosionDamage;

        public Builder addArmor(int hp) {
            features.add(DefenderFeature.ADD_ARMOR);
            this.armorHp = hp;
            return this;
        }

        public Builder increaseReflect(int amount) {
            features.add(DefenderFeature.REFLECT_DAMAGE);
            this.reflectBonus = amount;
            return this;
        }

        public Builder healFully() {
            features.add(DefenderFeature.HEAL_FULLY);
            return this;
        }

        public Builder moveAllZombies() {
            features.add(DefenderFeature.MOVE_ALL_ZOMBIES);
            return this;
        }

        public Builder attractAll() {
            features.add(DefenderFeature.ATTRACT_ALL);
            return this;
        }

        public Builder explodeOnArmorBreak(int damage) {
            features.add(DefenderFeature.EXPLODE_ON_ARMOR_BREAK);
            this.armorExplosionDamage = damage;
            return this;
        }

        public PlantDefenderEffect build() {
            return new PlantDefenderEffect(
                    EnumSet.copyOf(features),
                    armorHp,
                    reflectBonus,
                    armorExplosionDamage
            );
        }
    }
}