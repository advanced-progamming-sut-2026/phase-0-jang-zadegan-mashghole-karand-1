package model.rule.rules.specialLevel;

import model.rule.LevelRule;

public class NightOpsRules implements LevelRule {

    @Override
    public boolean shouldDropSkySun() {
        return false;
    }

}
