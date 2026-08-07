package model.data.plant.effects.runtime;

import model.core.EventBus;
import model.core.GameState;
import model.data.plant.Plant;
import model.data.plant.abilities.config.DefenderFeature;
import model.data.plant.abilities.config.PlantAbilityConfig;
import model.data.plant.abilities.runtime.PlantDefenderAbility;
import model.data.plant.effects.config.PlantEffectConfig;
import model.data.zombie.Zombie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

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
    @Override
    public void onActivate(Plant plant, GameState state, EventBus event) {
        if (features.contains(DefenderFeature.HEAL_FULLY)) {
            plant.hp = plant.totalHP;
        }
        if (features.contains(DefenderFeature.ADD_ARMOR)) {
            plant.hp += armorHp;
            plant.totalHP += armorHp;
        }
        if (features.contains(DefenderFeature.REFLECT_DAMAGE)) {
            for (PlantAbilityConfig a : plant.abilities) {
                if (a instanceof PlantDefenderAbility def) {
                    def.addReflectDamage(reflectBonus);
                }
            }
        }
        if (features.contains(DefenderFeature.MOVE_ALL_ZOMBIES)) {
                for (Zombie attacker : state.zombies){
                    if (!attacker.isAlive) continue;

                    List<Integer> validRows = new ArrayList<>();
                    if (attacker.row > 0) {
                        validRows.add(attacker.row - 1);
                    }
                    if (attacker.row < GameState.GRID_ROWS - 1) {
                        validRows.add(attacker.row + 1);
                    }
                    if (validRows.isEmpty()) continue;
                    Collections.shuffle(validRows);
                    int newRow = validRows.get(0);
                    attacker.row = newRow;
                    attacker.position.y =
                            newRow * GameState.CELL_HEIGHT
                                    + GameState.CELL_HEIGHT / 2f;
                }

        }
        if (features.contains(DefenderFeature.ATTRACT_ALL)) {
            for (Zombie z : state.zombies) {
                if (!z.isAlive) continue;
                z.row = plant.row;
                z.position.y = plant.row * GameState.CELL_HEIGHT + GameState.CELL_HEIGHT / 2f;
            }
        }
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