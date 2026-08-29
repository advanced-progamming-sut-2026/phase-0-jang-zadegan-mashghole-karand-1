package view.gdx.ui.widgets;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Actor;

import pvz.libpvz.pam.ClipRef;
import view.gdx.AssetContext;

public final class PamPreviewActor extends Actor {
    private final AssetContext assets;
    private final String pamPath;
    private final String clipName;
    private Map<String, Boolean> visibility;
    private float stateTime;
    private float drawScale = 1f;

    public PamPreviewActor(AssetContext assets, String pamPath, String clipName, float drawScale) {
        this(assets, pamPath, clipName, drawScale, Collections.emptyMap());
    }

    public PamPreviewActor(AssetContext assets, String pamPath, String clipName, float drawScale,
        Map<String, Boolean> visibility){
            this.assets = assets;
            this.pamPath = pamPath;
            this.clipName = clipName;
            this.drawScale = drawScale;
            this.visibility = visibility != null ? visibility : Collections.emptyMap();
        }
    

    public void setDrawScale(float drawScale) {
        this.drawScale = drawScale;
    }

    @Override
    public void act(float delta) {
        stateTime += delta;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (assets == null || assets.pamPlayer() == null) {
            return;
        }
        ClipRef clip = assets.clip(pamPath, clipName);
        if (clip == null) {
            return;
        }

        com.badlogic.gdx.graphics.g2d.SpriteBatch spriteBatch =
                (com.badlogic.gdx.graphics.g2d.SpriteBatch) batch;
        Matrix4 old = spriteBatch.getTransformMatrix().cpy();
        Matrix4 scaled = old.cpy();
        float centerX = getX() + getWidth() * 0.5f;
        float centerY = getY() + getHeight() * 0.5f;
        scaled.translate(centerX, centerY, 0f);
        scaled.scale(drawScale, drawScale, 1f);

        spriteBatch.flush();
        spriteBatch.setTransformMatrix(scaled);
        if (visibility == null || visibility.isEmpty()) {
            assets.pamPlayer().draw(spriteBatch, clip, stateTime, 0f, 0f, true);
        } else {
            assets.pamPlayer().draw(spriteBatch, clip, stateTime, 0f, 0f, true, visibility);
        }
        spriteBatch.flush();
        spriteBatch.setTransformMatrix(old);
    }
}
