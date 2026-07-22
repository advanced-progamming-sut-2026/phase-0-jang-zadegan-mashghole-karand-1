package model.events;

public class GameOverEvent {
    public final GameOverReason reason;

    public GameOverEvent(GameOverReason reason) {
        this.reason = reason;
    }
}
