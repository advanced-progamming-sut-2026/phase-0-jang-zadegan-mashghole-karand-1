package model.systems;

import model.board.IceDirection;
import model.board.Tile;
import model.board.TileType;
import model.core.EventBus;
import model.core.GameState;
import model.core.SessionEnd;
import model.data.brain.Brain;
import model.data.plant.abilities.config.Direction;
import model.data.projectile.HomingProjectile;
import model.data.projectile.PiercingProjectile;
import model.data.projectile.Projectile;
import model.data.zombie.Zombie;
import model.event.events.GameOverReason;
import model.lawnmower.LawnMower;

import java.util.ArrayList;

public class MovementSystem {

    public void update(GameState state, EventBus eventBus) {
        for (Zombie zombie : state.zombies) {
            if (!zombie.isAlive || zombie.stunned || !zombie.canMove())
                continue;
            float currentSpeed = zombie.getCurrentSpeed();
            float nextX = zombie.position.x - currentSpeed;

            if (zombie.hasSandstorm() && nextX <= zombie.getSandstorm().finalX) {
                zombie.position.x -= zombie.getSandstorm().finalX;
                zombie.clearSandstorm();
            } else {
                zombie.position.x = nextX;
            }

            int col = (int) (zombie.position.x / GameState.CELL_WIDTH);
            zombie.col = Math.max(0, Math.min(GameState.GRID_COLS - 1, col));
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
        for (Zombie z : new ArrayList<>(state.zombies)) {
            if (!z.isAlive || z.isHypnotized)
                continue;
            if (z.position.x > 0)
                continue;
            if (rowHandled[z.row])
                continue;
            rowHandled[z.row] = true;

            if (state.brainsMode) {
                Brain brain = state.getBrainAtRow(z.row);
                if (brain != null && !brain.isCollected()) {
                    brain.collect();
                }
                continue;
            }

            LawnMower lawnMower = state.getBoard().getLawnMowers(z.row);
            if (lawnMower != null && lawnMower.isActive()) {
                lawnMower.destroyZombiesInRow(state);
            } else {
                SessionEnd.lose(state, eventBus, GameOverReason.LAWN_BREACHED);
                return;
            }
        }

        for (Projectile projectile : state.projectiles) {
            if (projectile instanceof HomingProjectile homing) {
                homing.updateMovement();
            } else {
                Direction direction = projectile.direction;
                float dx = direction.vx * projectile.speed;
                float dy = direction.vy * projectile.speed;
                if (direction.vx != 0 && direction.vy != 0) {
                    float inv = 1f / (float) Math.sqrt(2);
                    dx *= inv;
                    dy *= inv;
                }
                if (projectile instanceof PiercingProjectile pierce && pierce.maxRange >= 0) {
                    float dist = (float) Math.hypot(dx, dy);
                    pierce.traveledDistance += dist;
                }
                projectile.position.x += dx;
                projectile.position.y += dy;
            }
            projectile.row = (int) (projectile.position.y / GameState.CELL_HEIGHT);
            projectile.col = (int) (projectile.position.x / GameState.CELL_WIDTH);
        }

        state.projectiles.removeIf(p -> (p.position.x > GameState.SCREEN_WIDTH ||
                p.position.y > GameState.SCREEN_HEIGHT ||
                p.position.x < 0 ||
                p.position.y < 0 ||
                p.row < 0 ||
                p.row >= GameState.GRID_ROWS)||
                (p instanceof PiercingProjectile pp
                        && pp.maxRange >= 0
                        && pp.traveledDistance >= pp.maxRange * GameState.CELL_WIDTH));
        state.zombies.removeIf(z -> z.isHypnotized && z.position.x > GameState.SCREEN_WIDTH);
        state.zombies.removeIf(z -> z.position.x < -GameState.CELL_WIDTH);
    }
}
