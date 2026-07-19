package model.data.wave;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import model.data.content.chapter.ChapterType;
import model.data.content.specialLevel.SpecialLevelType;
import model.data.plant.PlantType;
import model.data.zombie.ZombieType;

public class LevelConfig {
    public final ChapterType chapterType;
    public final int levelNumber;
    public final int totalWaves;
    public final int wavePointBase;
    public final int startingSun;
    public final List<ZombieType> availableZombies;
    public final List<PlantType> availablePlants;
    public final SpecialLevelType specialLevelType;

    public LevelConfig(ChapterType chapterType, int levelNumber, int totalWaves,
            int wavePointBase, int startingSun,
            List<ZombieType> availableZombies,
            List<PlantType> availablePlants,
            SpecialLevelType specialLevelType) {
        this.chapterType = chapterType;
        this.levelNumber = levelNumber;
        this.totalWaves = totalWaves;
        this.wavePointBase = wavePointBase;
        this.startingSun = startingSun;
        this.availableZombies = availableZombies != null
                ? Collections.unmodifiableList(new ArrayList<>(availableZombies))
                : List.of();
        this.availablePlants = availablePlants != null
                ? Collections.unmodifiableList(new ArrayList<>(availablePlants))
                : List.of();
        this.specialLevelType = specialLevelType;
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
        private List<PlantType> availablePlants = List.of();
        private List<ZombieType> availableZombies = List.of();
        private SpecialLevelType specialLevelType = null;
        private int totalWaves = 5;
        private int wavePointBase = 100;
        private int startingSun = 150;

        private Builder(ChapterType chapterType, int levelNumber) {
            this.chapterType = chapterType;
            this.levelNumber = levelNumber;
        }

        public Builder plants(List<PlantType> plants) {
            this.availablePlants = plants;
            return this;
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

        public Builder wavePointBase(int wavePointBase) {
            this.wavePointBase = wavePointBase;
            return this;
        }

        public Builder startingSun(int startingSun) {
            this.startingSun = startingSun;
            return this;
        }

        public LevelConfig build() {
            return new LevelConfig(
                    chapterType,
                    levelNumber,
                    totalWaves,
                    wavePointBase,
                    startingSun,
                    availableZombies,
                    availablePlants,
                    specialLevelType);
        }
    }
}
