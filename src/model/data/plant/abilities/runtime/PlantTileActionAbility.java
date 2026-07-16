package model.data.plant.abilities.runtime;

import model.core.EventBus;
import model.core.GameState;
import model.data.plant.Plant;
import model.data.plant.abilities.config.AreaShape;
import model.data.plant.abilities.config.PlantAbilityConfig;
import model.board.Tile;
import model.board.TileType;

public class PlantTileActionAbility implements PlantAbilityConfig {
    private final TileType targetTile ;
    private final AreaShape baseShape;
    private final int ActionDuration;

    public PlantTileActionAbility(TileType targetTile, AreaShape baseShape, int baseActionDuration) {
        this.targetTile = targetTile;
        this.baseShape = baseShape;
        this.ActionDuration = baseActionDuration;
    }


    @Override
    public PlantAbilityConfig createInstance(Plant plant) {
        return new PlantTileActionAbility(targetTile,baseShape, ActionDuration);
    }

    @Override
    public void onTick(Plant plant, GameState state, EventBus event) {
        ///
    }
}
