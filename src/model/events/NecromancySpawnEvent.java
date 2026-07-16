package model.events;

public class NecromancySpawnEvent {
    public final int row;
    public final int col;

    public NecromancySpawnEvent(int row, int col) {
        this.row = row;
        this.col = col;
    }
}
