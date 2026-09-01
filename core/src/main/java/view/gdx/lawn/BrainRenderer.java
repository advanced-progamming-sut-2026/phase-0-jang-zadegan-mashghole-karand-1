package view.gdx.lawn;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import model.core.ReadOnlyGameState;
import model.data.brain.Brain;
import view.gdx.AssetContext;

public final class BrainRenderer {
    public static final String BRAIN_IMAGE = "IMAGE_UI_CALENDAR_TIMER_DECO_BIGBRAINZ";
    private static final float BRAIN_HEIGHT_IN_CELLS = 0.35f;
    private static final float BRAIN_LEFT_OFFSET_CELLS = 0.45f;

    private final LawnLayout layout;

    public BrainRenderer(LawnLayout layout) {
        this.layout = layout;
    }

    public void render(SpriteBatch batch, AssetContext assets, ReadOnlyGameState state) {
        if (state == null || !state.isBrainsMode() || assets == null) {
            return;
        }
        TextureRegion brain = assets.region(BRAIN_IMAGE);
        if (brain == null || brain.getRegionHeight() <= 0) {
            return;
        }
        float h = layout.cellHeight() * BRAIN_HEIGHT_IN_CELLS;
        float w = h * (brain.getRegionWidth() / (float) brain.getRegionHeight());
        for (Brain brainState : state.getBrains()) {
            if (brainState.isCollected()) {
                continue;
            }
            float x = layout.cellLeft(0) - layout.cellWidth() * BRAIN_LEFT_OFFSET_CELLS;
            float y = layout.cellCenterY(brainState.row);
            Color previous = batch.getColor();
            batch.setColor(Color.WHITE);
            batch.draw(brain, x - w * 0.5f, y - h * 0.5f, w, h);
            batch.setColor(previous);
        }
    }
}
