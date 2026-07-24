package model.service;

import model.quest.Quest;
import model.quest.QuestCategory;
import model.quest.QuestPriority;
import model.quest.RewardType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class QuestViewState {

    public enum RewardKind {
        CURRENCY, // coins / gems
        UNLOCKABLE, // plant unlock Locked → Available
        INVENTORY // seed packs and consumables
    }

    public static final class Entry {
        public final String name;
        public final String description;
        public final QuestCategory category;
        public final QuestPriority priority;
        public final int progress;
        public final int target;
        public final boolean completed;
        public final RewardKind rewardKind;
        public final String rewardLabel;

        public Entry(String name, String description, QuestCategory category, QuestPriority priority,
                int progress, int target, boolean completed, RewardKind rewardKind, String rewardLabel) {
            this.name = name;
            this.description = description;
            this.category = category;
            this.priority = priority;
            this.progress = progress;
            this.target = target;
            this.completed = completed;
            this.rewardKind = rewardKind;
            this.rewardLabel = rewardLabel;
        }
    }

    public final List<Entry> critical;
    public final List<Entry> high;
    public final List<Entry> mediumAndLow;
    public final String filter;

    public QuestViewState(List<Entry> critical, List<Entry> high, List<Entry> mediumAndLow, String filter) {
        this.critical = critical != null ? List.copyOf(critical) : List.of();
        this.high = high != null ? List.copyOf(high) : List.of();
        this.mediumAndLow = mediumAndLow != null ? List.copyOf(mediumAndLow) : List.of();
        this.filter = filter == null ? "all" : filter;
    }

    public static QuestViewState empty() {
        return new QuestViewState(List.of(), List.of(), List.of(), "all");
    }

    public static QuestViewState fromQuests(List<Quest> quests, String filter) {
        String normalized = filter == null ? "all" : filter.trim().toLowerCase();
        List<Entry> critical = new ArrayList<>();
        List<Entry> high = new ArrayList<>();
        List<Entry> mediumAndLow = new ArrayList<>();

        if (quests != null) {
            List<Quest> sorted = new ArrayList<>(quests);
            sorted.sort(engagementOrder());
            for (Quest quest : sorted) {
                if (!matchesFilter(quest, normalized)) {
                    continue;
                }
                Entry entry = toEntry(quest);
                switch (quest.getPriority()) {
                    case CRITICAL -> critical.add(entry);
                    case HIGH -> high.add(entry);
                    case MEDIUM, LOW -> mediumAndLow.add(entry);
                }
            }
        }
        return new QuestViewState(critical, high, mediumAndLow, normalized);
    }

    private static Comparator<Quest> engagementOrder() {
        return Comparator
                .comparingInt((Quest q) -> priorityRank(q.getPriority()))
                .thenComparing(q -> q.isCompleted())
                .thenComparing(q -> q.getCategory().name())
                .thenComparing(Quest::getName);
    }

    private static int priorityRank(QuestPriority priority) {
        return switch (priority) {
            case CRITICAL -> 0;
            case HIGH -> 1;
            case MEDIUM -> 2;
            case LOW -> 3;
        };
    }

    private static Entry toEntry(Quest quest) {
        return new Entry(
                quest.getName(),
                quest.getDescription(),
                quest.getCategory(),
                quest.getPriority(),
                quest.getProgress(),
                quest.getTarget(),
                quest.isCompleted(),
                rewardKind(quest.getRewardType()),
                rewardLabel(quest));
    }

    private static RewardKind rewardKind(RewardType type) {
        return switch (type) {
            case COIN, DIAMOND -> RewardKind.CURRENCY;
            case PLANT -> RewardKind.UNLOCKABLE;
            case SEED_PACK -> RewardKind.INVENTORY;
        };
    }

    private static String rewardLabel(Quest quest) {
        return switch (quest.getRewardType()) {
            case COIN -> quest.getRewardAmount() + " Coins";
            case DIAMOND -> quest.getRewardAmount() + " Gems";
            case PLANT -> "Unlock random plant";
            case SEED_PACK -> {
                String plant = quest.getRewardPlant() != null ? quest.getRewardPlant().name : "plant";
                yield quest.getRewardAmount() + " seed packs (" + plant + ")";
            }
        };
    }

    private static boolean matchesFilter(Quest quest, String filter) {
        if (filter.isEmpty() || filter.equals("all")) {
            return true;
        }
        return switch (filter) {
            case "critical" -> quest.getPriority() == QuestPriority.CRITICAL;
            case "high" -> quest.getPriority() == QuestPriority.HIGH;
            case "medium", "low", "daily" ->
                quest.getPriority() == QuestPriority.MEDIUM
                        || quest.getPriority() == QuestPriority.LOW
                        || quest.getCategory() == QuestCategory.DAILY;
            case "main" -> quest.getCategory() == QuestCategory.MAIN;
            case "epic", "challenge" -> quest.getCategory() == QuestCategory.EPIC;
            case "completed" -> quest.isCompleted();
            case "active" -> !quest.isCompleted();
            default -> true;
        };
    }

    public boolean isEmpty() {
        return critical.isEmpty() && high.isEmpty() && mediumAndLow.isEmpty();
    }

    public int totalCount() {
        return critical.size() + high.size() + mediumAndLow.size();
    }
}
