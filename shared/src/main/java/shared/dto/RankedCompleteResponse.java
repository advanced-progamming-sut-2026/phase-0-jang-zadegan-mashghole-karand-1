package shared.dto;

public final class RankedCompleteResponse {
    public boolean ok;
    public String error;
    public int highestScore;
    public boolean newRecord;

    public static RankedCompleteResponse success(int highestScore, boolean newRecord) {
        RankedCompleteResponse r = new RankedCompleteResponse();
        r.ok = true;
        r.highestScore = highestScore;
        r.newRecord = newRecord;
        return r;
    }

    public static RankedCompleteResponse fail(String error) {
        RankedCompleteResponse r = new RankedCompleteResponse();
        r.ok = false;
        r.error = error;
        return r;
    }
}
