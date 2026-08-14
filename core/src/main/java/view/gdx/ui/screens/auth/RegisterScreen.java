package view.gdx.ui.screens.auth;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import controller.AuthController;
import controller.CommandResult.CommandResult;
import controller.ControllerManager;
import model.storage.user.SafetyQuestion;
import view.gdx.ui.UiScreen;
import view.gdx.ui.UiViewContext;

public final class RegisterScreen implements UiScreen {
    private final Stage stage;
    private final Label title;
    private final Table content;
    private final Table formPanel;
    private final Table questionPanel;

    private final TextField username;
    private final TextField password;
    private final TextField passwordConfirm;
    private final TextField nickname;
    private final TextField email;
    private final SelectBox<String> gender;

    private final SelectBox<String> questionBox;
    private final TextField answer;
    private final TextField answerConfirm;
    private final Label questionHint;

    private boolean awaitingQuestion;
    private ControllerManager controller;

    public RegisterScreen() {
        stage = new Stage(new ScreenViewport());

        username = AuthWidgets.field("Username", false);
        password = AuthWidgets.field("Password", true);
        passwordConfirm = AuthWidgets.field("Confirm password", true);
        nickname = AuthWidgets.field("Nickname", false);
        email = AuthWidgets.field("Email", false);

        Array<String> genders = new Array<>();
        genders.add("male");
        genders.add("female");
        gender = AuthWidgets.selectBox(genders);

        questionBox = AuthWidgets.selectBox(new Array<>());
        answer = AuthWidgets.field("Answer", false);
        answerConfirm = AuthWidgets.field("Confirm answer", false);
        questionHint = AuthWidgets.body("Pick a security question to finish registration.");
        questionHint.setWrap(true);

        formPanel = buildFormPanel();
        questionPanel = buildQuestionPanel();

        title = AuthWidgets.title("Register");
        content = new Table();

        Table root = new Table();
        root.setFillParent(true);
        root.center();
        root.add(title).padBottom(18f).row();
        root.add(content).width(440f);
        stage.addActor(root);
        showQuestionStep(false);
    }

    private Table buildFormPanel() {
        Table table = new Table();
        TextButton register = AuthWidgets.primary("Continue");
        TextButton login = AuthWidgets.plain("Already have an account");
        TextButton quit = AuthWidgets.plain("Quit");

        AuthWidgets.onChange(register, this::submitRegister);
        AuthWidgets.onChange(login, this::goLogin);
        AuthWidgets.onChange(quit, this::quit);

        table.add(username).growX().height(44f).padBottom(8f).row();
        table.add(password).growX().height(44f).padBottom(8f).row();
        table.add(passwordConfirm).growX().height(44f).padBottom(8f).row();
        table.add(nickname).growX().height(44f).padBottom(8f).row();
        table.add(email).growX().height(44f).padBottom(8f).row();
        table.add(gender).growX().height(44f).padBottom(12f).row();
        table.add(register).growX().height(48f).padBottom(8f).row();
        table.add(login).growX().height(44f).padBottom(8f).row();
        table.add(quit).growX().height(44f).row();
        return table;
    }

    private Table buildQuestionPanel() {
        Table table = new Table();
        TextButton submit = AuthWidgets.primary("Create account");
        TextButton back = AuthWidgets.plain("Back");

        AuthWidgets.onChange(submit, this::submitQuestion);
        AuthWidgets.onChange(back, this::backToForm);

        table.add(questionHint).growX().padBottom(10f).row();
        table.add(questionBox).growX().height(44f).padBottom(8f).row();
        table.add(answer).growX().height(44f).padBottom(8f).row();
        table.add(answerConfirm).growX().height(44f).padBottom(12f).row();
        table.add(submit).growX().height(48f).padBottom(8f).row();
        table.add(back).growX().height(44f).row();
        return table;
    }

    private void showQuestionStep(boolean enabled) {
        awaitingQuestion = enabled;
        title.setText(enabled ? "Security question" : "Register");
        content.clearChildren();
        content.add(enabled ? questionPanel : formPanel).growX();
        content.invalidateHierarchy();
    }

    private void fillQuestions(UiViewContext context) {
        Array<String> items = new Array<>();
        if (context.authState != null && context.authState.questions != null) {
            for (SafetyQuestion question : context.authState.questions) {
                items.add(question.type.question);
            }
        }
        questionBox.setItems(items);
    }

    private AuthController auth() {
        return controller.getAuthController();
    }

    private void submitRegister() {
        CommandResult result = auth().register(
                AuthWidgets.text(username),
                AuthWidgets.raw(password),
                AuthWidgets.raw(passwordConfirm),
                AuthWidgets.text(nickname),
                AuthWidgets.text(email),
                gender.getSelected());
        AuthWidgets.apply(controller, result);
        if (result.isSuccess()) {
            showQuestionStep(true);
        }
    }

    private void submitQuestion() {
        int index = questionBox.getSelectedIndex();
        CommandResult result = auth().pickQuestion(
                index + 1,
                AuthWidgets.raw(answer),
                AuthWidgets.raw(answerConfirm));
        if (result.isSuccess()) {
            awaitingQuestion = false;
            answer.setText("");
            answerConfirm.setText("");
        }
        AuthWidgets.apply(controller, result);
    }

    private void backToForm() {
        auth().clearPendingRegistration();
        answer.setText("");
        answerConfirm.setText("");
        showQuestionStep(false);
        controller.refreshView();
    }

    private void goLogin() {
        awaitingQuestion = false;
        AuthWidgets.apply(controller, controller.enterMenu("login"));
    }

    private void quit() {
        controller.quit();
    }

    @Override
    public void show(UiViewContext context) {
        this.controller = context.controller;
        fillQuestions(context);
        showQuestionStep(awaitingQuestion);
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
