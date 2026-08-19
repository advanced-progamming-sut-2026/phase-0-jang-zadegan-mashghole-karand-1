package view.gdx.ui.screens.auth;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import controller.ControllerManager;
import view.gdx.ui.UiScreen;
import view.gdx.ui.UiViewContext;


public final class SettingsOverlayScreen implements UiScreen {
    private final Stage stage;
    private ControllerManager controllerManager;
    private Label difficultyLabel;
    private Label gameSpeedLabel;
    private CheckBox groundWebbing;
    private CheckBox debugMode;
    private boolean syncing;

    public SettingsOverlayScreen(){
        stage = new Stage(new ScreenViewport());

        Label title = AuthWidgets.title("Settings");
        difficultyLabel = AuthWidgets.body("3");
        gameSpeedLabel = AuthWidgets.body("2");
        TextButton minusDif = AuthWidgets.plain("-");
        TextButton plusDif = AuthWidgets.plain("+");
        TextButton minusSpeed = AuthWidgets.plain("-");
        TextButton plusSpeed = AuthWidgets.plain("+");
        TextButton back = AuthWidgets.plain("Back");
        groundWebbing = AuthWidgets.checkBox("Ground Webbing");
        debugMode = AuthWidgets.checkBox("Debug Mode");

        AuthWidgets.onChange(minusDif, this::decreaseDifficulty);
        AuthWidgets.onChange(plusDif, this::increaseDifficulty);
        AuthWidgets.onChange(minusSpeed, this::decreaseSpeed);
        AuthWidgets.onChange(plusSpeed, this::increaseSpeed);
        AuthWidgets.onChange(back, ()-> AuthWidgets.apply(controllerManager,controllerManager.exitMenu()));
        AuthWidgets.onChange(groundWebbing, () -> {
            if(syncing) return;
            AuthWidgets.apply(controllerManager, controllerManager.getSettingController().
                    setShowGroundWebbing(groundWebbing.isChecked()));
        });
        AuthWidgets.onChange(debugMode, () -> {
            if(syncing) return;
            AuthWidgets.apply(controllerManager, controllerManager.getSettingController().
                    setDebugMode(debugMode.isChecked()));
        });

        Table panel = new Table();
        panel.add(title).padBottom(16f).colspan(3).row();
        panel.add(AuthWidgets.body("Difficulty")).left();
        panel.add(minusDif).size(44f);
        panel.add(difficultyLabel).pad(0,8f,0,8f);
        panel.add(plusDif).size(44f).row();
        panel.add(AuthWidgets.body("Game Speed")).left();
        panel.add(minusSpeed).size(44f);
        panel.add(gameSpeedLabel).pad(0,8f,0,8f);
        panel.add(plusSpeed).size(44f).row();
        panel.add(groundWebbing).left().colspan(4).padTop(8f).row();
        panel.add(debugMode).left().colspan(4).padTop(4f).row();
        panel.add(back).colspan(3).growX().height(44f).padTop(16f);

        Table root = new Table();
        root.setFillParent(true);
        root.center();
        root.add(panel).width(420f);
        stage.addActor(root);
    }

    @Override
    public void show(UiViewContext context) {
        controllerManager = context.controller;
        if (context.settings != null) {
            difficultyLabel.setText(String.valueOf(context.settings.difficultyLevel));
            gameSpeedLabel.setText(String.valueOf(context.settings.gameSpeed));

            syncing = true;
            groundWebbing.setChecked(context.settings.showGroundWebbing);
            debugMode.setChecked(context.settings.debugMode);
            syncing = false;
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
    }

    @Override
    public Stage stage() {
        return stage;
    }


    private int labelInt(Label label) {
        return Integer.parseInt(label.getText().toString().trim());
    }

    private void decreaseDifficulty() {
        AuthWidgets.apply(controllerManager,
                controllerManager.getSettingController().changeDifficulty(labelInt(difficultyLabel) - 1));
    }

    private void increaseDifficulty() {
        AuthWidgets.apply(controllerManager,
                controllerManager.getSettingController().changeDifficulty(labelInt(difficultyLabel) + 1));
    }

    private void decreaseSpeed() {
        AuthWidgets.apply(controllerManager,
                controllerManager.getSettingController().changeGameSpeed(labelInt(gameSpeedLabel) - 1));
    }

    private void increaseSpeed() {
        AuthWidgets.apply(controllerManager,
                controllerManager.getSettingController().changeGameSpeed(labelInt(gameSpeedLabel) + 1));
    }

}
