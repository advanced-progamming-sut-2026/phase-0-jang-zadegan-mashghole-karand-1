package model.systems;

import model.core.GameState;
import model.data.projectile.Projectile;
import model.data.zombie.Zombie;
import model.data.zombie.SandstormEffect;

public class MovementSystem {

    public void update(GameState state) {
        for (Zombie zombie : state.zombies) {
            float currentSpeed = zombie.hasSandstorm() ? zombie.speed * SandstormEffect.SPEED_MULTIPLIER
                    : zombie.speed;
            float nextX = zombie.position.x - currentSpeed;

            if (zombie.hasSandstorm() && nextX <= zombie.getSandstorm().finalX) {
                zombie.position.x -= zombie.getSandstorm().finalX;
                zombie.clearSandstorm();
            } else {
                zombie.position.x = nextX;
            }
        }

        for (Projectile projectile : state.projectiles) {
            projectile.position.x += projectile.speed;
        }

        state.projectiles.removeIf(p -> p.position.x > GameState.SCREEN_WIDTH);
    }
}