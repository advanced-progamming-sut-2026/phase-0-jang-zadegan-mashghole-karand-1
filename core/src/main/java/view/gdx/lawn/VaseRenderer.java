package view.gdx.lawn;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;

import model.core.ReadOnlyGameState;
import model.data.vase.Vase;
import model.data.vase.VaseType;
import pvz.libpvz.pam.ClipRef;
import pvz.libpvz.pam.PamPlayer;
import view.gdx.AssetContext;
import view.gdx.anim.AnimStateStore;
import view.gdx.anim.EntityAnimState;

public final class VaseRenderer {
    private static final String VASE_NORMAL_PAM = "768/FULL/VASEBREAKER/VASE_BROWN/VASE_BROWN.PAM";
    private static final String VASE_PLANT_PAM = "768/FULL/VASEBREAKER/VASE_GREEN/VASE_GREEN.PAM";
    private static final String VASE_ZOMBIE_PAM = "768/FULL/VASEBREAKER/VASE_GARGANTUAR/VASE_GARGANTUAR.PAM";
    private static final String VASE_CLIP = "idle";
    private static final float VASE_HEIGHT_IN_CELLS = 1.5f;
    private static final float PAM_CANVAS = 390f;

    private final LawnLayout layout;
    private final AnimStateStore animStates;
    private final Matrix4 savedTransform = new Matrix4();
    private final Matrix4 entityTransform = new Matrix4();

    public VaseRenderer(LawnLayout layout, AnimStateStore animStates) {
        this.layout = layout;
        this.animStates = animStates;
    }

    public void render(SpriteBatch batch, AssetContext assets, ReadOnlyGameState state) {
        if (state == null || assets.pamPlayer() == null) {
            return;
        }
        PamPlayer player = assets.pamPlayer();
        for (Vase vase : state.getVases()) {
            String pamPath = pamPath(vase.vaseType);
            ClipRef clip = assets.clip(pamPath, VASE_CLIP);
            if (clip == null) {
                continue;
            }
            float scale = (layout.cellHeight() * VASE_HEIGHT_IN_CELLS) / PAM_CANVAS;
            EntityAnimState anim = animStates.getOrCreate(animKey(vase.row, vase.col), VASE_CLIP);
            float x = layout.cellCenterX(vase.col);
            float y = layout.cellCenterY(vase.row);
            beginEntityScale(batch, x, y, scale);
            try {
                player.draw(batch, clip, anim.stateTime, x, y, true);
            } finally {
                endEntityScale(batch);
            }
        }
    }

    private static String pamPath(VaseType type) {
        if (type == null) {
            return VASE_NORMAL_PAM;
        }
        return switch (type) {
            case PLANT -> VASE_PLANT_PAM;
            case ZOMBIE -> VASE_ZOMBIE_PAM;
            case NORMAL -> VASE_NORMAL_PAM;
        };
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
        return (((long) 12) << 32) | ((row * ReadOnlyGameState.GRID_COLS + col) & 0xffffffffL);
    }
}
