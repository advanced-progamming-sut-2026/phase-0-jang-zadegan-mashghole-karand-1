package model.rule.rules.specialLevel;

import model.core.EventBus;
import model.core.GameState;
import model.core.SessionEnd;
import model.data.plant.Plant;
import model.events.GameOverReason;
import model.rule.LevelRule;
import model.rule.SessionContext;

public class LoveYourPlantsRules implements LevelRule {
    private static final int MAX_PLANT_DEATHS = 5;
    private int plantDeaths = 0;

    @Override
    public void onSessionStart(SessionContext context, GameState state, EventBus bus) {
        plantDeaths = 0;
    }

    @Override
    public void onPlantDied(Plant plant, GameState state, EventBus bus) {
        plantDeaths++;

        if (plantDeaths >= MAX_PLANT_DEATHS) {
            SessionEnd.lose(state, bus, GameOverReason.PLANT_DEATH_LIMIT);
        }
    }
}
