package model.storage.user;

import model.data.content.chapter.ChapterCatalog;
import model.data.content.chapter.ChapterType;
import model.data.content.minigame.MiniGameType;

import java.util.HashSet;
import java.util.Set;
import java.util.Collections;

public class GameProgress {
    private final Set<String> completedLevels = new HashSet<>();
    private final Set<ChapterType> unlockedChapters = new HashSet<>();
    private final Set<MiniGameType> unlockedMinigames = new HashSet<>();
    private ChapterType lastChapter = ChapterType.ANCIENT_EGYPT;
    private int lastLevel = 1;
    public boolean isChapterUnlocked(ChapterType chapter) {
        return unlockedChapters.contains(chapter);
    }

    public void unlockChapter(ChapterType chapter) {
        unlockedChapters.add(chapter);
    }

    public void unlockMinigame(MiniGameType minigame) {
        unlockedMinigames.add(minigame);
    }

    public boolean isMinigameUnlocked(MiniGameType minigame) {
        return unlockedMinigames.contains(minigame);
    }

    public Set<MiniGameType> getUnlockedMinigames() {
        return unlockedMinigames;
    }

    public Set<ChapterType> getUnlockedChapters() {
        return unlockedChapters;
    }

    public void completeLevel(String levelId) {
        if (levelId != null && !levelId.isBlank()) {
            completedLevels.add(levelId);
        }
    }

    public int getCompletedLevelCount() {
        return completedLevels.size();
    }

    public Set<String> getCompletedLevelIds() {
        return Collections.unmodifiableSet(completedLevels);
    }
    public void setLastProgress(ChapterType chapter, int level) {
        if (chapter == null) return;
        if (level < 1 || level > ChapterCatalog.LEVELS_PER_CHAPTER) return;

        int newChapter = chapter.ordinal() + 1;
        int oldChapter = lastChapter.ordinal() + 1;
        if (newChapter > oldChapter
                || (newChapter == oldChapter && level > lastLevel)) {
            lastChapter = chapter;
            lastLevel = level;
        }
    }
    public int getLastChapter() {
        return lastChapter.ordinal() + 1;
    }

    public int getLastLevel() {
        return lastLevel;
    }
}
