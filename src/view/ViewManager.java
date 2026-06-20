package view;

import controller.InputHandler;

public class ViewManager {
    private final Renderer renderer;
    private final InputHandler inputHandler;
    private final InputListener inputListener;

    public ViewManager(Renderer renderer, InputHandler inputHandler) {
        this.renderer = renderer;
        this.inputHandler = inputHandler;
        this.inputListener = new InputListener(inputHandler);
    }

    public void start() {
        inputListener.listen();
    }
}
