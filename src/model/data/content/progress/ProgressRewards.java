package model.data.content.progress;

import java.util.List;

import model.data.content.chapter.ChapterCatalog;
import model.data.content.chapter.ChapterType;
import model.data.content.minigame.MiniGameType;

public final class ProgressRewards {
    private ProgressRewards() {
    }

    public static ChapterType nextChapter(ChapterType completedChapter, int levelNumber) {
        if (completedChapter == null || levelNumber < ChapterCatalog.LEVELS_PER_CHAPTER) {
            return null;
        }
        ChapterType[] chapters = ChapterType.values();
        int next = completedChapter.ordinal() + 1;
        if (next >= chapters.length) {
            return null;
        }
        return chapters[next];
    }

    public static List<MiniGameType> minigamesFor(ChapterType completedChapter, int levelNumber) {
        if (completedChapter == null || levelNumber < ChapterCatalog.LEVELS_PER_CHAPTER) {
            return List.of();
        }
        return switch (completedChapter) {
            case ANCIENT_EGYPT -> List.of(MiniGameType.VASE_BREAKER);
            case FROSTBITE_CAVES -> List.of(MiniGameType.WALLNUT_BOWLING);
            case BIG_WAVE_BEACH -> List.of(MiniGameType.I_ZOMBIE);
            case DARK_AGES -> List.of(MiniGameType.BEGHOULED, MiniGameType.ZOMBOTANY);
        };
    }
}
