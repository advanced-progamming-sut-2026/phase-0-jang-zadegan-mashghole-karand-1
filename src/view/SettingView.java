package view;

public class SettingView {
    private final Renderer renderer;

    public SettingView(Renderer renderer) {
        this.renderer = renderer;
    }

    public void showSetting(int difficultyLevel) {
        renderer.print("=== Setting ===");
        renderer.print("Difficulty: " + difficultyLevel + " (1-5)");
    }

    public void showDifficultyChanged(int level) {
        renderer.print("Difficulty changed to " + level + ".");
    }
}
