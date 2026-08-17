package view.gdx.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;

import view.ScreenType;
import view.gdx.AssetContext;

public final class MenuBackdrop {
    public static final String MAIN_MENU_BACKGROUND = "IMAGE_MAINMENU_BACKGROUND";
    public static final String GARDEN_BACKGROUND = "IMAGE_BACKGROUNDS_ZEN_GARDEN";

    private final Matrix4 screenProjection = new Matrix4();
    private TextureRegion background;

    public void bind(AssetContext assets, ScreenType screenType) {
        if (assets == null) {
            background = null;
            return;
        }
        if (screenType == ScreenType.MAIN || screenType == ScreenType.LOGIN || screenType == ScreenType.LEVEL_SELECTOR) {

            background = assets.region(MAIN_MENU_BACKGROUND);
        } else if (screenType == ScreenType.GREEN_HOUSE) {
            background = assets.region(GARDEN_BACKGROUND);
        }
    }

    public void render(SpriteBatch batch, int screenWidth, int screenHeight) {
        if (batch == null || background == null || screenWidth <= 0 || screenHeight <= 0) {
            return;
        }
        screenProjection.setToOrtho2D(0, 0, screenWidth, screenHeight);
        batch.setProjectionMatrix(screenProjection);
        batch.begin();
        float regionW = background.getRegionWidth();
        float regionH = background.getRegionHeight();
        float scale = Math.max(screenWidth / regionW, screenHeight / regionH);
        float drawW = regionW * scale;
        float drawH = regionH * scale;
        float x = (screenWidth - drawW) * 0.5f;
        float y = (screenHeight - drawH) * 0.5f;
        batch.draw(background, x, y, drawW, drawH);
        batch.end();
    }

    public boolean ready() {
        return background != null;
    }
}
