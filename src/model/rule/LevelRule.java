package model.rule;

import model.core.EventBus;
import model.core.GameState;
import model.data.plant.Plant;
import model.data.plant.PlantType;
import model.data.zombie.Zombie;

public interface LevelRule {
    /**
     * use for initial setup
     */
    default void onSessionStart(SessionContext context, GameState state, EventBus bus) {
    }

    /**
     * use this for effects that need to apply before the tick
     */
    default void preTick(SessionContext context, GameState state, EventBus bus) {
    }

    /**
     * use this for effects that need to apply after the tick
     */
    default void postTick(SessionContext context, GameState state, EventBus bus) {
    }

    default void onWaveStart(SessionContext context, GameState state, EventBus bus) {
    }

    default void onWaveEnd(SessionContext context, GameState state, EventBus bus) {
    }

    default void onPlantPlaced(Plant plant, GameState state) {
    }

    default void onPlantDied(Plant plant, GameState state, EventBus bus) {
    }

    default void onZombieSpawned(Zombie zombie, SessionContext context, GameState state) {
    }

    default void onZombieDied(Zombie zombie, GameState state, EventBus bus) {
    }

    default boolean shouldDropSkySun() {
        return true;
    }

    default boolean freezeProjectilesEnabled() {
        return true;
    }

    default boolean skipsPlantSelection() {
        return false;
    }

    default boolean shouldSpawnWaves() {
        return true;
    }

    default boolean lawnMowersEnabled() {
        return true;
    }

    default boolean usesSunCurrency() {
        return true;
    }

    default boolean canPlaceZombies() {
        return false;
    }

    default boolean canPlant(PlantType type, int row, int col, GameState state, SessionContext context) {
        return true;
    }

    default boolean canPlaceZombie(model.data.zombie.ZombieType type, int row, int col, GameState state,
            SessionContext context) {
        return true;
    }

    default boolean winsOnWaveClear() {
        return true;
    }

    default void onSunCollected(model.data.sun.Sun sun, GameState state, EventBus bus) {
    }
}