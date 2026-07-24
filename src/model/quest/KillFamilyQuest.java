package model.quest;

import model.core.GameState;
import model.data.content.chapter.ChapterType;
import model.data.plant.PlantCategory;
import model.data.plant.PlantType;
import model.events.GameOverEvent;
import model.events.LevelCompleteEvent;
import model.events.WaveStartedEvent;
import model.events.ZombieDiedEvent;
import model.storage.user.User;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class KillFamilyQuest extends Quest {
    private static final Set<PlantCategory> ATTACKER_FAMILIES = Set.of(
            PlantCategory.SHOOTER,
            PlantCategory.LOBBER,
            PlantCategory.EXPLOSIVE,
            PlantCategory.MELEE,
            PlantCategory.STRIKE_THROUGH,
            PlantCategory.HOMING
    );

    private final PlantCategory requiredCategory;
    private final boolean allFamiliesMode;
    private final Map<PlantCategory, Boolean> familyCompleted = new HashMap<>();
    private final Set<PlantCategory> familiesUsedThisLevel = new HashSet<>();

   protected KillFamilyQuest(String name, QuestPriority priority, QuestCategory category, String description,
                             int target,RewardType rewardType, int rewardAmount, PlantType rewardPlant, PlantCategory requiredCategory) {
       super(name, priority, category, description, target, rewardType, rewardAmount, rewardPlant);
       this.requiredCategory = requiredCategory;
       this.allFamiliesMode = false;
   }


    protected KillFamilyQuest(String name, QuestPriority priority, QuestCategory category, String description,
                             RewardType rewardType, int rewardAmount, PlantType rewardPlant) {
        super(name, priority, category, description, ATTACKER_FAMILIES.size(), rewardType, rewardAmount, rewardPlant);
        this.requiredCategory = null;
        this.allFamiliesMode = true;
        for (PlantCategory family : ATTACKER_FAMILIES) {
            familyCompleted.put(family, false);
        }
        progress = 0;
    }

    @Override
    public void onEvent(Object event, User user, GameState state, ChapterType chapter) {
        if (completed) return;

        if (allFamiliesMode) {
            handleAllFamiliesMode(event, user);
            return;
        }


        if (!(event instanceof ZombieDiedEvent e)) return;
        if (e.killerPlant == null) return;


        if (requiredCategory != null && e.killerPlant.category != requiredCategory) {
            return;
        }

        progress++;
        if (progress >= target) {
            completed = true;
            reward(user);
        }
    }

    private void handleAllFamiliesMode(Object event, User user) {
        if (event instanceof ZombieDiedEvent e) {
            if (e.killerPlant == null) return;
            PlantCategory family = e.killerPlant.category;
            if (ATTACKER_FAMILIES.contains(family)) {
                familiesUsedThisLevel.add(family);
            }
            return;
        }

        if (event instanceof GameOverEvent) {
            familiesUsedThisLevel.clear();
            return;
        }

        if (event instanceof LevelCompleteEvent) {
            if (familiesUsedThisLevel.size() == 1) {
                PlantCategory onlyFamily = familiesUsedThisLevel.iterator().next();
                if (!Boolean.TRUE.equals(familyCompleted.get(onlyFamily))) {
                    familyCompleted.put(onlyFamily, true);
                    progress++;
                }
            }
            familiesUsedThisLevel.clear();

            if (familyCompleted.values().stream().allMatch(Boolean::booleanValue)) {
                completed = true;
                reward(user);
            }
        }
    }
}
