package pvz.model.user;

import pvz.model.minigame.MinigameType;
import pvz.model.world.ChapterType;

import java.util.Set;

public class GameProgress {

    private Set<String> completedLevels;
    private Set<ChapterType> unlockedChapters;
    private Set<MinigameType> unlockedMinigames;
}
