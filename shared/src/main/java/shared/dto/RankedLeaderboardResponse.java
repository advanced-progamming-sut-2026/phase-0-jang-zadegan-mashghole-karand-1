package shared.dto;

import java.util.ArrayList;
import java.util.List;

public final class RankedLeaderboardResponse {
    public boolean ok;
    public String error;
    public List<RankedLeaderboardEntry> entries = new ArrayList<>();

    public static RankedLeaderboardResponse success(List<RankedLeaderboardEntry> entries) {
        RankedLeaderboardResponse r = new RankedLeaderboardResponse();
        r.ok = true;
        r.entries = entries != null ? new ArrayList<>(entries) : new ArrayList<>();
        return r;
    }

    public static RankedLeaderboardResponse fail(String error) {
        RankedLeaderboardResponse r = new RankedLeaderboardResponse();
        r.ok = false;
        r.error = error;
        return r;
    }
}
