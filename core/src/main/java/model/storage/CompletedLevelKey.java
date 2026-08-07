package model.storage;

import model.data.content.chapter.ChapterType;
import model.data.content.minigame.MiniGameType;

public final class CompletedLevelKey {
    private CompletedLevelKey() {
    }

    public static String campaign(ChapterType chapter, int levelNumber) {
        return chapter.name() + "_" + levelNumber;
    }

    public static String minigame(MiniGameType miniGameType) {
        return "MINIGAME_" + miniGameType.name();
    }
}
