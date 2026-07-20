package controller;

import controller.CommandResult.CommandResult;
import model.service.LeaderboardViewState;
import model.storage.StorageManager;
import view.MenuType;
import view.ScreenType;

public class LeaderboardMenuController {
    private final ControllerManager controllerManager;
    private final StorageManager storage;
    private LeaderboardViewState.SortDirection direction = LeaderboardViewState.SortDirection.HTL;
    private LeaderboardViewState.SortColumn column = LeaderboardViewState.SortColumn.SCORE;

    public LeaderboardMenuController(ControllerManager controllerManager, StorageManager storage) {
        this.controllerManager = controllerManager;
        this.storage = storage;
    }

    public CommandResult sort(String sortClass, String sortType) {
        CommandResult screenCheck = controllerManager.requireScreen(ScreenType.MAIN);
        if (screenCheck != null) {
            return screenCheck;
        }
        if (controllerManager.getCurrentMenu() != MenuType.LEADERBOARD) {
            return failure("Open the leaderboard first with: menu enter leaderboard");
        }
        if (sortClass == null || sortType == null) {
            return failure("Usage: menu leaderboard sort -c <SCORE|LEVELS|MINIGAMES> -t <HTL|LTH>");
        }
        try {
            column = LeaderboardViewState.SortColumn.valueOf(sortClass.trim().toUpperCase());
            direction = LeaderboardViewState.SortDirection.valueOf(sortType.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return failure("Invalid sort options. Use -c SCORE|LEVELS|MINIGAMES and -t HTL|LTH.");
        }
        return success("Leaderboard sorted by " + column.name() + " (" + direction.name() + ").");
    }

    public LeaderboardViewState getViewState() {
        return LeaderboardViewState.fromUsers(storage.getUsers(), column, direction);
    }

    public LeaderboardViewState.SortColumn getColumn() {
        return column;
    }

    public LeaderboardViewState.SortDirection getDirection() {
        return direction;
    }

    private CommandResult success(String message) {
        return new CommandResult(message, true);
    }

    private CommandResult failure(String message) {
        return new CommandResult(message, false);
    }
}
