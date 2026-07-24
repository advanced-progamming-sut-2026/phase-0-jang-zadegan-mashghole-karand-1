package model.quest;

import model.core.GameState;
import model.data.content.chapter.ChapterType;
import model.data.plant.PlantCategory;
import model.data.plant.PlantType;
import model.event.events.PlantPlacedEvent;
import model.event.events.SunCollectedEvent;
import model.storage.user.User;

public class CollectSunQuest extends Quest{
    private final boolean shouldCollectSun;
    protected CollectSunQuest(String name, QuestPriority priority, QuestCategory category,String description, int target, RewardType rewardType, int rewardAmount, PlantType rewardPlant, boolean shouldCollectSun) {
        super(name, priority, category,description, target, rewardType, rewardAmount, rewardPlant);
        this.shouldCollectSun = shouldCollectSun;
    }

    @Override
    public void onEvent(Object event, User user, GameState state, ChapterType chapter) {
        if(completed) return;
        if(event instanceof SunCollectedEvent e && shouldCollectSun){
            progress += e.sun.amount;
            if(progress >= this.target) {
                completed = true;
                reward(user);
            }
            return;
        }
        if(event instanceof PlantPlacedEvent e && e.plant.type.category == PlantCategory.SUN_PRODUCER){
            progress ++;
            if(progress >= this.target) {
                completed = true;
                reward(user);
            }
            return;
        }

    }
}
