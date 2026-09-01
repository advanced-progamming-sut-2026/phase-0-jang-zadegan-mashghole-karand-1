package controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import controller.CommandResult.CommandResult;
import model.service.LeaderboardViewState;
import model.storage.StorageManager;
import model.storage.user.User;
import network.NetworkSession;
import shared.dto.RankedLeaderboardEntry;
import shared.dto.RankedLeaderboardResponse;
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
        CommandResult openCheck = requireLeaderboardOpen();
        if (openCheck != null) {
            return openCheck;
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

    public CommandResult sortBy(LeaderboardViewState.SortColumn newColumn) {
        CommandResult openCheck = requireLeaderboardOpen();
        if (openCheck != null) {
            return openCheck;
        }
        if (newColumn == column) {
            direction = direction == LeaderboardViewState.SortDirection.HTL
                    ? LeaderboardViewState.SortDirection.LTH
                    : LeaderboardViewState.SortDirection.HTL;
        } else {
            column = newColumn;
            direction = LeaderboardViewState.SortDirection.HTL;
        }
        return success("Leaderboard sorted by " + column.name() + " (" + direction.name() + ").");
    }

    public CommandResult toggleSortDirection() {
        CommandResult openCheck = requireLeaderboardOpen();
        if (openCheck != null) {
            return openCheck;
        }
        direction = direction == LeaderboardViewState.SortDirection.HTL
                ? LeaderboardViewState.SortDirection.LTH
                : LeaderboardViewState.SortDirection.HTL;
        return success("Leaderboard sorted by " + column.name() + " (" + direction.name() + ").");
    }

    private CommandResult requireLeaderboardOpen() {
        CommandResult screenCheck = controllerManager.requireScreen(ScreenType.LEVEL_SELECTOR);
        if (screenCheck != null) {
            return screenCheck;
        }
        if (controllerManager.currentMenu != MenuType.LEADERBOARD) {
            return failure("Open the leaderboard first.");
        }
        return null;
    }

    public LeaderboardViewState getViewState() {
        if (column == LeaderboardViewState.SortColumn.SCORE) {
            LeaderboardViewState remote = fetchRemoteScoreBoard();
            if (remote != null) {
                return remote;
            }
        }
        return LeaderboardViewState.fromUsers(storage.getUsers(), column, direction);
    }

    private LeaderboardViewState fetchRemoteScoreBoard() {
        NetworkSession net = controllerManager.getNetworkSession();
        if (net == null || !net.isLoggedIn()) {
            return null;
        }
        try {
            RankedLeaderboardResponse res = net.authApi().rankedLeaderboard(net.token());
            if (res == null || !res.ok || res.entries == null) {
                return null;
            }
            List<RankedLeaderboardEntry> entries = new ArrayList<>(res.entries);
            if (direction == LeaderboardViewState.SortDirection.LTH) {
                entries.sort(Comparator.comparingInt((RankedLeaderboardEntry e) -> e.score)
                        .thenComparing(e -> e.username, String.CASE_INSENSITIVE_ORDER));
            }
            List<LeaderboardViewState.Entry> mapped = new ArrayList<>();
            int rank = 1;
            for (RankedLeaderboardEntry e : entries) {
                User local = storage.getUserByUsername(e.username);
                int chapter = local != null ? local.gameProgress.getLastChapter() : 0;
                int level = local != null ? local.gameProgress.getLastLevel() : 0;
                int minigames = local != null ? local.gameProgress.getUnlockedMinigames().size() : 0;
                mapped.add(new LeaderboardViewState.Entry(
                        rank++,
                        e.username,
                        chapter,
                        level,
                        e.score,
                        minigames));
            }
            return new LeaderboardViewState(mapped, column, direction);
        } catch (Exception e) {
            return null;
        }
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
