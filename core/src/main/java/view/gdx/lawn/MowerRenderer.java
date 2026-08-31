package view.gdx.lawn;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;

import model.core.ReadOnlyGameState;
import model.data.content.chapter.ChapterType;
import model.lawnmower.LawnMower;
import pvz.libpvz.pam.ClipRef;
import pvz.libpvz.pam.PamPlayer;
import view.gdx.AssetContext;
import view.gdx.anim.AnimStateStore;
import view.gdx.anim.EntityAnimState;
import view.gdx.catalog.MowerVisualDef;

public final class MowerRenderer {
    private static final float MOWER_HEIGHT_IN_CELLS = 1.6f;
    private static final float MOWER_LEFT_OFFSET_CELLS = 0.45f;

    private final LawnLayout layout;
    private final AnimStateStore animStates;
    private final Matrix4 savedTransform = new Matrix4();
    private final Matrix4 entityTransform = new Matrix4();

    public MowerRenderer(LawnLayout layout, AnimStateStore animStates) {
        this.layout = layout;
        this.animStates = animStates;
    }

    public void render(SpriteBatch batch, AssetContext assets, ReadOnlyGameState state, ChapterType chapter) {
        if (state == null || assets.pamPlayer() == null || state.isBrainsMode()) {
            return;
        }
        MowerVisualDef visual = MowerVisualDef.forChapter(chapter);
        PamPlayer player = assets.pamPlayer();
        ClipRef clip = assets.clip(visual.pamPath, visual.idleClip);
        if (clip == null) {
            return;
        }

        float scale = (layout.cellHeight() * MOWER_HEIGHT_IN_CELLS) / visual.pamCanvas;
        for (int row = 0; row < ReadOnlyGameState.GRID_ROWS; row++) {
            LawnMower mower = state.getBoard().getLawnMowers(row);
            if (mower == null || !mower.isActive()) {
                continue;
            }
            EntityAnimState anim = animStates.getOrCreate(animKey(row), visual.idleClip);
            float x = layout.cellLeft(0) - layout.cellWidth() * MOWER_LEFT_OFFSET_CELLS;
            float y = layout.cellCenterY(row);
            beginEntityScale(batch, x, y, scale);
            try {
                player.draw(batch, clip, anim.stateTime, x, y, true);
            } finally {
                endEntityScale(batch);
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

    private static long animKey(int row) {
        return (((long) 7) << 32) | (row & 0xffffffffL);
    }
}
