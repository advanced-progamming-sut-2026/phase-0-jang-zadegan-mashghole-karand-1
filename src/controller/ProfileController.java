package controller;

import controller.CommandResult.CommandResult;
import model.service.AuthValidator;
import model.storage.StorageManager;
import view.MenuType;
import view.ScreenType;
import view.messages.ErrorMessages;

public class ProfileController {

    private final ControllerManager controllerManager;
    private final StorageManager storage;

    public ProfileController(ControllerManager controllerManager, StorageManager storage) {
        this.controllerManager = controllerManager;
        this.storage = storage;
    }

    public CommandResult refreshInfo() {
        CommandResult menuCheck = requireProfileMenu();
        if (menuCheck != null) {
            return menuCheck;
        }
        return success("Profile refreshed.");
    }

    public CommandResult changeUsername(String newUsername) {
        CommandResult menuCheck = requireProfileMenu();
        if (menuCheck != null) {
            return menuCheck;
        }

        String error = AuthValidator.validateUsername(newUsername);
        if (error != null) {
            return failure(error);
        }
        if (newUsername.equals(storage.getCurrentUsername())) {
            return failure(ErrorMessages.USERNAME_SAME_AS_CURRENT.getMessage());
        }
        if (storage.usernameExists(newUsername)) {
            return failure(ErrorMessages.USERNAME_TAKEN.getMessage());
        }
        if (!storage.changeUsername(newUsername)) {
            return failure("Could not change username.");
        }
        storage.saveProgress();
        return success("Username updated to " + newUsername + ".");
    }

    public CommandResult changeNickname(String nickname) {
        CommandResult menuCheck = requireProfileMenu();
        if (menuCheck != null) {
            return menuCheck;
        }

        String error = AuthValidator.validateNickname(nickname);
        if (error != null) {
            return failure(error);
        }
        if (nickname.equals(storage.getCurrentUser().nickname)) {
            return failure(ErrorMessages.NICKNAME_SAME_AS_CURRENT.getMessage());
        }
        storage.changeNickname(nickname);
        storage.saveProgress();
        return success("Nickname updated.");
    }

    public CommandResult changeEmail(String email) {
        CommandResult menuCheck = requireProfileMenu();
        if (menuCheck != null) {
            return menuCheck;
        }

        String error = AuthValidator.validateEmail(email);
        if (error != null) {
            return failure(error);
        }
        if (email.equalsIgnoreCase(storage.getCurrentUser().email)) {
            return failure(ErrorMessages.EMAIL_SAME_AS_CURRENT.getMessage());
        }
        storage.changeEmail(email);
        storage.saveProgress();
        return success("Email updated.");
    }

    public CommandResult changePassword(String oldPassword, String newPassword) {
        CommandResult menuCheck = requireProfileMenu();
        if (menuCheck != null) {
            return menuCheck;
        }

        String error = AuthValidator.validatePassword(newPassword);
        if (error != null) {
            return failure(error);
        }
        if (newPassword.equals(oldPassword)) {
            return failure(ErrorMessages.NEW_PASSWORD_SAME_AS_CURRENT.getMessage());
        }
        if (!storage.changeProfilePassword(oldPassword, newPassword)) {
            return failure(ErrorMessages.OLD_PASSWORD_INCORRECT.getMessage());
        }
        storage.saveProgress();
        return success("Password updated.");
    }

    private CommandResult requireProfileMenu() {
        CommandResult screenCheck = controllerManager.requireScreen(ScreenType.MAIN);
        if (screenCheck != null) {
            return screenCheck;
        }
        CommandResult loggedInCheck = controllerManager.requireLoggedIn();
        if (loggedInCheck != null) {
            return loggedInCheck;
        }
        if (controllerManager.getCurrentMenu() != MenuType.PROFILE) {
            return failure("Open the profile menu first with: menu enter profile");
        }
        return null;
    }

    private CommandResult success(String message) {
        return new CommandResult(message, true);
    }

    private CommandResult failure(String message) {
        return new CommandResult(message, false);
    }
}
