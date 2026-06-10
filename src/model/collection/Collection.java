package pvz.model.collection;

import pvz.model.plant.PlantKind;

import java.util.Set;

public class Collection {

    private Set<PlantKind> unlockedPlants;
    private Set<PlantKind> lockedPlants;

    public void unlockPlant(PlantKind kind) {
    }

    public boolean isUnlocked(PlantKind kind) {
        return false;
    }
}
