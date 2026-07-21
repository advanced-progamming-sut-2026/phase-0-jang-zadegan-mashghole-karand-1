package model.rule.rules;

import java.util.List;
import java.util.ArrayList;

import model.data.content.minigame.MiniGameType;
import model.rule.LevelRule;
import model.rule.rules.minigame.VaseBreakerRules;
import model.rule.rules.minigame.WallnutBowlingRules;

public class MiniGameRules {

    public static List<LevelRule> forMiniGame(MiniGameType miniGameType) {
        List<LevelRule> rules = new ArrayList<>();

        switch (miniGameType) {
            case VASE_BREAKER:
                rules.add(new VaseBreakerRules());
                break;
            case WALLNUT_BOWLING:
                rules.add(new WallnutBowlingRules());
                break;
            case I_ZOMBIE:
                // will implement later
                break;
            case BEGHOULED:
                // will implement later
                break;
            case ZOMBOTANY:
                // will implement later
                break;
            default:
                // no rules
                break;
        }

        return rules;
    }
}
