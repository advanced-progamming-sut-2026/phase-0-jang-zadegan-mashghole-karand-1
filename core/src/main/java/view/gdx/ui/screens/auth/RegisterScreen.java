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
import view.gdx.ui.widgets.UiWidgets;

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

        username = UiWidgets.field("Username", false);
        password = UiWidgets.field("Password", true);
        passwordConfirm = UiWidgets.field("Confirm password", true);
        nickname = UiWidgets.field("Nickname", false);
        email = UiWidgets.field("Email", false);

        Array<String> genders = new Array<>();
        genders.add("male");
        genders.add("female");
        gender = UiWidgets.selectBox(genders);

        questionBox = UiWidgets.selectBox(new Array<>());
        answer = UiWidgets.field("Answer", false);
        answerConfirm = UiWidgets.field("Confirm answer", false);
        questionHint = UiWidgets.body("Pick a security question to finish registration.");
        questionHint.setWrap(true);

        formPanel = buildFormPanel();
        questionPanel = buildQuestionPanel();

        title = UiWidgets.title("Register");
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
        TextButton register = UiWidgets.primary("Continue");
        TextButton login = UiWidgets.plain("Already have an account");
        TextButton quit = UiWidgets.plain("Quit");

        UiWidgets.onChange(register, this::submitRegister);
        UiWidgets.onChange(login, this::goLogin);
        UiWidgets.onChange(quit, this::quit);

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
        TextButton submit = UiWidgets.primary("Create account");
        TextButton back = UiWidgets.plain("Back");

        UiWidgets.onChange(submit, this::submitQuestion);
        UiWidgets.onChange(back, this::backToForm);

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
                UiWidgets.text(username),
                UiWidgets.raw(password),
                UiWidgets.raw(passwordConfirm),
                UiWidgets.text(nickname),
                UiWidgets.text(email),
                gender.getSelected());
        UiWidgets.apply(controller, result);
        if (result.isSuccess()) {
            showQuestionStep(true);
        }
    }

    private void submitQuestion() {
        int index = questionBox.getSelectedIndex();
        CommandResult result = auth().pickQuestion(
                index + 1,
                UiWidgets.raw(answer),
                UiWidgets.raw(answerConfirm));
        if (result.isSuccess()) {
            awaitingQuestion = false;
            answer.setText("");
            answerConfirm.setText("");
        }
        UiWidgets.apply(controller, result);
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
        UiWidgets.apply(controller, controller.enterMenu("login"));
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
