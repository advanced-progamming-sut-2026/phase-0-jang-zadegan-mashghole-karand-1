package view;

public class ProfileView {
    private final Renderer renderer;

    public ProfileView(Renderer renderer) {
        this.renderer = renderer;
    }

    public void showProfile(String username, String nickname, int gamesPlayed,
                            int coins, int gems, int levelsCleared, int maxMoePoints) {
        renderer.print("=== Profile ===");
        renderer.print("Username  : " + username);
        renderer.print("Nickname  : " + nickname);
        renderer.print("Games     : " + gamesPlayed);
        renderer.print("Coins     : " + coins);
        renderer.print("Gems      : " + gems);
        renderer.print("Levels    : " + levelsCleared);
        renderer.print("Max Score : " + maxMoePoints);
    }

    public void showUpdateSuccess(String field, String newValue) {
        renderer.print(field + " updated to: " + newValue);
    }
    public void showError(String error){
        renderer.print("[ERROR] " + error);
    }

}
