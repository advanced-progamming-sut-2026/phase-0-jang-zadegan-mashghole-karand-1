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

    default boolean canPlant(PlantType type, int row, int col, GameState state) {
        return true;
    }

    /**
     * use this for additional spawn offset for zombies
     */
    default int getSpawnOffset(Zombie zombie) {
        return 0;
    }
}