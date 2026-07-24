package model.data.zombie.abilities.runtime;

import model.core.EventBus;
import model.core.GameState;
import model.data.Barrel.Barrel;
import model.data.plant.Plant;
import model.data.zombie.Zombie;
import model.data.zombie.abilities.config.ZombieAbilityConfig;
import model.event.events.BarrelCreatedEvent;
import model.event.events.PlantDiedEvent;
import model.event.events.ZombieDiedEvent;

public class ZombieBarrelRollerAbility implements ZombieAbilityConfig {
    private static final int BARREL_OFFSET = 1;

    private Barrel barrel;
    private boolean enabled = true;

    @Override
    public void onAttach(Zombie zombie) {
        enabled = true;
    }


    @Override
    public void onTick(Zombie zombie, GameState state, EventBus bus) {
        if(barrel == null) {
            spawnBarrel(zombie, state, bus);
            return;
        }

        if(!barrel.ownerDead()){
            int barrelCol = Math.max(0, zombie.col - BARREL_OFFSET);
            barrel.syncTo(zombie.row, barrelCol);
        }

        if(enabled){
            crushAtBarrel(state, bus);
        }

    }

    @Override
    public void onDeath(Zombie zombie, GameState state, EventBus bus) {
        if(barrel != null) {
            barrel.detach();
            barrel = null;
        }
    }

    private void spawnBarrel(Zombie zombie, GameState state, EventBus bus) {
        int barrelCol = Math.max(0, zombie.col - BARREL_OFFSET);
        barrel = new Barrel(zombie.row, barrelCol, zombie);
        state.barrels.add(barrel);
        bus.publish(new BarrelCreatedEvent(barrel));
    }

    private void crushAtBarrel(GameState state, EventBus bus) {
        Plant plant = state.plants.stream().filter(p -> p.row == barrel.row && p.col == barrel.col).findAny().orElse(null);
        Zombie hypnoZombie = state.zombies.stream().filter(z -> z.row == barrel.row && z.col == barrel.col && z.isHypnotized).findAny().orElse(null);

        if(plant != null) {
            plant.isAlive = false;
            bus.publish(new PlantDiedEvent(plant));
        }
        if(hypnoZombie != null) {
            hypnoZombie.isAlive = false;
            bus.publish(new ZombieDiedEvent(hypnoZombie, null));
        }
    }

    @Override
    public ZombieAbilityConfig createInstance(Zombie zombie) {
        return new ZombieBarrelRollerAbility();
    }

}
