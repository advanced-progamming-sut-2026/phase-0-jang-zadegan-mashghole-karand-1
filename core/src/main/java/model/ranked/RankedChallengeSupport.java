package model.ranked;

import java.util.ArrayList;
import java.util.List;

import model.data.content.chapter.ChapterType;
import model.data.wave.LevelConfig;
import model.data.zombie.ZombieType;
import shared.dto.RankedChallengeDto;

public final class RankedChallengeSupport {
    private RankedChallengeSupport() {
    }

    public static LevelConfig toLevelConfig(RankedChallengeDto challenge) {
        if (challenge == null) {
            throw new IllegalArgumentException("challenge");
        }
        ChapterType chapter = ChapterType.valueOf(challenge.chapter);
        List<ZombieType> zombies = new ArrayList<>();
        if (challenge.availableZombies != null) {
            for (String name : challenge.availableZombies) {
                ZombieType type = ZombieType.fromName(name);
                if (type != null) {
                    zombies.add(type);
                }
            }
        }
        if (zombies.isEmpty()) {
            zombies.add(ZombieType.BASIC);
            zombies.add(ZombieType.CONE_HEAD);
        }
        return LevelConfig.builder(chapter, Math.max(1, challenge.levelNumber))
                .waves(Math.max(1, challenge.totalWaves))
                .startingSun(Math.max(25, challenge.startingSun))
                .zombies(zombies)
                .build();
    }
}
