package model.storage.collection;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import model.data.plant.PlantType;
import model.data.zombie.ZombieType;

public class Collection {

    public static final List<PlantType> DEFAULT_STARTER_PLANTS = List.of(
            PlantType.Sunflower, PlantType.PeaShooter, PlantType.Repeater);

    private final Set<PlantType> unlockedPlants = new HashSet<>();
    private final Set<ZombieType> unlockedZombies = new HashSet<>();

    public void unlockPlant(PlantType type) {
        unlockedPlants.add(type);
    }

    public void unlockPlants(List<PlantType> types) {
        unlockedPlants.addAll(types);
    }

    public void unlockStarterPlants() {
        unlockPlants(DEFAULT_STARTER_PLANTS);
    }

    public void unlockZombie(ZombieType type) {
        unlockedZombies.add(type);
    }

    public void unlockZombies(List<ZombieType> types) {
        unlockedZombies.addAll(types);
    }

    public boolean isPlantUnlocked(PlantType type) {
        return unlockedPlants.contains(type);
    }

    public boolean isZombieUnlocked(ZombieType type) {
        return unlockedZombies.contains(type);
    }

    public Set<PlantType> getUnlockedPlants() {
        return unlockedPlants;
    }

    public Set<ZombieType> getUnlockedZombies() {
        return unlockedZombies;
    }
}
