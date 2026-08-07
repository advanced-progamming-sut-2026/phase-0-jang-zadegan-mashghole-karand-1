package model.shop;

public enum ShopItems {
    POT(new ShopItem("pot" , "Pot",2000,ShopCurrency.COIN,
            "Opens one slot in the greenhouse"  , 1)),
    PLANT_FOOD(new ShopItem("plant food","Plant food", 3,ShopCurrency.GEM,
            "You can hold up to 3 at a time.",1)),
    SEED_PACK_RANDOM(new ShopItem("seed pack random","Random Seed Pack",1000,ShopCurrency.COIN,
            "Unlocks one random plant from your unlocked plants.",5)),
    SEED_PACK_SELECTABLE(new ShopItem("seed pack selectable", "Selectable Seed Pack", 5,
            ShopCurrency.GEM,
            "Choose one plant from your unlocked plants.",
            10)),

    GEM_TO_COIN(new ShopItem("gem to coin", "Gem to Coin Exchange", 5, ShopCurrency.GEM,
            "Converts 5 gems into 500 coins.",
            500));


    private final ShopItem shopItem;

    ShopItems(ShopItem shopItem) {
        this.shopItem = shopItem;
    }

    public ShopItem getShopItem() {
        return shopItem;
    }
    public static ShopItems getFromId(String id){
        for (ShopItems items : ShopItems.values()){
            if (items.shopItem.getId().equals(id))
                return items;
        }
        return null;
    }
}
