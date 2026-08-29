package view.gdx.ui.widgets;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Actor;

import pvz.libpvz.pam.ClipRef;
import view.gdx.AssetContext;

public final class PamPreviewActor extends Actor {
    private final AssetContext assets;
    private final String pamPath;
    private final String clipName;
    private float stateTime;
    private float drawScale = 1f;

    public PamPreviewActor(AssetContext assets, String pamPath, String clipName, float drawScale) {
        this.assets = assets;
        this.pamPath = pamPath;
        this.clipName = clipName;
        this.drawScale = drawScale;
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
        assets.pamPlayer().draw(spriteBatch, clip, stateTime, 0f, 0f, true);
        spriteBatch.flush();
        spriteBatch.setTransformMatrix(old);
    }
}
