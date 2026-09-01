package shared.dto;

public final class RankedTodayResponse {
    public boolean ok;
    public String error;
    public RankedChallengeDto challenge;
    public boolean alreadyPlayed;
    public int highestScore;

    public static RankedTodayResponse success(RankedChallengeDto challenge, boolean alreadyPlayed, int highestScore) {
        RankedTodayResponse r = new RankedTodayResponse();
        r.ok = true;
        r.challenge = challenge;
        r.alreadyPlayed = alreadyPlayed;
        r.highestScore = highestScore;
        return r;
    }

    public static RankedTodayResponse fail(String error) {
        RankedTodayResponse r = new RankedTodayResponse();
        r.ok = false;
        r.error = error;
        return r;
    }
}
