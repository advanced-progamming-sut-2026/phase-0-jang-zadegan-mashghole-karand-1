package model.data.content.specialLevel;

public final class SpecialLevelCatalog {

    private SpecialLevelCatalog() {
    }

    public static boolean skipsPlantSelection(SpecialLevelType type) {
        return type == SpecialLevelType.CONVEYOR_BELT;
    }
}
