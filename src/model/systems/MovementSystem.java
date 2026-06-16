package model.systems;

import model.core.GameState;
import model.data.projectile.Projectile;
import model.data.zombie.Zombie;

public class MovementSystem {

    public void update(GameState state) {
        for (Zombie zombie : state.zombies) {
            zombie.position.x -= zombie.type.baseStats.speed;
        }

        for (Projectile projectile : state.projectiles) {
            projectile.position.x += projectile.speed;
        }

        state.projectiles.removeIf(p -> p.position.x > GameState.SCREEN_WIDTH);
    }
}