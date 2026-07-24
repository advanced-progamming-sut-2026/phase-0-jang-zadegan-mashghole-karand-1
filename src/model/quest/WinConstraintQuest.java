package model.quest;

import model.core.GameState;
import model.data.content.chapter.ChapterType;
import model.data.plant.PlantCategory;
import model.data.plant.PlantTag;
import model.data.plant.PlantType;
import model.event.events.LevelCompleteEvent;
import model.event.events.PlantDiedEvent;
import model.event.events.PlantPlacedEvent;
import model.storage.user.User;

public class WinConstraintQuest extends Quest {
    private final int MAX_PLANT_LOSS;
    private int plantLost = 0;
    private int sunProducer = 0;
    private final int SUN_PRODUCER_REQUIRED;
    private boolean failed = false;
    private final Constraint quest;
    private final PlantCategory forbiddenCategory;

    public enum Constraint{
        MAX_PLANT_LOSS,
        SUN_AT_END,
        NIGHT_OR_MORNING,
        FORBIDDEN_FAMILY,
        WIN_HARD,
        CLOUDY_DAY
    }

    protected WinConstraintQuest(String name, QuestPriority priority, QuestCategory category, String description, int target, RewardType rewardType, int rewardAmount, PlantType rewardPlant, int maxPlantLoss, int sunProducerRequired, Constraint quest, PlantCategory forbiddenCategory) {
        super(name, priority, category, description, target, rewardType, rewardAmount, rewardPlant);
        MAX_PLANT_LOSS = maxPlantLoss;
        SUN_PRODUCER_REQUIRED = sunProducerRequired;
        this.quest = quest;
        this.forbiddenCategory = forbiddenCategory;
    }

    @Override
    public void onEvent(Object event, User user, GameState state, ChapterType chapter) {
        if(completed) return;
        if(event instanceof PlantDiedEvent && quest == Constraint.MAX_PLANT_LOSS){
            plantLost++;
            if(plantLost > MAX_PLANT_LOSS) failed = true;
        }
        if(event instanceof PlantPlacedEvent e){
            if(e.plant.type.category == forbiddenCategory && quest == Constraint.FORBIDDEN_FAMILY){
                failed = true;
            }
            if(e.plant.type.category == PlantCategory.SUN_PRODUCER && quest == Constraint.CLOUDY_DAY){
                sunProducer++;
            }
        }
        if(event instanceof LevelCompleteEvent e){
            if(quest == Constraint.SUN_AT_END && state.sunAmount == 0){
                completed = true;
                reward(user);
            }

            if(quest == Constraint.MAX_PLANT_LOSS && !failed){
                completed = true;
                reward(user);

            }
            if(quest == Constraint.FORBIDDEN_FAMILY && !failed){
                completed = true;
                reward(user);
            }
            if(quest == Constraint.WIN_HARD){
                if(user.preferredSetting.getDifficultyLevel()==5){
                    progress++;
                    if(progress >= target) {
                        completed = true;
                        reward(user);
                    }
                }else {
                    progress = 0;
                }
            }

            if(quest == Constraint.NIGHT_OR_MORNING ){
                completed = state.plants.stream().allMatch(p ->
                        p.type.tags != null && p.type.tags.contains(PlantTag.NIGHT));
                if(completed) reward(user);
            }

            if(quest == Constraint.CLOUDY_DAY){
                if(sunProducer == SUN_PRODUCER_REQUIRED){
                    completed = true;
                    reward(user);
                }
            }
            plantLost = 0;
            sunProducer = 0;
            failed = false;
        }
    }
}
