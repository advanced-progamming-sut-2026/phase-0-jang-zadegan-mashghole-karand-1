package model.shop;

public class ShopItem {

    private String id;
    private String name;
    private int price;
    private ShopCurrency currency;
    private String description;
    private int  purchaseUnit;

    public ShopItem(String id, String name, int price,ShopCurrency currency ,String description, int  purchaseUnit) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this. purchaseUnit =  purchaseUnit;
        this.currency = currency;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public int getPurchaseUnit() {
        return purchaseUnit;
    }

    public ShopCurrency getCurrency() {
        return currency;
    }
}
