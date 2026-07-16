package model.data.wave;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import model.data.content.chapter.ChapterType;
import model.data.zombie.ZombieType;

public class LevelConfig {
    public final ChapterType chapterType;
    public final int levelNumber;
    public final int totalWaves;
    public final int wavePointBase;
    public final int startingSun;
    public final List<ZombieType> availableZombies;

    public LevelConfig(ChapterType chapterType, int levelNumber, int totalWaves,
            int wavePointBase,
            int startingSun, List<ZombieType> availableZombies) {
        this.chapterType = chapterType;
        this.levelNumber = levelNumber;
        this.totalWaves = totalWaves;
        this.wavePointBase = wavePointBase;
        this.startingSun = startingSun;
        this.availableZombies = Collections.unmodifiableList(availableZombies);
    }

    public static LevelConfig createDefault(ChapterType chapterType, int levelNumber) {
        return new LevelConfig(
                chapterType,
                levelNumber,
                5,
                100,
                150,
                Arrays.asList(ZombieType.BASIC, ZombieType.CONE_HEAD));
    }
}