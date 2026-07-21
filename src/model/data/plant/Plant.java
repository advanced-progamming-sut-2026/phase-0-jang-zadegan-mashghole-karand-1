package model.data.plant;

import java.util.ArrayList;
import java.util.List;

import model.core.EventBus;
import model.core.GameState;
import model.data.plant.abilities.config.PlantAbilityConfig;
import model.data.plant.effects.config.PlantEffectConfig;
import model.data.plant.stuns.PlantStun;
import model.data.plant.upgrades.PlantLevelUpgrade;

public class Plant {
    private static final int DOUBLE_SUN_DROP_CHANCE = 25;

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

    // Frostbite caves chapter specific
    private int frostbiteFreezeLevel = 0;
    private int frostbiteFreezeHP = 0;
    private boolean isFrostbiteFreezeActive = false;

    private PlantStun activeStun;

    public Plant(PlantType type, int row, int col, int level, EventBus bus) {
        this.instanceId = nextId++;
        this.type = type;
        this.row = row;
        this.col = col;
        this.level = Math.min(level, 4);

        this.totalHP = type.baseStats.hp;
        this.hp = type.baseStats.hp;
        this.cost = type.baseStats.cost;
        this.damage = type.baseStats.damage;
        this.actionInterval = type.baseStats.actionInterval;

        this.eventBus = bus;

        applyLevelUpgrades();

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

    private void applyLevelUpgrades() {
        List<PlantLevelUpgrade> upgrades = type.levelUpgrades.getForLevel(level);

        for (PlantLevelUpgrade upgrade : upgrades) {
            switch (upgrade.stat) {
                case HP:
                    this.totalHP += upgrade.getIntValue();
                    break;
                case DAMAGE:
                    this.damage += upgrade.getIntValue();
                    break;
                case COST:
                    this.cost = Math.max(0, this.cost + upgrade.getIntValue());
                    break;
                case COOLDOWN:
                    this.actionInterval = Math.max(0, this.actionInterval + upgrade.getIntValue());
                    break;
                case DOUBLE_SUN_CHANCE:
                    this.doubleSunChance = DOUBLE_SUN_DROP_CHANCE;
                    break;
                case RESET_FAMILY_COOLDOWN:
                    this.resetFamilyCooldowns = true;
                    break;
                // ...
            }
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
        if (plantFoodEffect == null || isPlantFoodActive) {
            return false;
        }
        if (!canUseAbilities()) {
            return false;
        }
        plantFoodEffect.onActivate(this, state, event);
        if (durationTicks > 0) {
            isPlantFoodActive = true;
            plantFoodDuration = durationTicks;
        }
        return true;
    }

    public void tickPlantFood(GameState state , EventBus bus) {
        if (isPlantFoodActive) {
            plantFoodDuration--;
             plantFoodEffect.onTick(this, state, bus);

            if (plantFoodDuration <= 0) {
                isPlantFoodActive = false;
                 plantFoodEffect.onDeactivate(this,state,bus);
            }
        }
    }

    public float getX() {
        return row * GameState.CELL_WIDTH + GameState.CELL_WIDTH / 2;
    }

    public float getY() {
        return col * GameState.CELL_HEIGHT + GameState.CELL_HEIGHT / 2;
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