package model.data.wave;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import model.data.content.chapter.ChapterType;
import model.data.content.specialLevel.LockedPlantsConfig;
import model.data.content.specialLevel.SpecialLevelType;
import model.data.content.specialLevel.TimedWarConfig;
import model.data.zombie.ZombieType;

public class LevelConfig {
    public final ChapterType chapterType;
    public final int levelNumber;
    public final int totalWaves;
    public final int startingSun;
    public final List<ZombieType> availableZombies;
    public final SpecialLevelType specialLevelType;
    public final TimedWarConfig timedWarConfig;
    public final LockedPlantsConfig lockedPlantsConfig;

    public LevelConfig(ChapterType chapterType, int levelNumber, int totalWaves,
            int startingSun,
            List<ZombieType> availableZombies,
            SpecialLevelType specialLevelType,
            TimedWarConfig timedWarConfig,
            LockedPlantsConfig lockedPlantsConfig) {
        this.chapterType = chapterType;
        this.levelNumber = levelNumber;
        this.totalWaves = totalWaves;
        this.startingSun = startingSun;
        this.availableZombies = availableZombies != null
                ? Collections.unmodifiableList(new ArrayList<>(availableZombies))
                : List.of();
        this.specialLevelType = specialLevelType;
        this.timedWarConfig = timedWarConfig;
        this.lockedPlantsConfig = lockedPlantsConfig;
    }

    public boolean isSpecial() {
        return specialLevelType != null;
    }

    public static Builder builder(ChapterType chapterType, int levelNumber) {
        return new Builder(chapterType, levelNumber);
    }

    public static final class Builder {
        private final ChapterType chapterType;
        private final int levelNumber;
        private List<ZombieType> availableZombies = List.of();
        private SpecialLevelType specialLevelType = null;
        private int totalWaves = 5;
        private int startingSun = 150;
        private TimedWarConfig timedWarConfig = null;
        private LockedPlantsConfig lockedPlantsConfig = null;

        private Builder(ChapterType chapterType, int levelNumber) {
            this.chapterType = chapterType;
            this.levelNumber = levelNumber;
        }

        public Builder zombies(List<ZombieType> zombies) {
            this.availableZombies = zombies;
            return this;
        }

        public Builder special(SpecialLevelType type) {
            this.specialLevelType = type;
            return this;
        }

        public Builder waves(int totalWaves) {
            this.totalWaves = totalWaves;
            return this;
        }

        public Builder startingSun(int startingSun) {
            this.startingSun = startingSun;
            return this;
        }

        public Builder timedWar(TimedWarConfig config) {
            this.timedWarConfig = config;
            return this;
        }

        public Builder lockedPlants(LockedPlantsConfig config) {
            this.lockedPlantsConfig = config;
            return this;
        }

        public LevelConfig build() {
            return new LevelConfig(
                    chapterType,
                    levelNumber,
                    totalWaves,
                    startingSun,
                    availableZombies,
                    specialLevelType,
                    timedWarConfig,
                    lockedPlantsConfig);
        }
    }
}
