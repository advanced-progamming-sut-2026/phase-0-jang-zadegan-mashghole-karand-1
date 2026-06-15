package model.systems;

import java.util.Random;

import model.core.EventBus;
import model.core.GameLoop;
import model.core.GameState;
import model.core.Position;
import model.data.sun.Sun;
import model.data.sun.SunType;
import model.events.SunDroppedEvent;

public class SunSpawnSystem {
    private float ticksSinceLastSpawn = 0;
    private float ticksSinceGameStart = 0;
    private Random random = new Random();

    private EventBus eventBus;

    public SunSpawnSystem(EventBus event) {
        this.eventBus = event;
    }

    public void update(GameState state) {
        ticksSinceLastSpawn += 1;
        ticksSinceGameStart += 1;

        float interval = calculateSpawnInterval(ticksSinceGameStart / GameLoop.TICKS_PER_SECOND);

        if (ticksSinceLastSpawn >= interval * GameLoop.TICKS_PER_SECOND) {
            spawnRandomSun(state, eventBus);
            ticksSinceLastSpawn = 0;
        }
    }

    private float calculateSpawnInterval(float t) {
        // x = max(6 + 0.05t, 12)
        float interval = 6 + 0.05f * t;
        return Math.max(interval, 12);
    }

    private void spawnRandomSun(GameState state, EventBus bus) {
        int row = random.nextInt(GameState.GRID_ROWS);
        int col = random.nextInt(GameState.GRID_COLS);
        float x = col * GameState.CELL_WIDTH + GameState.CELL_WIDTH / 2;

        int roll = random.nextInt(100);
        SunType type;
        int amount;

        if (roll < SunType.NORMAL.spawnChance) {
            type = SunType.NORMAL;
            amount = SunType.NORMAL.amount;
        } else if (roll < SunType.NORMAL.spawnChance + SunType.SPECIAL.spawnChance) {
            type = SunType.SPECIAL;
            amount = SunType.SPECIAL.amount;
        } else {
            type = SunType.RADIO_ACTIVE;
            amount = SunType.RADIO_ACTIVE.amount;
        }

        Sun sun = new Sun(row, new Position(x, 0), amount, type);
        sun.isFalling = true;
        state.sunDrops.add(sun);

        bus.publish(new SunDroppedEvent(sun));
    }
}