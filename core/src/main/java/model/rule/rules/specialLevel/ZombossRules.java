package model.rule.rules.specialLevel;

import model.core.EventBus;
import model.core.GameState;
import model.core.Position;
import model.core.SessionEnd;
import model.data.content.chapter.ChapterType;
import model.data.zombie.Zombie;
import model.data.zombie.ZombieType;
import model.event.events.ZombieSpawnedEvent;
import model.rule.LevelRule;
import model.rule.SessionContext;

public class ZombossRules implements LevelRule {
    private boolean bossSpawned;

    @Override
    public boolean skipsPlantSelection() {
        return true;
    }

    @Override
    public boolean shouldSpawnWaves() {
        return false;
    }

    @Override
    public boolean winsOnWaveClear() {
        return false;
    }

    @Override
    public boolean usesSunCurrency() {
        return false;
    }

    @Override
    public void onSessionStart(SessionContext context, GameState state, EventBus bus) {
        ZombieType type = bossFor(context.getConfig().levelConfig.chapterType);
        if (type == null) {
            return;
        }
        int row = 1;
        Zombie boss = new Zombie(type, row, GameState.GRID_COLS - 1,
                new Position(GameState.SCREEN_WIDTH - GameState.CELL_WIDTH * 0.4f,
                        GameState.CELL_HEIGHT * row + GameState.CELL_HEIGHT),
                bus, 0f);
        boss.syncY();
        state.addZombie(boss);
        bossSpawned = true;
        bus.publish(new ZombieSpawnedEvent(boss));
    }

    @Override
    public void postTick(SessionContext context, GameState state, EventBus bus) {
        if (!bossSpawned || state == null || bus == null || state.gameOver || state.levelComplete) {
            return;
        }
        for (Zombie z : state.zombies) {
            if (z != null && z.isAlive && z.hp > 0 && z.type != null && z.type.isZomboss()) {
                return;
            }
        }
        SessionEnd.win(state, bus);
    }

    public static ZombieType bossFor(ChapterType chapter) {
        if (chapter == null) {
            return null;
        }
        return switch (chapter) {
            case ANCIENT_EGYPT -> ZombieType.ZOMBOT_SPHINX;
            case DARK_AGES -> ZombieType.ZOMBOT_DRAGON;
            case BIG_WAVE_BEACH -> ZombieType.ZOMBOT_SHARK;
            case FROSTBITE_CAVES -> ZombieType.ZOMBOT_MAMMOTH;
        };
    }
}
