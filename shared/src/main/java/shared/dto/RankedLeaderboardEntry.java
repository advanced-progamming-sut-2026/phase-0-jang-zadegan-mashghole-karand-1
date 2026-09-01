package shared.dto;

public final class RankedLeaderboardEntry {
    public int rank;
    public String username;
    public int score;

    public RankedLeaderboardEntry() {
    }

    public RankedLeaderboardEntry(int rank, String username, int score) {
        this.rank = rank;
        this.username = username;
        this.score = score;
    }
}
