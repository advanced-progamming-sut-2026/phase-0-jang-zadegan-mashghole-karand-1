package model.data.zombie;

import java.util.*;

import model.core.EventBus;
import model.core.GameState;
import model.core.Position;
import model.data.zombie.abilities.config.ZombieAbilityConfig;
import model.data.zombie.armor.runtime.ZombieArmor;
import model.events.GlowingZombieDiedEvent;
import model.events.ZombieDroppedLootEvent;

public class Zombie {
    public final int instanceId;
    public final ZombieType type;
    public int row;
    public int col;
    public float speed;
    public Position position;
    public int hp;
    public int totalHp;
    public boolean isAlive = true;
    private SandstormEffect activeSandstorm = null;

    public List<ZombieAbilityConfig> abilities = new ArrayList<>();

    public ZombieArmor armor;

    public boolean isFrozen = false;
    public int frozenTicks = 0;
    public boolean isHypnotized = false;
    public final boolean isGlowing;
    Random randomizer = new Random();

    public EventBus eventBus;

    private static int nextId = 0;

    public Zombie(ZombieType type, int row, int col, Position position, EventBus bus) {
        this.instanceId = nextId++;
        this.type = type;
        this.row = row;
        this.col = col;
        this.position = position;
        this.hp = type.baseStats.hp;
        this.totalHp = type.baseStats.hp;
        this.eventBus = bus;
        this.speed = type.baseStats.speed;

        for (ZombieAbilityConfig config : type.abilities) {
            ZombieAbilityConfig ability = config.createInstance(this);
            if (ability != null) {
                abilities.add(ability);
                ability.onAttach(this);
            }
        }

        if (type.armorConfig != null) {
            this.armor = new ZombieArmor(type.armorConfig);
        }

        isGlowing = randomizer.nextInt(20) == 0;
    }

    public void takeDamage(int damage) {
        if (armor != null && armor.isIntact()) {
            damage = armor.absorbDamage(damage);
        }

        this.hp -= damage;

        if (this.hp <= 0) {
            this.isAlive = false;
        }
    }

    public void tick(GameState state) {
       if (isFrozen) {
           frozenTicks--;
           if (frozenTicks <= 0) {
               isFrozen = false;
           }
           return;
       }

    }

    public void onTickAbilities(GameState state) {
        for (ZombieAbilityConfig ability : abilities) {
            ability.onTick(this, state, eventBus);
        }
    }

    public void onDeath(GameState state) {
        for (ZombieAbilityConfig ability : abilities) {
            ability.onDeath(this, state, eventBus);
        }
        if(isGlowing) {
            state.plantFoodAmount++;
            eventBus.publish(new GlowingZombieDiedEvent(this));
        }

        boolean drop = randomizer.nextInt(10)==0;
        if (drop) {
            ZombieLootType lootType = ZombieLootType.values()[randomizer.nextInt(ZombieLootType.values().length)];
            if (Objects.requireNonNull(lootType) == ZombieLootType.COIN) {
                eventBus.publish(new ZombieDroppedLootEvent(lootType, 50, position));
            } else {
                eventBus.publish(new ZombieDroppedLootEvent(lootType, 1, position));
            }
        }

    }

    public boolean hasSandstorm() {
        return activeSandstorm != null;
    }

    public void setSandstorm(float targetX) {
        this.activeSandstorm = new SandstormEffect(targetX);
    }

    public void clearSandstorm() {
        this.activeSandstorm = null;
    }

    public SandstormEffect getSandstorm() {
        return activeSandstorm;
    }

    public float getCurrentSpeed() {
        if (hasSandstorm()) {
            return speed * activeSandstorm.SPEED_MULTIPLIER;
        }
        return speed;
    }
}