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


//    public void showPlantDetails(String name, String category, int health,
//                                 int damage, int cost, int cooldown,
//                                 int level, List<String> tags) {
//        renderer.print("=== Plant: " + name + " ===");
//        renderer.print("Category : " + category);
//        renderer.print("Health   : " + health);
//        renderer.print("Damage   : " + damage);
//        renderer.print("Cost     : " + cost + " sun");
//        renderer.print("Cooldown : " + cooldown + "s");
//        renderer.print("Level    : " + level);
//        renderer.print("Tags: " + String.join(", ", tags));
//    }
//
//    public void showZombieDetails(String name, int health, int damage,
//                                  float speed, int waveCost,
//                                  String armor) {
//        renderer.print("=== Zombie: " + name + " ===");
//        renderer.print("HitPoints: " + health);
//        renderer.print("EatDPS   : " + damage);
//        renderer.print("Speed    : " + speed);
//        renderer.print("WaveCost : " + waveCost);
//        renderer.print("Armor    : " + (armor.isEmpty() ? "none" : armor));
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
