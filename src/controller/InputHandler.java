package controller;

import model.ModelManager;
import view.ViewManager;

public class InputHandler {
    private ModelManager model;
    private ViewManager view;
    private ControllerManager controllerManager;

    public InputHandler(ModelManager model, ViewManager view, ControllerManager controllerManager) {
        this.model = model;
        this.view = view;
        this.controllerManager = controllerManager;
    }

    public void handleInput() {}

}
