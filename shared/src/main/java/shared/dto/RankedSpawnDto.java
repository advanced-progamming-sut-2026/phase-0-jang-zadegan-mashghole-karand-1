package shared.dto;

public final class RankedSpawnDto {
    public String zombieType;
    public int count;
    public int row;

    public RankedSpawnDto() {
    }

    public RankedSpawnDto(String zombieType, int count, int row) {
        this.zombieType = zombieType;
        this.count = count;
        this.row = row;
    }
}
