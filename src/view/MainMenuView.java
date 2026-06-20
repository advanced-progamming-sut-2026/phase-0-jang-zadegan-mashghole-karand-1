package view;

public class MainMenuView {
    private final Renderer renderer;

    public MainMenuView(Renderer renderer) {
        this.renderer = renderer;
    }
    public void showWelcome(){
        renderer.print("=== Main Menu ===\n" +
                "1. Play\n" +
                "2. Settings\n" +
                "3. News\n" +
                "4. Profile\n" +
                "5. Logout");
    }
    public void showLoggedOut(){
        renderer.print("Logged out successfully. Returning to register menu.");
    }
}
