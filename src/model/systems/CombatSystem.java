package model.systems;

import java.util.Iterator;

import model.core.EventBus;
import model.core.GameState;
import model.data.plant.Plant;
import model.data.projectile.Projectile;
import model.data.zombie.Zombie;
import model.events.PlantDiedEvent;
import model.events.ZombieDiedEvent;

public class CombatSystem {
    public EventBus eventBus;

    public CombatSystem(EventBus bus) {
        this.eventBus = bus;
    }

    public void update(GameState state, EventBus eventBus) {
        Iterator<Projectile> projIter = state.projectiles.iterator();
        while (projIter.hasNext()) {
            Projectile p = projIter.next();

            Iterator<Zombie> zombieIter = state.zombies.iterator();
            while (zombieIter.hasNext()) {
                Zombie z = zombieIter.next();

                if (z.row == p.row && Math.abs(z.position.x - p.position.x) < GameState.PROJECTILE_HIT_RADIUS) {
                    z.hp -= p.damage;
                    projIter.remove();

                    if (z.hp <= 0) {
                        eventBus.publish(new ZombieDiedEvent(z));
                    }
                    break;
                }
            }
        }

        Iterator<Zombie> zombieIter = state.zombies.iterator();
        while (zombieIter.hasNext()) {
            Zombie z = zombieIter.next();

            Plant targetPlant = findPlantAt(state, z.row, z.position.x);
            if (targetPlant != null) {
                targetPlant.hp -= z.type.baseStats.eatDPS / 10;
                if (targetPlant.hp <= 0) {
                    state.plants.remove(targetPlant);
                    eventBus.publish(new PlantDiedEvent(targetPlant));
                }
                break;
            }
        }

        // events should be triggered here!
        state.zombies.removeIf(z -> z.hp <= 0);

        state.plants.removeIf(p -> p.hp <= 0);
    }

    private Plant findPlantAt(GameState state, int row, float x) {
        int col = (int) (x / GameState.CELL_WIDTH);
        return state.getPlantAt(row, col);
    }
}