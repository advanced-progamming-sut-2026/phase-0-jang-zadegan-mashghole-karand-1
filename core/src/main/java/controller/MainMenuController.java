package controller;

import controller.CommandResult.CommandResult;
import model.storage.StorageManager;
import view.ScreenType;

public class MainMenuController {

    private final ControllerManager controllerManager;
    private final StorageManager storage;

    public MainMenuController(ControllerManager controllerManager, StorageManager storage) {
        this.controllerManager = controllerManager;
        this.storage = storage;
    }

    public CommandResult logout() {
        CommandResult screenCheck = controllerManager.requireScreen(ScreenType.MAIN);
        if (screenCheck != null) {
            return screenCheck;
        }
        CommandResult loggedInCheck = controllerManager.requireLoggedIn();
        if (loggedInCheck != null) {
            return loggedInCheck;
        }

        storage.saveProgress();
        storage.logout();
        controllerManager.initShopForCurrentUser();
        controllerManager.clearCurrentMenu();
        controllerManager.getAuthController().clearPasswordResetState();
        controllerManager.setScreen(ScreenType.REGISTER);
        return new CommandResult("Logged out successfully.", true);
    }
}
