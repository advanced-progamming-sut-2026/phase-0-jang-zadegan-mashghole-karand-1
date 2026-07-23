package model.rule.rules;

import java.util.List;
import java.util.ArrayList;

import model.data.content.specialLevel.SpecialLevelType;
import model.data.wave.LevelConfig;
import model.rule.LevelRule;
import model.rule.rules.specialLevel.ConveyorBeltRules;
import model.rule.rules.specialLevel.DeadlineRules;
import model.rule.rules.specialLevel.LockedPlantsRules;
import model.rule.rules.specialLevel.LoveYourPlantsRules;
import model.rule.rules.specialLevel.NightOpsRules;
import model.rule.rules.specialLevel.PlantWhatYouGetRules;
import model.rule.rules.specialLevel.SaveOurSeedsRules;
import model.rule.rules.specialLevel.TimedWarRules;

public class SpecialLevelRules {

    public static List<LevelRule> forSpecialLevel(SpecialLevelType specialLevelType) {
        return forSpecialLevel(specialLevelType, null);
    }

    public static List<LevelRule> forSpecialLevel(SpecialLevelType specialLevelType, LevelConfig levelConfig) {
        List<LevelRule> rules = new ArrayList<>();

        switch (specialLevelType) {
            case CONVEYOR_BELT:
                rules.add(new ConveyorBeltRules());
                break;
            case LOCKED_PLANTS:
                rules.add(new LockedPlantsRules(
                        levelConfig != null ? levelConfig.lockedPlantsConfig : null));
                break;
            case SAVE_OUR_SEEDS:
                rules.add(new SaveOurSeedsRules());
                break;
            case TIMED_WAR:
                rules.add(new TimedWarRules());
                break;
            case NIGHT_OPS:
                rules.add(new NightOpsRules());
                break;
            case DEAD_LINE:
                rules.add(new DeadlineRules());
                break;
            case LOVE_YOUR_PLANTS:
                rules.add(new LoveYourPlantsRules());
                break;
            case PLANT_WHAT_YOU_GET:
                rules.add(new PlantWhatYouGetRules());
                break;
            default:
                break;
        }

        return rules;
    }
}
