package model.data.plant;

import java.util.ArrayList;
import java.util.List;

import model.core.EventBus;
import model.core.GameLoop;
import model.core.GameState;
import model.data.plant.abilities.config.PlantAbilityConfig;
import model.data.plant.effects.config.PlantEffectConfig;
import model.data.plant.stuns.PlantStun;
import model.data.plant.upgrades.PlantUpgradeState;
import model.event.events.PlantDiedEvent;

public class Plant {
    // private static final int DOUBLE_SUN_DROP_CHANCE = 25;

    // Frostbite caves chapter specific
    private static final int FROSTBITE_FREEZE_MAX_LEVEL = 3;
    private static final int FROSTBITE_FREEZE_HP = 600;

    public final int instanceId;
    public final PlantType type;
    public int row;
    public int col;
    public final int level;

    public int totalHP;
    public int hp;
    public int cost;
    public int damage;
    public float actionInterval;
    public List<PlantAbilityConfig> abilities = new ArrayList<>();

    public PlantEffectConfig plantFoodEffect;
    public boolean isPlantFoodActive = false;
    public int plantFoodDuration = 0;

    public boolean isAlive = true;
    public int doubleSunChance = 0;

    public EventBus eventBus;

    private static int nextId = 0;
    public boolean resetFamilyCooldowns = false;
    private boolean deathHandled = false;

    // Frostbite caves chapter specific
    private int frostbiteFreezeLevel = 0;
    private int frostbiteFreezeHP = 0;
    private boolean isFrostbiteFreezeActive = false;

    private PlantStun activeStun;
    public final PlantUpgradeState upgradeState;

    public Plant(PlantType type, int row, int col, int level, EventBus bus) {
        this.instanceId = nextId++;
        this.type = type;
        this.row = row;
        this.col = col;
        this.level = Math.min(level, 4);
        this.upgradeState = new PlantUpgradeState(type, this.level);

        this.totalHP = upgradeState.totalHP;
        this.hp = upgradeState.hp;
        this.cost = upgradeState.cost;
        this.damage = upgradeState.damage;
        this.actionInterval = upgradeState.actionInterval;
        this.eventBus = bus;
        this.doubleSunChance = upgradeState.doubleSunChance;
        this.resetFamilyCooldowns = upgradeState.resetFamilyCooldowns;
        for (PlantAbilityConfig def : type.abilities) {
            PlantAbilityConfig ability = def.createInstance(this);
            if (ability != null) {
                abilities.add(ability);
                // ability.onAttach(this);
            }
        }

        if (type.plantFoodEffect != null) {
            this.plantFoodEffect = type.plantFoodEffect.createInstance(this);
        }
    }

    public void kill(GameState state, EventBus bus) {
        if (deathHandled) {
            if (state != null) {
                state.removePlant(this);
            }
            return;
        }
        deathHandled = true;
        this.hp = 0;
        this.isAlive = false;
        EventBus publishBus = bus != null ? bus : eventBus;
        if (publishBus != null) {
            publishBus.publish(new PlantDiedEvent(this));
        }
        if (state != null) {
            state.removePlant(this);
        }
    }

    public boolean activatePlantFood(
            GameState state,
            EventBus event) {
        if (plantFoodEffect == null)
            return false;
        return activatePlantFood(
                state,
                event,
                plantFoodEffect.getDurationTicks());
    }

    public boolean activatePlantFood(
            GameState state,
            EventBus event,
            int durationTicks) {
        int fianlDuration = durationTicks + upgradeState.plantFoodDurationBonus * GameLoop.TICKS_PER_SECOND;
        if (plantFoodEffect == null || isPlantFoodActive) {
            return false;
        }
        if (!canUseAbilities()) {
            return false;
        }
        plantFoodEffect.onActivate(this, state, event);
        if (fianlDuration > 0) {
            isPlantFoodActive = true;
            plantFoodDuration = fianlDuration;
        }
        return true;
    }

    public void tickPlantFood(GameState state, EventBus bus) {
        if (isPlantFoodActive) {
            plantFoodDuration--;
            plantFoodEffect.onTick(this, state, bus);

            if (plantFoodDuration <= 0) {
                isPlantFoodActive = false;
                plantFoodEffect.onDeactivate(this, state, bus);
            }
        }
    }

    public float getX() {
        return col * GameState.CELL_WIDTH + GameState.CELL_WIDTH / 2;
    }

    public float getY() {
        return row * GameState.CELL_HEIGHT + GameState.CELL_HEIGHT / 2;
    }

    public boolean hasTag(PlantTag tag) {
        return type.hasTag(tag);
    }

    // Frostbite caves chapter specific
    public void increaseFrostbiteFreezeLevel() {
        if (type.hasTag(PlantTag.FIRE))
            return;

        frostbiteFreezeLevel++;
        if (frostbiteFreezeLevel >= FROSTBITE_FREEZE_MAX_LEVEL) {
            frostbiteFreezeLevel = FROSTBITE_FREEZE_MAX_LEVEL;
            isFrostbiteFreezeActive = true;
            frostbiteFreezeHP = FROSTBITE_FREEZE_HP;
        }
    }

    public void damageFrostbiteFreeze(int damage) {
        if (!isFrostbiteFreezeActive)
            return;
        frostbiteFreezeHP -= damage;
        if (frostbiteFreezeHP <= 0) {
            removeFrostbiteFreeze();
        }
    }

    public void removeFrostbiteFreeze() {
        frostbiteFreezeLevel = 0;
        frostbiteFreezeHP = 0;
        isFrostbiteFreezeActive = false;
    }

    public boolean isFrostbiteFreezeActive() {
        return isFrostbiteFreezeActive;
    }

    public int getFrostbiteFreezeLevel() {
        return frostbiteFreezeLevel;
    }

    public int getFrostbiteFreezeHP() {
        return frostbiteFreezeHP;
    }

    public void applyStun(PlantStun stun) {
        if (activeStun != null) {
            activeStun.onRemove(this);
        }
        activeStun = stun;
        if (activeStun != null) {
            activeStun.onApply(this);
        }
    }

    public void clearStun() {
        if (activeStun != null) {
            activeStun.onRemove(this);
            activeStun = null;
        }
    }

    public PlantStun getActiveStun() {
        return activeStun;
    }

    public boolean canAttack() {
        return activeStun == null || activeStun.canAttack();
    }

    public boolean canUseAbilities() {
        return activeStun == null || activeStun.canUseAbilities();
    }

    public boolean canBeDamaged() {
        return activeStun == null || activeStun.canBeDamaged();
    }

    public boolean canBeEaten() {
        if (hasTag(PlantTag.BOWLING)) {
            return false;
        }
        return activeStun == null || activeStun.canBeEaten();
    }

    public boolean blocksProjectile(model.data.projectile.Projectile projectile) {
        return activeStun != null && activeStun.blocksProjectile(projectile);
    }

    public void receiveAllyHit(int damage) {
        if (activeStun != null) {
            activeStun.onHitByAlly(this, damage);
            return;
        }
    }
}