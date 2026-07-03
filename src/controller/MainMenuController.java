package controller;

import model.storage.StorageManager;
import view.ScreenType;

public class MainMenuController {

    private final ControllerManager controllerManager;
    private final StorageManager storage;

    public MainMenuController(ControllerManager controllerManager, StorageManager storage) {
        this.controllerManager = controllerManager;
        this.storage = storage;
    }

    public void logout() {
        storage.saveProgress();
        storage.logout();
        controllerManager.getAuthController().clearPasswordResetState();
        controllerManager.setScreen(ScreenType.REGISTER);
        controllerManager.sendMessage("Logged out successfully.");
    }
}
