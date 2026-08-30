package view.gdx.ui.screens.izombie;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import controller.ControllerManager;
import network.NetworkSession;
import shared.message.QuickMessageId;
import view.gdx.ui.UiScreen;
import view.gdx.ui.UiViewContext;
import view.gdx.ui.widgets.UiWidgets;

public final class QuickMessageOverlayScreen implements UiScreen {
    private final Stage stage;
    private final Texture brownTexture;
    private final Texture panelTexture;
    private ControllerManager controller;

    public QuickMessageOverlayScreen() {
        stage = new Stage(new ScreenViewport());
        brownTexture = makeRoundedRect(new Color(0.36f, 0.18f, 0.07f, 1f), 256, 256, 12);
        panelTexture = makeRoundedRect(new Color(0.75f, 0.55f, 0.30f, 1f), 256, 256, 8);

        Table panel = new Table();
        panel.setBackground(new TextureRegionDrawable(new TextureRegion(panelTexture)));
        panel.pad(24f);
        panel.add(UiWidgets.title("Quick Message")).padBottom(12f).colspan(3).row();

        int col = 0;
        for (QuickMessageId id : QuickMessageId.catalog()) {
            TextButton button = UiWidgets.plain(id.display);
            UiWidgets.onChange(button, () -> {
                NetworkSession net = controller.getNetworkSession();
                if (net != null) {
                    net.socket().quickMessage(id.name());
                }
                controller.clearCurrentMenu();
                controller.refreshView();
            });
            panel.add(button).width(100f).height(40f).pad(4f);
            col++;
            if (col % 3 == 0) {
                panel.row();
            }
        }
        TextButton close = UiWidgets.plain("Close");
        UiWidgets.onChange(close, () -> {
            controller.clearCurrentMenu();
            controller.refreshView();
        });
        panel.row();
        panel.add(close).colspan(3).width(200f).height(40f).padTop(8f);

        Table brownOuter = new Table();
        brownOuter.setBackground(new TextureRegionDrawable(new TextureRegion(brownTexture)));
        brownOuter.setTouchable(Touchable.enabled);
        brownOuter.pad(16f);
        brownOuter.add(panel);

        Table root = new Table();
        root.setFillParent(true);
        root.center();
        root.add(brownOuter);
        stage.addActor(root);
    }

    @Override
    public void show(UiViewContext context) {
        this.controller = context.controller;
    }

    @Override
    public void act(float deltaSeconds) {
        stage.act(deltaSeconds);
    }

    @Override
    public Stage stage() {
        return stage;
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        brownTexture.dispose();
        panelTexture.dispose();
    }

    private static Texture makeRoundedRect(Color color, int w, int h, int radius) {
        Pixmap pixmap = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fillRectangle(radius, 0, w - 2 * radius, h);
        pixmap.fillRectangle(0, radius, w, h - 2 * radius);
        pixmap.fillCircle(radius, radius, radius);
        pixmap.fillCircle(w - radius - 1, radius, radius);
        pixmap.fillCircle(radius, h - radius - 1, radius);
        pixmap.fillCircle(w - radius - 1, h - radius - 1, radius);
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }
}
