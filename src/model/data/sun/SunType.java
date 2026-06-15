package model.data.sun;

public enum SunType {
    NORMAL(25, 80),
    SPECIAL(15, 100),
    RADIO_ACTIVE(0, 5);

    public final int amount;
    public final int spawnChance;

    SunType(int amount, int spawnChance) {
        this.amount = amount;
        this.spawnChance = spawnChance;
    }
}
