package model.core;

import java.util.List;

import model.board.GameBoard;
import model.data.Barrel.Barrel;
import model.data.Grave.Grave;
import model.data.brain.Brain;
import model.data.plant.Plant;
import model.data.projectile.Projectile;
import model.data.seed.PlantSeedDrop;
import model.data.sun.Sun;
import model.data.vase.Vase;
import model.data.zombie.Zombie;
import model.event.events.GameOverReason;

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

    GameBoard getBoard();

    List<Plant> getPlants();

    List<Zombie> getZombies();

    List<Projectile> getProjectiles();

    List<Sun> getSunDrops();

    List<Vase> getVases();

    List<PlantSeedDrop> getSeedDrops();

    List<Brain> getBrains();

    boolean isBrainsMode();

    int getSunAmount();

    int getPlantFoodAmount();

    int getCurrentWave();

    int getZombiesRemaining();

    boolean isGameOver();

    boolean isLevelComplete();

    GameOverReason getGameOverReason();

    int getTotalTicks();

    Plant getPlantAt(int row, int col);

    Vase getVaseAt(int row, int col);

    PlantSeedDrop getSeedDropAt(int row, int col);

    Barrel getBarrelAt(int row, int col);

    Grave getGraveAt(int row, int col);
}
