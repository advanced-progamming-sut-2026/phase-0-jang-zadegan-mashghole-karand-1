package model.data.vfx;

public class LawnEffect {
    private static int nextId = 0;

    public final int id;
    public final String pamPath;
    public final String clipName;
    public final int row;
    public final int col;
    public int ticksRemaining;
    public final boolean loop;

    public LawnEffect(String pamPath, String clipName, int row, int col, int ticksRemaining, boolean loop) {
        this.id = nextId++;
        this.pamPath = pamPath;
        this.clipName = clipName;
        this.row = row;
        this.col = col;
        this.ticksRemaining = ticksRemaining;
        this.loop = loop;
    }
}
