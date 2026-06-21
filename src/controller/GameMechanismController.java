package controller;

import model.CommandResult.CommandResult;
import model.core.GameLoop;
import model.core.GameState;
import model.core.Position;
import model.data.plant.PlantType;

public class GameMechanismController {
    private GameLoop gameLoop;
    private GameState gameState;

    public  GameMechanismController(GameLoop gameLoop, GameState gameState) {
        this.gameLoop = gameLoop;
        this.gameState = gameState;
    }

    public void AdvanceTicks(int amount){}

    public void collectSun(Position position){}

    public int showSunAmount(){
        return gameState.sunAmount;
    }

    public void addSun(int count){}

    public void releaseNuke(){}

    public void plantPlant(Position position, PlantType plantType){}

    public void pluckPlant(Position position){}

    public void removeCooldown(){}

    public void feedPlant(Position position){}

    public void addPlantFood(){}

    public CommandResult showMap(){return null;}

    public String showPlantsStatus(){return null;}

    public String showTilesStatus(Position position){return null;}

}
