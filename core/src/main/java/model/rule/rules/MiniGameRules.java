package model.rule.rules;

import java.util.ArrayList;
import java.util.List;

import model.data.content.minigame.MiniGameType;
import model.rule.LevelRule;
import model.rule.SessionConfig;
import model.rule.rules.minigame.IZombiePvPRules;
import model.rule.rules.minigame.IZombieRules;
import model.rule.rules.minigame.VaseBreakerRules;
import model.rule.rules.minigame.WallnutBowlingRules;
import shared.izombie.IZombiePlayMode;

public class MiniGameRules {

    public static List<LevelRule> forMiniGame(MiniGameType miniGameType) {
        return forMiniGame(miniGameType, null);
    }

    public static List<LevelRule> forMiniGame(MiniGameType miniGameType, SessionConfig config) {
        List<LevelRule> rules = new ArrayList<>();

        switch (miniGameType) {
            case VASE_BREAKER:
                rules.add(new VaseBreakerRules());
                break;
            case WALLNUT_BOWLING:
                rules.add(new WallnutBowlingRules());
                break;
            case I_ZOMBIE:
                if (config != null && config.iZombiePlayMode != null
                        && config.iZombiePlayMode != IZombiePlayMode.OFFLINE) {
                    rules.add(new IZombiePvPRules());
                } else {
                    rules.add(new IZombieRules());
                }
                break;
            case BEGHOULED:
                break;
            case ZOMBOTANY:
                break;
            default:
                break;
        }

        return rules;
    }
}
