package controller;

import controller.CommandResult.CommandResult;
import model.data.plant.PlantType;
import model.shop.Shop;

public class ShopController {
    private Shop shop;

    public ShopController(Shop shop) {
        this.shop = shop;
    }

    public String List() {
        return "";
    }

    public String daily() {
        return "";
    }

    public CommandResult buy(String itemId, int quantity) {
        return null;
    }

    public CommandResult buy(String itemId, int quantity, PlantType plantType) {
        return null;
    }
}
