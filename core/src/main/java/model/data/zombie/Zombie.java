package model.data.zombie;

import java.util.*;

import model.core.EventBus;
import model.core.GameState;
import model.core.Position;
import model.data.plant.PlantType;
import model.data.projectile.Projectile;
import model.data.zombie.abilities.config.ZombieAbilityConfig;
import model.data.zombie.armor.runtime.ZombieArmor;
import model.event.events.GlowingZombieDiedEvent;
import model.event.events.ZombieDiedEvent;
import model.event.events.ZombieDroppedLootEvent;

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
    public PlantType lastHitBy = null;
    public boolean killedByLawnMower = false;
    public boolean isEating = false;
    public float dpsMultiplier = 1;

    public List<ZombieAbilityConfig> abilities = new ArrayList<>();

    public ZombieArmor armor;

    public boolean isFrozen = false;
    public int frozenTicks = 0;
    public boolean isHypnotized = false;
    public final boolean isGlowing;

    public boolean stunned = false;
    public int stunTicks = 0;
    public boolean forceWalkAnim = false;
    public String animClip;
    public boolean animClipLoop;

    private int iceHP = 0;
    private boolean isIced = false;
    private boolean deathHandled = false;

    // Ancient Egypt chapter specific
    private SandstormEffect activeSandstorm = null;

    Random randomizer = new Random();

    public EventBus eventBus;

    private static int nextId = 0;
    public final boolean canBeFrozen;

    public Zombie(ZombieType type, int row, int col, Position position, EventBus eventBus) {
        this(type,row,col,position,eventBus,0.05f);
    }
    public Zombie(ZombieType type, int row, int col, Position position, EventBus bus , float glowChance) {
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

        isGlowing = randomizer.nextFloat() < glowChance  ;

        canBeFrozen = !(type.isZomboss()
                || type == ZombieType.DODO_RIDER_ZOMBIE
                || type == ZombieType.HUNTER
                || type == ZombieType.TROGLOBITE);
    }

    public void takeDamage(int damage) {
        int hpBefore = this.hp;
        if (armor != null && armor.isIntact()) {
            damage = armor.absorbDamage(damage);
        }

        this.hp -= damage;

        if (this.hp <= 0) {
            this.isAlive = false;
        }
        applyZombossPhaseStun(hpBefore);
    }

    public void takeDamage(int damage, boolean poisonous) {
        if (!poisonous) {
            takeDamage(damage);
            return;
        }
        int hpBefore = this.hp;
        this.hp -= damage;

        if (this.hp <= 0) {
            this.isAlive = false;
            isEating = false;
        }
        applyZombossPhaseStun(hpBefore);
    }

    private void applyZombossPhaseStun(int hpBefore) {
        if (!type.isZomboss() || totalHp <= 0 || hp <= 0) {
            return;
        }
        int[] cuts = { totalHp * 2 / 3, totalHp / 3 };
        for (int cut : cuts) {
            if (hpBefore > cut && hp <= cut) {
                stunned = true;
                stunTicks = 50;
                return;
            }
        }
    }

    public void kill(GameState state) {
        if (deathHandled) {
            return;
        }
        deathHandled = true;
        this.hp = 0;
        this.isAlive = false;
        this.isEating = false;

        if (state == null) {
            return;
        }
        for (ZombieAbilityConfig ability : abilities) {
            ability.onDeath(this, state, eventBus);
        }
        if (isGlowing) {
            state.plantFoodAmount++;
            eventBus.publish(new GlowingZombieDiedEvent(this));
        } else {
            eventBus.publish(new ZombieDiedEvent(this, lastHitBy));
        }

        boolean drop = !type.isZomboss() && randomizer.nextInt(10) == 0;
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
        if (isHypnotized)
            s *= -1;
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

    public boolean canBeInstakilled() {
        return !type.isZomboss();
    }

    public boolean canBeHypnotized() {
        return type != null && !type.isZomboss();
    }

    public boolean canMove() {
        if (type.isZomboss()) {
            return false;
        }
        return isAlive && !isEating;
    }

    public int rowSpan() {
        return type.isZomboss() ? 2 : 1;
    }

    public boolean occupiesRow(int r) {
        return r >= row && r < row + rowSpan();
    }

    public boolean isHitByProjectile(Projectile p) {
        if (!isAlive || p == null || p.position == null || position == null) {
            return false;
        }
        if (!occupiesRow(p.row)) {
            return false;
        }
        if (type != null && type.isZomboss()) {
            float left = position.x - GameState.CELL_WIDTH * 1.6f;
            float right = position.x + GameState.CELL_WIDTH * 0.45f;
            return p.position.x >= left && p.position.x <= right;
        }
        return Math.abs(position.x - p.position.x) < GameState.PROJECTILE_HIT_RADIUS;
    }

    public boolean occupiesNearbyRow(int r, int dist) {
        for (int i = 0; i < rowSpan(); i++) {
            if (Math.abs((row + i) - r) <= dist) {
                return true;
            }
        }
        return false;
    }

    public void playAnim(String clip, boolean loop) {
        this.animClip = clip;
        this.animClipLoop = loop;
    }

    public void clearAnim() {
        this.animClip = null;
        this.animClipLoop = false;
    }

    public void syncY() {
        if (position == null) {
            return;
        }
        float mid = row + (rowSpan() - 1) * 0.5f;
        position.y = GameState.CELL_HEIGHT * mid + GameState.CELL_HEIGHT / 2f;
    }

    public float getDPS() {
        return type.baseStats.eatDPS * dpsMultiplier;
    }

}