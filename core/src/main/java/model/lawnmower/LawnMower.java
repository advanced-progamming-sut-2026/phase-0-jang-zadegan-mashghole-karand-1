package model.lawnmower;

import model.core.GameState;
import model.data.zombie.Zombie;

public class LawnMower {

    private int row;
    private boolean active;

    public LawnMower(int row) {
        this.row = row;
        this.active = true;
    }

    public void destroyZombiesInRow(GameState gameState) {
        if (!active)
            return;
        active = false;
        for (Zombie z : gameState.getZombies()) {
            if (z.isAlive && z.row == row) {
                z.killedByLawnMower = true;
                z.lastHitBy = null;
                z.kill(gameState);
            }
        }
        gameState.removeDeadZombies();
    }

    public boolean isActive() {
        return active;
    }

    public void deactivate() {
        active = false;
    }
}
