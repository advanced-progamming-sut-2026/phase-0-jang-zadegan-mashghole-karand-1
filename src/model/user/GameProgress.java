package model.user;

import model.minigame.MinigameType;
import model.world.ChapterType;

import java.util.Set;

public class GameProgress {

    private Set<String> completedLevels;
    private Set<ChapterType> unlockedChapters;
    private Set<MinigameType> unlockedMinigames;
}
