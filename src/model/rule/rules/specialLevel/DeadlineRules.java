package model.rule.rules.specialLevel;

import model.core.EventBus;
import model.core.GameState;
import model.core.SessionEnd;
import model.data.zombie.Zombie;
import model.event.events.GameOverReason;
import model.rule.LevelRule;
import model.rule.SessionContext;

public class DeadlineRules implements LevelRule {
    public static final int DEFAULT_DEADLINE_COLUMN = 2;

    private final int deadlineColumn;

    public DeadlineRules() {
        this(DEFAULT_DEADLINE_COLUMN);
    }

    public DeadlineRules(int deadlineColumn) {
        this.deadlineColumn = deadlineColumn;
    }

    @Override
    public void postTick(SessionContext context, GameState state, EventBus bus) {
        if (state.gameOver || state.levelComplete) {
            return;
        }

        for (Zombie zombie : state.zombies) {
            if (!zombie.isAlive || zombie.isHypnotized) {
                continue;
            }
            if (zombie.col <= deadlineColumn) {
                SessionEnd.lose(state, bus, GameOverReason.DEADLINE_REACHED);
                return;
            }
        }
    }
}
