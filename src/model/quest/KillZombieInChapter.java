package model.quest;

import model.core.GameState;
import model.data.content.chapter.ChapterType;
import model.data.plant.PlantType;
import model.event.events.ZombieDiedEvent;
import model.storage.user.User;

public class KillZombieInChapter extends Quest {
    ChapterType chapterType;

    protected KillZombieInChapter(String name, QuestPriority priority, QuestCategory category ,String description, int target, ChapterType chapterType, RewardType rewardType, int rewardAmount, PlantType rewardPlant) {
        super(name, priority, category,description, target, rewardType, rewardAmount, rewardPlant);
        this.chapterType = chapterType;
    }


    @Override
    public void onEvent(Object event, User user, GameState state, ChapterType chapter) {
        if(completed) return;
        if(event instanceof ZombieDiedEvent e && chapter == this.chapterType) {
            progress++;
            if(progress >= this.target) {
                completed = true;
                reward(user);
            }
        }
    }
}
