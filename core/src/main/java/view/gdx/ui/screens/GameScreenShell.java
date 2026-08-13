package view.gdx.ui.screens;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import view.gdx.ui.UiScreen;
import view.gdx.ui.UiViewContext;

/** No-op screen so the lawn stays visible during {@link view.ScreenType#GAME}. */
public final class GameScreenShell implements UiScreen {
    private final Stage stage = new Stage(new ScreenViewport());

    @Override
    public void show(UiViewContext context) {
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
