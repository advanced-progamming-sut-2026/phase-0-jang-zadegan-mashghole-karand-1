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
import model.data.content.minigame.MiniGameType;
import model.service.GameNavigationState;
import model.service.MatchResultUi;
import network.NetworkSession;
import view.MenuType;
import view.gdx.ui.UiScreen;
import view.gdx.ui.UiViewContext;
import view.gdx.ui.widgets.UiWidgets;

public final class MatchResultOverlayScreen implements UiScreen {
    private final Stage stage;
    private final Texture dimTexture;
    private final Texture brownTexture;
    private final Texture panelTexture;
    private final Label titleLabel;
    private final Label detailLabel;
    private final TextButton restartButton;
    private ControllerManager controller;

    public MatchResultOverlayScreen() {
        stage = new Stage(new ScreenViewport());
        dimTexture = solid(new Color(0f, 0f, 0f, 0.55f), 1, 1);
        brownTexture = makeRoundedRect(new Color(0.36f, 0.18f, 0.07f, 1f), 256, 256, 12);
        panelTexture = makeRoundedRect(new Color(0.75f, 0.55f, 0.30f, 1f), 256, 256, 8);

        titleLabel = UiWidgets.title("Match Over");
        detailLabel = UiWidgets.body("");
        restartButton = UiWidgets.primary("Restart");
        TextButton exit = UiWidgets.secondary("Exit");

        UiWidgets.onChange(restartButton, this::onRestart);
        UiWidgets.onChange(exit, this::onExit);

        Table panel = new Table();
        panel.setBackground(new TextureRegionDrawable(new TextureRegion(panelTexture)));
        panel.pad(36f, 40f, 36f, 40f);
        panel.add(titleLabel).padBottom(12f).row();
        panel.add(detailLabel).width(360f).padBottom(20f).row();
        Table buttons = new Table();
        buttons.add(restartButton).width(140f).height(44f).padRight(8f);
        buttons.add(exit).width(140f).height(44f);
        panel.add(buttons);

        Table brownOuter = new Table();
        brownOuter.setBackground(new TextureRegionDrawable(new TextureRegion(brownTexture)));
        brownOuter.setTouchable(Touchable.enabled);
        brownOuter.pad(12f);
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
        controller = context.controller;
        MatchResultUi ui = controller != null
                ? controller.getSessionLifecycleController().matchResultUi()
                : null;
        if (ui != null) {
            titleLabel.setText(ui.title);
            detailLabel.setText(ui.detail);
            boolean canRestart = ui.localRestart || ui.onlineRestart || ui.returnToIZombieModes;
            restartButton.setVisible(canRestart);
            restartButton.setDisabled(!canRestart);
            if (ui.returnToIZombieModes) {
                restartButton.setText("Play again");
            } else {
                restartButton.setText("Restart");
            }
        }
    }

    private void onRestart() {
        if (controller == null) {
            return;
        }
        MatchResultUi ui = controller.getSessionLifecycleController().matchResultUi();
        if (ui == null) {
            onExit();
            return;
        }
        if (ui.onlineRestart) {
            NetworkSession net = controller.getNetworkSession();
            if (net != null && net.socket().requestRestart()) {
                controller.openMenu(MenuType.MATCH_RESTART_WAIT);
                return;
            }
        }
        if (ui.returnToIZombieModes) {
            controller.getSessionLifecycleController().returnToLevelSelect();
            GameNavigationState nav = controller.getGameNavigation();
            nav.pendingMiniGame = MiniGameType.I_ZOMBIE;
            nav.phase = GameNavigationState.Phase.MINIGAME;
            controller.openMenu(MenuType.I_ZOMBIE_MODE);
            return;
        }
        if (ui.localRestart) {
            UiWidgets.apply(controller, controller.getSessionLifecycleController().restartLevel());
        }
    }

    private void onExit() {
        if (controller == null) {
            return;
        }
        UiWidgets.apply(controller, controller.getSessionLifecycleController().returnToLevelSelect());
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
