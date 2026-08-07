package model.service;

import model.storage.user.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LeaderboardViewState {
    public final List<Entry> entries;
    public final SortColumn sortColumn;
    public final SortDirection sortDirection;

    public LeaderboardViewState(List<Entry> entries, SortColumn sortColumn, SortDirection sortDirection) {
        this.entries = entries != null ? List.copyOf(entries) : List.of();
        this.sortColumn = sortColumn != null ? sortColumn : SortColumn.SCORE;
        this.sortDirection = sortDirection != null ? sortDirection : SortDirection.HTL;
    }

    public enum SortColumn {
        SCORE,
        LEVELS,
        MINIGAMES
    }

    public enum SortDirection {
        LTH, // low → high
        HTL // high → low
    }

    public static class Entry {
        public final int rank;
        public final String username;
        public final int chapter;
        public final int level;
        public final int score;
        public final int minigames;

        public Entry(int rank, String username, int chapter, int level, int score, int minigames) {
            this.rank = rank;
            this.username = username;
            this.chapter = chapter;
            this.level = level;
            this.score = score;
            this.minigames = minigames;
        }
    }

    public static LeaderboardViewState fromUsers(List<User> users, SortColumn column, SortDirection direction) {
        SortColumn sortColumn = column != null ? column : SortColumn.SCORE;
        SortDirection sortDirection = direction != null ? direction : SortDirection.HTL;
        if (users == null) {
            return empty(sortColumn, sortDirection);
        }

        Comparator<User> cmp = switch (sortColumn) {
            case SCORE -> Comparator.comparingInt(u -> u.highestScore);
            case LEVELS -> Comparator.comparingInt((User u) -> u.gameProgress.getLastChapter())
                    .thenComparingInt(u -> u.gameProgress.getLastLevel());
            case MINIGAMES -> Comparator.comparingInt(u -> u.gameProgress.getUnlockedMinigames().size());
        };
        if (sortDirection == SortDirection.HTL) {
            cmp = cmp.reversed();
        }
        cmp = cmp.thenComparing(u -> u.username, String.CASE_INSENSITIVE_ORDER);

        List<User> sorted = new ArrayList<>(users);
        sorted.sort(cmp);

        List<Entry> entry = new ArrayList<>();
        int rank = 1;
        for (User user : sorted) {
            entry.add(new Entry(
                    rank++,
                    user.username,
                    user.gameProgress.getLastChapter(),
                    user.gameProgress.getLastLevel(),
                    user.highestScore,
                    user.gameProgress.getUnlockedMinigames().size()));
        }
        return new LeaderboardViewState(entry, sortColumn, sortDirection);
    }

    public static LeaderboardViewState empty() {
        return empty(SortColumn.SCORE, SortDirection.HTL);
    }

    public static LeaderboardViewState empty(SortColumn column, SortDirection direction) {
        return new LeaderboardViewState(Collections.emptyList(), column, direction);
    }
}
