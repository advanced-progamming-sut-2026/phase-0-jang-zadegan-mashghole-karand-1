package model.events;

public enum GameOverReason {
    LAWN_BREACHED("A zombie reached your house!"),
    NO_RESOURCES("Not enough sun to continue."),
    PLANT_DEATH_LIMIT("Too many plants died."),
    PROTECTED_PLANT_DIED("A protected plant was destroyed."),
    DEADLINE_REACHED("A zombie crossed the deadline!");

    public final String message;

    GameOverReason(String message) {
        this.message = message;
    }
}
