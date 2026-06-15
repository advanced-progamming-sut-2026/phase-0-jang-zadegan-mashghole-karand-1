package model.core;

import java.util.ArrayList;
import java.util.List;

import model.data.plant.Plant;
import model.data.projectile.Projectile;
import model.data.sun.Sun;
import model.data.zombie.Zombie;

public class GameState {
    public List<Plant> plants = new ArrayList<>();
    public List<Zombie> zombies = new ArrayList<>();
    public List<Projectile> projectiles = new ArrayList<>();
    public List<Sun> sunDrops = new ArrayList<>();

    public static final int INITIAL_SUN_AMOUNT = 150;
    public int sunAmount = INITIAL_SUN_AMOUNT;

    public int currentWave = 1;
    public int zombiesRemaining = 0;
    public boolean gameOver = false;
    public boolean levelComplete = false;

    public static final int GRID_COLS = 9;
    public static final int GRID_ROWS = 5;
    public static final int CELL_WIDTH = 80;
    public static final int CELL_HEIGHT = 100;
    public static final int SCREEN_WIDTH = GRID_COLS * CELL_WIDTH;
    public static final int SCREEN_HEIGHT = GRID_ROWS * CELL_HEIGHT;

    public static final int PROJECTILE_HIT_RADIUS = 25;
    public static final int ZOMBIE_ATTACK_RANGE = 40;
    public static final int PLANT_TOUCH_DISTANCE = 30;

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
