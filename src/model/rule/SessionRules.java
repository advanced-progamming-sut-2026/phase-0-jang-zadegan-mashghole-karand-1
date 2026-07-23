package model.rule;

import java.util.ArrayList;
import java.util.List;

import model.rule.rules.ChapterRules;
import model.rule.rules.MiniGameRules;
import model.rule.rules.SpecialLevelRules;

public final class SessionRules {
    private SessionRules() {
    }

    public static List<LevelRule> resolve(SessionConfig config) {
        List<LevelRule> rules = new ArrayList<>();
        if (config == null || config.levelConfig == null) {
            return rules;
        }

        if (!config.isMinigame()) {
            rules.addAll(ChapterRules.forChapter(config.levelConfig.chapterType));
        }
        if (config.isSpecial()) {
            rules.addAll(SpecialLevelRules.forSpecialLevel(config.specialLevelType));
        }
        if (config.isMinigame()) {
            rules.addAll(MiniGameRules.forMiniGame(config.miniGameType));
        }
        return rules;
    }

    public static boolean skipsPlantSelection(SessionConfig config) {
        for (LevelRule rule : resolve(config)) {
            if (rule.skipsPlantSelection()) {
                return true;
            }
        }
        return false;
    }
}
