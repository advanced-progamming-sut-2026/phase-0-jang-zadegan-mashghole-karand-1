package model.shop;

import model.data.plant.PlantType;
import model.storage.user.User;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Shop {

    private List<ShopItems> permanentItems;
    private User user;
    public final static int LIMIT_OF_POT = 20;
    public final static int LIMIT_OF_PLANT_FOOD = 3;
    public Shop(User user) {
        this.permanentItems = Arrays.asList(ShopItems.values());
        this.user = user;
    }


    public void ensureDailyFresh() {
        LocalDate today = LocalDate.now();
        if (user.dailyDeal.lastRefreshDate == null || !user.dailyDeal.lastRefreshDate.equals(today)) {
            refreshDailyDeal();
            user.dailyDeal.lastRefreshDate = today;
        }
    }
    private void refreshDailyDeal() {
        PlantType plant = pickRandomUnlockedPlant();
        user.dailyDeal.dailyDealPlant = plant;
        user.dailyDeal.dailyDealPurchased = false;
        user.dailyDeal.dailyDealPrice = 1600;
    }

    public PlantType pickRandomUnlockedPlant() {
        List<PlantType> unlocked = Arrays.stream(PlantType.values())
                .filter(p -> user.collection.isPlantUnlocked(p))
                .filter(p -> !p.isBowlingExclusive())
                .toList();
        if (unlocked.isEmpty()) {
            return null;
        }
        return unlocked.get(ThreadLocalRandom.current().nextInt(unlocked.size()));
    }
    public List<ShopItems> getPermanentItems() {
        return permanentItems;
    }

    public User getUser() {
        return user;
    }
}
