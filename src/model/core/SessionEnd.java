package model.core;

import model.event.events.GameOverEvent;
import model.event.events.GameOverReason;
import model.event.events.LevelCompleteEvent;

public final class SessionEnd {
    private SessionEnd() {
    }

    public static void win(GameState state, EventBus bus) {
        if (state.gameOver || state.levelComplete) {
            return;
        }
        state.levelComplete = true;
        state.gameOverReason = null;
        bus.publish(new LevelCompleteEvent());
    }

    public static void lose(GameState state, EventBus bus, GameOverReason reason) {
        if (state.gameOver || state.levelComplete) {
            return;
        }
        state.gameOver = true;
        state.gameOverReason = reason;
        bus.publish(new GameOverEvent(reason));
    }
}
