
package view.gdx.ui.screens.menus;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import controller.ControllerManager;
import model.service.ProfileViewState;
import view.gdx.AssetContext;
import view.gdx.ui.UiScreen;
import view.gdx.ui.UiViewContext;
import view.gdx.ui.widgets.UiWidgets;

public class ProfileOverlayScreen implements UiScreen {
    private static final String EDIT_ICON = "IMAGE_UI_MAINMENU_EDIT_BTN_NORMAL";

    private final Stage stage;
    private final Texture brownTexture;
    private final Texture panelTexture;
    private ControllerManager controllerManager;

    private Label menuName;
    private Label usernameLabel;
    private ImageButton editUsernameButton;
    private Label nickNameLabel;
    private ImageButton editNickNameButton;
    private Label emailLabel;
    private ImageButton editEmailButton;
    private TextButton changePasswordButton;
    private Label gamesPlayed;
    private Label levelsCompleted;
    private Label MaxMewPionts;

    private AssetContext assets;

    public ProfileOverlayScreen() {
        stage = new Stage(new ScreenViewport());

        Pixmap brownPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        brownPixmap.setColor(new Color(0.36f, 0.18f, 0.07f, 1f)); // dark brown
        brownPixmap.fill();
        brownTexture = makeRoundedRect(new Color(0.36f, 0.18f, 0.07f, 1f), 512, 512, 12);
        brownPixmap.dispose();

        Pixmap panelPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        panelPixmap.setColor(new Color(0.75f, 0.55f, 0.30f, 1f)); // warm tan
        panelPixmap.fill();
        panelTexture = makeRoundedRect(new Color(0.75f, 0.55f, 0.30f, 1f), 512, 512, 8);
        panelPixmap.dispose();

        menuName = UiWidgets.title("Profile Menu");

        usernameLabel = UiWidgets.body("");
        editUsernameButton = new ImageButton(new ImageButton.ImageButtonStyle());
        TextField usernameField = UiWidgets.field("new username", false);
        TextButton saveUsername = UiWidgets.primary("Save");
        UiWidgets.onChange(editUsernameButton, () -> {
            boolean open = !usernameField.isVisible();
            usernameField.setVisible(open);
            saveUsername.setVisible(open);
            if (open) {
                usernameField.setText("");
            }
        });
        UiWidgets.onChange(saveUsername, () -> {
            UiWidgets.apply(controllerManager,
                    controllerManager.getProfileController().changeUsername(UiWidgets.text(usernameField)));
        });

        nickNameLabel = UiWidgets.body("");
        editNickNameButton = new ImageButton(new ImageButton.ImageButtonStyle());
        TextField nicknameField = UiWidgets.field("new nickname", false);
        TextButton saveNick = UiWidgets.primary("save");
        UiWidgets.onChange(editNickNameButton, () -> {
            boolean open = !nicknameField.isVisible();
            nicknameField.setVisible(open);
            saveNick.setVisible(open);
            if (open) {
                nicknameField.setText("");
            }
        });
        UiWidgets.onChange(saveNick, () -> {
            UiWidgets.apply(controllerManager,
                    controllerManager.getProfileController().changeNickname(UiWidgets.text(nicknameField)));
        });

        emailLabel = UiWidgets.body("");
        editEmailButton = new ImageButton(new ImageButton.ImageButtonStyle());
        TextField emailField = UiWidgets.field("new email", false);
        TextButton saveEmail = UiWidgets.primary("save");
        UiWidgets.onChange(editEmailButton, () -> {
            boolean open = !emailField.isVisible();
            emailField.setVisible(open);
            saveEmail.setVisible(open);
            if (open) {
                emailField.setText("");
            }
        });
        UiWidgets.onChange(saveEmail, () -> {
            UiWidgets.apply(controllerManager,
                    controllerManager.getProfileController().changeEmail(UiWidgets.text(emailField)));
        });

        changePasswordButton = UiWidgets.plain("Change password");
        TextField oldPassword = UiWidgets.field("enter current password", true);
        TextField newPassword = UiWidgets.field("enter new password", true);
        TextButton savePass = UiWidgets.primary("save password");

        UiWidgets.onChange(changePasswordButton, () -> {
            boolean open = !oldPassword.isVisible();
            oldPassword.setVisible(open);
            newPassword.setVisible(open);
            savePass.setVisible(open);
            if (open) {
                oldPassword.setText("");
                newPassword.setText("");
            }
        });
        UiWidgets.onChange(savePass, () -> {
            UiWidgets.apply(controllerManager,
                    controllerManager.getProfileController()
                            .changePassword(UiWidgets.text(oldPassword), UiWidgets.text(newPassword)));
        });
        gamesPlayed = UiWidgets.body("");
        levelsCompleted = UiWidgets.body("");
        MaxMewPionts = UiWidgets.body("");

        Table panel = new Table();
        panel.setBackground(new TextureRegionDrawable(new TextureRegion(panelTexture)));
        panel.pad(20f);

        usernameField.setVisible(false);
        saveUsername.setVisible(false);
        nicknameField.setVisible(false);
        saveNick.setVisible(false);
        emailField.setVisible(false);
        saveEmail.setVisible(false);
        oldPassword.setVisible(false);
        newPassword.setVisible(false);
        savePass.setVisible(false);

        panel.add(menuName).colspan(2).padBottom(12f).row();

        panel.add(usernameLabel).left().expandX();
        panel.add(editUsernameButton).size(36f).row();
        panel.add(usernameField).growX().height(40f).padTop(4f);
        panel.add(saveUsername).width(90f).height(40f).padLeft(8f).row();

        panel.add(nickNameLabel).left().expandX();
        panel.add(editNickNameButton).size(36f).row();
        panel.add(nicknameField).growX().height(40f).padTop(4f);
        panel.add(saveNick).width(90f).height(40f).padLeft(8f).row();

        panel.add(emailLabel).left().expandX();
        panel.add(editEmailButton).size(36f).row();
        panel.add(emailField).growX().height(40f).padTop(4f);
        panel.add(saveEmail).width(90f).height(40f).padLeft(8f).row();

        panel.add(changePasswordButton).colspan(2).growX().height(40f).padTop(8f).row();
        panel.add(oldPassword).colspan(2).growX().height(40f).padTop(4f).row();
        panel.add(newPassword).colspan(2).growX().height(40f).padTop(4f).row();
        panel.add(savePass).colspan(2).growX().height(36f).padTop(4f).row();

        panel.add(gamesPlayed).colspan(2).left().padTop(8f).row();
        panel.add(levelsCompleted).colspan(2).left().padTop(8f).row();
        panel.add(MaxMewPionts).colspan(2).left().row();

        TextButton back = UiWidgets.plain("Back");
        UiWidgets.onChange(back, () -> UiWidgets.apply(controllerManager, controllerManager.exitMenu()));
        panel.add(back).colspan(2).width(160f).height(40f).padTop(12f);

        Table brownOuter = new Table();
        brownOuter.setBackground(new TextureRegionDrawable(new TextureRegion(brownTexture)));
        brownOuter.setTouchable(Touchable.enabled);
        brownOuter.pad(8f);
        brownOuter.add(panel);

        Table root = new Table();
        root.setFillParent(true);
        root.center();
        root.add(brownOuter);

        stage.addActor(root);
    }

    @Override
    public void show(UiViewContext context) {
        controllerManager = context.controller;
        assets = context.assets;

        ProfileViewState p = context.profile;
        usernameLabel.setText("Username: " + p.username);
        nickNameLabel.setText("Nickname: " + p.nickname);
        gamesPlayed.setText("Games played:" + p.gamesPlayed);
        levelsCompleted.setText("Levels completed: " + p.completedLevels);
        MaxMewPionts.setText("Highest MewPoints: " + p.highestScore);//for now shows highes score

        String email = controllerManager.getStorage().getCurrentUser().email;
        emailLabel.setText("Email: " + email);
    }

    @Override
    public void act(float deltaSeconds) {
        styleEditButton(editUsernameButton);
        styleEditButton(editNickNameButton);
        styleEditButton(editEmailButton);

        stage.act(deltaSeconds);

    }

    @Override
    public void resize(int width, int height) {
        stage().getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        brownTexture.dispose();
        panelTexture.dispose();
    }

    @Override
    public Stage stage() {
        return stage;
    }

    private void styleEditButton(ImageButton button) {
        if (button.getStyle().imageUp != null || assets == null) {
            return;
        }
        var region = assets.region(EDIT_ICON);
        if (region == null)
            return;

        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(button.getStyle());
        style.imageUp = new TextureRegionDrawable(region);
        button.setStyle(style);
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