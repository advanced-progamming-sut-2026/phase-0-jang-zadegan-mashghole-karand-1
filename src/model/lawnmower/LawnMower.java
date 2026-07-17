package model.lawnmower;

import model.core.GameState;
import model.data.zombie.Zombie;
import model.data.zombie.ZombieType;

public class LawnMower {

    private int row;
    private boolean active;

    public LawnMower(int row) {
        this.row = row;
        this.active = true;
    }

    public void destroyZombiesInRow(GameState gameState) {
        if (!active) return;
        active = false;
        for (Zombie z : gameState.getZombies()){
            //handle boss next phase
            if (z.isAlive && z.row == row){
                z.isAlive = false;
                z.onDeath(gameState);
            }
        }
        gameState.zombies.removeIf(z -> !z.isAlive);
    }

    public boolean isActive() {
        return active;
    }
}
