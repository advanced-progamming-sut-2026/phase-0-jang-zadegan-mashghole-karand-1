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
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import controller.ControllerManager;
import view.gdx.ui.UiScreen;
import view.gdx.ui.UiViewContext;
import view.gdx.ui.widgets.UiWidgets;

public final class IZombieModeOverlayScreen implements UiScreen {
    private final Stage stage;
    private final Texture brownTexture;
    private final Texture panelTexture;
    private ControllerManager controller;
    private TextField inviteField;

    public IZombieModeOverlayScreen() {
        stage = new Stage(new ScreenViewport());
        brownTexture = makeRoundedRect(new Color(0.36f, 0.18f, 0.07f, 1f), 256, 256, 12);
        panelTexture = makeRoundedRect(new Color(0.75f, 0.55f, 0.30f, 1f), 256, 256, 8);
        stage.addActor(buildRoot());
    }

    private Table buildRoot() {
        inviteField = new TextField("", UiWidgets.skin());
        inviteField.setMessageText("opponent username");
        Table panel = buildModePanel();
        Table brownOuter = new Table();
        brownOuter.setBackground(new TextureRegionDrawable(new TextureRegion(brownTexture)));
        brownOuter.setTouchable(Touchable.enabled);
        brownOuter.pad(16f);
        brownOuter.add(panel);

        Table root = new Table();
        root.setFillParent(true);
        root.center();
        root.add(brownOuter);
        return root;
    }

    private Table buildModePanel() {
        Label title = UiWidgets.title("I, Zombie");
        TextButton offline = UiWidgets.primary("Offline");
        TextButton couch = UiWidgets.primary("Couch Play");
        TextButton random = UiWidgets.primary("Online Random");
        TextButton invite = UiWidgets.primary("Invite Player");
        TextButton back = UiWidgets.plain("Back");

        UiWidgets.onChange(offline, () -> UiWidgets.apply(controller,
                controller.getGameMenuController().startIZombieOffline()));
        UiWidgets.onChange(couch, () -> UiWidgets.apply(controller,
                controller.getGameMenuController().startIZombieCouch()));
        UiWidgets.onChange(random, () -> UiWidgets.apply(controller,
                controller.getGameMenuController().startIZombieOnlineRandom()));
        UiWidgets.onChange(invite, () -> UiWidgets.apply(controller,
                controller.getGameMenuController().startIZombieInvite(inviteField.getText())));
        UiWidgets.onChange(back, () -> {
            controller.clearCurrentMenu();
            controller.refreshView();
        });

        Table panel = new Table();
        panel.setBackground(new TextureRegionDrawable(new TextureRegion(panelTexture)));
        panel.pad(40f);
        panel.add(title).padBottom(12f).row();
        panel.add(UiWidgets.body("Choose how to play")).padBottom(16f).row();
        panel.add(offline).width(280f).height(44f).padBottom(8f).row();
        panel.add(couch).width(280f).height(44f).padBottom(8f).row();
        panel.add(random).width(280f).height(44f).padBottom(8f).row();
        panel.add(inviteField).width(280f).height(40f).padBottom(8f).row();
        panel.add(invite).width(280f).height(44f).padBottom(8f).row();
        panel.add(back).width(280f).height(40f);
        return panel;
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
