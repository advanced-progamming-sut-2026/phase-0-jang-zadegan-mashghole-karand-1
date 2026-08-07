package view.gdx.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import pvz.skin.PvzSkin;

public final class HudStage implements Disposable {
    private final Stage stage;
    private final Skin skin;
    private final Label statusLabel;
    private final String skinStatus;

    public HudStage(String assetStatus) {
        Stage builtStage = null;
        Skin builtSkin = null;
        Label label = null;
        String status = "ok";
        try {
            builtSkin = PvzSkin.get();
            builtStage = new Stage(new ScreenViewport());
            Table root = new Table();
            root.setFillParent(true);
            root.top().left().pad(12f);

            Label title = new Label("PVZ2 graphics skeleton", builtSkin, "big");
            label = new Label(assetStatus, builtSkin, "default");
            TextButton pause = new TextButton("Pause", builtSkin, "default");

            root.add(title).left().row();
            root.add(label).left().padTop(6f).row();
            root.add(pause).left().padTop(10f);
            builtStage.addActor(root);
            Gdx.input.setInputProcessor(builtStage);
        } catch (RuntimeException e) {
            status = "pvz-skin failed: " + e.getMessage();
            Gdx.app.error("HudStage", status, e);
        }
        this.skin = builtSkin;
        this.stage = builtStage;
        this.statusLabel = label;
        this.skinStatus = status;
    }

    public void setStatusText(String text) {
        if (statusLabel != null) {
            statusLabel.setText(text);
        }
    }

    public void act(float delta) {
        if (stage != null) {
            stage.act(delta);
        }
    }

    public void draw() {
        if (stage != null) {
            stage.getViewport().apply();
            stage.draw();
        }
    }

    public void resize(int width, int height) {
        if (stage != null) {
            stage.getViewport().update(width, height, true);
        }
    }

    public String skinStatus() {
        return skinStatus;
    }

    @Override
    public void dispose() {
        if (stage != null) {
            stage.dispose();
        }
        if (skin != null) {
            skin.dispose();
        }
    }
}
