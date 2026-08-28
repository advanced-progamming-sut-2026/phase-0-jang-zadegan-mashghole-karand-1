package shared.dto;

import shared.izombie.MatchRole;

public final class MatchStartPayload {
    public String roomId;
    public String opponent;
    public MatchRole yourRole;
    public int survivalSeconds;
    public int plantSun;
    public int zombieSun;

    public MatchStartPayload() {
    }

    public MatchStartPayload(String roomId, String opponent, MatchRole yourRole,
            int survivalSeconds, int plantSun, int zombieSun) {
        this.roomId = roomId;
        this.opponent = opponent;
        this.yourRole = yourRole;
        this.survivalSeconds = survivalSeconds;
        this.plantSun = plantSun;
        this.zombieSun = zombieSun;
    }
}
