package model.systems;

import model.board.IceDirection;
import model.board.Tile;
import model.board.TileType;
import model.core.GameState;
import model.data.projectile.Projectile;
import model.data.zombie.Zombie;
import model.lawnmower.LawnMower;

import java.util.ArrayList;

public class MovementSystem {

    public void update(GameState state) {
        for (Zombie zombie : state.zombies) {
            float currentSpeed = zombie.getCurrentSpeed();
            float nextX = zombie.position.x - currentSpeed;

            if (zombie.hasSandstorm() && nextX <= zombie.getSandstorm().finalX) {
                zombie.position.x -= zombie.getSandstorm().finalX;
                zombie.clearSandstorm();
            } else {
                zombie.position.x = nextX;
            }

            int col = (int) (zombie.position.x / GameState.CELL_WIDTH);
            Tile tile = state.getBoard().getTile(zombie.row, col);

            if (tile != null && tile.getType() == TileType.ICE) {
                IceDirection direction = tile.getDirection();
                int newRow = zombie.row;
                if (direction == IceDirection.UP && zombie.row > 0) {
                    newRow = zombie.row - 1;
                } else if (direction == IceDirection.DOWN && zombie.row < GameState.GRID_ROWS - 1) {
                    newRow = zombie.row + 1;
                }
                if (newRow != zombie.row) {
                    zombie.row = newRow;
                    zombie.position.y = GameState.CELL_HEIGHT * newRow + (GameState.CELL_HEIGHT / 2);
                }
            }
        }
        boolean[] rowHandled = new boolean[GameState.GRID_ROWS];
        for (Zombie z :new ArrayList<>(state.zombies)){
            if (!z.isAlive || z.isHypnotized) continue;
            if (z.position.x > 0) continue;
            if (rowHandled[z.row]) continue;
            rowHandled[z.row] = true;
            LawnMower lawnMower = state.getBoard().getLawnMowers(z.row);
            if (lawnMower!= null && lawnMower.isActive()){
                lawnMower.destroyZombiesInRow(state);
            }else {
                state.gameOver = true;
                return;
            }
        }
        for (Projectile projectile : state.projectiles) {
            projectile.position.x += projectile.speed;
        }

        state.projectiles.removeIf(p -> p.position.x > GameState.SCREEN_WIDTH);
    }
}