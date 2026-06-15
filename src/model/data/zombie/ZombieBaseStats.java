package model.data.zombie;

public class ZombieBaseStats {
    public final int hp;
    public final int eatDPS;
    public final float speed;
    public final int wavePointCost;
    public final int weight;

    public ZombieBaseStats(int hp, int eatDPS, float speed, int wavePointCost, int weight) {
        this.hp = hp;
        this.eatDPS = eatDPS;
        this.speed = speed;
        this.wavePointCost = wavePointCost;
        this.weight = weight;
    }
}
