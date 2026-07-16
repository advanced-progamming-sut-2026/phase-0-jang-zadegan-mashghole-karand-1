package model.data.plant.abilities.runtime;

import model.core.EventBus;
import model.core.GameState;
import model.core.Position;
import model.data.plant.Plant;
import model.data.plant.abilities.config.DefenderFeature;
import model.data.plant.abilities.config.PlantAbilityConfig;
import model.data.plant.abilities.effects.HitEffect;
import model.data.sun.Sun;
import model.data.sun.SunType;
import model.data.zombie.Zombie;
import model.events.SunProducedEvent;
import model.events.ZombieDiedEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

public class PlantDefenderAbility implements PlantAbilityConfig {
    private final EnumSet<DefenderFeature> features;
    private final int reflectDamage;
    private final int explosionDamage;
    private final int sunPerHit;

    public PlantDefenderAbility(EnumSet<DefenderFeature> features, int reflectDamage, int explosionDamage, int sunPerHit) {
        this.features = features;
        this.reflectDamage = reflectDamage;
        this.explosionDamage = explosionDamage;
        this.sunPerHit = sunPerHit;
    }

    @Override
    public PlantAbilityConfig createInstance(Plant plant) {
        return new PlantDefenderAbility(features, reflectDamage, explosionDamage, sunPerHit);
    }

    @Override
    public void onTick(Plant plant, GameState state, EventBus event) {
        if (features.contains(DefenderFeature.ATTRACT_ZOMBIES)) {
            attractAdjacentZombies(plant, state);
        }
    }

    public void onDamaged(Plant plant, Zombie attacker,
                          GameState state, EventBus event) {
        if (features.contains(DefenderFeature.REFLECT_DAMAGE)) {
            attacker.takeDamage(reflectDamage);
        }
        if (features.contains(DefenderFeature.MOVE_ATTACKER)) {
            moveAttacker(attacker);
        }
        if (features.contains(DefenderFeature.PRODUCE_SUN_ON_HIT)) {
            produceSun(plant, state, sunPerHit, event);
        }
    }

    public void onDeath(Plant plant, GameState state, EventBus event) {
        if (features.contains(DefenderFeature.EXPLODE_ON_DEATH)) {
            explode(plant, state, event, explosionDamage);
        }
    }
    public boolean blocksJump() {
        return features.contains(DefenderFeature.BLOCK_JUMP);
    }
    public void produceSun(Plant plant, GameState state, int sunPerHit, EventBus event) {
        Sun sun = new Sun(
                plant.row,
                new Position(plant.getX(), plant.getY()),
                sunPerHit,
                plant
        );
        state.sunDrops.add(sun);
        event.publish(new SunProducedEvent(plant, sun));

    }

    public void moveAttacker(Zombie attacker) {
        List<Integer> validRows = new ArrayList<>();
        if (attacker.row > 0) {
            validRows.add(attacker.row - 1);
        }
        if (attacker.row < GameState.GRID_ROWS - 1) {
            validRows.add(attacker.row + 1);
        }
        if (validRows.isEmpty()) return;
        Collections.shuffle(validRows);
        int newRow = validRows.get(0);
        attacker.row = newRow;
        attacker.position.y =
                newRow * GameState.CELL_HEIGHT
                        + GameState.CELL_HEIGHT / 2f;
    }

    public void explode(Plant plant, GameState state, EventBus event, int explosionDamage) {
        List<Zombie> targets = findTarget(state, plant);
        for (Zombie z : targets) {
            z.takeDamage(explosionDamage);
            if (!z.isAlive)
                event.publish(new ZombieDiedEvent(z));
        }
    }

    private List<Zombie> findTarget(GameState state, Plant plant) {
        List<Zombie> targets = state.zombies.stream()
                .filter(z -> z.isAlive)
                .filter(z -> z.row == plant.row && (int) (z.position.x / GameState.CELL_WIDTH) == plant.col)
                .collect(Collectors.toList());

        return targets;

    }

    public static class Builder {
        private final EnumSet<DefenderFeature> features =
                EnumSet.noneOf(DefenderFeature.class);

        private int reflectDamage;
        private int explosionDamage;
        private int sunPerHit;


        public Builder reflectDamage(int damage) {
            features.add(DefenderFeature.REFLECT_DAMAGE);
            reflectDamage = damage;
            return this;
        }

        public Builder explodeOnDeath(int damage) {
            features.add(DefenderFeature.EXPLODE_ON_DEATH);
            explosionDamage = damage;
            return this;
        }

        public Builder produceSunOnHit(int amount) {
            features.add(DefenderFeature.PRODUCE_SUN_ON_HIT);
            sunPerHit = amount;
            return this;
        }
        public Builder attractZombies() {
            features.add(DefenderFeature.ATTRACT_ZOMBIES);
            return this;
        }
        public Builder moveZombie(){
            features.add(DefenderFeature.MOVE_ATTACKER);
            return this;
        }
        public Builder blockJump(){
            features.add(DefenderFeature.BLOCK_JUMP);
            return this;
        }

        public PlantDefenderAbility build() {
            return new PlantDefenderAbility(
                    EnumSet.copyOf(features),
                    reflectDamage,
                    explosionDamage,
                    sunPerHit
            );
        }
    }

    private void attractAdjacentZombies(Plant plant, GameState state) {
        List<Zombie> targets = findNearTarget(state, plant);
        for (Zombie z : targets) {
            z.row = plant.row;
            z.position.y =
                    plant.row * GameState.CELL_HEIGHT
                            + GameState.CELL_HEIGHT / 2f;
        }
    }

    private List<Zombie> findNearTarget(GameState state, Plant plant) {
        List<Zombie> targets = state.zombies.stream()
                .filter(z -> z.isAlive)
                .filter(z -> (z.row == plant.row + 1 || z.row == plant.row - 1) && (int) (z.position.x / GameState.CELL_WIDTH) == plant.col)
                .collect(Collectors.toList());
        return targets;
    }
}
