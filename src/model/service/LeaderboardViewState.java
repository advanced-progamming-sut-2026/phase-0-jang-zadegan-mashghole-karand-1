package model.service;

import model.storage.user.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LeaderboardViewState {
    public LeaderboardViewState(List<model.service.LeaderboardViewState.Entry> entries) {
        this.entries = entries != null ? List.copyOf(entries) : List.of();
    }

    public enum SortColumn {
        SCORE,
        LEVELS,
        MINIGAMES,
//        QUESTS
    }

    public enum SortDirection {
        LTH,   // low → high
        HTL   // high → low
    }
    public final List<Entry> entries;
    public static class Entry{
        public final int rank;
        public final String username;
        public final int chapter;
        public final int level;
        public final int score;
        public final int minigames;
//        public final int quests;


        public Entry(int rank, String username, int chapter, int level, int score, int minigames) {
            this.rank = rank;
            this.username = username;
            this.chapter = chapter;
            this.level = level;
            this.score = score;
            this.minigames = minigames;
            //add quest
        }
    }
    public static LeaderboardViewState fromUsers(List<User> users,SortColumn column,SortDirection direction){
        if (users == null)
            return empty();
        Comparator<User> cmp = switch (column){
            case SCORE -> Comparator.comparingInt(u -> u.highestScore);
            case LEVELS -> Comparator.comparingInt((User u)-> u.gameProgress.getLastChapter())
                    .thenComparingInt(u -> u.gameProgress.getLastLevel());
            case MINIGAMES -> Comparator.comparingInt(u -> u.gameProgress.getUnlockedMinigames().size());
            //            case QUESTS -> Comparator.comparingInt()

        };
        if (direction == SortDirection.HTL)
            cmp = cmp.reversed();
        cmp = cmp.thenComparing(u ->u.username);
        List<User> sorted = new ArrayList<>(users);
        sorted.sort(cmp);
        List<Entry> entry = new ArrayList<>();
        int rank = 1;
        for (User user : sorted){
            entry.add(new Entry(
                    rank++,
                    user.username,
                    user.gameProgress.getLastChapter(),
                    user.gameProgress.getLastLevel(),
                    user.highestScore,
                    user.gameProgress.getUnlockedMinigames().size()
            ));
        }
        return new LeaderboardViewState(entry);
    }
    public static LeaderboardViewState empty(){
        return new LeaderboardViewState(Collections.emptyList());
    }
}
