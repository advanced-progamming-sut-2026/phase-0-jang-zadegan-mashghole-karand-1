package view.gdx.ui.screens.izombie;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import controller.ControllerManager;
import network.NetworkSession;
import view.MenuType;
import view.gdx.ui.UiScreen;
import view.gdx.ui.UiViewContext;
import view.gdx.ui.widgets.UiWidgets;

/** Full-screen blocking prompt: accept/decline opponent restart, or wait after requesting. */
public final class MatchRestartOverlayScreen implements UiScreen {
    private final Stage stage;
    private final Texture dimTexture;
    private final Texture brownTexture;
    private final Texture panelTexture;
    private final Label titleLabel;
    private final Label bodyLabel;
    private final TextButton primaryButton;
    private final TextButton secondaryButton;
    private ControllerManager controller;
    private boolean waitingMode;

    public MatchRestartOverlayScreen() {
        stage = new Stage(new ScreenViewport());
        dimTexture = solid(new Color(0f, 0f, 0f, 0.55f), 1, 1);
        brownTexture = makeRoundedRect(new Color(0.36f, 0.18f, 0.07f, 1f), 256, 256, 12);
        panelTexture = makeRoundedRect(new Color(0.75f, 0.55f, 0.30f, 1f), 256, 256, 8);

        titleLabel = UiWidgets.title("Restart Match?");
        bodyLabel = UiWidgets.body("Your opponent wants to restart this match.");
        primaryButton = UiWidgets.primary("Restart");
        secondaryButton = UiWidgets.plain("Decline");

        UiWidgets.onChange(primaryButton, this::onPrimary);
        UiWidgets.onChange(secondaryButton, this::onSecondary);

        Table panel = new Table();
        panel.setBackground(new TextureRegionDrawable(new TextureRegion(panelTexture)));
        panel.pad(40f);
        panel.add(titleLabel).padBottom(12f).row();
        panel.add(bodyLabel).width(340f).padBottom(16f).row();
        Table buttons = new Table();
        buttons.add(primaryButton).width(140f).height(44f).padRight(8f);
        buttons.add(secondaryButton).width(140f).height(44f);
        panel.add(buttons);

        Table brownOuter = new Table();
        brownOuter.setBackground(new TextureRegionDrawable(new TextureRegion(brownTexture)));
        brownOuter.setTouchable(Touchable.enabled);
        brownOuter.pad(16f);
        brownOuter.add(panel);

        Table root = new Table();
        root.setFillParent(true);
        root.setBackground(new TextureRegionDrawable(new TextureRegion(dimTexture)));
        root.setTouchable(Touchable.enabled);
        root.center();
        root.add(brownOuter);
        stage.addActor(root);
    }

    @Override
    public void show(UiViewContext context) {
        this.controller = context.controller;
        waitingMode = context.menu == MenuType.MATCH_RESTART_WAIT;
        primaryButton.setVisible(true);
        secondaryButton.setVisible(true);
        if (waitingMode) {
            titleLabel.setText("Restart requested");
            bodyLabel.setText("Waiting for your opponent to accept or decline...");
            primaryButton.setVisible(false);
            secondaryButton.setText("Cancel");
        } else {
            titleLabel.setText("Restart Match?");
            bodyLabel.setText("Your opponent wants to restart this match.");
            primaryButton.setText("Restart");
            secondaryButton.setText("Decline");
        }
    }

    private void onPrimary() {
        if (controller == null || waitingMode) {
            return;
        }
        NetworkSession net = controller.getNetworkSession();
        if (net != null) {
            net.socket().acceptRestart();
        }
        controller.openMenu(MenuType.MATCH_RESTART_WAIT);
        titleLabel.setText("Restarting...");
        bodyLabel.setText("Applying restart...");
        primaryButton.setVisible(false);
        secondaryButton.setVisible(false);
    }

    private void onSecondary() {
        if (controller == null) {
            return;
        }
        NetworkSession net = controller.getNetworkSession();
        if (net != null) {
            if (waitingMode) {
                net.socket().cancelRestart();
            } else {
                net.socket().rejectRestart();
            }
        }
        controller.clearCurrentMenu();
        controller.refreshView();
    }

    @Override
    public void act(float deltaSeconds) {
        stage.act(deltaSeconds);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        dimTexture.dispose();
        brownTexture.dispose();
        panelTexture.dispose();
    }

    @Override
    public Stage stage() {
        return stage;
    }

    private static Texture solid(Color color, int w, int h) {
        Pixmap px = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        px.setColor(color);
        px.fill();
        Texture tex = new Texture(px);
        px.dispose();
        return tex;
    }

    private Texture makeRoundedRect(Color color, int w, int h, int radius) {
        Pixmap px = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        px.setColor(0, 0, 0, 0);
        px.fill();
        px.setColor(color);
        px.fillRectangle(radius, 0, w - 2 * radius, h);
        px.fillRectangle(0, radius, w, h - 2 * radius);
        px.fillCircle(radius, radius, radius);
        px.fillCircle(w - radius - 1, radius, radius);
        px.fillCircle(radius, h - radius - 1, radius);
        px.fillCircle(w - radius - 1, h - radius - 1, radius);
        Texture tex = new Texture(px);
        px.dispose();
        return tex;
    }
}
