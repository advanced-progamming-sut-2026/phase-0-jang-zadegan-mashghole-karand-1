package model.rule.rules;

import java.util.ArrayList;
import java.util.List;

import model.data.content.chapter.ChapterType;
import model.rule.LevelRule;

public class ChapterRules {

    public static List<LevelRule> forChapter(ChapterType chapterType) {
        List<LevelRule> rules = new ArrayList<>();

        switch (chapterType) {
            case ANCIENT_EGYPT:
                // will implement later
                break;
            case FROSTBITE_CAVES:
                // will implement later
                break;
            case BIG_WAVE_BEACH:
                // will implement later
                break;
            case DARK_AGES:
                // will implement later
                break;
            default:
                // no rules
                break;
        }

        return rules;
    }
}