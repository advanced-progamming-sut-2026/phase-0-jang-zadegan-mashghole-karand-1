package model.rule.rules;

import java.util.List;
import java.util.ArrayList;

import model.data.content.specialLevel.SpecialLevelType;
import model.rule.LevelRule;

public class SpecialLevelRules {

    public static List<LevelRule> forSpecialLevel(SpecialLevelType specialLevelType) {
        List<LevelRule> rules = new ArrayList<>();

        switch (specialLevelType) {
            case CONVEYOR_BELT:
                // will implement later
                break;
            case LOCKED_PLANTS:
                // will implement later
                break;
            case SAVE_OUR_SEEDS:
                // will implement later
                break;
            case TIMED_WAR:
                // will implement later
                break;
            case NIGHT_OPS:
                // will implement later
                break;
            case DEAD_LINE:
                // will implement later
                break;
            case LOVE_YOUR_PLANTS:
                // will implement later
                break;
            case PLANT_WHAT_YOU_GET:
                // will implement later
                break;
            default:
                // no rules
                break;
        }

        return rules;
    }
}
