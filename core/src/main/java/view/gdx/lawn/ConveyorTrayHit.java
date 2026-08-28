package view.gdx.lawn;

public final class ConveyorTrayHit {
    public static final ConveyorTrayHit MISS = new ConveyorTrayHit(false, -1);

    private final boolean hit;
    private final int slotIndex;

    private ConveyorTrayHit(boolean hit, int slotIndex) {
        this.hit = hit;
        this.slotIndex = slotIndex;
    }

    public static ConveyorTrayHit beltArea() {
        return new ConveyorTrayHit(true, -1);
    }

    public static ConveyorTrayHit slot(int index) {
        return new ConveyorTrayHit(true, index);
    }

    public boolean isHit() {
        return hit;
    }

    public int slotIndex() {
        return slotIndex;
    }

    public boolean isSlot() {
        return hit && slotIndex >= 0;
    }
}
