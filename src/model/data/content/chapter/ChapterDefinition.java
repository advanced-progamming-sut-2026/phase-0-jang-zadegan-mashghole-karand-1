package model.data.content.chapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import model.data.wave.LevelConfig;

public class ChapterDefinition {
    public final ChapterType type;
    public final List<LevelConfig> levels;

    public ChapterDefinition(ChapterType type, List<LevelConfig> levels) {
        this.type = type;
        this.levels = Collections.unmodifiableList(new ArrayList<>(levels));
    }

    public LevelConfig getLevel(int levelNumber) {
        for (LevelConfig level : levels) {
            if (level.levelNumber == levelNumber) {
                return level;
            }
        }
        return null;
    }

    public int getLevelCount() {
        return levels.size();
    }
}
