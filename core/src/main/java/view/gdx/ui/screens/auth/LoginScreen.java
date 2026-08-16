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

        loginUsername = AuthWidgets.field("Username", false);
        loginPassword = AuthWidgets.field("Password", true);
        stayLoggedIn = AuthWidgets.checkBox("Stay logged in");

        forgotUsername = AuthWidgets.field("Username", false);
        forgotEmail = AuthWidgets.field("Email", false);

        securityQuestion = AuthWidgets.body("");
        securityQuestion.setWrap(true);
        securityAnswer = AuthWidgets.field("Security answer", false);

        newPassword = AuthWidgets.field("New password", true);
        newPasswordConfirm = AuthWidgets.field("Confirm password", true);

        loginPanel = buildLoginPanel();
        forgotPanel = buildForgotPanel();
        answerPanel = buildAnswerPanel();
        resetPanel = buildResetPanel();

        title = AuthWidgets.title("Login");
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
        TextButton login = AuthWidgets.primary("Login");
        TextButton forgot = AuthWidgets.secondary("Forgot password");
        TextButton register = AuthWidgets.plain("Create account");
        TextButton quit = AuthWidgets.plain("Quit");

        AuthWidgets.onChange(login, this::submitLogin);
        AuthWidgets.onChange(forgot, () -> showPanel(Panel.FORGOT));
        AuthWidgets.onChange(register, this::goRegister);
        AuthWidgets.onChange(quit, this::quit);

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
        TextButton submit = AuthWidgets.primary("Continue");
        TextButton back = AuthWidgets.plain("Back");

        AuthWidgets.onChange(submit, this::submitForgot);
        AuthWidgets.onChange(back, () -> showPanel(Panel.LOGIN));

        table.add(AuthWidgets.body("Recover password")).padBottom(10f).row();
        table.add(forgotUsername).growX().height(44f).padBottom(8f).row();
        table.add(forgotEmail).growX().height(44f).padBottom(12f).row();
        table.add(submit).growX().height(48f).padBottom(8f).row();
        table.add(back).growX().height(44f).row();
        return table;
    }

    private Table buildAnswerPanel() {
        Table table = new Table();
        TextButton submit = AuthWidgets.primary("Submit answer");
        TextButton cancel = AuthWidgets.plain("Cancel");

        AuthWidgets.onChange(submit, this::submitAnswer);
        AuthWidgets.onChange(cancel, this::cancelRecovery);

        table.add(AuthWidgets.body("Security question")).padBottom(8f).row();
        table.add(securityQuestion).growX().padBottom(10f).row();
        table.add(securityAnswer).growX().height(44f).padBottom(12f).row();
        table.add(submit).growX().height(48f).padBottom(8f).row();
        table.add(cancel).growX().height(44f).row();
        return table;
    }

    private Table buildResetPanel() {
        Table table = new Table();
        TextButton submit = AuthWidgets.primary("Reset password");
        TextButton cancel = AuthWidgets.plain("Cancel");

        AuthWidgets.onChange(submit, this::submitReset);
        AuthWidgets.onChange(cancel, this::cancelRecovery);

        table.add(AuthWidgets.body("Choose a new password")).padBottom(10f).row();
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
        AuthWidgets.apply(controller, auth().login(
                AuthWidgets.text(loginUsername),
                AuthWidgets.raw(loginPassword),
                stayLoggedIn.isChecked()));
    }

    private void submitForgot() {
        AuthWidgets.apply(controller, auth().forgotPassword(
                AuthWidgets.text(forgotUsername),
                AuthWidgets.text(forgotEmail)));
    }

    private void submitAnswer() {
        AuthWidgets.apply(controller, auth().answer(AuthWidgets.raw(securityAnswer)));
    }

    private void submitReset() {
        AuthWidgets.apply(controller, auth().resetPassword(
                AuthWidgets.raw(newPassword),
                AuthWidgets.raw(newPasswordConfirm)));
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
        AuthWidgets.apply(controller, controller.exitMenu());
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
