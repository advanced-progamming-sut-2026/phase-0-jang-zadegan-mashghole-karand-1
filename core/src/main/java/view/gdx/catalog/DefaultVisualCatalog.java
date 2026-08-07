package view.gdx.catalog;

import java.util.Collection;
import java.util.Map;

import model.data.plant.PlantType;
import model.data.zombie.ZombieType;

public final class DefaultVisualCatalog implements VisualCatalog {
    private final Map<PlantType, PlantVisualDef> plants = PlantVisualDefs.create();
    private final Map<ZombieType, ZombieVisualDef> zombies = ZombieVisualDefs.create();

    @Override
    public PlantVisualDef plant(PlantType type) {
        return plants.get(type);
    }

    @Override
    public ZombieVisualDef zombie(ZombieType type) {
        return zombies.get(type);
    }

    @Override
    public Collection<PlantVisualDef> allPlants() {
        return plants.values();
    }

    @Override
    public Collection<ZombieVisualDef> allZombies() {
        return zombies.values();
    }
}
