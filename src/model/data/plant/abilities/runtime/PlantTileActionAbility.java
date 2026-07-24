package model.data.plant.abilities.runtime;

import model.board.IceDirection;
import model.core.EventBus;
import model.core.GameLoop;
import model.core.GameState;
import model.data.Grave.Grave;
import model.data.plant.Plant;
import model.data.plant.abilities.config.AreaShape;
import model.data.plant.abilities.config.PlantAbilityConfig;
import model.board.Tile;
import model.board.TileType;
import model.data.zombie.Zombie;

import java.util.ArrayList;
import java.util.List;

public class PlantTileActionAbility implements PlantAbilityConfig {
    private final TileType targetTile ;
    private final AreaShape baseShape;
    private final int ActionDuration;
    private boolean done = false;
    private int tickWait = 0;
    private final ActionTarget actionTarget;
    public PlantTileActionAbility(ActionTarget actionTarget,TileType targetTile, AreaShape baseShape, int baseActionDuration) {
        this.targetTile = targetTile;
        this.baseShape = baseShape;
        this.ActionDuration = baseActionDuration;
        this.actionTarget = actionTarget;
    }


    @Override
    public PlantAbilityConfig createInstance(Plant plant) {
        AreaShape shape = baseShape;
        if (plant.upgradeState.meltArea3x3 && actionTarget == ActionTarget.ICE) {
            shape = AreaShape.RADIUS_3x3;
        }
        int finalDuration = Math.max(0,ActionDuration + plant.upgradeState.cooldownBonus * GameLoop.TICKS_PER_SECOND);
        return new PlantTileActionAbility(actionTarget,targetTile,shape, finalDuration);
    }

    @Override
    public void onTick(Plant plant, GameState state, EventBus event) {
        if (done) return;
        if (tickWait < ActionDuration) {
            tickWait++;
            return;
        }
        if (actionTarget == ActionTarget.ICE)
             meltTiles(plant,state);
        else if (actionTarget == ActionTarget.GRAVE) {
            eatGrave(plant,state,event);
        }
        if (plant.upgradeState.explodeOnFinish) {
            damageZombiesOnPlantTile(plant, state, event,  400);
        }
        done = true;
        plant.hp = 0;

    }
    private void meltTiles(Plant plant, GameState state ){
        for (int[] cell : cellsInShape(plant , baseShape)){
            Tile tile = state.getBoard().getTile(cell[0] , cell[1]);
            if (tile == null) continue;
            if (tile.getType() == targetTile) {
                tile.setType(TileType.NORMAL);
                tile.setDirection(IceDirection.NONE);
            }
        }
    }
    private void eatGrave(Plant plant,GameState state,EventBus bus){
        Grave grave = state.getGraveAt(plant.row,plant.col);
        if (grave != null)
            grave.destroy(state,bus);
        Tile tile = state.getBoard().getTile(plant.row, plant.col);
        if (tile!=null)
            tile.removeGrave();
    }
    private List<int[]> cellsInShape(Plant plant, AreaShape shape) {
        List<int[]> cells = new ArrayList<>();
        switch (shape) {
            case SINGLE_TILE -> cells.add(new int[]{plant.row, plant.col});
            case RADIUS_3x3 -> {
                for (int r = plant.row - 1; r <= plant.row + 1; r++) {
                    for (int c = plant.col - 1; c <= plant.col + 1; c++) {
                        cells.add(new int[]{r, c});
                    }
                }
            }
            default -> cells.add(new int[]{plant.row, plant.col});
        }
        return cells;
    }
    private void damageZombiesOnPlantTile(Plant plant , GameState state , EventBus bus , int damage){
        for (int[] cell : cellsInShape(plant, baseShape)) {
            for (Zombie z : state.zombies) {
                if (!z.isAlive) continue;
                int zCol = (int) (z.position.x / GameState.CELL_WIDTH);
                if (z.row != cell[0] || zCol != cell[1]) continue;
                z.lastHitBy = plant.type;
                z.takeDamage(damage);
                if (!z.isAlive) {
                    z.kill(state);
                }
            }
        }
    }
}
