package controller;

import model.service.AuthValidator;
import model.storage.StorageManager;
import model.storage.user.Gender;
import model.storage.user.SafetyQuestion;
import model.storage.user.SafetyQuestionType;
import model.storage.user.User;
import view.ScreenType;
import view.messages.ErrorMessages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import controller.CommandResult.CommandResult;

public class AuthController {

    private final ControllerManager controllerManager;
    private final StorageManager storage;

    private final List<SafetyQuestion> questions;

    private PendingRegistration pendingRegistration;
    private String passwordResetUsername;
    private boolean awaitingSecurityAnswer;
    private boolean awaitingNewPassword;

    public AuthController(ControllerManager controllerManager, StorageManager storage) {
        this.controllerManager = controllerManager;
        this.storage = storage;
        List<SafetyQuestion> available = new ArrayList<>();
        for (SafetyQuestionType type : SafetyQuestionType.values()) {
            available.add(new SafetyQuestion(type, ""));
        }
        this.questions = Collections.unmodifiableList(available);
    }

    public List<SafetyQuestion> getQuestions() {
        return questions;
    }

    public String getPasswordResetQuestion() {
        if (passwordResetUsername == null) {
            return null;
        }
        User user = storage.getUserByUsername(passwordResetUsername);
        if (user == null || user.safetyQuestion == null) {
            return null;
        }
        return user.safetyQuestion.type.question;
    }

    public boolean isAwaitingSecurityAnswer() {
        return awaitingSecurityAnswer;
    }

    public boolean isAwaitingNewPassword() {
        return awaitingNewPassword;
    }

    public CommandResult register(String username, String password, String passwordConfirm, String nickName,
            String email, String gender) {
        CommandResult screenCheck = controllerManager.requireScreen(ScreenType.REGISTER);
        if (screenCheck != null) {
            return screenCheck;
        }
        CommandResult loggedInCheck = controllerManager.requireNotLoggedIn();
        if (loggedInCheck != null) {
            return loggedInCheck;
        }

        String error = AuthValidator.validateUsername(username);
        if (error != null) {
            return failure(error);
        }
        if (storage.usernameExists(username)) {
            return failure(ErrorMessages.USERNAME_TAKEN.getMessage());
        }

        error = AuthValidator.validatePassword(password);
        if (error != null) {
            return failure(error);
        }

        error = AuthValidator.validatePasswordMatch(password, passwordConfirm);
        if (error != null) {
            return failure(error);
        }

        error = AuthValidator.validateNickname(nickName);
        if (error != null) {
            return failure(error);
        }

        error = AuthValidator.validateEmail(email);
        if (error != null) {
            return failure(error);
        }

        error = AuthValidator.validateGender(gender);
        if (error != null) {
            return failure(error);
        }

        Gender parsedGender = AuthValidator.parseGender(gender).equals("male") ? Gender.MALE : Gender.FEMALE;
        pendingRegistration = new PendingRegistration(username, password, email, nickName, parsedGender);
        return success("Registration info accepted. Pick a security question to finish creating your account.");
    }

    public CommandResult pickQuestion(int questionNum, String answer, String answerConfirm) {
        CommandResult screenCheck = controllerManager.requireScreen(ScreenType.REGISTER);
        if (screenCheck != null) {
            return screenCheck;
        }
        if (pendingRegistration == null) {
            return failure("Complete registration first.");
        }
        if (questionNum < 1 || questionNum > questions.size()) {
            return failure("Invalid security question number.");
        }

        if (answer == null || answer.isEmpty()) {
            return failure("Security answer cannot be empty.");
        }

        String error = AuthValidator.validatePasswordMatch(answer, answerConfirm);
        if (error != null) {
            return failure(error);
        }

        SafetyQuestion selectedQuestion = questions.get(questionNum - 1);
        SafetyQuestion userQuestion = new SafetyQuestion(selectedQuestion.type, answer);

        boolean registered = storage.register(
                pendingRegistration.username,
                pendingRegistration.password,
                pendingRegistration.email,
                pendingRegistration.nickname,
                pendingRegistration.gender,
                userQuestion);

        pendingRegistration = null;

        if (!registered) {
            return failure(ErrorMessages.USERNAME_TAKEN.getMessage());
        }

        controllerManager.setScreen(ScreenType.LOGIN);
        return success("Account created successfully. Please log in.");
    }

    public CommandResult login(String username, String password, boolean stayLoggedIn) {
        CommandResult screenCheck = controllerManager.requireScreen(ScreenType.LOGIN);
        if (screenCheck != null) {
            return screenCheck;
        }
        CommandResult loggedInCheck = controllerManager.requireNotLoggedIn();
        if (loggedInCheck != null) {
            return loggedInCheck;
        }

        if (storage.login(username, password, stayLoggedIn)) {
            storage.saveProgress();
            controllerManager.setScreen(ScreenType.MAIN);
            return success("Welcome back, " + username + "!");
        }
        return failure(ErrorMessages.LOGIN_FAILED.getMessage());
    }

    public CommandResult forgotPassword(String username, String email) {
        CommandResult screenCheck = controllerManager.requireScreen(ScreenType.LOGIN);
        if (screenCheck != null) {
            return screenCheck;
        }

        awaitingSecurityAnswer = false;
        awaitingNewPassword = false;
        passwordResetUsername = null;

        User user = storage.getUserByUsername(username);
        if (user == null || user.email == null || !user.email.equalsIgnoreCase(email)) {
            return failure(ErrorMessages.INVALID_SECURITY_ANSWER.getMessage());
        }

        passwordResetUsername = username;
        awaitingSecurityAnswer = true;
        return success("Answer your security question: " + user.safetyQuestion.type.question);
    }

    public CommandResult answer(String answer) {
        CommandResult screenCheck = controllerManager.requireScreen(ScreenType.LOGIN);
        if (screenCheck != null) {
            return screenCheck;
        }
        if (!awaitingSecurityAnswer || passwordResetUsername == null) {
            return failure("Start password recovery with forget password first.");
        }

        User user = storage.getUserByUsername(passwordResetUsername);
        if (user == null || user.safetyQuestion == null) {
            clearPasswordResetState();
            return failure(ErrorMessages.INVALID_SECURITY_ANSWER.getMessage());
        }

        if (!user.safetyQuestion.answer.equals(answer)) {
            clearPasswordResetState();
            controllerManager.setScreen(ScreenType.LOGIN);
            return failure(ErrorMessages.INVALID_SECURITY_ANSWER.getMessage());
        }

        awaitingSecurityAnswer = false;
        awaitingNewPassword = true;
        return success("Security answer accepted. Enter a new password with: "
                + "reset password -p <password> <password_confirm>");
    }

    public CommandResult resetPassword(String password, String passwordConfirm) {
        CommandResult screenCheck = controllerManager.requireScreen(ScreenType.LOGIN);
        if (screenCheck != null) {
            return screenCheck;
        }
        if (!awaitingNewPassword || passwordResetUsername == null) {
            return failure("Verify your security answer before resetting your password.");
        }

        String error = AuthValidator.validatePassword(password);
        if (error != null) {
            return failure(error);
        }

        error = AuthValidator.validatePasswordMatch(password, passwordConfirm);
        if (error != null) {
            return failure(error);
        }

        if (!storage.updatePassword(passwordResetUsername, password)) {
            clearPasswordResetState();
            return failure(ErrorMessages.INVALID_SECURITY_ANSWER.getMessage());
        }

        clearPasswordResetState();
        controllerManager.setScreen(ScreenType.LOGIN);
        return success("Password updated successfully. Please log in with your new password.");
    }

    public void clearPasswordResetState() {
        passwordResetUsername = null;
        awaitingSecurityAnswer = false;
        awaitingNewPassword = false;
    }

    public void clearPendingRegistration() {
        pendingRegistration = null;
    }

    private CommandResult success(String message) {
        return new CommandResult(message, true);
    }

    private CommandResult failure(String message) {
        return new CommandResult(message, false);
    }

    private static class PendingRegistration {
        final String username;
        final String password;
        final String email;
        final String nickname;
        final Gender gender;

        PendingRegistration(String username, String password, String email, String nickname, Gender gender) {
            this.username = username;
            this.password = password;
            this.email = email;
            this.nickname = nickname;
            this.gender = gender;
        }
    }
}
