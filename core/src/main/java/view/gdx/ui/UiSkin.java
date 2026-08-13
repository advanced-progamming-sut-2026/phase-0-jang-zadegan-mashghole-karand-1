package view.gdx.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import pvz.skin.PvzSkin;

/** Shared pvz-skin access for all Scene2D screens. */
public final class UiSkin {
    private UiSkin() {
    }

    public static Skin get() {
        return PvzSkin.get();
    }
}
