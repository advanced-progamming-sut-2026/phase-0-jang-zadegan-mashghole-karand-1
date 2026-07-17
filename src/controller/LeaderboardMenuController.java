package controller;

import controller.CommandResult.CommandResult;
import model.service.LeaderboardViewState;
import model.storage.StorageManager;

public class LeaderboardMenuController {
    private final ControllerManager controllerManager;
    private final StorageManager storage;
    private final LeaderboardViewState leaderboardViewState;
    private LeaderboardViewState.SortDirection direction = LeaderboardViewState.SortDirection.HTL;
    private LeaderboardViewState.SortColumn column = LeaderboardViewState.SortColumn.SCORE;
    public LeaderboardMenuController(ControllerManager controllerManager, StorageManager storage,LeaderboardViewState leaderboardViewState) {
        this.controllerManager = controllerManager;
        this.storage = storage;
        this.leaderboardViewState = leaderboardViewState;
    }
    public CommandResult sort(String sortClass , String sortType){
      try {
          column = LeaderboardViewState.SortColumn.valueOf(sortClass.toUpperCase());
          direction = LeaderboardViewState.SortDirection.valueOf(sortType.trim().toUpperCase());
      }catch (IllegalArgumentException e){
          return failure("Invalid sort options");
      }
      return success("Leaderboard sorted.");

    }
    public LeaderboardViewState getViewState(){
        return LeaderboardViewState.fromUsers(storage.getUsers(),column,direction);
    }
    private CommandResult success(String message) {
        return new CommandResult(message, true);
    }

    private CommandResult failure(String message) {
        return new CommandResult(message, false);
    }
}
