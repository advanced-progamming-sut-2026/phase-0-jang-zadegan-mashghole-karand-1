package model.data.zombie;

import java.util.*;

import model.core.EventBus;
import model.core.GameState;
import model.core.Position;
import model.data.zombie.abilities.config.ZombieAbilityConfig;
import model.data.zombie.armor.runtime.ZombieArmor;
import model.events.GlowingZombieDiedEvent;
import model.events.ZombieDiedEvent;
import model.events.ZombieDroppedLootEvent;

public class Zombie {
    private static final int ICE_HP = 600;

    public final int instanceId;
    public final ZombieType type;
    public int row;
    public int col;
    public float speed;
    public Position position;
    public int hp;
    public int totalHp;
    public boolean isAlive = true;

    public List<ZombieAbilityConfig> abilities = new ArrayList<>();

    public ZombieArmor armor;

    public boolean isFrozen = false;
    public int frozenTicks = 0;
    public boolean isHypnotized = false;
    public final boolean isGlowing;

    public boolean stunned = false;
    public int stunTicks = 0;

    private int iceHP = 0;
    private boolean isIced = false;

    // Ancient Egypt chapter specific
    private SandstormEffect activeSandstorm = null;

    Random randomizer = new Random();

    public EventBus eventBus;

    private static int nextId = 0;
    public final boolean canBeFrozen;

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

        canBeFrozen = !(type == ZombieType.DODO_RIDER_ZOMBIE || type == ZombieType.HUNTER
                || type == ZombieType.TROGLOBITE);
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

    public void takeDamage(int damage, boolean poisonous) {
        if(!poisonous) {
            takeDamage(damage);
            return;
        }
        this.hp -= damage;

        if (this.hp <= 0) {
            this.isAlive = false;
        }
    }

    public void onDeath(GameState state) {
        for (ZombieAbilityConfig ability : abilities) {
            ability.onDeath(this, state, eventBus);
        }
        if (isGlowing) {
            state.plantFoodAmount++;
            eventBus.publish(new GlowingZombieDiedEvent(this));
        } else {
            eventBus.publish(new ZombieDiedEvent(this));
        }

        boolean drop = randomizer.nextInt(10) == 0;
        if (drop) {
            ZombieLootType lootType = ZombieLootType.values()[randomizer.nextInt(ZombieLootType.values().length)];
            if (Objects.requireNonNull(lootType) == ZombieLootType.COIN) {
                eventBus.publish(new ZombieDroppedLootEvent(lootType, 50, position));
            } else {
                eventBus.publish(new ZombieDroppedLootEvent(lootType, 1, position));
            }
        }

    }

    public float getCurrentSpeed() {
        float s = speed;
        if (isIced)
            return 0;
        if (hasSandstorm()) {
            s *= activeSandstorm.SPEED_MULTIPLIER;
        }
        if (isFrozen)
            s *= 0.5f;
        return s;
    }

    public void ice() {
        this.isIced = true;
        this.iceHP = ICE_HP;
    }

    public void damageIce(int damage) {
        if (!isIced)
            return;
        iceHP -= damage;
        if (iceHP <= 0) {
            removeIce();
        }
    }

    public void removeIce() {
        this.isIced = false;
        this.iceHP = 0;
    }

    public boolean isIced() {
        return isIced;
    }

    public int getIceHP() {
        return iceHP;
    }

    // Ancient Egypt chapter specific
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

}