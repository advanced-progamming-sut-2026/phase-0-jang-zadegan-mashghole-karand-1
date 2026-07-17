package model.rule.rules;

import java.util.List;
import java.util.ArrayList;

import model.data.content.specialLevel.SpecialLevelType;
import model.rule.LevelRule;
import model.rule.rules.specialLevel.LoveYourPlantsRules;
import model.rule.rules.specialLevel.NightOpsRules;
import model.rule.rules.specialLevel.SaveOurSeedsRules;

public class SpecialLevelRules {

    public static List<LevelRule> forSpecialLevel(SpecialLevelType specialLevelType) {
        List<LevelRule> rules = new ArrayList<>();

        switch (specialLevelType) {
            case CONVEYOR_BELT:
                break;
            case LOCKED_PLANTS:
                break;
            case SAVE_OUR_SEEDS:
                rules.add(new SaveOurSeedsRules());
                break;
            case TIMED_WAR:
                break;
            case NIGHT_OPS:
                rules.add(new NightOpsRules());
                break;
            case DEAD_LINE:
                break;
            case LOVE_YOUR_PLANTS:
                rules.add(new LoveYourPlantsRules());
                break;
            case PLANT_WHAT_YOU_GET:
                break;
            default:
                break;
        }

        return rules;
    }
}
