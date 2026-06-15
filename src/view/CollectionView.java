package view;

import java.util.List;

public class CollectionView {
    private final Renderer renderer;

    public CollectionView(Renderer renderer) {
        this.renderer = renderer;
    }

    public void showPlantList(List<String> plants) {
        renderer.print("=== Plants ===");
        plants.forEach(renderer::print);
    }
    public void showAllPlantList(List<String> plants) {
        renderer.print("=== All Plants ===");
        plants.forEach(renderer::print);
    }

    public void showZombieList(List<String> zombies) {
        renderer.print("=== Zombies ===");
        zombies.forEach(renderer::print);
    }
    public void showAllZombieList(List<String> zombies) {
        renderer.print("=== all Zombies ===");
        zombies.forEach(renderer::print);
    }

//    public void showPlantDetails() {
//
//    }
//
//    public void showZombieDetails() {
//
//    }

    public void showUpgradeSuccess(String plantName) {
        renderer.print("Plant " + plantName + " upgraded successfully.");
    }

    public void showPurchaseSuccess(String plantName) {
        renderer.print("Plant " + plantName + " purchased successfully.");
    }
    public void showError(String error){
        renderer.print("[ERROR]: " + error);
    }
}
