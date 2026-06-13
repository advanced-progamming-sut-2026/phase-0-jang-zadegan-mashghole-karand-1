package view;

public class ViewManager {
    private final Renderer renderer;
    private final InputListener inputListener;


    public ViewManager(Renderer renderer, InputListener inputListener) {
        this.renderer = renderer;
        this.inputListener = inputListener;
    }
    public void start(){
        inputListener.listen();
    }
}
