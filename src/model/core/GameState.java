package model.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import model.data.Barrel.Barrel;
import model.data.brain.Brain;
import model.data.plant.Plant;
import model.data.plant.PlantType;
import model.data.projectile.Projectile;
import model.data.seed.PlantSeedDrop;
import model.data.sun.Sun;
import model.data.zombie.Zombie;
import model.board.GameBoard;
import model.board.Tile;
import model.data.Grave.Grave;
import model.data.vase.Vase;
import model.event.events.GameOverReason;

public class GameState implements ReadOnlyGameState {
    public List<Plant> plants = new ArrayList<>();
    public List<Zombie> zombies = new ArrayList<>();
    public List<Projectile> projectiles = new ArrayList<>();
    public List<Sun> sunDrops = new ArrayList<>();
    public List<Grave> graves = new ArrayList<>();
    public List<Vase> vases = new ArrayList<>();
    public List<PlantSeedDrop> seedDrops = new ArrayList<>();
    public List<Barrel> barrels = new ArrayList<>();
    public List<Brain> brains = new ArrayList<>();
    public boolean brainsMode = false;
    private GameBoard board = new GameBoard(GameState.GRID_ROWS, GameState.GRID_COLS, this);

    public int sunAmount = INITIAL_SUN_AMOUNT;
    public int plantFoodAmount = 0;

    public int currentWave = 0;
    public int zombiesRemaining = 0;
    public boolean gameOver = false;
    public boolean levelComplete = false;
    public GameOverReason gameOverReason = null;
    public int totalTicks = 0;
    public int sessionScore = 0;
    public boolean hasSessionScore = false;
    public boolean sessionScoreNewRecord = false;

    @Override
    public GameBoard getBoard() {
        return board;
    }

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
    public List<Vase> getVases() {
        return Collections.unmodifiableList(vases);
    }

    @Override
    public List<PlantSeedDrop> getSeedDrops() {
        return Collections.unmodifiableList(seedDrops);
    }

    @Override
    public List<Brain> getBrains() {
        return Collections.unmodifiableList(brains);
    }

    @Override
    public boolean isBrainsMode() {
        return brainsMode;
    }

    public Brain getBrainAtRow(int row) {
        return brains.stream().filter(b -> b.row == row).findFirst().orElse(null);
    }

    public int getCollectedBrainCount() {
        int count = 0;
        for (Brain brain : brains) {
            if (brain.isCollected()) {
                count++;
            }
        }
        return count;
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

    public void setCurrentWave(int newWave) {
        currentWave = newWave;
    }

    public void incrementCurrentWave() {
        currentWave++;
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
    public GameOverReason getGameOverReason() {
        return gameOverReason;
    }

    @Override
    public int getTotalTicks() {
        return totalTicks;
    }

    @Override
    public int getSessionScore() {
        return sessionScore;
    }

    @Override
    public boolean hasSessionScore() {
        return hasSessionScore;
    }

    @Override
    public boolean isSessionScoreNewRecord() {
        return sessionScoreNewRecord;
    }

    public void setSessionScore(int score, boolean newRecord) {
        this.sessionScore = Math.max(0, score);
        this.hasSessionScore = true;
        this.sessionScoreNewRecord = newRecord;
    }

    public void clearSessionScore() {
        this.sessionScore = 0;
        this.hasSessionScore = false;
        this.sessionScoreNewRecord = false;
    }

    @Override
    public Plant getPlantAt(int row, int col) {
        Tile tile = board.getTile(row, col);
        if (tile != null) {
            if (tile.getPlant() != null) {
                return tile.getPlant();
            }
            if (tile.getLilyPad() != null) {
                return tile.getLilyPad();
            }
        }
        return plants.stream().filter(p -> p.row == row && p.col == col).findFirst().orElse(null);
    }

    public Grave getGraveAt(int row, int col) {
        Tile tile = board.getTile(row, col);
        if (tile != null && tile.getGrave() != null) {
            return tile.getGrave();
        }
        return graves.stream().filter(g -> g.row == row && g.col == col).findFirst().orElse(null);
    }

    public void addPlant(Plant plant) {
        if (plant == null) {
            return;
        }
        if (!plants.contains(plant)) {
            plants.add(plant);
        }
        attachPlantToTile(plant);
    }

    public boolean removePlant(Plant plant) {
        if (plant == null) {
            return false;
        }
        boolean removed = plants.remove(plant);
        detachPlantFromTile(plant);
        return removed;
    }

    public void removeDeadPlants() {
        for (int i = plants.size() - 1; i >= 0; i--) {
            Plant plant = plants.get(i);
            if (plant.hp <= 0 || !plant.isAlive) {
                plants.remove(i);
                detachPlantFromTile(plant);
            }
        }
    }

    public void removeDeadZombies() {
        zombies.removeIf(z -> z == null || !z.isAlive || z.hp <= 0);
    }

    public void addGrave(Grave grave) {
        if (grave == null) {
            return;
        }
        if (!graves.contains(grave)) {
            graves.add(grave);
        }
        Tile tile = board.getTile(grave.row, grave.col);
        if (tile != null) {
            tile.setGrave(grave);
        }
    }

    public void removeGrave(Grave grave) {
        if (grave == null) {
            return;
        }
        graves.remove(grave);
        Tile tile = board.getTile(grave.row, grave.col);
        if (tile != null && tile.getGrave() == grave) {
            tile.removeGrave();
        }
    }

    private void attachPlantToTile(Plant plant) {
        Tile tile = board.getTile(plant.row, plant.col);
        if (tile == null) {
            return;
        }
        if (plant.type == PlantType.Lily_Pad) {
            tile.setLilyPad(plant);
        } else {
            tile.setPlant(plant);
        }
    }

    private void detachPlantFromTile(Plant plant) {
        Tile tile = board.getTile(plant.row, plant.col);
        if (tile == null) {
            return;
        }
        tile.detachPlant(plant);
    }

    @Override
    public Vase getVaseAt(int row, int col) {
        return vases.stream().filter(v -> v.row == row && v.col == col).findFirst().orElse(null);
    }

    @Override
    public PlantSeedDrop getSeedDropAt(int row, int col) {
        return seedDrops.stream().filter(s -> s.row == row && s.col == col).findFirst().orElse(null);
    }

    public Barrel getBarrelAt(int row, int col) {
        return barrels.stream().filter(b -> b.row == row && b.col == col).findFirst().orElse(null);
    }

    public void addZombie(Zombie zombie) {
        zombies.add(zombie);
    }

    public void reset() {
        plants.clear();
        zombies.clear();
        projectiles.clear();
        sunDrops.clear();
        graves.clear();
        vases.clear();
        seedDrops.clear();
        board.reset();
        barrels.clear();
        brains.clear();
        brainsMode = false;
        sunAmount = INITIAL_SUN_AMOUNT;
        plantFoodAmount = 0;
        currentWave = 0;
        zombiesRemaining = 0;
        gameOver = false;
        levelComplete = false;
        gameOverReason = null;
        totalTicks = 0;
        clearSessionScore();
    }
}
