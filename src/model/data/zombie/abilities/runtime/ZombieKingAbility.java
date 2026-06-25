package model.data.zombie.abilities.runtime;

import model.core.EventBus;
import model.core.GameState;
import model.core.ReadOnlyGameState;
import model.data.zombie.Zombie;
import model.data.zombie.ZombieBaseStats;
import model.data.zombie.ZombieType;
import model.data.zombie.abilities.config.ZombieAbilityConfig;
import model.data.zombie.armor.config.ZombieArmorConfig;
import model.data.zombie.armor.runtime.ZombieArmor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ZombieKingAbility implements ZombieAbilityConfig {
    private boolean onCooldown= false;
    private int cooldown=60;
    @Override
    public void onTick(Zombie zombie, GameState state, EventBus bus) {
        int RANGE_RADIUS = ReadOnlyGameState.CELL_WIDTH * 2;
        double rangeSq = RANGE_RADIUS * RANGE_RADIUS;

        List<Zombie> zombiesInRange = state.getZombies().stream()
                .filter(z -> {
                    double dx = z.position.x - zombie.position.x;
                    double dy = z.position.y - zombie.position.y;
                    return (dx * dx + dy * dy) <= rangeSq;
                }).filter(z -> z.type == ZombieType.BASIC)
                .toList();
        if(!onCooldown && !zombiesInRange.isEmpty()) {
            Random rand = new Random();
            Zombie z = zombiesInRange.get(rand.nextInt(zombiesInRange.size()));
            z.armor = new ZombieArmor(ZombieArmorConfig.knight_armor());
            onCooldown = true;
        }
        if(onCooldown) {
            cooldown--;
            if(cooldown == 0) {
                onCooldown = false;
                cooldown=60;
            }
        }
    }

    @Override
    public ZombieAbilityConfig createInstance(Zombie zombie) {
        return new ZombieKingAbility();
    }
}
