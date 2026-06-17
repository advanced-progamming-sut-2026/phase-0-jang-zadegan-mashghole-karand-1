package view;

public class MetaView {
    private final Renderer renderer;

    public MetaView(Renderer renderer) {
        this.renderer = renderer;
    }
    public void showCurrentMenu(String menuName){
        renderer.print("Current Menu: " + menuName);
    }
    public void showError(String error){
        renderer.print("[ERROR] " + error);
    }
    public void showInvalidCommand(){
        renderer.print("Invalid Command");
    }
}
