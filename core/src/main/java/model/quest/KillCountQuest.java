package model.quest;

import model.core.GameState;
import model.data.content.chapter.ChapterType;
import model.data.plant.PlantType;
import model.event.events.WaveStartedEvent;
import model.event.events.ZombieDiedEvent;
import model.storage.user.User;

public class KillCountQuest extends Quest {
    private boolean started = false;
    private int ticksPassed = 0;
    private final int maxTicks = 300;
    private boolean failed = false;
    private final SpecificQuest quest;

    public enum SpecificQuest {
        SPEED_KILLING,
        LAWNMOWER_KILLING,
        NO_LAWNMOWER_KILLING,
    }


    protected KillCountQuest(String name,
            QuestPriority priority,
            QuestCategory category,
            SpecificQuest quest,
            String description,
            int target,
            RewardType rewardType,
            int rewardAmount,
            PlantType rewardPlant) {
        super(name, priority, category, description, target, rewardType, rewardAmount, rewardPlant);
        this.quest = quest;
    }

    @Override
    public void onEvent(Object event, User user, GameState state, ChapterType chapter) {
        if(completed) return;
        if(event instanceof WaveStartedEvent e && quest == SpecificQuest.SPEED_KILLING){
            if(e.waveNumber ==1 ){
                started = true;
                ticksPassed = 0;
            }
        }

        if(event instanceof ZombieDiedEvent e) {

            if (started && !failed) {
                progress++;
                if (progress >= target) {
                    completed = true;
                    reward(user);
                }
            }
            if (quest == SpecificQuest.LAWNMOWER_KILLING) {
                if (e.zombie.killedByLawnMower) {
                    progress++;
                    if (progress >= target) {
                        completed = true;
                        reward(user);
                    }
                }
            }

            if (quest == SpecificQuest.NO_LAWNMOWER_KILLING) {
                if(!state.getBoard().getLawnMowers(e.zombie.row).isActive() &&
                e.zombie.col == 0) {
                    progress++;
                    if (progress >= target) {
                        completed = true;
                        reward(user);
                    }
                }
            }
        }
    }

    public void onTick(){
        if(started && !failed && quest == SpecificQuest.SPEED_KILLING){
            ticksPassed++;
            if(ticksPassed >= maxTicks && !completed){
                failed = true;
            }
        }
    }
}
