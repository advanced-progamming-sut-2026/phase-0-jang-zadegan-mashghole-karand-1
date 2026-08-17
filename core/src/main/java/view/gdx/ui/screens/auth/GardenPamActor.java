package view.gdx.ui.screens.auth;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import pvz.libpvz.pam.ClipRef;
import view.gdx.AssetContext;
import view.gdx.catalog.PlantVisualDef;

final class GardenPamActor extends Actor {
    private final AssetContext assets;
    private final String pamPath;
    private final String clipName;
    private float stateTime;
    private float scale;
    GardenPamActor(AssetContext assets, String pamPath, String clipName, float x, float y , float scale) {
        this.assets = assets;
        this.pamPath = pamPath;
        this.clipName = clipName;
        this.scale = scale;
        setPosition(x, y);
    }

    @Override
    public void act(float delta) {
        stateTime += delta;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (assets.pamPlayer() == null) return;
        ClipRef clip = assets.clip(pamPath, clipName);
        if (clip == null) return;

        com.badlogic.gdx.graphics.g2d.SpriteBatch sb =
                (com.badlogic.gdx.graphics.g2d.SpriteBatch) batch;
        com.badlogic.gdx.math.Matrix4 old = sb.getTransformMatrix().cpy();
        com.badlogic.gdx.math.Matrix4 scaled = old.cpy();
        scaled.translate(getX(), getY(), 0f);
        scaled.scale(this.scale, this.scale, 1f);

        sb.flush();
        sb.setTransformMatrix(scaled);
        assets.pamPlayer().draw(sb, clip, stateTime, 0f, 0f, true);
        sb.flush();
        sb.setTransformMatrix(old);
    }
}