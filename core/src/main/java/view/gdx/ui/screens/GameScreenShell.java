package view.gdx.ui.screens;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import view.gdx.ui.UiScreen;
import view.gdx.ui.UiViewContext;

public final class GameScreenShell implements UiScreen {
    private final Stage stage = new Stage(new ScreenViewport());

    @Override
    public void show(UiViewContext context) {
<<<<<<< Updated upstream
=======
        controller = context.controller;
        assets = context.assets;
        pause.setVisible(context.menu == null || context.menu == MenuType.NONE);
>>>>>>> Stashed changes
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
