package model.data.plant.upgrades;

public class PlantLevelUpgrade {
    public final int level;
    public final PlantStatBonus stat;
    public final int value;

    public PlantLevelUpgrade(int level, PlantStatBonus stat, int value) {
        this.level = level;
        this.stat = stat;
        this.value = value;
    }

    public static PlantLevelUpgrade atLevel(int level, PlantStatBonus stat, int value) {
        return new PlantLevelUpgrade(level, stat, value);
    }
}
