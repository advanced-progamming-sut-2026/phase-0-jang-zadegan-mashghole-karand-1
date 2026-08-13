package view.gdx.ui.screens;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import view.gdx.ui.UiScreen;
import view.gdx.ui.UiSkin;
import view.gdx.ui.UiViewContext;

/**
 * Temporary screen shown until real UI is implemented for each {@link view.ScreenType}.
 * Teammates replace these one-by-one with dedicated screen classes.
 */
public final class PlaceholderScreen implements UiScreen {
    private final String title;
    private final Stage stage;
    private final Label heading;
    private final Label detail;

    public PlaceholderScreen(String title) {
        this.title = title;
        this.stage = new Stage(new ScreenViewport());
        Table root = new Table();
        root.setFillParent(true);
        root.center();

        heading = new Label(title, UiSkin.get(), "big");
        detail = new Label("", UiSkin.get(), "default");
        root.add(heading).row();
        root.add(detail).padTop(12f);
        stage.addActor(root);
    }

    @Override
    public void show(UiViewContext context) {
        StringBuilder info = new StringBuilder();
        info.append("Screen: ").append(context.screen.name());
        if (context.menu != null && context.menu.name() != null && !"NONE".equals(context.menu.name())) {
            info.append("\nMenu overlay: ").append(context.menu.name());
        }
        info.append("\n\nReplace PlaceholderScreen with a real screen implementation.");
        detail.setText(info.toString());
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

    public String title() {
        return title;
    }
}
