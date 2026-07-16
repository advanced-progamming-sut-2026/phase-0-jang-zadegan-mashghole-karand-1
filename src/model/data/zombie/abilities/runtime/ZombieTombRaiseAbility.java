package model.data.zombie.abilities.runtime;

import model.core.EventBus;
import model.core.GameState;
import model.data.Grave.Grave;
import model.data.Grave.GraveContent;
import model.data.plant.Plant;
import model.data.zombie.Zombie;
import model.data.zombie.abilities.config.ZombieAbilityConfig;
import model.events.GraveCreatedEvent;

import java.util.Collections;
import java.util.Comparator;
import java.util.*;

public class ZombieTombRaiseAbility implements ZombieAbilityConfig {
    private final int COOLDOWN_TICKS = 50;
    private int cooldownTicks= 0;
    private boolean onCooldown = false;
    private final Random rand = new Random();

    @Override
    public void onTick(Zombie zombie, GameState state, EventBus bus) {
        if(onCooldown){
            cooldownTicks++;
            if(cooldownTicks >= COOLDOWN_TICKS){
                onCooldown = false;
                cooldownTicks = 0;
            }
            return;
        }
        List<int[]> empty = new ArrayList<>();
        for (int r = 0; r < GameState.GRID_ROWS; r++) {
            for (int c = 0; c < GameState.GRID_COLS; c++) {
                if (state.getPlantAt(r, c) == null && state.getGraveAt(r, c) == null) {
                    empty.add(new int[]{r, c});
                }
            }
        }
        if (empty.isEmpty()) return;
        Collections.shuffle(empty, rand);
        int toPlace = Math.min(2, empty.size());
        for (int i = 0; i < toPlace; i++) {
            int r = empty.get(i)[0];
            int c = empty.get(i)[1];
            Grave grave = new Grave(r, c, GraveContent.NONE);
            state.graves.add(grave);
            bus.publish(new GraveCreatedEvent(grave));
        }
        onCooldown = true;
        cooldownTicks = 0;
    }

    @Override
    public ZombieAbilityConfig createInstance(Zombie zombie) {
        return new  ZombieTombRaiseAbility();
    }
}
