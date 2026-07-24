package model.data.zombie.abilities.runtime;

import model.core.EventBus;
import model.core.GameLoop;
import model.core.GameState;
import model.core.Position;
import model.data.sun.Sun;
import model.data.sun.SunType;
import model.data.zombie.Zombie;
import model.data.zombie.abilities.config.ZombieAbilityConfig;
import model.event.events.SunDroppedEvent;

public class ZombieSunProduceAbility implements ZombieAbilityConfig {
    private static final float COOLDOWN_SECONDS = 24f;
    private static final int COOLDOWN_TICKS = (int) (COOLDOWN_SECONDS * GameLoop.TICKS_PER_SECOND);

    private int ageTicks;
    private int cooldownTicks;

    public ZombieSunProduceAbility() {
        this.ageTicks = 0;
        this.cooldownTicks = COOLDOWN_TICKS;
    }

    @Override
    public ZombieAbilityConfig createInstance(Zombie zombie) {
        return new ZombieSunProduceAbility();
    }

    @Override
    public void onTick(Zombie zombie, GameState state, EventBus bus) {
        ageTicks++;
        if (cooldownTicks > 0) {
            cooldownTicks--;
            return;
        }

        int ageSeconds = ageTicks / GameLoop.TICKS_PER_SECOND;
        int amount = Math.min(50, (int) Math.floor(5 + 0.1 * ageSeconds));
        if (amount <= 0) {
            amount = 5;
        }

        Sun sun = new Sun(zombie.row, new Position(zombie.position.x, zombie.position.y), amount, SunType.NORMAL);
        sun.isFalling = false;
        sun.position.y = zombie.row * GameState.CELL_HEIGHT + GameState.CELL_HEIGHT / 2f;
        state.sunDrops.add(sun);
        bus.publish(new SunDroppedEvent(sun));

        cooldownTicks = COOLDOWN_TICKS;
    }
}
