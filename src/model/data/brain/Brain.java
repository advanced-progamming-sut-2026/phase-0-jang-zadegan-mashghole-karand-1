package model.data.brain;

public class Brain {
    public final int row;
    private boolean collected;

    public Brain(int row) {
        this.row = row;
        this.collected = false;
    }

    public boolean isCollected() {
        return collected;
    }

    public void collect() {
        collected = true;
    }
}
