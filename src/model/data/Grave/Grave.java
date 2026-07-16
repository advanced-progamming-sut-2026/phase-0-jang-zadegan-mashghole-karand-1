package model.data.Grave;

import model.core.EventBus;
import model.core.GameState;
import model.core.Position;
import model.data.sun.Sun;
import model.data.sun.SunType;
import model.events.GraveDestroyedEvent;
import model.events.SunDroppedEvent;

public class Grave {
    private int hp = 700;
    public final int row;
    public final int col;
    public final Position pos;
    public GraveContent graveContent;
    private boolean destroyed = false;

    public Grave(int row, int col, GraveContent graveContent) {
        this.row = row;
        this.col = col;
        this.graveContent = graveContent;
        pos = new Position((col + 0.5f) * GameState.CELL_WIDTH, (row + 0.5f) * GameState.CELL_HEIGHT);
    }

    public void takeDamage(int damage, GameState state, EventBus eventBus) {
        hp -= damage;
        if (hp <= 0) {
            destroy(state, eventBus);
        }
    }

    public void destroy(GameState state, EventBus eventBus) {
        destroyed = true;
        state.graves.remove(this);
        eventBus.publish(new GraveDestroyedEvent(this));
        switch (graveContent) {
            case SUN_50:
                Sun sun = new Sun(row, new Position(pos.x, pos.y),
                        50, SunType.NORMAL);
                sun.isFalling = false;
                eventBus.publish(new SunDroppedEvent(sun));
                state.sunDrops.add(sun);
                return;
            case PLANT_FOOD:
                state.plantFoodAmount++;

        }
    }
}
