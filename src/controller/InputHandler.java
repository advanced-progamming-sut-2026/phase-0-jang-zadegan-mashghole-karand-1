package controller;

import model.ModelManager;
import view.ViewManager;

public class InputHandler {
    private ControllerManager controllerManager;

    public InputHandler(ControllerManager controllerManager) {
        this.controllerManager = controllerManager;
    }

    public void handleInput(String input) {
        controllerManager.sendMessage(input);
    }

}
