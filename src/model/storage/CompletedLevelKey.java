package model.storage;

import model.data.content.chapter.ChapterType;
import model.data.content.minigame.MiniGameType;

final class CompletedLevelKey {
    private CompletedLevelKey() {
    }

    static String campaign(ChapterType chapter, int levelNumber) {
        return chapter.name() + "_" + levelNumber;
    }

    static String minigame(MiniGameType miniGameType) {
        return "MINIGAME_" + miniGameType.name();
    }
}
