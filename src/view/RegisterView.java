package view;

import java.util.List;

public class RegisterView {
    private final Renderer renderer;

    public RegisterView(Renderer renderer) {
        this.renderer = renderer;
    }
    public void showRegisterSuccess(String  username){
        renderer.print("User " + username + "registered successfully");
    }
    public void showError(String error){
        renderer.print("[ERROR] " + error);
    }
    public void showSecurityQuestion(List<String> questions){
        renderer.print("=== Security Questions ===");
        for (int i =0 ; i < questions.size() ; i++){
            renderer.print((i + 1) + ": " + questions.get(i));
        }
    }
}
