package view.gdx.catalog;

import java.util.Collection;

import model.data.plant.PlantType;
import model.data.projectile.ProjectileType;
import model.data.zombie.ZombieType;

public interface VisualCatalog {
    PlantVisualDef plant(PlantType type);

    ZombieVisualDef zombie(ZombieType type);

    BarrelVisualDef barrel();

    ProjectileVisualDef projectile(ProjectileType type);

    Collection<ProjectileVisualDef> allProjectile();

    Collection<PlantVisualDef> allPlants();

    Collection<ZombieVisualDef> allZombies();
}
