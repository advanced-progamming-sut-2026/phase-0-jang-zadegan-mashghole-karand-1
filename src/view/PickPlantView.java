package view;

import java.util.List;

public class PickPlantView {
    private final Renderer renderer;

    public PickPlantView(Renderer renderer) {
        this.renderer = renderer;
    }
    public void showAllPlants(List<String> plants){
        renderer.print("=== All Plants ===");
        plants.forEach(renderer::print);
    }
    public void showAvailablePlants(List<String> plants){
        renderer.print("=== Available Plants ===");
        plants.forEach(renderer::print);
    }
    public void showAddPlant(String type){
        renderer.print(type + "added");
    }
    public void showRemovePlant(String type){
        renderer.print(type + "removed");
    }
    public void showBoostPlant(String type){
        renderer.print(type + "boosted");
    }
    public void showError(String error){
        renderer.print("[ERROR] " + error);
    }
}
