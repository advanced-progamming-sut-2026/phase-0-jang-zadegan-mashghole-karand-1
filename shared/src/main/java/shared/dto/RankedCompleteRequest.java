package shared.dto;

public final class RankedCompleteRequest {
    public String date;
    public boolean won;
    public int score;

    public RankedCompleteRequest() {
    }

    public RankedCompleteRequest(String date, boolean won, int score) {
        this.date = date;
        this.won = won;
        this.score = score;
    }
}
