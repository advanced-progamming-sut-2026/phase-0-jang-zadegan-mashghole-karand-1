package model.quest;

import model.core.GameState;
import model.data.content.chapter.ChapterType;
import model.data.plant.PlantCategory;
import model.data.plant.PlantType;
import model.event.events.ZombieDiedEvent;
import model.storage.user.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class KillWithPlantQuest extends Quest {
    public PlantType plantType= null;
    private final Map<PlantType, Integer> plants= new HashMap<>();
    private final Map<PlantType, Boolean> plantCompleted = new HashMap<>();

    protected KillWithPlantQuest(String name, QuestPriority priority, QuestCategory category,String description, int target,RewardType rewardType, int rewardAmount, PlantType rewardPlant, PlantType plantType) {
        super(name, priority, category,description, target,rewardType, rewardAmount, rewardPlant);
        this.plantType = plantType;
    }
    protected KillWithPlantQuest(String name, QuestPriority priority, QuestCategory category,String description, int target,RewardType rewardType, int rewardAmount, PlantType rewardPlant){
        super(name, priority, category,description, target,rewardType, rewardAmount, rewardPlant);
        for (PlantType type : PlantType.values()) {
            Set<PlantCategory> ATTACKER_CATEGORIES = Set.of(
                    PlantCategory.SHOOTER,
                    PlantCategory.LOBBER,
                    PlantCategory.EXPLOSIVE,
                    PlantCategory.MELEE,
                    PlantCategory.STRIKE_THROUGH,
                    PlantCategory.HOMING
            );
            if (ATTACKER_CATEGORIES.contains(type.category)) {
                plants.put(type, 0);
                plantCompleted.put(type, false);
            }
        }
    }


    @Override
    public void onEvent(Object event, User user, GameState state, ChapterType chapter) {
        if(completed) return;

        if(event instanceof ZombieDiedEvent e){
            if(plantType == null){
                plants.put(plantType, plants.getOrDefault(plantType, 0) + 1);
                if(plants.getOrDefault(plantType, 0) >= target){
                    plantCompleted.put(plantType, true);
                    reward(user);
                }
            }
            if(e.killerPlant == this.plantType){
                progress++;
                if(progress >= target){
                    completed = true;
                    reward(user);
                }
            }
        }
    }
}
