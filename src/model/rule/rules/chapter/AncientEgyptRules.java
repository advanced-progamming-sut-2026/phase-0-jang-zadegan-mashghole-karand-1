package model.rule.rules.chapter;

import java.util.Random;

import model.board.Tile;
import model.core.EventBus;
import model.core.GameState;
import model.data.Grave.Grave;
import model.data.Grave.GraveContent;
import model.data.zombie.Zombie;
import model.rule.LevelRule;
import model.rule.SessionContext;

public class AncientEgyptRules implements LevelRule {
    private static final Random RANDOM = new Random();
    private static final int MIN_COL = 3;
    private static final double SANDSTORM_CHANCE = 0.20;
    private static final int MIN_TILES_ADVANCE = 1;
    private static final int MAX_TILES_ADVANCE = 4;

    @Override
    public void onSessionStart(SessionContext context, GameState state, EventBus bus) {
        int placed = 0;
        int attempts = 0;
        int target = 5;

        while (placed < target && attempts < 200) {
            attempts++;
            int row = RANDOM.nextInt(GameState.GRID_ROWS);
            int col = MIN_COL + RANDOM.nextInt(GameState.GRID_COLS - MIN_COL);

            Tile tile = state.getBoard().getTile(row, col);
            if (tile.canSetGrave()) {
                tile.setGrave(new Grave(row, col, GraveContent.NONE));
                placed++;
            }
        }
    }

    @Override
    public void onZombieSpawned(Zombie zombie, SessionContext context, GameState state) {
        if (context.getConfig().levelConfig.totalWaves == state.currentWave) {
            if (RANDOM.nextDouble() < SANDSTORM_CHANCE) {
                int tilesToAdvance = MIN_TILES_ADVANCE + RANDOM.nextInt(MAX_TILES_ADVANCE - MIN_TILES_ADVANCE + 1);
                float advanceLength = tilesToAdvance * GameState.CELL_WIDTH;

                float targetX = zombie.position.x - advanceLength;

                zombie.setSandstorm(targetX);
            }
        }
    }
}
