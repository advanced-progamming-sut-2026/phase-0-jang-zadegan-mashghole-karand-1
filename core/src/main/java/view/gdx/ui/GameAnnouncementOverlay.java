package view.gdx.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public final class GameAnnouncementOverlay implements Disposable {
    private static final float DEFAULT_DURATION_SECONDS = 2.5f;

    private final Stage stage;
    private final Label announcementLabel;
    private float remainingSeconds;

    public GameAnnouncementOverlay() {
        stage = new Stage(new ScreenViewport());
        Table root = new Table();
        root.setFillParent(true);
        root.center();

        announcementLabel = new Label("", UiSkin.get(), "big");
        announcementLabel.setColor(Color.RED);
        announcementLabel.setWrap(true);
        announcementLabel.setAlignment(com.badlogic.gdx.utils.Align.center);
        root.add(announcementLabel).width(640f);

        stage.addActor(root);
        clear();
    }

    public void show(String message) {
        show(message, DEFAULT_DURATION_SECONDS);
    }

    public void show(String message, float durationSeconds) {
        if (message == null || message.isBlank()) {
            clear();
            return;
        }
        announcementLabel.setText(message);
        remainingSeconds = Math.max(0.1f, durationSeconds);
    }

    public void clear() {
        announcementLabel.setText("");
        remainingSeconds = 0f;
    }

    public boolean isVisible() {
        return remainingSeconds > 0f;
    }

    public void act(float deltaSeconds) {
        if (remainingSeconds > 0f) {
            remainingSeconds -= deltaSeconds;
            if (remainingSeconds <= 0f) {
                clear();
            }
        }
        stage.act(deltaSeconds);
    }

    public void draw() {
        if (!isVisible()) {
            return;
        }
        stage.getViewport().apply();
        stage.draw();
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
