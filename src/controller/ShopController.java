package controller;

import controller.CommandResult.CommandResult;
import model.data.plant.PlantType;
import model.greenhouse.Greenhouse;
import model.shop.Shop;
import model.shop.ShopItem;
import model.shop.ShopItems;
import model.storage.StorageManager;
import model.storage.user.User;


public class ShopController {
    public enum ShopDisplayMode {
        MENU,
        LIST,
        DAILY
    }

    private ShopDisplayMode shopDisplayMode = ShopDisplayMode.MENU;
    private Shop shop;
    private StorageManager storageManager;

    public ShopController(Shop shop, StorageManager storageManager) {
        this.shop = shop;
        this.storageManager = storageManager;
    }

    public CommandResult List() {
        if (storageManager.getCurrentUser() == null) {
            return failure("You must be logged in.");
        }
        shop.ensureDailyFresh();
        shopDisplayMode = ShopDisplayMode.LIST;
        return success("Showing permanent shop items.");
    }

    public CommandResult daily() {
        if (storageManager.getCurrentUser() == null) {
            return failure("You must be logged in.");
        }
        shop.ensureDailyFresh();
        shopDisplayMode = ShopDisplayMode.DAILY;
        return success("Showing daily deal.");
    }

    public CommandResult buy(String itemId, int quantity) {
        if (quantity <= 0) {
            return failure("Quantity must be positive.");
        }
        User user = storageManager.getCurrentUser();
        if (user == null) {
            return failure("You must be logged in.");
        }
        if (itemId.equals("daily")) {
            shop.ensureDailyFresh();
            if (user.dailyDeal.dailyDealPlant == null) {
                return failure("No daily deal available.");
            }
            if (user.dailyDeal.dailyDealPurchased) {
                return failure("Daily deal already purchased today.");
            }
            if (quantity != 1) {
                return failure("You can buy the daily deal only once.");
            }
            int price = user.dailyDeal.dailyDealPrice;
            if (user.coins < price) {
                return failure("Not enough coins.");
            }
            PlantType plant = user.dailyDeal.dailyDealPlant;
            user.coins -= price;
            user.addSeedPackets(plant, 10);
            user.dailyDeal.dailyDealPurchased = true;
            storageManager.saveProgress();
            return success("Purchased daily deal: " + plant.name);
        }
        ShopItems shopItemEnum = ShopItems.getFromId(itemId);
        if (shopItemEnum == null) {
            return failure("Unknown shop item.");
        }
        ShopItem item = shopItemEnum.getShopItem();
        switch (itemId) {
            case "pot" -> {
                if (user.greenhouse == null) {
                    user.greenhouse = new Greenhouse();
                }
                if (user.greenhouse.getUnlockPotCount() + quantity > Shop.LIMIT_OF_POT) {
                    return failure("Greenhouse is full. Max pots is 20.");
                }
                int totalCost = item.getPrice() * quantity;
                if (user.coins < totalCost) {
                    return failure("Not enough coins.");
                }
                for (int i = 0; i < quantity; i++) {
                    user.greenhouse.unlockSlot();
                }
                user.coins -= totalCost;
                storageManager.saveProgress();
                return success("Purchased " + quantity + " pot(s).");
            }
            case "seed pack random" -> {
                int totalCost = item.getPrice() * quantity;
                if (user.coins < totalCost) {
                    return failure("Not enough coins.");
                }
                for (int i = 0; i < quantity; i++) {
                    PlantType randomPlant = shop.pickRandomUnlockedPlant();
                    if (randomPlant == null) {
                        return failure("No unlocked plants available.");
                    }
                    user.addSeedPackets(randomPlant, item.getPurchaseUnit());
                }
                user.coins -= totalCost;
                storageManager.saveProgress();
                return success("Purchased " + quantity + " seed pack.");
            }
            case "plant food" -> {
                int currentPlantFood = user.plantFood;
                if (currentPlantFood + quantity > Shop.LIMIT_OF_PLANT_FOOD) {
                    return failure("Plant food is full. Max pots is 3.");
                }
                int totalCost = item.getPrice() * quantity;
                if (user.gems < totalCost) {
                    return failure("Not enough gems.");
                }
                for (int i = 0; i < quantity; i++) {
                    user.plantFood += item.getPurchaseUnit();
                }
                user.gems -= totalCost;
                storageManager.saveProgress();
                return success("Purchased " + quantity + " plant food.");
            }
            case "gem to coin" -> {
                int totalCost = item.getPrice() * quantity;
                if (user.gems < totalCost) {
                    return failure("Not enough gems.");
                }
                int totalGain = item.getPurchaseUnit() * quantity;
                user.gems -= totalCost;
                user.coins += totalGain;
                storageManager.saveProgress();
                return success("Purchased " + quantity + " coins.");
            }
            default -> {
                return failure("Unknown shop item.");
            }


        }
    }

    public CommandResult buy(String itemId, int quantity, PlantType plantType) {
        User user = storageManager.getCurrentUser();
        if (user == null) {
            return failure("You must be logged in.");
        }
        if (!itemId.equals("seed pack selectable"))
            return failure("Unknown shop item.");
        shop.ensureDailyFresh();
        if (quantity <= 0) {
            return failure("Quantity must be positive.");
        }
        ShopItems shopItemEnum = ShopItems.getFromId(itemId);
        if (shopItemEnum == null) {
            return failure("Unknown shop item.");
        }
        ShopItem item = shopItemEnum.getShopItem();
        int totalCost = item.getPrice() * quantity;
        if (user.gems < totalCost) {
            return failure("Not enough gems.");
        }
        if (plantType == null) {
            return failure("Invalid plant type.");
        }
        if (!user.collection.isPlantUnlocked(plantType)) {
            return failure("Plant is not unlocked.");
        }
        for (int i = 0; i < quantity; i++) {
            user.addSeedPackets(plantType, item.getPurchaseUnit());
        }
        user.gems -= totalCost;
        storageManager.saveProgress();
        return success("Purchased " + quantity + " seed pack for " + plantType.name + ".");
    }

    public ShopDisplayMode getShopDisplayMode() {
        return shopDisplayMode;
    }

    public void setShopDisplayMode(ShopDisplayMode shopDisplayMode) {
        this.shopDisplayMode = shopDisplayMode;
    }

    private CommandResult success(String message) {
        return new CommandResult(message, true);
    }

    private CommandResult failure(String message) {
        return new CommandResult(message, false);
    }
}
