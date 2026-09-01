package view.gdx.ui.screens.menus;

import java.util.List;

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
import view.gdx.ui.UiScreen;
import view.gdx.ui.UiViewContext;
import view.gdx.ui.widgets.UiWidgets;

public final class LevelObjectivesOverlayScreen implements UiScreen {
    private static final float PANEL_WIDTH = 520f;

    private final Stage stage;
    private final Texture dimTexture;
    private final Texture brownTexture;
    private final Texture headerTexture;
    private final Texture contentTexture;
    private final Table objectivesList;

    private ControllerManager controller;

    public LevelObjectivesOverlayScreen() {
        stage = new Stage(new ScreenViewport());
        dimTexture = solid(new Color(0f, 0f, 0f, 0.55f), 1, 1);
        brownTexture = makeRoundedRect(new Color(0.36f, 0.18f, 0.07f, 1f), 256, 256, 12);
        headerTexture = makeRoundedRect(new Color(1f, 0.78f, 0.12f, 1f), 256, 256, 6);
        contentTexture = makeRoundedRect(new Color(0.70f, 0.50f, 0.28f, 1f), 256, 256, 8);

        Label title = UiWidgets.body("Level Objectives");
        objectivesList = new Table();
        objectivesList.defaults().left().padBottom(10f);

        TextButton continueButton = UiWidgets.primary("CONTINUE");
        UiWidgets.onChange(continueButton, () -> {
            if (controller != null) {
                UiWidgets.apply(controller, controller.dismissLevelObjectives());
            }
        });

        Table header = new Table();
        header.setBackground(new TextureRegionDrawable(new TextureRegion(headerTexture)));
        header.pad(4f, 16f, 4f, 16f);
        header.add(title).expandX().center();

        Table content = new Table();
        content.setBackground(new TextureRegionDrawable(new TextureRegion(contentTexture)));
        content.pad(16f, 24f, 14f, 24f);
        content.add(objectivesList).growX().left().row();
        content.add(continueButton).width(180f).height(44f).padTop(10f);

        Table brownOuter = new Table();
        brownOuter.setBackground(new TextureRegionDrawable(new TextureRegion(brownTexture)));
        brownOuter.setTouchable(Touchable.enabled);
        brownOuter.pad(10f);
        brownOuter.add(header).growX().height(36f).padBottom(4f).row();
        brownOuter.add(content).width(PANEL_WIDTH);

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
        refreshObjectives();
    }

    private void refreshObjectives() {
        objectivesList.clearChildren();
        List<String> lines = controller != null ? controller.currentLevelObjectives() : List.of();
        for (String line : lines) {
            Label bullet = UiWidgets.body("○");
            Label text = UiWidgets.body(line);
            text.setWrap(true);
            Table row = new Table();
            row.add(bullet).padRight(12f).top();
            row.add(text).width(420f).left().growX();
            objectivesList.add(row).growX().row();
        }
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
        headerTexture.dispose();
        contentTexture.dispose();
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
