package view.gdx.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;

import controller.CommandResult.CommandResult;
import controller.ControllerManager;
import view.gdx.ui.UiSkin;

public final class UiWidgets {
    private UiWidgets() {
    }

    public static Skin skin() {
        return UiSkin.get();
    }

    public static Label title(String text) {
        return new Label(text, skin(), "big");
    }

    public static Label body(String text) {
        return new Label(text, skin(), "default");
    }

    public static TextField field(String message, boolean password) {
        TextField field = new TextField("", skin(), "default");
        field.setMessageText(message);
        if (password) {
            field.setPasswordMode(true);
            field.setPasswordCharacter('*');
        }
        return field;
    }

    public static CheckBox checkBox(String text) {
        return new CheckBox("  " + text, skin(), "default");
    }

    public static TextButton primary(String text) {
        return new TextButton(text, skin(), "green");
    }

    public static TextButton secondary(String text) {
        return new TextButton(text, skin(), "brown");
    }

    public static TextButton plain(String text) {
        return new TextButton(text, skin(), "default");
    }

    public static SelectBox<String> selectBox(Array<String> items) {
        SelectBox<String> box = new SelectBox<>(skin(), "default");
        box.setItems(items);
        return box;
    }

    public static void onChange(Actor actor, Runnable action) {
        actor.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                action.run();
            }
        });
    }

    public static void apply(ControllerManager controller, CommandResult result) {
        controller.handleCommandResult(result);
    }

    public static String text(TextField field) {
        return field.getText() == null ? "" : field.getText().trim();
    }

    public static String raw(TextField field) {
        return field.getText() == null ? "" : field.getText();
    }
}
