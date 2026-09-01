package view.gdx.lawn;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;

import model.board.IceDirection;
import model.board.Tile;
import model.core.ReadOnlyGameState;
import model.data.content.chapter.ChapterType;
import pvz.libpvz.pam.ClipRef;
import pvz.libpvz.pam.PamPlayer;
import view.gdx.AssetContext;
import view.gdx.anim.AnimStateStore;
import view.gdx.anim.EntityAnimState;

public final class ChapterTerrainRenderer {
    private static final int MAX_TIDE_LEVEL = 5;
    private static final float TILE_HEIGHT_IN_CELLS = 1.05f;
    private static final float OVERLAY_HEIGHT_IN_CELLS = 1.4f;
    private static final float PAM_CANVAS = 390f;

    private static final String ICE_UP_PAM =
            "768/FULL/EFFECTS/TILESLIDER_ICEAGE_UP/TILESLIDER_ICEAGE_UP.PAM";
    private static final String ICE_DOWN_PAM =
            "768/FULL/EFFECTS/TILESLIDER_ICEAGE_DOWN/TILESLIDER_ICEAGE_DOWN.PAM";
    private static final String WATER_SQUARE_PAM =
            "768/FULL/BACKGROUNDS/WATER_SQUARE/WATER_SQUARE.PAM";
    private static final String WATER_TIDE_LINE_PAM =
            "768/FULL/BACKGROUNDS/WATER_TIDE_LINE/WATER_TIDE_LINE.PAM";
    private static final String WATER_FOAM_PAM =
            "768/FULL/EFFECTS/WATER_FOAM/WATER_FOAM.PAM";
    private static final String BEACH_POST_PAM =
            "768/FULL/EFFECTS/SURF_BOARD/SURF_BOARD.PAM";
    private static final String NECRO_BOTTOM_PAM =
            "768/FULL/BACKGROUNDS/BACKGROUND_DARK_BRAZIER_BOTTOM/BACKGROUND_DARK_BRAZIER_BOTTOM.PAM";
    private static final String NECRO_TOP_PAM =
            "768/FULL/BACKGROUNDS/BACKGROUND_DARK_BRAZIER_TOP/BACKGROUND_DARK_BRAZIER_TOP.PAM";

    private final LawnLayout layout;
    private final AnimStateStore animStates;
    private final Matrix4 savedTransform = new Matrix4();
    private final Matrix4 entityTransform = new Matrix4();

    public ChapterTerrainRenderer(LawnLayout layout, AnimStateStore animStates) {
        this.layout = layout;
        this.animStates = animStates;
    }

    public void render(SpriteBatch batch, AssetContext assets, ReadOnlyGameState state, ChapterType chapter) {
        if (state == null || assets.pamPlayer() == null) {
            return;
        }
        PamPlayer player = assets.pamPlayer();
        drawIceTiles(batch, assets, player, state);
        if (chapter == ChapterType.BIG_WAVE_BEACH || hasBeachPosts(state) || hasWater(state)) {
            drawWaterAndTide(batch, assets, player, state);
        }
        drawBeachPosts(batch, assets, player, state);
        drawNecromancyTiles(batch, assets, player, state);
    }

    private void drawIceTiles(SpriteBatch batch, AssetContext assets, PamPlayer player,
            ReadOnlyGameState state) {
        for (int r = 0; r < ReadOnlyGameState.GRID_ROWS; r++) {
            for (int c = 0; c < ReadOnlyGameState.GRID_COLS; c++) {
                Tile tile = state.getBoard().getTile(r, c);
                if (tile == null || !tile.isIce()) {
                    continue;
                }
                IceDirection dir = tile.getDirection();
                String pam = dir == IceDirection.DOWN ? ICE_DOWN_PAM : ICE_UP_PAM;
                ClipRef clip = assets.clip(pam, "idle");
                if (clip == null) {
                    continue;
                }
                EntityAnimState anim = animStates.getOrCreate(animKey(10, r, c), "idle");
                drawCentered(batch, player, clip, anim.stateTime, r, c, TILE_HEIGHT_IN_CELLS, true);
            }
        }
    }

    private void drawWaterAndTide(SpriteBatch batch, AssetContext assets, PamPlayer player,
            ReadOnlyGameState state) {
        ClipRef waterClip = assets.clip(WATER_SQUARE_PAM, "Water");
        ClipRef foamClip = assets.clip(WATER_FOAM_PAM, "water_foam_left");
        ClipRef tideClip = assets.clip(WATER_TIDE_LINE_PAM, "idle");
        TextureRegion fill = assets.region("IMAGE_UI_HUD_INGAME_PROGRESS_METER_FILL");

        int floodMinCol = ReadOnlyGameState.GRID_COLS - MAX_TIDE_LEVEL;
        boolean anyBeach = drawDryBeachCells(batch, state, fill, floodMinCol);
        boolean anyWater = drawWaterRows(batch, player, state, waterClip, foamClip, tideClip, fill);

        if (anyWater || anyBeach || hasBeachPosts(state)) {
            drawMaxTideLine(batch, assets, fill);
        }
    }

    private boolean drawDryBeachCells(SpriteBatch batch, ReadOnlyGameState state,
            TextureRegion fill, int floodMinCol) {
        if (fill == null) {
            return false;
        }
        boolean anyBeach = false;
        for (int r = 0; r < ReadOnlyGameState.GRID_ROWS; r++) {
            for (int c = Math.max(0, floodMinCol); c < ReadOnlyGameState.GRID_COLS; c++) {
                Tile tile = state.getBoard().getTile(r, c);
                if (tile == null || tile.isWater()) {
                    continue;
                }
                anyBeach = true;
                batch.setColor(0.92f, 0.82f, 0.55f, 0.28f);
                batch.draw(fill, layout.cellLeft(c), layout.cellBottom(r),
                        layout.cellWidth(), layout.cellHeight());
                batch.setColor(Color.WHITE);
            }
        }
        return anyBeach;
    }

    private boolean drawWaterRows(SpriteBatch batch, PamPlayer player, ReadOnlyGameState state,
            ClipRef waterClip, ClipRef foamClip, ClipRef tideClip, TextureRegion fill) {
        boolean anyWater = false;
        for (int r = 0; r < ReadOnlyGameState.GRID_ROWS; r++) {
            int rowLeftmost = -1;
            for (int c = 0; c < ReadOnlyGameState.GRID_COLS; c++) {
                Tile tile = state.getBoard().getTile(r, c);
                if (tile == null || !tile.isWater()) {
                    continue;
                }
                anyWater = true;
                if (rowLeftmost < 0) {
                    rowLeftmost = c;
                }
                drawWaterCell(batch, player, state, waterClip, fill, r, c);
            }
            drawWaterRowEdge(batch, player, r, rowLeftmost, foamClip, tideClip);
        }
        return anyWater;
    }

    private void drawWaterCell(SpriteBatch batch, PamPlayer player, ReadOnlyGameState state,
            ClipRef waterClip, TextureRegion fill, int row, int col) {
        if (waterClip != null) {
            EntityAnimState anim = animStates.getOrCreate(animKey(11, row, col), "Water");
            drawCentered(batch, player, waterClip, anim.stateTime, row, col, TILE_HEIGHT_IN_CELLS, true);
        } else if (fill != null) {
            batch.setColor(0.15f, 0.45f, 0.85f, 0.55f);
            batch.draw(fill, layout.cellLeft(col), layout.cellBottom(row),
                    layout.cellWidth(), layout.cellHeight());
            batch.setColor(Color.WHITE);
        }
    }

    private void drawWaterRowEdge(SpriteBatch batch, PamPlayer player, int row, int rowLeftmost,
            ClipRef foamClip, ClipRef tideClip) {
        if (rowLeftmost < 0) {
            return;
        }
        if (foamClip != null) {
            EntityAnimState foamAnim = animStates.getOrCreate(animKey(12, row, rowLeftmost),
                    "water_foam_left");
            drawCentered(batch, player, foamClip, foamAnim.stateTime, row, rowLeftmost,
                    TILE_HEIGHT_IN_CELLS, true);
        }
        if (tideClip != null) {
            EntityAnimState tideAnim = animStates.getOrCreate(animKey(13, row, rowLeftmost), "idle");
            drawCentered(batch, player, tideClip, tideAnim.stateTime, row, rowLeftmost,
                    TILE_HEIGHT_IN_CELLS, true);
        }
    }


    private void drawMaxTideLine(SpriteBatch batch, AssetContext assets, TextureRegion fill) {
        int maxCol = ReadOnlyGameState.GRID_COLS - MAX_TIDE_LEVEL;
        if (maxCol < 0) {
            return;
        }
        ClipRef tideClip = assets.clip(WATER_TIDE_LINE_PAM, "idle");
        float lineX = layout.cellLeft(maxCol);
        float bottom = layout.cellBottom(ReadOnlyGameState.GRID_ROWS - 1);
        float top = layout.cellBottom(0) + layout.cellHeight();
        float height = top - bottom;

        if (tideClip != null) {
            PamPlayer player = assets.pamPlayer();
            for (int r = 0; r < ReadOnlyGameState.GRID_ROWS; r++) {
                EntityAnimState anim = animStates.getOrCreate(animKey(14, r, maxCol), "max_tide");
                if (!"idle".equals(anim.clipName)) {
                    anim.clipName = "idle";
                    anim.stateTime = 0f;
                }
                float x = lineX;
                float y = layout.cellCenterY(r);
                float scale = (layout.cellHeight() * TILE_HEIGHT_IN_CELLS) / PAM_CANVAS;
                beginScale(batch, x, y, scale);
                try {
                    player.draw(batch, tideClip, anim.stateTime, x, y, true);
                } finally {
                    endScale(batch);
                }
            }
        } else if (fill != null) {
            batch.setColor(0.95f, 0.85f, 0.2f, 0.7f);
            batch.draw(fill, lineX - 2f, bottom, 4f, height);
            batch.setColor(Color.WHITE);
        }
    }

    private void drawBeachPosts(SpriteBatch batch, AssetContext assets, PamPlayer player,
            ReadOnlyGameState state) {
        ClipRef postClip = assets.clip(BEACH_POST_PAM, "undamaged");
        TextureRegion fill = assets.region("IMAGE_UI_HUD_INGAME_PROGRESS_METER_FILL");
        for (int r = 0; r < ReadOnlyGameState.GRID_ROWS; r++) {
            for (int c = 0; c < ReadOnlyGameState.GRID_COLS; c++) {
                Tile tile = state.getBoard().getTile(r, c);
                if (tile == null || !tile.hasBeachPost()) {
                    continue;
                }
                if (postClip != null) {
                    EntityAnimState anim = animStates.getOrCreate(animKey(15, r, c), "undamaged");
                    drawCentered(batch, player, postClip, anim.stateTime, r, c, OVERLAY_HEIGHT_IN_CELLS, true);
                } else if (fill != null && !tile.isWater()) {
                    // Low-tide / beach cell marker when PAM missing
                    batch.setColor(0.9f, 0.75f, 0.35f, 0.4f);
                    batch.draw(fill, layout.cellLeft(c), layout.cellBottom(r),
                            layout.cellWidth(), layout.cellHeight());
                    batch.setColor(Color.WHITE);
                }
            }
        }
    }

    private void drawNecromancyTiles(SpriteBatch batch, AssetContext assets, PamPlayer player,
            ReadOnlyGameState state) {
        ClipRef bottom = assets.clip(NECRO_BOTTOM_PAM, "animation");
        ClipRef top = assets.clip(NECRO_TOP_PAM, "animation");
        TextureRegion fill = assets.region("IMAGE_UI_HUD_INGAME_PROGRESS_METER_FILL");
        for (int r = 0; r < ReadOnlyGameState.GRID_ROWS; r++) {
            for (int c = 0; c < ReadOnlyGameState.GRID_COLS; c++) {
                Tile tile = state.getBoard().getTile(r, c);
                if (tile == null || !tile.isNecromancy()) {
                    continue;
                }
                if (bottom != null) {
                    EntityAnimState animB = animStates.getOrCreate(animKey(16, r, c), "animation");
                    drawCentered(batch, player, bottom, animB.stateTime, r, c, OVERLAY_HEIGHT_IN_CELLS, true);
                }
                if (top != null) {
                    EntityAnimState animT = animStates.getOrCreate(animKey(17, r, c), "animation");
                    drawCentered(batch, player, top, animT.stateTime, r, c, OVERLAY_HEIGHT_IN_CELLS, true);
                }
                if (bottom == null && top == null && fill != null) {
                    batch.setColor(0.45f, 0.15f, 0.55f, 0.4f);
                    batch.draw(fill, layout.cellLeft(c), layout.cellBottom(r),
                            layout.cellWidth(), layout.cellHeight());
                    batch.setColor(Color.WHITE);
                }
            }
        }
    }

    private static boolean hasWater(ReadOnlyGameState state) {
        for (int r = 0; r < ReadOnlyGameState.GRID_ROWS; r++) {
            for (int c = 0; c < ReadOnlyGameState.GRID_COLS; c++) {
                Tile tile = state.getBoard().getTile(r, c);
                if (tile != null && tile.isWater()) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean hasBeachPosts(ReadOnlyGameState state) {
        for (int r = 0; r < ReadOnlyGameState.GRID_ROWS; r++) {
            for (int c = 0; c < ReadOnlyGameState.GRID_COLS; c++) {
                Tile tile = state.getBoard().getTile(r, c);
                if (tile != null && tile.hasBeachPost()) {
                    return true;
                }
            }
        }
        return false;
    }

    private void drawCentered(SpriteBatch batch, PamPlayer player, ClipRef clip, float time,
            int row, int col, float heightInCells, boolean loop) {
        float x = layout.cellCenterX(col);
        float y = layout.cellCenterY(row);
        float scale = (layout.cellHeight() * heightInCells) / PAM_CANVAS;
        beginScale(batch, x, y, scale);
        try {
            player.draw(batch, clip, time, x, y, loop);
        } finally {
            endScale(batch);
        }
    }

    private void beginScale(SpriteBatch batch, float x, float y, float scale) {
        savedTransform.set(batch.getTransformMatrix());
        entityTransform.set(savedTransform);
        entityTransform.translate(x, y, 0f).scale(scale, scale, 1f).translate(-x, -y, 0f);
        batch.setTransformMatrix(entityTransform);
    }

    private void endScale(SpriteBatch batch) {
        batch.setTransformMatrix(savedTransform);
    }

    private static long animKey(int kind, int row, int col) {
        return (((long) kind) << 32) | ((row * 16 + col) & 0xffffffffL);
    }
}
