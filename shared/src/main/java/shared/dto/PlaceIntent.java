package shared.dto;

public final class PlaceIntent {
    public String type;
    public int row;
    public int col;

    public PlaceIntent() {
    }

    public PlaceIntent(String type, int row, int col) {
        this.type = type;
        this.row = row;
        this.col = col;
    }
}
