package shared.dto;

import java.util.ArrayList;
import java.util.List;

public final class RankedChallengeDto {
    public String date;
    public long seed;
    public String chapter;
    public int levelNumber;
    public int totalWaves;
    public int startingSun;
    public List<String> availableZombies = new ArrayList<>();
    public List<RankedWaveDto> waves = new ArrayList<>();

    public RankedChallengeDto() {
    }
}
