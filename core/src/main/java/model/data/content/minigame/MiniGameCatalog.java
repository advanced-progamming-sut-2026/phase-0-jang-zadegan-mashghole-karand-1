package model.data.content.minigame;

import java.util.List;

import model.data.content.chapter.ChapterType;
import model.data.wave.LevelConfig;
import model.data.zombie.ZombieType;

public final class MiniGameCatalog {
    private static final List<ZombieType> BOWLING_ZOMBIES = List.of(
            ZombieType.BASIC,
            ZombieType.CONE_HEAD,
            ZombieType.BUCKET_HEAD,
            ZombieType.IMP);

    private MiniGameCatalog() {
    }

    public static boolean isPlayable(MiniGameType type) {
        return type == MiniGameType.VASE_BREAKER
                || type == MiniGameType.WALLNUT_BOWLING
                || type == MiniGameType.I_ZOMBIE;
    }

    public static LevelConfig levelConfig(MiniGameType type) {
        if (type == null) {
            return null;
        }
        return switch (type) {
            case WALLNUT_BOWLING -> LevelConfig.builder(ChapterType.ANCIENT_EGYPT, 1)
                    .zombies(BOWLING_ZOMBIES)
                    .waves(5)
                    .startingSun(0)
                    .build();
            case VASE_BREAKER, I_ZOMBIE, BEGHOULED, ZOMBOTANY -> LevelConfig.builder(ChapterType.ANCIENT_EGYPT, 1)
                    .waves(1)
                    .startingSun(150)
                    .build();
        };
    }
}
