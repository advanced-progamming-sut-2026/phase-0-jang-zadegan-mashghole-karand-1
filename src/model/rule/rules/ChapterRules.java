package model.rule.rules;

import java.util.ArrayList;
import java.util.List;

import model.data.content.chapter.ChapterType;
import model.rule.LevelRule;
import model.rule.rules.chapter.AncientEgyptRules;
import model.rule.rules.chapter.BigWaveBeachRules;
import model.rule.rules.chapter.DarkAgesRules;

public class ChapterRules {

    public static List<LevelRule> forChapter(ChapterType chapterType) {
        List<LevelRule> rules = new ArrayList<>();

        switch (chapterType) {
            case ANCIENT_EGYPT:
                rules.add(new AncientEgyptRules());
                break;
            case FROSTBITE_CAVES:
                rules.add(new AncientEgyptRules());
                break;
            case BIG_WAVE_BEACH:
                rules.add(new BigWaveBeachRules());
                break;
            case DARK_AGES:
                rules.add(new DarkAgesRules());
                break;
            default:
                break;
        }

        return rules;
    }
}