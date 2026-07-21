package model.data.zombie.abilities.runtime;

import model.board.Tile;
import model.core.EventBus;
import model.core.GameState;
import model.data.plant.Plant;
import model.data.plant.PlantType;
import model.data.projectile.Projectile;
import model.data.projectile.ProjectileType;
import model.data.zombie.Zombie;
import model.data.zombie.abilities.config.ZombieAbilityConfig;

public class ZombieSnorkelAbility implements ZombieAbilityConfig {
    private boolean snorkeling = false;

    @Override
    public boolean passProjectiles(Zombie zombie, Projectile projectile) {
        return snorkeling && projectile.type != null &&
                projectile.type != ProjectileType.CABBAGE &&
                projectile.type != ProjectileType.KERNEL &&
                projectile.type != ProjectileType.BUTTER&&
                projectile.type != ProjectileType.MELON &&
                projectile.type != ProjectileType.ICE_MELON&&
                projectile.type != ProjectileType.PEPPER;
    }

    @Override
    public void onTick(Zombie zombie, GameState state, EventBus bus) {
        Tile tileOn = state.getBoard().getTile(zombie.row, zombie.col);
        Plant plantOnTile = state.getPlantAt(zombie.row, zombie.col);
        if(tileOn.isWater() && plantOnTile == null){
            snorkeling = true;
        }
        else {
            snorkeling = false;
        }
    }

    @Override
    public ZombieAbilityConfig createInstance(Zombie zombie) {
        return new ZombieSnorkelAbility();
    }
}
