package view.gdx.lawn;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;

import model.core.ReadOnlyGameState;
import model.data.Grave.Grave;
import model.data.content.chapter.ChapterType;
import pvz.libpvz.pam.ClipRef;
import pvz.libpvz.pam.PamPlayer;
import view.gdx.AssetContext;
import view.gdx.anim.AnimStateStore;
import view.gdx.anim.EntityAnimState;
import view.gdx.catalog.GraveVisualDef;

public final class GraveRenderer {
    private static final float GRAVE_HEIGHT_IN_CELLS = 1.4f;

    private final LawnLayout layout;
    private final AnimStateStore animStates;
    private final Matrix4 savedTransform = new Matrix4();
    private final Matrix4 entityTransform = new Matrix4();

    public GraveRenderer(LawnLayout layout, AnimStateStore animStates) {
        this.layout = layout;
        this.animStates = animStates;
    }

    public void render(SpriteBatch batch, AssetContext assets, ReadOnlyGameState state, ChapterType chapter) {
        if (state == null || assets.pamPlayer() == null) {
            return;
        }
        PamPlayer player = assets.pamPlayer();
        for (int row = 0; row < ReadOnlyGameState.GRID_ROWS; row++) {
            for (int col = 0; col < ReadOnlyGameState.GRID_COLS; col++) {
                Grave grave = state.getGraveAt(row, col);
                if (grave == null) {
                    continue;
                }
                GraveVisualDef visual = GraveVisualDef.forGrave(chapter, grave.graveContent);
                ClipRef clip = assets.clip(visual.pamPath, visual.clip);
                if (clip == null) {
                    continue;
                }
                float scale = (layout.cellHeight() * GRAVE_HEIGHT_IN_CELLS) / visual.pamCanvas;
                EntityAnimState anim = animStates.getOrCreate(animKey(row, col), visual.clip);
                float x = layout.cellCenterX(col);
                float y = layout.cellCenterY(row);
                beginEntityScale(batch, x, y, scale);
                try {
                    player.draw(batch, clip, anim.stateTime, x, y, true);
                } finally {
                    endEntityScale(batch);
                }
            }
        }
    }

    private void beginEntityScale(SpriteBatch batch, float x, float y, float scale) {
        savedTransform.set(batch.getTransformMatrix());
        entityTransform.set(savedTransform);
        entityTransform.translate(x, y, 0f).scale(scale, scale, 1f).translate(-x, -y, 0f);
        batch.setTransformMatrix(entityTransform);
    }

    private void endEntityScale(SpriteBatch batch) {
        batch.setTransformMatrix(savedTransform);
    }

    private static long animKey(int row, int col) {
        return (((long) 8) << 32) | ((row * ReadOnlyGameState.GRID_COLS + col) & 0xffffffffL);
    }
}
