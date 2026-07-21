package model.data.plant;

public final class PlantUpgradeCosts {
    private PlantUpgradeCosts() {
    }

    private static final int[] COIN_COST_BY_TARGET_LEVEL = {
            0, // unused (level 0)
            0, // unused (already at level 1)
            1000, // 1 → 2
            2500, // 2 → 3
            5000 // 3 → 4
    };

    private static final int[] SEED_COST_BY_TARGET_LEVEL = {
            0,
            0,
            10, // 1 → 2
            20, // 2 → 3
            40 // 3 → 4
    };

    public static int coinCostToReach(int targetLevel) {
        if (targetLevel < 2 || targetLevel > PlantStats.MAX_LEVEL) {
            return 0;
        }
        return COIN_COST_BY_TARGET_LEVEL[targetLevel];
    }

    public static int seedPacketCostToReach(int targetLevel) {
        if (targetLevel < 2 || targetLevel > PlantStats.MAX_LEVEL) {
            return 0;
        }
        return SEED_COST_BY_TARGET_LEVEL[targetLevel];
    }
}
