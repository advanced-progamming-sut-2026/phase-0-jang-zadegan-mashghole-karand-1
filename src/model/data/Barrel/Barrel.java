package model.data.Barrel;

import model.core.EventBus;
import model.core.GameState;
import model.core.Position;
import model.core.ReadOnlyGameState;
import model.data.zombie.Zombie;
import model.data.zombie.ZombieType;
import model.events.BarrelDestroyedEvent;
import model.events.ZombieSpawnedEvent;

public class Barrel {
    private static final int MAX_HP = 500;

    private int hp = MAX_HP;
    public int row;
    public int col;
    public Position pos;
    public Zombie owner;

    public Barrel(int row, int col, Zombie owner) {
        this.row = row;
        this.col = col;
        this.owner = owner;
        this.pos = new Position((col + 0.5f) * GameState.CELL_WIDTH,
                (row + 0.5f) * GameState.CELL_HEIGHT);
    }

    public boolean ownerDead() {
        return owner == null;
    }

    public void detach() {
        owner = null;
    }

    public void syncTo(int row, int col) {
        this.row = row;
        this.col = col;
        this.pos.x = (col + 0.5f) * GameState.CELL_WIDTH;
        this.pos.y = (row + 0.5f) * GameState.CELL_HEIGHT;
    }

    public void takeDamage(int damage, GameState state, EventBus eventBus) {
        hp -= damage;
        if (hp <= 0) {
            destroy(state, eventBus);
        }
    }

    public void destroy(GameState state, EventBus eventBus) {
        state.barrels.remove(this);
        spawnImps(state, eventBus);
        eventBus.publish(new BarrelDestroyedEvent(this));
    }

    private void spawnImps(GameState state, EventBus eventBus) {
        spawnImp(state, eventBus, row, col);
        spawnImp(state, eventBus, row, Math.max(0, col - 1));
    }

    private void spawnImp(GameState state, EventBus eventBus , int row, int col) {
        Zombie imp = new Zombie(ZombieType.IMP, row, col,
                new Position((col + 0.5f) * ReadOnlyGameState.CELL_WIDTH,
                        row * ReadOnlyGameState.CELL_HEIGHT + ReadOnlyGameState.CELL_HEIGHT / 2f),
                eventBus);
        state.addZombie(imp);
        eventBus.publish(new ZombieSpawnedEvent(imp));

    }
}
