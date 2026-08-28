package shared.dto;

import shared.izombie.MatchRole;

public final class MatchStatePayload {
    public String roomId;
    public int plantSun;
    public int zombieSun;
    public int elapsedSeconds;
    public int survivalSeconds;
    public boolean gameOver;
    public String winnerRole; // PLANTS / ZOMBIES / null
    public String endReason;
    public java.util.List<PlacedEntity> plants = new java.util.ArrayList<>();
    public java.util.List<PlacedEntity> zombies = new java.util.ArrayList<>();
    public boolean[] brainsCollected = new boolean[5];

    public static final class PlacedEntity {
        public String type;
        public int row;
        public int col;
        public float hp;
        public MatchRole owner;

        public PlacedEntity() {
        }

        public PlacedEntity(String type, int row, int col, float hp, MatchRole owner) {
            this.type = type;
            this.row = row;
            this.col = col;
            this.hp = hp;
            this.owner = owner;
        }
    }
}
