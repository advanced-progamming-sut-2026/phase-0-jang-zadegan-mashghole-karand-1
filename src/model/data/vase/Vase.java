package model.data.vase;

import model.board.Tile;
import model.core.EventBus;
import model.core.GameState;
import model.core.Position;
import model.data.plant.PlantType;
import model.data.seed.PlantSeedDrop;
import model.data.zombie.Zombie;
import model.data.zombie.ZombieType;
import model.event.events.SeedDroppedEvent;
import model.event.events.VaseBrokenEvent;
import model.event.events.ZombieSpawnedEvent;

public class Vase {
    public final int row;
    public final int col;
    public final VaseType vaseType;
    public final VaseContentType contentType;
    public final PlantType plantType;
    public final ZombieType zombieType;

    public Vase(int row, int col, VaseType vaseType, VaseContentType contentType,
            PlantType plantType, ZombieType zombieType) {
        this.row = row;
        this.col = col;
        this.vaseType = vaseType;
        this.contentType = contentType;
        this.plantType = plantType;
        this.zombieType = zombieType;
    }

    public static Vase empty(int row, int col, VaseType vaseType) {
        return new Vase(row, col, vaseType, VaseContentType.EMPTY, null, null);
    }

    public static Vase plantSeed(int row, int col, VaseType vaseType, PlantType plantType) {
        return new Vase(row, col, vaseType, VaseContentType.PLANT_SEED, plantType, null);
    }

    public static Vase zombie(int row, int col, VaseType vaseType, ZombieType zombieType) {
        return new Vase(row, col, vaseType, VaseContentType.ZOMBIE, null, zombieType);
    }

    public void breakOpen(GameState state, EventBus eventBus) {
        state.vases.remove(this);
        Tile tile = state.getBoard().getTile(row, col);
        if (tile != null) {
            tile.removeVase();
        }
        eventBus.publish(new VaseBrokenEvent(this));

        switch (contentType) {
            case EMPTY -> {
            }
            case PLANT_SEED -> {
                if (plantType == null) {
                    break;
                }
                PlantSeedDrop drop = new PlantSeedDrop(row, col, plantType);
                state.seedDrops.add(drop);
                eventBus.publish(new SeedDroppedEvent(drop));
            }
            case ZOMBIE -> {
                ZombieType type = zombieType != null ? zombieType : ZombieType.BASIC;
                Zombie zombie = new Zombie(type, row, col, new Position(
                        col * GameState.CELL_WIDTH + GameState.CELL_WIDTH / 2f,
                        row * GameState.CELL_HEIGHT + GameState.CELL_HEIGHT / 2f), eventBus);
                state.addZombie(zombie);
                eventBus.publish(new ZombieSpawnedEvent(zombie));
            }
        }
    }
}
