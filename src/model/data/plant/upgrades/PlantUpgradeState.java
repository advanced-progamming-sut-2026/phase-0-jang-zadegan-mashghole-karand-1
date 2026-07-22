package model.data.plant.upgrades;

import model.data.plant.PlantCategory;
import model.data.plant.PlantType;

import java.util.List;

public class PlantUpgradeState {
    private static final int DOUBLE_SUN_DROP_CHANCE = 25;
    public final PlantType type;
    public final int level;
    public int effectDurationBonus = 0;
    public int attackSpeedPercent = 0;
    public int pierceBonus = 0;
    public int rangeBonus = 0;
    public int aoeDamageBonus = 0;
    public int aoeRadiusBonus = 0;
    public int sunDropBonus = 0;
    public int poisonDamagePerTickBonus = 0;
    public float specialChanceBonus = 0f;
    public int lifeSpanBonus = 0;
    public int doubleCrushCount = 1;
    public int plantFoodDurationBonus = 0;
    public int targetPriorityBonus = 0;
    public int doubleSunChance = 0;
    public int  cooldownBonus =0;
    public int recharge = 0;
    public boolean resetFamilyCooldowns = false;
    public boolean meltArea3x3 = false;
    public boolean explodeOnFinish = false;
    public boolean aoeOnDeath = false;
    public boolean zombieHpBuff = false;
    public boolean zombieDamageBuff = false;

    public int totalHP;
    public int hp;
    public int cost;
    public int damage;
    public float actionInterval;


    public PlantUpgradeState(PlantType type,int level) {
        this.type = type;
        this.level = Math.min(level,4);
        this.totalHP = type.baseStats.hp;
        this.hp = type.baseStats.hp;
        this.cost = type.baseStats.cost;
        this.damage = type.baseStats.damage;
        this.actionInterval = type.baseStats.actionInterval;
        this.recharge = (int) type.baseStats.recharge;
        applyLevelUpgrades();

    }

    private void applyLevelUpgrades() {
        List<PlantLevelUpgrade> upgrades = type.levelUpgrades.getForLevel(level);

        for (PlantLevelUpgrade upgrade : upgrades) {
            switch (upgrade.stat) {
                case HP:
                    totalHP += upgrade.getIntValue();
                    hp += upgrade.getIntValue();
                    break;
                case DAMAGE:
                    damage += upgrade.getIntValue();
                    break;
                case COST:
                    cost = Math.max(0, cost + upgrade.getIntValue());
                    break;
                case COOLDOWN:
                    if (type.baseStats.actionInterval> 0) {
                        actionInterval = Math.max(0, actionInterval + upgrade.getIntValue());
                    }
                    else {
                        recharge = Math.max(0, recharge + upgrade.getIntValue());
                    }
                    cooldownBonus += upgrade.getIntValue();
                    break;
                case DOUBLE_SUN_CHANCE:
                    doubleSunChance = DOUBLE_SUN_DROP_CHANCE;
                    break;
                case RESET_FAMILY_COOLDOWN:
                    resetFamilyCooldowns = true;
                    break;
                case RANGE:
                    rangeBonus += upgrade.getIntValue();
                    break;
                case REGEN:
                    actionInterval = Math.max(0, actionInterval + upgrade.getIntValue());
                    break;
                case RADIUS:
                    aoeRadiusBonus += upgrade.getIntValue();
                    break;
                case SUN_DROP:
                    sunDropBonus += upgrade.getIntValue();
                    break;
                case LIFE_SPAN:
                    lifeSpanBonus += upgrade.getIntValue();
                    break;
                case ATTACK_SPEED:
                    attackSpeedPercent += upgrade.getIntValue();
                    break;
                case AOE_DAMAGE:
                    aoeDamageBonus += upgrade.getIntValue();
                    break;
                case AOE_ON_DEATH:
                    aoeOnDeath = upgrade.getBoolValue();
                    break;
                case DOUBLE_CRUSH:
                    doubleCrushCount = upgrade.getIntValue();
                    break;
                case PIERCE_COUNT:
                    pierceBonus += upgrade.getIntValue();
                    break;
                case MELT_AREA_3x3:
                    meltArea3x3 = upgrade.getBoolValue();
                    break;
                case SPECIAL_CHANGE:
                    specialChanceBonus += upgrade.getIntValue()/100f;
                    break;
                case ZOMBIE_HP_BUFF:
                    zombieHpBuff = upgrade.getBoolValue();
                    break;
                case DAMAGE_PER_TICK:
                    poisonDamagePerTickBonus += upgrade.getIntValue();
                    break;
                case EFFECT_DURATION:
                    if (type.category == PlantCategory.MINT) {
                        plantFoodDurationBonus += upgrade.getIntValue();
                    } else {
                        effectDurationBonus += upgrade.getIntValue();
                    }
                    break;
                case TARGET_PRIORITY:
                    targetPriorityBonus += upgrade.getIntValue();
                    break;
                case FOOD_ON_ENTRANCE:
                    break;
                case EXPLODE_ON_FINISH:
                    explodeOnFinish = upgrade.getBoolValue();
                    break;
                case ZOMBIE_DAMAGE_BUFF:
                    zombieDamageBuff =  upgrade.getBoolValue();
                    break;
                case BOUNCES_COUNT:
                    break;
                default:
                    break;

            }
        }
        if (attackSpeedPercent > 0) {
            actionInterval = Math.max(0.1f, actionInterval * (100 - attackSpeedPercent) / 100f);
        }
        if (lifeSpanBonus > 0 && totalHP == 0) {
            totalHP = lifeSpanBonus;
            hp = lifeSpanBonus;
        }
    }

}
