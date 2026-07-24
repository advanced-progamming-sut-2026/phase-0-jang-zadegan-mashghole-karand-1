package model.quest;

import model.core.GameState;
import model.data.content.chapter.ChapterType;
import model.data.plant.PlantType;
import model.storage.user.User;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public abstract class Quest{
    private final String id;
    private final String name;
    private final QuestPriority priority;
    private final QuestCategory category;
    private final String description;

    protected int progress =0;
    protected int target;
    protected boolean completed;

    protected final RewardType rewardType;
    protected final int rewardAmount;
    protected final PlantType rewardPlant;

    protected Quest(String name, QuestPriority priority,QuestCategory category ,String description, int target ,RewardType rewardType, int rewardAmount, PlantType rewardPlant) {
        this.name = name;
        this.priority = priority;
        this.description = description;
        this.rewardType = rewardType;
        this.rewardAmount = rewardAmount;
        this.rewardPlant = rewardPlant;
        this.target = target;
        this.category = category;
        this.id = name + "|" + category.name() + "|" + target + "|" + description;
    }


    public abstract void onEvent(Object event, User user, GameState state, ChapterType chapter);


    public void reward(User user) {
        switch (rewardType) {
            case COIN -> user.coins+=rewardAmount;
            case DIAMOND -> user.gems+=rewardAmount;
            case SEED_PACK -> user.addSeedPackets(rewardPlant, rewardAmount);
            case PLANT -> {
                PlantType plantType = pickRandomLockedPlant(user);
                if(plantType != null){
                    user.collection.unlockPlant(plantType);
                }
            }
        }
    }

    private PlantType pickRandomLockedPlant(User user) {
        List<PlantType> locked = Arrays.stream(PlantType.values())
                .filter(p -> !user.collection.isPlantUnlocked(p))
                .filter(p -> !p.isBowlingExclusive())
                .toList();
        if (locked.isEmpty()) {
            return null;
        }
        return locked.get(ThreadLocalRandom.current().nextInt(locked.size()));
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public QuestCategory getCategory() { return category; }
    public QuestPriority getPriority() { return priority; }
    public int getTarget() { return target; }
    public int getProgress() { return progress; }
    public boolean isCompleted() { return completed; }
    public RewardType getRewardType() { return rewardType; }
    public int getRewardAmount() { return rewardAmount; }
    public PlantType getRewardPlant() { return rewardPlant; }
    public void setProgress(int progress) { this.progress = progress; }
    public void setCompleted(boolean completed) { this.completed = completed; }

}
