package view.gdx.lawn;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Disposable;

import model.core.ReadOnlyGameState;
import view.gdx.ui.UiSkin;

public final class LawnGridDebugOverlay implements Disposable {
    public static boolean ENABLED = false;

    private static final Color GRID = new Color(0.2f, 1f, 0.35f, 0.85f);
    private static final Color CENTER_DOT = new Color(1f, 0.85f, 0.2f, 1f);
    private static final Color STRIP = new Color(0.3f, 0.6f, 1f, 0.55f);
    private static final Color LABEL = Color.WHITE;

    private final ShapeRenderer shapes = new ShapeRenderer();
    private boolean visible = true;
    private BitmapFont font;
    private boolean loggedLayout;

    public void render(OrthographicCamera camera, SpriteBatch batch,
            LawnLayout layout, LawnBackgroundRenderer background) {
        if (!ENABLED || camera == null || layout == null) {
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.G)) {
            visible = !visible;
            Gdx.app.log("LawnGridDebug", "overlay " + (visible ? "on" : "off") + " (G)");
        }
        if (!visible) {
            return;
        }

        if (!loggedLayout) {
            loggedLayout = true;
            Gdx.app.log("LawnGridDebug", String.format(
                    "origin=(%.1f,%.1f) scale=(%.3f,%.3f) cell=(%.1f x %.1f) atlasInsets LRTB=(%.0f,%.0f,%.0f,%.0f)",
                    layout.originX, layout.originY, layout.scaleX, layout.scaleY,
                    layout.cellWidth(), layout.cellHeight(),
                    layout.atlasInsetLeft, layout.atlasInsetRight,
                    layout.atlasInsetTop, layout.atlasInsetBottom));
            if (background != null && background.ready()) {
                Gdx.app.log("LawnGridDebug", String.format(
                        "bg leftW=%.1f centerW=%.1f rightW=%.1f stripH=%.1f",
                        background.leftW(), background.centerW(), background.rightW(), background.stripH()));
            }
        }

        shapes.setProjectionMatrix(camera.combined);
        shapes.begin(ShapeRenderer.ShapeType.Line);

        if (background != null && background.ready()) {
            shapes.setColor(STRIP);
            float x = background.drawX();
            float y = background.drawY();
            shapes.rect(x, y, background.leftW(), background.stripH());
            x += background.leftW();
            shapes.rect(x, y, background.centerW(), background.stripH());
            x += background.centerW();
            shapes.rect(x, y, background.rightW(), background.stripH());
        }

        float cw = layout.cellWidth();
        float ch = layout.cellHeight();
        shapes.setColor(GRID);
        for (int row = 0; row < ReadOnlyGameState.GRID_ROWS; row++) {
            for (int col = 0; col < ReadOnlyGameState.GRID_COLS; col++) {
                float left = layout.cellLeft(col);
                float bottom = layout.cellBottom(row);
                shapes.rect(left, bottom, cw, ch);
            }
        }

        shapes.setColor(CENTER_DOT);
        for (int row = 0; row < ReadOnlyGameState.GRID_ROWS; row++) {
            for (int col = 0; col < ReadOnlyGameState.GRID_COLS; col++) {
                float cx = layout.cellCenterX(col);
                float cy = layout.cellCenterY(row);
                shapes.line(cx - 4f, cy, cx + 4f, cy);
                shapes.line(cx, cy - 4f, cx, cy + 4f);
            }
        }
        shapes.end();

        ensureFont();
        if (font == null || batch == null) {
            return;
        }
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        font.setColor(LABEL);
        for (int row = 0; row < ReadOnlyGameState.GRID_ROWS; row++) {
            for (int col = 0; col < ReadOnlyGameState.GRID_COLS; col++) {
                String text = row + "," + col;
                float cx = layout.cellCenterX(col);
                float cy = layout.cellCenterY(row);
                font.draw(batch, text, cx - 10f, cy + 6f);
            }
        }
        font.draw(batch, "GRID DEBUG (G toggles)", layout.originX + 4f,
                layout.originY + ReadOnlyGameState.SCREEN_HEIGHT * layout.scaleY - 6f);
        batch.end();
    }

    private void ensureFont() {
        if (font != null) {
            return;
        }
        Skin skin = UiSkin.get();
        if (skin != null && skin.has("default", Label.LabelStyle.class)) {
            Label.LabelStyle style = skin.get("default", Label.LabelStyle.class);
            if (style != null && style.font != null) {
                font = style.font;
                font.setUseIntegerPositions(true);
                return;
            }
        }
        font = new BitmapFont();
        font.setUseIntegerPositions(true);
    }

    @Override
    public void dispose() {
        shapes.dispose();
    }
}
