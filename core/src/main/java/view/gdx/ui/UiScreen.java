package view.gdx.ui;

import com.badlogic.gdx.scenes.scene2d.Stage;

/** One full-screen Scene2D view (login, main menu, level select, …). */
public interface UiScreen {
    void show(UiViewContext context);

    void act(float deltaSeconds);

    void resize(int width, int height);

    void dispose();

    Stage stage();
}
