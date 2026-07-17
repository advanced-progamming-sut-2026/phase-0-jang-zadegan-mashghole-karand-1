package model.data.zombie.abilities.runtime;

import model.board.IceDirection;
import model.board.Tile;
import model.board.TileType;
import model.core.EventBus;
import model.core.GameState;
import model.data.plant.Plant;
import model.data.plant.PlantType;
import model.data.zombie.Zombie;
import model.data.zombie.abilities.config.ZombieAbilityConfig;

import java.util.Comparator;
import java.util.List;

public class ZombieFlyAbility implements ZombieAbilityConfig {

    @Override
    public void onTick(Zombie zombie, GameState state, EventBus bus) {
        Plant plantForward = state.getPlants().stream().filter(p -> p.row == zombie.row && p.col <= zombie.col)
                .max(Comparator.comparingInt(p -> p.col)).orElse(null);
        Tile tileForward = state.getBoard().getTile(zombie.row, zombie.col-1);
        if(tileForward == null && plantForward == null) return;
        if( plantForward != null && zombie.position.x - plantForward.getX() <= GameState.ZOMBIE_ATTACK_RANGE && isJumpable(plantForward)){
            jump(zombie, 2);
        }
        if(tileForward != null && tileForward.getType()== TileType.ICE && tileForward.getDirection()!= IceDirection.NONE){
            jump(zombie, 2);
        }
    }

    @Override
    public ZombieAbilityConfig createInstance(Zombie zombie) {
        return new ZombieFlyAbility();
    }

    private boolean isJumpable(Plant plant){
        return switch (plant.type) {
            case Wall_nut, Explode_o_nut, Pumpkin, Endurian, Sweet_Potato, Sun_Bean, Garlic, Potato_Mine,
                 Primal_Potato_Mine, Squash, Iceberg_Lettuce -> true;
            default -> false;
        };
    }

    private void jump(Zombie zombie, int len) {
        int newCol = zombie.col - len;
        if (newCol < 0) return; // or clamp
        zombie.col = newCol;
        zombie.position.x -= GameState.CELL_WIDTH * len;
    }
}
