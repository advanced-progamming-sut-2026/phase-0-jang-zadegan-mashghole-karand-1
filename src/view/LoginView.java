package view;

public class LoginView {
    private final Renderer renderer;

    public LoginView(Renderer renderer) {
        this.renderer = renderer;
    }
    public void showLoginSuccess(String username){
        renderer.print("Welcome " + username + ":)");
    }
    public void showNewPassword(){
        renderer.print("Enter your new password: ");
    }
    public void showSecurityQuestion(String question){
        renderer.print("Security Question: " + question);
    }
    public void showError(String error){
        renderer.print("[ERROR] " + error);
    }
}
