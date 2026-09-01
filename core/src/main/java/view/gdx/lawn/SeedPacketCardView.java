package view.gdx.lawn;

import model.service.HudViewState;

public final class SeedPacketCardView {
    public final String plantName;
    public final int cost;
    public final int level;
    public final boolean showCost;
    public final boolean ready;
    public final float cooldownFraction;
    public final boolean highlighted;
    public final boolean selected;
    public final boolean boosted;
    public final boolean hasStoredBoost;
    public final boolean affordable;
    public final boolean locked;
    public final boolean showFamilyIcon;
    public final boolean showReadyBar;

    public SeedPacketCardView(String plantName, int cost, int level, boolean showCost,
            boolean ready, float cooldownFraction, boolean highlighted, boolean selected,
            boolean boosted, boolean hasStoredBoost, boolean affordable) {
        this(plantName, cost, level, showCost, ready, cooldownFraction, highlighted, selected,
                boosted, hasStoredBoost, affordable, false, false, false);
    }

    public SeedPacketCardView(String plantName, int cost, int level, boolean showCost,
            boolean ready, float cooldownFraction, boolean highlighted, boolean selected,
            boolean boosted, boolean hasStoredBoost, boolean affordable,
            boolean locked, boolean showFamilyIcon, boolean showReadyBar) {
        this.plantName = plantName;
        this.cost = cost;
        this.level = level;
        this.showCost = showCost;
        this.ready = ready;
        this.cooldownFraction = cooldownFraction;
        this.highlighted = highlighted;
        this.selected = selected;
        this.boosted = boosted;
        this.hasStoredBoost = hasStoredBoost;
        this.affordable = affordable;
        this.locked = locked;
        this.showFamilyIcon = showFamilyIcon;
        this.showReadyBar = showReadyBar;
    }

    public boolean isEmpty() {
        return plantName == null || plantName.isBlank();
    }

    public static SeedPacketCardView empty() {
        return new SeedPacketCardView(null, 0, 1, false, true, 0f, false, false, false, false, true);
    }

    public static SeedPacketCardView fromTraySlot(HudViewState.TraySlot slot, boolean showSun,
            int sunAmount, String selectedPlantName, boolean boosted) {
        if (slot == null) {
            return empty();
        }
        boolean affordable = !showSun || sunAmount >= slot.cost;
        boolean usable = slot.ready && affordable;
        boolean selected = usable && selectedPlantName != null && selectedPlantName.equals(slot.name);
        return new SeedPacketCardView(
                slot.name,
                slot.cost,
                slot.level,
                showSun,
                slot.ready,
                slot.cooldownFraction,
                slot.highlighted,
                selected,
                boosted,
                false,
                affordable,
                false,
                false,
                false);
    }
}
