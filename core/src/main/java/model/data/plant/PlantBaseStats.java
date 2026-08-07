package model.data.plant;

public class PlantBaseStats {
    public final int cost;
    public final int hp;
    public final int damage;
    public final float actionInterval;
    public final float recharge;

    public PlantBaseStats(int cost, int hp, int damage, float actionInterval, float recharge) {
        this.cost = cost;
        this.hp = hp;
        this.damage = damage;
        this.actionInterval = actionInterval;
        this.recharge = recharge;
    }
}