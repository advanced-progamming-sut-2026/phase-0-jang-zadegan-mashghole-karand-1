package controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import controller.CommandResult.CommandResult;
import model.data.plant.PlantStats;
import model.data.plant.PlantType;
import model.data.plant.PlantUpgradeCosts;
import model.data.zombie.ZombieType;
import model.service.CollectionViewState;
import model.service.CollectionViewState.Entry;
import model.service.CollectionViewState.Mode;
import model.service.CollectionViewState.Tab;
import model.service.GameNavigationState.Phase;
import model.storage.StorageManager;
import view.ScreenType;
import view.messages.ErrorMessages;

public class CollectionController {

    private static final int PLANT_PURCHASE_COST = 2000;

    private final ControllerManager controllerManager;
    private final StorageManager storage;

    private Tab tab = Tab.PLANTS;
    private Mode mode = Mode.UNLOCKED;
    private String detailTitle = null;
    private List<String> detailLines = List.of();

    public CollectionController(ControllerManager controllerManager, StorageManager storage) {
        this.controllerManager = controllerManager;
        this.storage = storage;
    }

    public CollectionViewState getViewState() {
        if (!storage.isLoggedIn()) {
            return CollectionViewState.empty();
        }
        return new CollectionViewState(tab, mode, buildEntries(), detailTitle, detailLines);
    }

    public void onOpened() {
        tab = Tab.PLANTS;
        mode = Mode.UNLOCKED;
        clearDetail();
    }

    public CommandResult showPlants() {
        CommandResult check = requireCollectionScreen();
        if (check != null) {
            return check;
        }
        tab = Tab.PLANTS;
        mode = Mode.UNLOCKED;
        clearDetail();
        return success("Showing unlocked plants.");
    }

    public CommandResult showZombies() {
        CommandResult check = requireCollectionScreen();
        if (check != null) {
            return check;
        }
        tab = Tab.ZOMBIES;
        mode = Mode.UNLOCKED;
        clearDetail();
        return success("Showing unlocked zombies.");
    }

    public CommandResult showAllPlants() {
        CommandResult check = requireCollectionScreen();
        if (check != null) {
            return check;
        }
        tab = Tab.PLANTS;
        mode = Mode.ALL;
        clearDetail();
        return success("Showing all plants.");
    }

    public CommandResult showAllZombies() {
        CommandResult check = requireCollectionScreen();
        if (check != null) {
            return check;
        }
        tab = Tab.ZOMBIES;
        mode = Mode.ALL;
        clearDetail();
        return success("Showing all zombies.");
    }

    public CommandResult showPlant(String plantName) {
        return showPlant(plantName, false);
    }

    public CommandResult showPlantDebug(String plantName) {
        return showPlant(plantName, true);
    }

    public CommandResult showZombie(String zombieName) {
        return showZombie(zombieName, false);
    }

    public CommandResult showZombieDebug(String zombieName) {
        return showZombie(zombieName, true);
    }

    private CommandResult showPlant(String plantName, boolean allowLocked) {
        CommandResult check = requireCollectionScreen();
        if (check != null) {
            return check;
        }
        if (plantName == null || plantName.isBlank()) {
            return failure(allowLocked
                    ? "Usage: debug show-plant -p <plantname>"
                    : "Usage: menu collection show-plant -p <plantname>");
        }

        PlantType type = PlantType.fromName(plantName.trim());
        if (type == null) {
            return failure(ErrorMessages.PLANT_NOT_FOUND.getMessage());
        }
        if (!allowLocked && !storage.isPlantUnlocked(type)) {
            return failure(ErrorMessages.PLANT_LOCKED.getMessage());
        }

        int level = PlantStats.DEFAULT_LEVEL;
        var user = storage.getCurrentUser();
        if (user != null && storage.isPlantUnlocked(type)) {
            level = user.getPlantLevel(type);
        }
        PlantStats stats = PlantStats.forLevel(type, level);
        tab = Tab.PLANTS;
        detailTitle = type.name;
        List<String> lines = new ArrayList<>();
        lines.add("Level: " + stats.level + "/" + PlantStats.MAX_LEVEL);
        lines.add("Category: " + type.category.name());
        lines.add("Cost: " + stats.cost);
        lines.add("HP: " + stats.hp);
        lines.add("Damage: " + stats.damage);
        lines.add("Action Interval: " + formatFloat(stats.actionInterval) + "s");
        lines.add("Recharge: " + formatFloat(stats.recharge) + "s");
        if (user != null) {
            lines.add("Seed Packets: " + user.getSeedPackets(type));
            if (stats.level < PlantStats.MAX_LEVEL) {
                int next = stats.level + 1;
                lines.add("Next Upgrade: " + PlantUpgradeCosts.coinCostToReach(next) + " coins, "
                        + PlantUpgradeCosts.seedPacketCostToReach(next) + " seed packets");
            }
        }
        detailLines = lines;
        return success("Showing details for " + type.name + ".");
    }

    private CommandResult showZombie(String zombieName, boolean allowLocked) {
        CommandResult check = requireCollectionScreen();
        if (check != null) {
            return check;
        }
        if (zombieName == null || zombieName.isBlank()) {
            return failure(allowLocked
                    ? "Usage: debug show-zombie -z <zombiename>"
                    : "Usage: menu collection show-zombie -z <zombiename>");
        }

        ZombieType type = ZombieType.fromName(zombieName.trim());
        if (type == null) {
            return failure("Zombie not found.");
        }
        if (!allowLocked && !storage.isZombieUnlocked(type)) {
            return failure("Zombie is locked.");
        }

        tab = Tab.ZOMBIES;
        detailTitle = type.name;
        List<String> lines = new ArrayList<>();
        lines.add("HP: " + type.baseStats.hp);
        lines.add("Eat DPS: " + type.baseStats.eatDPS);
        lines.add("Speed: " + formatFloat(type.baseStats.speed));
        lines.add("Wave Cost: " + type.baseStats.wavePointCost);
        lines.add("Weight: " + type.baseStats.weight);
        lines.add("Abilities: " + type.abilities.size());
        if (type.armorConfig != null) {
            lines.add("Armor: " + type.armorConfig.type.name() + " (" + type.armorConfig.hp + " HP)");
        } else {
            lines.add("Armor: None");
        }
        detailLines = lines;
        return success("Showing details for " + type.name + ".");
    }

    public CommandResult upgradePlant(String plantName) {
        CommandResult check = requireCollectionScreen();
        if (check != null) {
            return check;
        }
        if (plantName == null || plantName.isBlank()) {
            return failure("Usage: menu collection upgrade-plant -p <plantname>");
        }

        PlantType type = PlantType.fromName(plantName.trim());
        if (type == null) {
            return failure(ErrorMessages.PLANT_NOT_FOUND.getMessage());
        }
        if (!storage.isPlantUnlocked(type)) {
            return failure(ErrorMessages.PLANT_NOT_IN_COLLECTION.getMessage());
        }

        var user = storage.getCurrentUser();
        if (user == null) {
            return failure("You must be logged in.");
        }

        int currentLevel = user.getPlantLevel(type);
        if (currentLevel >= PlantStats.MAX_LEVEL) {
            return failure(ErrorMessages.PLANT_AT_MAX_LEVEL.getMessage());
        }

        int targetLevel = currentLevel + 1;
        int coinCost = PlantUpgradeCosts.coinCostToReach(targetLevel);
        int seedCost = PlantUpgradeCosts.seedPacketCostToReach(targetLevel);

        if (user.coins < coinCost) {
            return failure(ErrorMessages.NOT_ENOUGH_COINS_UPGRADE.getMessage());
        }
        if (user.getSeedPackets(type) < seedCost) {
            return failure(ErrorMessages.NOT_ENOUGH_SEED_PACKETS.getMessage());
        }

        user.coins -= coinCost;
        user.useSeedPackets(type, seedCost);
        user.setPlantLevel(type, targetLevel);
        storage.saveProgress();

        tab = Tab.PLANTS;
        return success("Upgraded " + type.name + " to level " + targetLevel
                + " (-" + coinCost + " coins, -" + seedCost + " seed packets).");
    }

    public CommandResult purchasePlant(String plantName) {
        CommandResult check = requireCollectionScreen();
        if (check != null) {
            return check;
        }
        if (plantName == null || plantName.isBlank()) {
            return failure("Usage: menu collection purchase-plant -p <plantname>");
        }

        PlantType type = PlantType.fromName(plantName.trim());
        if (type == null) {
            return failure(ErrorMessages.PLANT_NOT_FOUND.getMessage());
        }
        if (storage.isPlantUnlocked(type)) {
            return failure(ErrorMessages.PLANT_ALREADY_PURCHASED.getMessage());
        }

        var user = storage.getCurrentUser();
        if (user == null) {
            return failure("You must be logged in.");
        }
        if (user.coins < PLANT_PURCHASE_COST) {
            return failure(ErrorMessages.NOT_ENOUGH_COINS_PURCHASE.getMessage());
        }

        user.coins -= PLANT_PURCHASE_COST;
        storage.updateUserProfile(user);
        storage.unlockPlant(type);

        tab = Tab.PLANTS;
        return success("Purchased " + type.name + " for " + PLANT_PURCHASE_COST + " coins.");
    }

    private List<Entry> buildEntries() {
        List<Entry> entries = new ArrayList<>();
        if (tab == Tab.PLANTS) {
            if (mode == Mode.UNLOCKED) {
                List<PlantType> unlocked = new ArrayList<>(storage.getUnlockedPlants());
                unlocked.sort(Comparator.comparing(p -> p.name));
                for (PlantType plant : unlocked) {
                    entries.add(new Entry(plant.name, true));
                }
            } else {
                for (PlantType plant : PlantType.values()) {
                    entries.add(new Entry(plant.name, storage.isPlantUnlocked(plant)));
                }
            }
        } else {
            if (mode == Mode.UNLOCKED) {
                List<ZombieType> unlocked = new ArrayList<>(storage.getUnlockedZombies());
                unlocked.sort(Comparator.comparing(z -> z.name));
                for (ZombieType zombie : unlocked) {
                    entries.add(new Entry(zombie.name, true));
                }
            } else {
                for (ZombieType zombie : ZombieType.values()) {
                    entries.add(new Entry(zombie.name, storage.isZombieUnlocked(zombie)));
                }
            }
        }
        return entries;
    }

    private void clearDetail() {
        detailTitle = null;
        detailLines = List.of();
    }

    private static String formatFloat(float value) {
        if (value == (int) value) {
            return Integer.toString((int) value);
        }
        return String.format("%.2f", value);
    }

    private CommandResult requireCollectionScreen() {
        CommandResult screenCheck = controllerManager.requireScreen(ScreenType.COLLECTION);
        if (screenCheck != null) {
            return screenCheck;
        }
        return controllerManager.requireLoggedIn();
    }

    public CommandResult requireCanOpenCollection() {
        CommandResult loggedInCheck = controllerManager.requireLoggedIn();
        if (loggedInCheck != null) {
            return loggedInCheck;
        }
        if (controllerManager.getCurrentScreen() != ScreenType.LEVEL_SELECTOR
                || controllerManager.getGameNavigation().phase != Phase.CHAPTER) {
            return failure("Open the game menu first with: menu enter game");
        }
        return null;
    }

    private CommandResult success(String message) {
        return new CommandResult(message, true);
    }

    private CommandResult failure(String message) {
        return new CommandResult(message, false);
    }
}
