package model.storage.user;

import model.data.content.chapter.ChapterType;
import model.minigame.MinigameType;

import java.util.HashSet;
import java.util.Set;
import java.util.Collections;

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

    public void unlockMinigame(MinigameType minigame) {
        unlockedMinigames.add(minigame);
    }

    public boolean isMinigameUnlocked(MinigameType minigame) {
        return unlockedMinigames.contains(minigame);
    }

    public Set<MinigameType> getUnlockedMinigames() {
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
}
