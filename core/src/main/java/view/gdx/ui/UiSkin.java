package view.gdx.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import pvz.skin.PvzSkin;

public final class UiSkin {
    private UiSkin() {
    }

    public static Skin get() {
        return PvzSkin.get();
    }
}
