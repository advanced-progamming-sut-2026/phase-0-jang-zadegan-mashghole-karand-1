package model.storage.user;

import model.minigame.MinigameType;
import model.world.ChapterType;

import java.util.HashSet;
import java.util.Set;

public class GameProgress {
    private final Set<String> completedLevels = new HashSet<>();
    private final Set<ChapterType> unlockedChapters = new HashSet<>();
    private final Set<MinigameType> unlockedMinigames = new HashSet<>();

    public boolean isChapterUnlocked(ChapterType chapter) {
        return unlockedChapters.contains(chapter);
    }

    public void unlockChapter(ChapterType chapter) {
        unlockedChapters.add(chapter);
    }

    public Set<ChapterType> getUnlockedChapters() {
        return unlockedChapters;
    }
}
