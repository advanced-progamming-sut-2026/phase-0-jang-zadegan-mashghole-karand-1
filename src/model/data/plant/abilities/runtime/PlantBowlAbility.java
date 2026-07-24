package model.data.plant.abilities.runtime;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import model.core.EventBus;
import model.core.GameState;
import model.data.plant.Plant;
import model.data.plant.abilities.config.PlantAbilityConfig;
import model.data.zombie.Zombie;

public class PlantBowlAbility implements PlantAbilityConfig {
    private static final float SPEED = 8f;
    private static final float HIT_RADIUS = 35f;
    private static final int HIT_DAMAGE = 1800;
    private static final int EXPLODE_DAMAGE = 1800;
    private static final Random RANDOM = new Random();

    private final BowlingNutMode mode;

    private boolean initialized;
    private float x;
    private float y;
    private float vx;
    private float vy;
    private int hitCount;
    private final Set<Integer> hitZombieIds = new HashSet<>();

    public PlantBowlAbility(BowlingNutMode mode) {
        this.mode = mode;
    }

    @Override
    public PlantAbilityConfig createInstance(Plant plant) {
        return new PlantBowlAbility(mode);
    }

    @Override
    public void onTick(Plant plant, GameState state, EventBus event) {
        if (!plant.isAlive || plant.hp <= 0) {
            return;
        }
        if (!initialized) {
            initialize(plant);
        }

        x += vx;
        y += vy;
        syncGrid(plant);

        if (handleEdgeBounce(plant, state, event)) {
            return;
        }

        if (x > GameState.SCREEN_WIDTH + GameState.CELL_WIDTH) {
            finish(plant, state, event);
            return;
        }

        Zombie target = findHitZombie(state, plant);
        if (target == null) {
            return;
        }

        hitZombieIds.add(target.instanceId);
        applyHit(plant, target, state, event);
    }

    private void initialize(Plant plant) {
        x = plant.col * GameState.CELL_WIDTH + GameState.CELL_WIDTH / 2f;
        y = plant.row * GameState.CELL_HEIGHT + GameState.CELL_HEIGHT / 2f;
        vx = SPEED;
        vy = 0f;
        hitCount = 0;
        initialized = true;
    }

    private void syncGrid(Plant plant) {
        int newCol = (int) (x / GameState.CELL_WIDTH);
        int newRow = (int) (y / GameState.CELL_HEIGHT);
        newCol = Math.max(0, Math.min(GameState.GRID_COLS - 1, newCol));
        newRow = Math.max(0, Math.min(GameState.GRID_ROWS - 1, newRow));
        plant.col = newCol;
        plant.row = newRow;
    }

    private boolean handleEdgeBounce(Plant plant, GameState state, EventBus event) {
        float minY = GameState.CELL_HEIGHT / 2f;
        float maxY = (GameState.GRID_ROWS - 1) * GameState.CELL_HEIGHT + GameState.CELL_HEIGHT / 2f;

        if (y < minY || y > maxY) {
            y = Math.max(minY, Math.min(maxY, y));
            if (mode == BowlingNutMode.GIANT) {
                vy = 0f;
                vx = Math.abs(vx) > 0.01f ? Math.abs(vx) : SPEED;
                syncGrid(plant);
                return false;
            }
            bounceFromWall();
            syncGrid(plant);
            return plant.hp <= 0;
        }
        return false;
    }

    private void bounceFromWall() {
        hitCount++;
        if (hitCount == 1) {
            applyFirstBounce();
        } else {
            rotateNinetyDegrees();
        }
        if (vx < 0) {
            vx = -vx;
        }
        hitZombieIds.clear();
    }

    private Zombie findHitZombie(GameState state, Plant plant) {
        Zombie closest = null;
        float bestDist = Float.MAX_VALUE;
        for (Zombie z : state.zombies) {
            if (!z.isAlive || hitZombieIds.contains(z.instanceId)) {
                continue;
            }
            float dx = z.position.x - x;
            float dy = z.position.y - y;
            float dist = (float) Math.sqrt(dx * dx + dy * dy);
            if (dist <= HIT_RADIUS && dist < bestDist) {
                bestDist = dist;
                closest = z;
            }
        }
        return closest;
    }

    private void applyHit(Plant plant, Zombie zombie, GameState state, EventBus event) {
        if (mode == BowlingNutMode.EXPLODE) {
            explode(plant, state, event);
            return;
        }

        zombie.lastHitBy = plant.type;
        zombie.takeDamage(HIT_DAMAGE);
        if (!zombie.isAlive) {
            zombie.kill(state);
        }

        if (mode == BowlingNutMode.GIANT) {
            return;
        }

        hitCount++;
        if (hitCount == 1) {
            applyFirstBounce();
        } else {
            rotateNinetyDegrees();
        }
        if (vx < 0) {
            vx = -vx;
        }
    }

    private void applyFirstBounce() {
        float component = SPEED / (float) Math.sqrt(2);
        vx = component;
        vy = RANDOM.nextBoolean() ? -component : component;
    }

    private void rotateNinetyDegrees() {
        float oldVx = vx;
        float oldVy = vy;
        if (RANDOM.nextBoolean()) {
            vx = oldVy;
            vy = -oldVx;
        } else {
            vx = -oldVy;
            vy = oldVx;
        }
        normalizeSpeed();
    }

    private void normalizeSpeed() {
        float mag = (float) Math.sqrt(vx * vx + vy * vy);
        if (mag < 0.01f) {
            vx = SPEED;
            vy = 0f;
            return;
        }
        vx = vx / mag * SPEED;
        vy = vy / mag * SPEED;
    }

    private void explode(Plant plant, GameState state, EventBus event) {
        int plantRow = plant.row;
        int plantCol = plant.col;
        List<Zombie> zombies = List.copyOf(state.zombies);
        for (Zombie z : zombies) {
            if (!z.isAlive) {
                continue;
            }
            int zombieCol = (int) (z.position.x / GameState.CELL_WIDTH);
            if (Math.abs(z.row - plantRow) <= 1 && Math.abs(zombieCol - plantCol) <= 1) {
                z.lastHitBy = plant.type;
                z.takeDamage(EXPLODE_DAMAGE);
                if (!z.isAlive) {
                    z.kill(state);
                }
            }
        }
        finish(plant, state, event);
    }

    private void finish(Plant plant, GameState state, EventBus event) {
        plant.kill(state, event);
    }
}
