package view.gdx.ui.screens;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import view.gdx.ui.UiScreen;
import view.gdx.ui.UiSkin;
import view.gdx.ui.UiViewContext;

/** Placeholder for menu overlays (pause, settings, profile, …). */
public final class PlaceholderOverlayScreen implements UiScreen {
    private final String title;
    private final Stage stage;
    private final Label heading;

    public PlaceholderOverlayScreen(String title) {
        this.title = title;
        this.stage = new Stage(new ScreenViewport());
        Table root = new Table();
        root.setFillParent(true);
        root.top().right().pad(24f);
        heading = new Label(title, UiSkin.get(), "medium");
        root.add(heading);
        stage.addActor(root);
    }

    @Override
    public void show(UiViewContext context) {
        heading.setText(title + " (" + context.menu.name() + ")");
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
    }

    @Override
    public Stage stage() {
        return stage;
    }
}
