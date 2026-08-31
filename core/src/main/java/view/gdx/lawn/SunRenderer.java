package view.gdx.lawn;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;

import model.core.ReadOnlyGameState;
import model.data.sun.Sun;
import model.data.sun.SunType;
import pvz.libpvz.pam.ClipRef;
import pvz.libpvz.pam.PamPlayer;
import view.gdx.AssetContext;
import view.gdx.anim.AnimStateStore;
import view.gdx.anim.EntityAnimState;
import view.gdx.catalog.SunVisualDef;

public final class SunRenderer {
    private static final float NORMAL_SIZE_CELLS = 0.55f;
    private static final float FALLING_ALPHA = 0.85f;

    private final LawnLayout layout;
    private final AnimStateStore animStates;
    private final Matrix4 savedTransform = new Matrix4();
    private final Matrix4 entityTransform = new Matrix4();

    public SunRenderer(LawnLayout layout, AnimStateStore animStates) {
        this.layout = layout;
        this.animStates = animStates;
    }

    public void render(SpriteBatch batch, AssetContext assets, ReadOnlyGameState state) {
        if (state == null || assets.pamPlayer() == null) {
            return;
        }
        PamPlayer player = assets.pamPlayer();
        for (Sun sun : new ArrayList<>(state.getSunDrops())) {
            drawSun(batch, assets, player, sun);
        }
    }

    private void drawSun(SpriteBatch batch, AssetContext assets, PamPlayer player, Sun sun) {
        SunVisualDef visual = SunVisualDef.forType(sun.type);
        EntityAnimState anim = animStates.getOrCreate(animKey(sun.id), visual.clipName);
        if (!visual.clipName.equals(anim.clipName)) {
            anim.clipName = visual.clipName;
            anim.stateTime = 0f;
        }
        ClipRef clip = assets.clip(visual.pamPath, anim.clipName);
        if (clip == null) {
            return;
        }

        float drawX = layout.worldX(sun.position);
        float drawY = layout.worldY(sun.position);
        float alpha = sun.isFalling ? FALLING_ALPHA : 1f;

        if (sun.type == SunType.RADIO_ACTIVE) {
            batch.setColor(0.65f, 0.35f, 1f, alpha);
        } else {
            batch.setColor(1f, 1f, 1f, alpha);
        }

        float targetSize = layout.cellWidth() * NORMAL_SIZE_CELLS * visual.sizeFactor;
        float scale = targetSize / visual.pamCanvas;
        beginEntityScale(batch, drawX, drawY, scale);
        try {
            player.draw(batch, clip, anim.stateTime, drawX, drawY, true);
        } finally {
            endEntityScale(batch);
            batch.setColor(Color.WHITE);
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

    private static long animKey(int sunId) {
        return (((long) 6) << 32) | (sunId & 0xffffffffL);
    }
}
