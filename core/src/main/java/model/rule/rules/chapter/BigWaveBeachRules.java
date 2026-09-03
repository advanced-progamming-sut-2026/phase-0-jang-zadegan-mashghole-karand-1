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
    private static final int BEACH_SPAWN_DELAY_TICKS = 20;

    private int pendingBeachSpawnTicks = -1;
    private SessionContext pendingSpawnContext;
    private GameState pendingSpawnState;
    private EventBus pendingSpawnBus;
    private final List<Tile> pendingBeachTiles = new ArrayList<>();

    @Override
    public void onSessionStart(SessionContext context, GameState state, EventBus bus) {
        pendingBeachSpawnTicks = -1;
        pendingBeachTiles.clear();
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
    public void preTick(SessionContext context, GameState state, EventBus bus) {
        if (pendingBeachSpawnTicks <= 0) {
            return;
        }
        pendingBeachSpawnTicks--;
        if (pendingBeachSpawnTicks == 0) {
            spawnPendingBeachZombies();
            pendingBeachSpawnTicks = -1;
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

        collectPendingBeachSpawns(context, state, bus);
        if (!pendingBeachTiles.isEmpty()) {
            pendingBeachSpawnTicks = BEACH_SPAWN_DELAY_TICKS;
        }
    }

    public boolean hasPendingBeachSpawn() {
        return pendingBeachSpawnTicks > 0;
    }

    private void collectPendingBeachSpawns(SessionContext context, GameState state, EventBus bus) {
        pendingBeachTiles.clear();
        pendingSpawnContext = context;
        pendingSpawnState = state;
        pendingSpawnBus = bus;

        GameBoard board = state.getBoard();
        for (int i = 0; i < ReadOnlyGameState.GRID_ROWS * ReadOnlyGameState.GRID_COLS; i++) {
            Tile tile = board.getTile(i / ReadOnlyGameState.GRID_COLS, i % ReadOnlyGameState.GRID_COLS);
            if (tile.isWater() && tile.hasBeachPost() && RANDOM.nextBoolean()) {
                pendingBeachTiles.add(tile);
            }
        }
    }

    private void spawnPendingBeachZombies() {
        if (pendingSpawnContext == null || pendingSpawnState == null || pendingSpawnBus == null) {
            return;
        }
        for (Tile tile : pendingBeachTiles) {
            pendingSpawnContext.getWaveManager().spawnPostBeachZombies(pendingSpawnState, pendingSpawnBus, tile);
        }
        pendingBeachTiles.clear();
        pendingSpawnContext = null;
        pendingSpawnState = null;
        pendingSpawnBus = null;
    }
}
