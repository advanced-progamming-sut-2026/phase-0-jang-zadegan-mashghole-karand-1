package shared.dto;

import java.util.ArrayList;
import java.util.List;

public final class RankedWaveDto {
    public List<RankedSpawnDto> spawns = new ArrayList<>();

    public RankedWaveDto() {
    }

    public RankedWaveDto(List<RankedSpawnDto> spawns) {
        this.spawns = spawns != null ? new ArrayList<>(spawns) : new ArrayList<>();
    }
}
