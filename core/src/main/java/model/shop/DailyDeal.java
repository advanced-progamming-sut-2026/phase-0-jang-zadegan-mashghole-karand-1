package model.shop;

import model.data.plant.PlantType;

import java.time.LocalDate;

public class DailyDeal {
    public LocalDate lastRefreshDate;
    public PlantType dailyDealPlant;
    public boolean dailyDealPurchased;
    public int dailyDealPrice;

    public DailyDeal() {
        this.lastRefreshDate = null;
        this.dailyDealPlant = null;
        this.dailyDealPurchased = false;
        this.dailyDealPrice = 1600;
    }
}