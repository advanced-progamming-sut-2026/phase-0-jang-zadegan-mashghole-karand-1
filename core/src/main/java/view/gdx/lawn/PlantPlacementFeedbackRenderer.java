package view.gdx.lawn;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;

import model.core.ReadOnlyGameState;
import model.data.plant.PlantType;
import model.data.zombie.ZombieType;
import pvz.libpvz.pam.ClipRef;
import pvz.libpvz.pam.PamPlayer;
import view.gdx.AssetContext;
import view.gdx.anim.AnimStateStore;
import view.gdx.anim.EntityAnimState;
import view.gdx.catalog.PlantVisualDef;
import view.gdx.catalog.VisualCatalog;
import view.gdx.catalog.ZombieVisualDef;

public final class PlantPlacementFeedbackRenderer {
    private static final float PAM_CANVAS = 390f;
    private static final float ENTITY_HEIGHT_IN_CELLS = 2f;
    private static final long PREVIEW_ANIM_KEY = Long.MIN_VALUE + 17L;
    private static final long ZOMBIE_PREVIEW_ANIM_KEY = Long.MIN_VALUE + 18L;
    private static final Color ROW_COL_TINT = new Color(1f, 1f, 1f, 0.18f);
    private static final Color CELL_TINT = new Color(1f, 1f, 1f, 0.42f);
    private static final Color PREVIEW_TINT = new Color(1f, 1f, 1f, 0.75f);
    private static final Color ZOMBIE_ROW_COL_TINT = new Color(0.95f, 0.35f, 0.55f, 0.18f);
    private static final Color ZOMBIE_CELL_TINT = new Color(0.95f, 0.35f, 0.55f, 0.42f);

    private final VisualCatalog catalog;
    private final LawnLayout layout;
    private final AnimStateStore animStates;
    private final Matrix4 savedTransform = new Matrix4();
    private final Matrix4 entityTransform = new Matrix4();

    public PlantPlacementFeedbackRenderer(VisualCatalog catalog, LawnLayout layout, AnimStateStore animStates) {
        this.catalog = catalog;
        this.layout = layout;
        this.animStates = animStates;
    }

    public void render(SpriteBatch batch, AssetContext assets, PlantType plantType, int row, int col) {
        if (batch == null || assets == null || layout == null) {
            return;
        }
        if (row < 0 || row >= ReadOnlyGameState.GRID_ROWS
                || col < 0 || col >= ReadOnlyGameState.GRID_COLS) {
            return;
        }
        drawTargetHighlight(batch, assets, row, col);
        if (plantType != null) {
            drawPlantPreview(batch, assets, plantType, row, col);
        }
    }

    public void renderPlantFoodTarget(SpriteBatch batch, AssetContext assets, int row, int col) {
        if (batch == null || assets == null || layout == null) {
            return;
        }
        if (row < 0 || row >= ReadOnlyGameState.GRID_ROWS
                || col < 0 || col >= ReadOnlyGameState.GRID_COLS) {
            return;
        }
        drawTargetHighlight(batch, assets, row, col);
    }

    public void renderZombieTarget(SpriteBatch batch, AssetContext assets, ZombieType zombieType, int row, int col) {
        if (batch == null || assets == null || layout == null) {
            return;
        }
        if (row < 0 || row >= ReadOnlyGameState.GRID_ROWS
                || col < 0 || col >= ReadOnlyGameState.GRID_COLS) {
            return;
        }
        drawZombieTargetHighlight(batch, assets, row, col);
        if (zombieType != null) {
            drawZombiePreview(batch, assets, zombieType, row, col);
        }
    }

    private void drawTargetHighlight(SpriteBatch batch, AssetContext assets, int row, int col) {
        drawTargetHighlight(batch, assets, row, col, ROW_COL_TINT, CELL_TINT);
    }

    private void drawZombieTargetHighlight(SpriteBatch batch, AssetContext assets, int row, int col) {
        drawTargetHighlight(batch, assets, row, col, ZOMBIE_ROW_COL_TINT, ZOMBIE_CELL_TINT);
    }

    private void drawTargetHighlight(SpriteBatch batch, AssetContext assets, int row, int col,
            Color rowColTint, Color cellTint) {
        TextureRegion fill = assets.region("IMAGE_UI_HUD_INGAME_PROGRESS_METER_FILL");
        if (fill == null) {
            return;
        }
        float cw = layout.cellWidth();
        float ch = layout.cellHeight();
        Color previous = batch.getColor();
        batch.setColor(rowColTint);
        for (int c = 0; c < ReadOnlyGameState.GRID_COLS; c++) {
            if (c == col) {
                continue;
            }
            batch.draw(fill, layout.cellLeft(c), layout.cellBottom(row), cw, ch);
        }
        for (int r = 0; r < ReadOnlyGameState.GRID_ROWS; r++) {
            if (r == row) {
                continue;
            }
            batch.draw(fill, layout.cellLeft(col), layout.cellBottom(r), cw, ch);
        }
        batch.setColor(cellTint);
        batch.draw(fill, layout.cellLeft(col), layout.cellBottom(row), cw, ch);
        batch.setColor(previous);
    }

    private void drawZombiePreview(SpriteBatch batch, AssetContext assets, ZombieType zombieType, int row, int col) {
        PamPlayer player = assets.pamPlayer();
        if (player == null) {
            return;
        }
        ZombieVisualDef visual = catalog.zombie(zombieType);
        if (visual == null || visual.idleClip == null) {
            return;
        }
        EntityAnimState anim = animStates.getOrCreate(ZOMBIE_PREVIEW_ANIM_KEY, visual.idleClip);
        if (!visual.idleClip.equals(anim.clipName)) {
            anim.clipName = visual.idleClip;
            anim.stateTime = 0f;
        }
        ClipRef clip = assets.clip(visual.pamPath, anim.clipName);
        if (clip == null) {
            return;
        }
        float x = layout.cellCenterX(col);
        float y = layout.cellCenterY(row);
        Color previous = batch.getColor();
        batch.setColor(PREVIEW_TINT);
        beginEntityScale(batch, x, y);
        try {
            player.draw(batch, clip, anim.stateTime, x, y, true);
        } finally {
            endEntityScale(batch);
            batch.setColor(previous);
        }
    }

    private void drawPlantPreview(SpriteBatch batch, AssetContext assets, PlantType plantType, int row, int col) {
        PamPlayer player = assets.pamPlayer();
        if (player == null) {
            return;
        }
        PlantVisualDef visual = catalog.plant(plantType);
        if (visual == null || visual.idleClip == null) {
            return;
        }
        EntityAnimState anim = animStates.getOrCreate(PREVIEW_ANIM_KEY, visual.idleClip);
        if (!visual.idleClip.equals(anim.clipName)) {
            anim.clipName = visual.idleClip;
            anim.stateTime = 0f;
        }
        ClipRef clip = assets.clip(visual.pamPath, anim.clipName);
        if (clip == null) {
            return;
        }
        float x = layout.cellCenterX(col);
        float y = layout.cellCenterY(row);
        Color previous = batch.getColor();
        batch.setColor(PREVIEW_TINT);
        beginEntityScale(batch, x, y);
        try {
            player.draw(batch, clip, anim.stateTime, x, y, true);
        } finally {
            endEntityScale(batch);
            batch.setColor(previous);
        }
    }

    private float entityScale() {
        return (layout.cellHeight() * ENTITY_HEIGHT_IN_CELLS) / PAM_CANVAS;
    }

    private void beginEntityScale(SpriteBatch batch, float x, float y) {
        float s = entityScale();
        savedTransform.set(batch.getTransformMatrix());
        entityTransform.set(savedTransform);
        entityTransform.translate(x, y, 0f).scale(s, s, 1f).translate(-x, -y, 0f);
        batch.setTransformMatrix(entityTransform);
    }

    private void endEntityScale(SpriteBatch batch) {
        batch.setTransformMatrix(savedTransform);
    }
}
