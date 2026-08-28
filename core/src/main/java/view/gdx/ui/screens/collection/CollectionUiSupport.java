package view.gdx.ui.screens.collection;

import model.data.plant.PlantStats;
import model.data.plant.PlantType;
import model.data.plant.PlantUpgradeCosts;
import model.service.CollectionViewState;
import model.storage.StorageManager;
import model.storage.user.User;

final class CollectionUiSupport {
    static final int PLANT_PURCHASE_COST = 2000;

    private CollectionUiSupport() {
    }

    static boolean isUpgradeable(PlantType type, User user, StorageManager storage) {
        if (type == null || user == null || storage == null || !storage.isPlantUnlocked(type)) {
            return false;
        }
        if (type.isBowlingExclusive()) {
            return false;
        }
        int level = user.getPlantLevel(type);
        if (level >= PlantStats.MAX_LEVEL) {
            return false;
        }
        int targetLevel = level + 1;
        return user.coins >= PlantUpgradeCosts.coinCostToReach(targetLevel)
                && user.getSeedPackets(type) >= PlantUpgradeCosts.seedPacketCostToReach(targetLevel);
    }

    static int seedPacketsRequired(PlantType type, User user) {
        if (type == null || user == null) {
            return 0;
        }
        int level = user.getPlantLevel(type);
        if (level >= PlantStats.MAX_LEVEL) {
            return 0;
        }
        return PlantUpgradeCosts.seedPacketCostToReach(level + 1);
    }

    static int seedPacketsRemaining(PlantType type, User user) {
        int required = seedPacketsRequired(type, user);
        if (required <= 0 || user == null) {
            return 0;
        }
        return Math.max(0, required - user.getSeedPackets(type));
    }

    static boolean matchesPlantFilters(CollectionViewState.Entry entry, PlantType type,
            String familyFilter, String lockFilter, boolean upgradeableOnly,
            User user, StorageManager storage) {
        if (entry == null || type == null) {
            return false;
        }
        if (familyFilter != null && !familyFilter.isBlank() && !"All".equals(familyFilter)) {
            if (!type.category.name().equals(familyFilter)) {
                return false;
            }
        }
        if (lockFilter != null && !lockFilter.isBlank() && !"All".equals(lockFilter)) {
            if ("Unlocked".equals(lockFilter) && !entry.unlocked) {
                return false;
            }
            if ("Locked".equals(lockFilter) && entry.unlocked) {
                return false;
            }
        }
        if (upgradeableOnly && !isUpgradeable(type, user, storage)) {
            return false;
        }
        return true;
    }
}
