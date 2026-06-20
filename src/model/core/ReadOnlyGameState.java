package model.core;

import java.util.List;

import model.data.plant.Plant;
import model.data.projectile.Projectile;
import model.data.sun.Sun;
import model.data.zombie.Zombie;

public interface ReadOnlyGameState {
    public static final int GRID_COLS = 9;
    public static final int GRID_ROWS = 5;
    public static final int CELL_WIDTH = 80;
    public static final int CELL_HEIGHT = 100;
    public static final int SCREEN_WIDTH = GRID_COLS * CELL_WIDTH;
    public static final int SCREEN_HEIGHT = GRID_ROWS * CELL_HEIGHT;

    public static final int PROJECTILE_HIT_RADIUS = 25;
    public static final int ZOMBIE_ATTACK_RANGE = 40;
    public static final int PLANT_TOUCH_DISTANCE = 30;

    public static final int INITIAL_SUN_AMOUNT = 150;

    List<Plant> getPlants();

    List<Zombie> getZombies();

    List<Projectile> getProjectiles();

    List<Sun> getSunDrops();

    int getSunAmount();

    int getPlantFoodAmount();

    int getCurrentWave();

    int getZombiesRemaining();

    boolean isGameOver();

    boolean isLevelComplete();

    int getTotalTicks();

    Plant getPlantAt(int row, int col);
}
