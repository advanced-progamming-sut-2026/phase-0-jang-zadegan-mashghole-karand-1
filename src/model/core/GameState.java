package model.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import model.data.plant.Plant;
import model.data.projectile.Projectile;
import model.data.sun.Sun;
import model.data.zombie.Zombie;

public class GameState implements ReadOnlyGameState {
    public List<Plant> plants = new ArrayList<>();
    public List<Zombie> zombies = new ArrayList<>();
    public List<Projectile> projectiles = new ArrayList<>();
    public List<Sun> sunDrops = new ArrayList<>();

    public int sunAmount = INITIAL_SUN_AMOUNT;
    public int plantFoodAmount = 0;

    public int currentWave = 1;
    public int zombiesRemaining = 0;
    public boolean gameOver = false;
    public boolean levelComplete = false;

    public int totalTicks = 0;

    @Override
    public List<Plant> getPlants() {
        return Collections.unmodifiableList(plants);
    }

    @Override
    public List<Zombie> getZombies() {
        return Collections.unmodifiableList(zombies);
    }

    @Override
    public List<Projectile> getProjectiles() {
        return Collections.unmodifiableList(projectiles);
    }

    @Override
    public List<Sun> getSunDrops() {
        return Collections.unmodifiableList(sunDrops);
    }

    @Override
    public int getSunAmount() {
        return sunAmount;
    }

    @Override
    public int getPlantFoodAmount() {
        return plantFoodAmount;
    }

    @Override
    public int getCurrentWave() {
        return currentWave;
    }

    @Override
    public int getZombiesRemaining() {
        return zombiesRemaining;
    }

    @Override
    public boolean isGameOver() {
        return gameOver;
    }

    @Override
    public boolean isLevelComplete() {
        return levelComplete;
    }

    @Override
    public int getTotalTicks() {
        return totalTicks;
    }

    @Override
    public Plant getPlantAt(int row, int col) {
        return plants.stream().filter(p -> p.row == row && p.col == col).findFirst().orElse(null);
    }

    public void reset() {
        plants.clear();
        zombies.clear();
        projectiles.clear();
        sunDrops.clear();
        sunAmount = INITIAL_SUN_AMOUNT;
        currentWave = 1;
        zombiesRemaining = 0;
        gameOver = false;
        levelComplete = false;
    }
}
