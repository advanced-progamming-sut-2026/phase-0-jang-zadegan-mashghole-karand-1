package model.data.zombie.abilities.runtime;

import model.core.EventBus;
import model.core.GameState;
import model.data.plant.abilities.config.Direction;
import model.data.projectile.Projectile;
import model.data.zombie.Zombie;
import model.data.zombie.abilities.config.ZombieAbilityConfig;

import java.util.List;

public class ZombieJesterAbility implements ZombieAbilityConfig {

    private boolean isSpinning = false;
    @Override
    public void onTick(Zombie zombie, GameState state, EventBus bus) {
        List<Projectile> projectilesInRow = state.getProjectiles().stream()
                .filter(p -> p.row == zombie.row && p.speed>0).toList();
        if(projectilesInRow.isEmpty()){
            if(isSpinning){
                zombie.speed /= 1.3f;
                isSpinning = false;
            }
            return;
        }
        if(!isSpinning){
            isSpinning = true;
            zombie.speed *= 1.3f;
        }
        int DEFENCE_RADIUS = 35;
        for(Projectile projectile : projectilesInRow){
            if((zombie.position.x - projectile.position.x)<=DEFENCE_RADIUS && zombie.position.x-projectile.position.x>=0){
                projectile.speed *= -1;
                projectile.direction = Direction.BACK;
            }
        }
    }

    @Override
    public ZombieAbilityConfig createInstance(Zombie zombie) {
        return new ZombieJesterAbility();
    }
}
