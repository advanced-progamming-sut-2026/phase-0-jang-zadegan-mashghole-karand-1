package view.gdx.catalog;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import model.data.plant.PlantType;
import model.data.projectile.ProjectileType;
import model.data.zombie.ZombieType;

public final class DefaultVisualCatalog implements VisualCatalog {
    private final Map<PlantType, PlantVisualDef> plants = PlantVisualDefs.create();
    private final Map<ZombieType, ZombieVisualDef> zombies = ZombieVisualDefs.create();
    private final Map<ProjectileType, ProjectileVisualDef> projectiles = ProjectileVisualDefs.create();
    @Override
    public PlantVisualDef plant(PlantType type) {
        return plants.get(type);
    }

    @Override
    public ZombieVisualDef zombie(ZombieType type) {
        return zombies.get(type);
    }

    @Override
    public BarrelVisualDef barrel() {
        return new  BarrelVisualDef();
    }

    @Override
    public ProjectileVisualDef projectile(ProjectileType type) {
        return projectiles.get(type);
    }

    @Override
    public Collection<ProjectileVisualDef> allProjectile() {
        return projectiles.values();
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
