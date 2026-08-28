package view.gdx.ui.screens;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import controller.ControllerManager;
import view.MenuType;
import view.gdx.AssetContext;
import view.gdx.ui.UiScreen;
import view.gdx.ui.UiViewContext;
import view.gdx.ui.widgets.UiWidgets;

public final class GameScreenShell implements UiScreen {
    private final Stage stage = new Stage(new ScreenViewport());
    private final ImageButton pause;
    private ControllerManager controller;
    private AssetContext assets;

    public GameScreenShell(){
        pause = new ImageButton(new ImageButton.ImageButtonStyle());
        UiWidgets.onChange(pause, () -> {
            if (controller != null) {
                UiWidgets.apply(controller, controller.enterMenu("pause"));
            }
        });

        Table root = new Table();
        root.setFillParent(true);
        root.setTouchable(Touchable.childrenOnly);
        root.top().right().pad(10f);
        root.add(pause).width(120f).height(44f);
        stage.addActor(root);
    }

    @Override
    public void show(UiViewContext context) {
        controller = context.controller;
        assets = context.assets;
        pause.setVisible(context.menu != MenuType.PAUSE);
    }

    @Override
    public void act(float deltaSeconds) {
        if (pause.getStyle().imageUp == null && assets != null) {
            var region = assets.region("IMAGE_UI_HUD_INGAME_PAUSE_BUTTON");
            if (region != null) {
                ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(pause.getStyle());
                style.imageUp = new TextureRegionDrawable(region);
                pause.setStyle(style);
            }
        }
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
