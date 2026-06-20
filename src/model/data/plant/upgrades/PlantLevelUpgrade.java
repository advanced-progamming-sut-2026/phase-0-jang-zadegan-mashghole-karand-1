package model.data.plant.upgrades;

public class PlantLevelUpgrade {
    public final int level;
    public final PlantStatBonus stat;
    public final Object value;

    public PlantLevelUpgrade(int level, PlantStatBonus stat, Object value) {
        this.level = level;
        this.stat = stat;
        this.value = value;
    }

    public static PlantLevelUpgrade atLevel(int level, PlantStatBonus stat, int value) {
        return new PlantLevelUpgrade(level, stat, value);
    }

    public static PlantLevelUpgrade atLevel(int level, PlantStatBonus stat, boolean value) {
        return new PlantLevelUpgrade(level, stat, value);
    }

    public static PlantLevelUpgrade atLevel(int level, PlantStatBonus stat, String value) {
        return new PlantLevelUpgrade(level, stat, value);
    }

    public int getIntValue() {
        return (int) value;
    }

    public boolean getBoolValue() {
        return (boolean) value;
    }

    public String getStringValue() {
        return (String) value;
    }
}
