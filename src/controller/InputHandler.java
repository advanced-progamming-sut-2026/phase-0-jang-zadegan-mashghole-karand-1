package controller;

import model.ModelManager;
import view.ViewManager;

public class InputHandler {
    private ModelManager model;
    private ViewManager view;

    public InputHandler(ModelManager model, ViewManager view) {
        this.model = model;
        this.view = view;
    }

}
