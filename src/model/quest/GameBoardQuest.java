package model.quest;

import model.core.GameState;
import model.core.ReadOnlyGameState;
import model.data.content.chapter.ChapterType;
import model.data.plant.Plant;
import model.data.plant.PlantType;
import model.event.events.LevelCompleteEvent;
import model.storage.user.User;

public class GameBoardQuest extends Quest {
    private final int row;
    private final int col;
    private final boardState boardState;

    public enum boardState {
        SYMMETRIC,
        ASYMMETRIC,
        ROW,
        COL,
        ROW_COL
    }


    protected GameBoardQuest(String name, QuestPriority priority, QuestCategory category, String description, int target, RewardType rewardType, int rewardAmount, PlantType rewardPlant, int row, int col, boardState boardState) {
        super(name, priority, category, description, target, rewardType, rewardAmount, rewardPlant);
        this.row = row;
        this.col = col;
        this.boardState = boardState;
    }

    @Override
    public void onEvent(Object event, User user, GameState state, ChapterType chapter) {
        if(event instanceof LevelCompleteEvent){
            if(boardState == boardState.SYMMETRIC){
                completed =  isSymmetric(state);
                if(completed){
                    reward(user);
                }
            }
            if(boardState == boardState.ASYMMETRIC){
                completed =  isAsymmetric(state);
                if(completed){
                    reward(user);
                }
            }
            if(boardState == boardState.ROW){
                completed = state.plants.stream().allMatch(plant -> plant.row != row);
                if(completed){
                    reward(user);
                }
            }
            if(boardState == boardState.COL){
                completed = state.plants.stream().allMatch(plant -> plant.col != col);
                if(completed){
                    reward(user);
                }
            }
            if(boardState == boardState.ROW_COL){
                completed = state.plants.stream().allMatch(plant -> plant.row != row && plant.col != col);
                if(completed){
                    reward(user);
                }
            }
        }
    }

    private boolean isSymmetric(GameState state){

        for(int i = 0; i<=GameState.GRID_COLS; i++){
            for(int j = 1; j<GameState.GRID_ROWS/2; j++){
                Plant plant1 = state.getPlantAt(j,i);
                Plant plant2 = state.getPlantAt(ReadOnlyGameState.GRID_ROWS-j,i);
                if((plant1 == null && plant2 != null) || (plant1 != null && plant2 == null)){
                    return false;
                }
                if((plant1!= null && plant2!=null)  && (plant1.type != plant2.type)){
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isAsymmetric(GameState state){
        for(int i = 1; i<=GameState.GRID_ROWS/2; i++){
            boolean symmetric = true;
            for(int j = 0; j<GameState.GRID_COLS; j++){
                Plant plant1 = state.getPlantAt(i,j);
                Plant plant2 = state.getPlantAt(ReadOnlyGameState.GRID_ROWS-i,j);
                if((plant1 == null && plant2 != null) || (plant1 != null && plant2 == null)){
                    symmetric = false;
                }
                if((plant1!= null && plant2!=null)  && (plant1.type != plant2.type)){
                    symmetric = false;
                }
            }
            if(symmetric){
                return false;
            }
        }
        return true;
    }
}
