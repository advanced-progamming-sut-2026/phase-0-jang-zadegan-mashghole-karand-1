package model.rule.rules.chapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import model.board.GameBoard;
import model.board.Tile;
import model.board.TileType;
import model.core.EventBus;
import model.core.GameState;
import model.core.ReadOnlyGameState;
import model.rule.LevelRule;
import model.rule.SessionContext;

public class BigWaveBeachRules implements LevelRule {
    private static final Random RANDOM = new Random();
    private static final int MIN_COL = 5;
    private static final int MIN_BEACH_POSTS = 1;
    private static final int MAX_BEACH_POSTS = 3;
    private static final int MIN_TIDE_LEVEL = 1;
    private static final int MAX_TIDE_LEVEL = 5;

    @Override
    public void onSessionStart(SessionContext context, GameState state, EventBus bus) {
        placeBeachPosts(state);
    }

    private void placeBeachPosts(GameState state) {
        int postCount = MIN_BEACH_POSTS + RANDOM.nextInt(MAX_BEACH_POSTS - MIN_BEACH_POSTS + 1);
        int placed = 0;
        int attempts = 0;

        while (placed < postCount && attempts < 200) {
            attempts++;
            int row = RANDOM.nextInt(GameState.GRID_ROWS);
            int col = MIN_COL + RANDOM.nextInt(GameState.GRID_COLS - MIN_COL);

            Tile tile = state.getBoard().getTile(row, col);
            if (!tile.hasBeachPost()) {
                tile.setBeachPost(true);
                placed++;
            }
        }
    }

    @Override
    public void onWaveStart(SessionContext context, GameState state, EventBus bus) {
        int tideLevel = MIN_TIDE_LEVEL + RANDOM.nextInt(MAX_TIDE_LEVEL - MIN_TIDE_LEVEL + 1);

        for (int row = 0; row < GameState.GRID_ROWS; row++) {
            for (int col = 0; col < GameState.GRID_COLS; col++) {
                Tile tile = state.getBoard().getTile(row, col);
                tile.setType(TileType.NORMAL);
            }
        }

        for (int row = 0; row < GameState.GRID_ROWS; row++) {
            for (int col = GameState.GRID_COLS - 1; col >= GameState.GRID_COLS - tideLevel; col--) {
                Tile tile = state.getBoard().getTile(row, col);
                tile.setType(TileType.WATER);
            }
        }
        spawnPostBeachZombies(context, state, bus);
    }

    private void spawnPostBeachZombies(SessionContext context, GameState state, EventBus bus) {
        GameBoard board = state.getBoard();
        for(int i = 0; i< ReadOnlyGameState.GRID_ROWS * ReadOnlyGameState.GRID_COLS; i++ ){
            Tile tile = board.getTile(i/ReadOnlyGameState.GRID_COLS, i%ReadOnlyGameState.GRID_COLS);
            if(tile.isWater() && tile.hasBeachPost() && RANDOM.nextBoolean()){
                context.getWaveManager().spawnPostBeachZombies(state,bus,tile);
            }
        }

    }
}