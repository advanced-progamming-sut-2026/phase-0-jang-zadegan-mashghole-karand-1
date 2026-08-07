package model.data.plant;

import java.util.List;

import model.data.plant.upgrades.PlantLevelUpgrade;

public final class PlantStats {
    public static final int DEFAULT_LEVEL = 1;
    public static final int MAX_LEVEL = 4;

    public final int level;
    public final int hp;
    public final int damage;
    public final int cost;
    public final float actionInterval;
    public final float recharge;

    private PlantStats(int level, int hp, int damage, int cost, float actionInterval, float recharge) {
        this.level = level;
        this.hp = hp;
        this.damage = damage;
        this.cost = cost;
        this.actionInterval = actionInterval;
        this.recharge = recharge;
    }

    public static PlantStats forLevel(PlantType type, int level) {
        int clamped = Math.max(DEFAULT_LEVEL, Math.min(level, MAX_LEVEL));
        int hp = type.baseStats.hp;
        int damage = type.baseStats.damage;
        int cost = type.baseStats.cost;
        float actionInterval = type.baseStats.actionInterval;
        float recharge = type.baseStats.recharge;

        List<PlantLevelUpgrade> upgrades = type.levelUpgrades.getForLevel(clamped);
        for (PlantLevelUpgrade upgrade : upgrades) {
            switch (upgrade.stat) {
                case HP -> hp += upgrade.getIntValue();
                case DAMAGE -> damage += upgrade.getIntValue();
                case COST -> cost = Math.max(0, cost + upgrade.getIntValue());
                case COOLDOWN -> {
                    if (type.baseStats.actionInterval > 0)
                        actionInterval = Math.max(0, actionInterval + upgrade.getIntValue());
                    else
                        recharge = Math.max(0, recharge + upgrade.getIntValue());
                }
                default -> {
                }
            }
        }
        return new PlantStats(clamped, hp, damage, cost, actionInterval, recharge);
    }
}
