package view.gdx.ui.screens.auth;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import controller.AuthController;
import controller.ControllerManager;
import view.gdx.ui.UiScreen;
import view.gdx.ui.UiViewContext;
import view.gdx.ui.widgets.UiWidgets;

public final class LoginScreen implements UiScreen {
    private enum Panel {
        LOGIN, FORGOT, ANSWER, RESET
    }

    private final Stage stage;
    private final Label title;
    private final Table content;
    private final Table loginPanel;
    private final Table forgotPanel;
    private final Table answerPanel;
    private final Table resetPanel;

    private final TextField loginUsername;
    private final TextField loginPassword;
    private final CheckBox stayLoggedIn;

    private final TextField forgotUsername;
    private final TextField forgotEmail;

    private final Label securityQuestion;
    private final TextField securityAnswer;

    private final TextField newPassword;
    private final TextField newPasswordConfirm;

    private Panel localPanel = Panel.LOGIN;
    private ControllerManager controller;

    public LoginScreen() {
        stage = new Stage(new ScreenViewport());

        loginUsername = UiWidgets.field("Username", false);
        loginPassword = UiWidgets.field("Password", true);
        stayLoggedIn = UiWidgets.checkBox("Stay logged in");

        forgotUsername = UiWidgets.field("Username", false);
        forgotEmail = UiWidgets.field("Email", false);

        securityQuestion = UiWidgets.body("");
        securityQuestion.setWrap(true);
        securityAnswer = UiWidgets.field("Security answer", false);

        newPassword = UiWidgets.field("New password", true);
        newPasswordConfirm = UiWidgets.field("Confirm password", true);

        loginPanel = buildLoginPanel();
        forgotPanel = buildForgotPanel();
        answerPanel = buildAnswerPanel();
        resetPanel = buildResetPanel();

        title = UiWidgets.title("Login");
        content = new Table();

        Table root = new Table();
        root.setFillParent(true);
        root.center();
        root.add(title).padBottom(18f).row();
        root.add(content).width(420f);
        stage.addActor(root);
        showPanel(Panel.LOGIN);
    }

    private Table buildLoginPanel() {
        Table table = new Table();
        TextButton login = UiWidgets.primary("Login");
        TextButton forgot = UiWidgets.secondary("Forgot password");
        TextButton register = UiWidgets.plain("Create account");
        TextButton quit = UiWidgets.plain("Quit");

        UiWidgets.onChange(login, this::submitLogin);
        UiWidgets.onChange(forgot, () -> showPanel(Panel.FORGOT));
        UiWidgets.onChange(register, this::goRegister);
        UiWidgets.onChange(quit, this::quit);

        table.add(loginUsername).growX().height(44f).padBottom(8f).row();
        table.add(loginPassword).growX().height(44f).padBottom(8f).row();
        table.add(stayLoggedIn).left().padBottom(12f).row();
        table.add(login).growX().height(48f).padBottom(8f).row();
        table.add(forgot).growX().height(44f).padBottom(8f).row();
        table.add(register).growX().height(44f).padBottom(8f).row();
        table.add(quit).growX().height(44f).row();
        return table;
    }

    private Table buildForgotPanel() {
        Table table = new Table();
        TextButton submit = UiWidgets.primary("Continue");
        TextButton back = UiWidgets.plain("Back");

        UiWidgets.onChange(submit, this::submitForgot);
        UiWidgets.onChange(back, () -> showPanel(Panel.LOGIN));

        table.add(UiWidgets.body("Recover password")).padBottom(10f).row();
        table.add(forgotUsername).growX().height(44f).padBottom(8f).row();
        table.add(forgotEmail).growX().height(44f).padBottom(12f).row();
        table.add(submit).growX().height(48f).padBottom(8f).row();
        table.add(back).growX().height(44f).row();
        return table;
    }

    private Table buildAnswerPanel() {
        Table table = new Table();
        TextButton submit = UiWidgets.primary("Submit answer");
        TextButton cancel = UiWidgets.plain("Cancel");

        UiWidgets.onChange(submit, this::submitAnswer);
        UiWidgets.onChange(cancel, this::cancelRecovery);

        table.add(UiWidgets.body("Security question")).padBottom(8f).row();
        table.add(securityQuestion).growX().padBottom(10f).row();
        table.add(securityAnswer).growX().height(44f).padBottom(12f).row();
        table.add(submit).growX().height(48f).padBottom(8f).row();
        table.add(cancel).growX().height(44f).row();
        return table;
    }

    private Table buildResetPanel() {
        Table table = new Table();
        TextButton submit = UiWidgets.primary("Reset password");
        TextButton cancel = UiWidgets.plain("Cancel");

        UiWidgets.onChange(submit, this::submitReset);
        UiWidgets.onChange(cancel, this::cancelRecovery);

        table.add(UiWidgets.body("Choose a new password")).padBottom(10f).row();
        table.add(newPassword).growX().height(44f).padBottom(8f).row();
        table.add(newPasswordConfirm).growX().height(44f).padBottom(12f).row();
        table.add(submit).growX().height(48f).padBottom(8f).row();
        table.add(cancel).growX().height(44f).row();
        return table;
    }

    private void showPanel(Panel panel) {
        localPanel = panel;
        Table active = switch (panel) {
            case LOGIN -> loginPanel;
            case FORGOT -> forgotPanel;
            case ANSWER -> answerPanel;
            case RESET -> resetPanel;
        };
        title.setText(switch (panel) {
            case LOGIN -> "Login";
            case FORGOT -> "Forgot password";
            case ANSWER -> "Security question";
            case RESET -> "Reset password";
        });
        content.clearChildren();
        content.add(active).growX();
        content.invalidateHierarchy();
    }

    private void syncFromAuthState(UiViewContext context) {
        if (context.authState != null && context.authState.isAwaitingNewPassword) {
            showPanel(Panel.RESET);
            return;
        }
        if (context.authState != null && context.authState.isAwaitingSecurityAnswer) {
            String question = context.authState.passwordResetQuestion;
            securityQuestion.setText(question == null ? "" : question);
            showPanel(Panel.ANSWER);
            return;
        }
        if (localPanel == Panel.ANSWER || localPanel == Panel.RESET) {
            showPanel(Panel.LOGIN);
        }
    }

    private AuthController auth() {
        return controller.getAuthController();
    }

    private void submitLogin() {
        UiWidgets.apply(controller, auth().login(
                UiWidgets.text(loginUsername),
                UiWidgets.raw(loginPassword),
                stayLoggedIn.isChecked()));
    }

    private void submitForgot() {
        UiWidgets.apply(controller, auth().forgotPassword(
                UiWidgets.text(forgotUsername),
                UiWidgets.text(forgotEmail)));
    }

    private void submitAnswer() {
        UiWidgets.apply(controller, auth().answer(UiWidgets.raw(securityAnswer)));
    }

    private void submitReset() {
        UiWidgets.apply(controller, auth().resetPassword(
                UiWidgets.raw(newPassword),
                UiWidgets.raw(newPasswordConfirm)));
    }

    private void cancelRecovery() {
        auth().clearPasswordResetState();
        securityAnswer.setText("");
        newPassword.setText("");
        newPasswordConfirm.setText("");
        showPanel(Panel.LOGIN);
        controller.refreshView();
    }

    private void goRegister() {
        UiWidgets.apply(controller, controller.exitMenu());
    }

    private void quit() {
        controller.quit();
    }

    @Override
    public void show(UiViewContext context) {
        this.controller = context.controller;
        syncFromAuthState(context);
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
