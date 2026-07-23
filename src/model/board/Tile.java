package model.board;

import model.core.GameState;
import model.data.Grave.Grave;
import model.data.plant.Plant;
import model.data.plant.PlantType;
import model.data.vase.Vase;

public class Tile {
    private final int row;
    private final int col;
    private TileType type;
    private GameState state;

    private IceDirection direction; // for ice tiles
    private boolean hasBeachPost;
    private Plant plant;
    private Plant lilyPad;

    private Grave grave; // null if doesn't have grave
    private Vase vase; // null if doesn't have vase

    public Tile(int row, int col, GameState state) {
        this.row = row;
        this.col = col;
        this.type = TileType.NORMAL;
        this.direction = IceDirection.NONE;
        this.hasBeachPost = false;
        this.grave = null;
        this.vase = null;
        this.state = state;
    }

    public boolean hasPlant() {
        return plant != null;
    }

    public boolean hasLilyPad() {
        return lilyPad != null;
    }

    public void setPlant(Plant plant) {
        this.plant = plant;
    }

    private void removePlant() {
        this.plant = null;
    }

    public void setLilyPad(Plant lilyPad) {
        this.lilyPad = lilyPad;
    }

    private void removeLilyPad() {
        this.lilyPad = null;
    }

    public boolean hasGrave() {
        return grave != null;
    }

    public Grave getGrave() {
        return grave;
    }

    public boolean canSetGrave() {
        if (type != TileType.NECROMANCY && type != TileType.NORMAL)
            return false;
        if (hasPlant() || hasLilyPad())
            return false;
        if (hasGrave())
            return false;
        if (hasVase())
            return false;
        if (hasBeachPost())
            return false;
        return true;
    }

    public void setGrave(Grave grave) {
        this.grave = grave;
    }

    public void removeGrave() {
        this.grave = null;
    }

    public boolean hasVase() {
        return vase != null;
    }

    public Vase getVase() {
        return vase;
    }

    public boolean canSetVase() {
        if (type != TileType.NORMAL)
            return false;
        if (hasPlant() || hasLilyPad())
            return false;
        if (hasGrave())
            return false;
        if (hasVase())
            return false;
        if (hasBeachPost())
            return false;
        return true;
    }

    public void setVase(Vase vase) {
        this.vase = vase;
    }

    public void removeVase() {
        this.vase = null;
    }

    public boolean isPlantable(PlantType plantType) {
        if (hasPlant())
            return false;
        if (plantType == PlantType.Hot_Potato) {
            return type == TileType.ICE
                    && !hasVase()
                    && !hasBeachPost()
                    && !hasGrave();
        }
        if (plantType == PlantType.Grave_Buster) {
            return hasGrave() && !hasVase() && !hasBeachPost();
        }
        if (type == TileType.WATER) {
            if (plantType == PlantType.Lily_Pad && hasLilyPad()) {
                return false;
            }
            if (plantType != PlantType.Lily_Pad && !hasLilyPad()) {
                return false;
            }
        }
        if (hasGrave())
            return false;
        if (hasVase())
            return false;
        if (hasBeachPost())
            return false;
        return true;
    }

    public boolean isWater() {
        return type == TileType.WATER;
    }

    public boolean isIce() {
        return type == TileType.ICE;
    }

    public boolean isNecromancy() {
        return type == TileType.NECROMANCY;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public TileType getType() {
        return type;
    }

    public void setType(TileType type) {
        this.type = type;
        if (type == TileType.WATER && !hasLilyPad() && hasPlant()) {
            removePlant();
            state.plants.remove(plant);
        }
    }

    public IceDirection getDirection() {
        return direction;
    }

    public void setDirection(IceDirection direction) {
        this.direction = direction;
    }

    public boolean hasBeachPost() {
        return hasBeachPost;
    }

    public void setBeachPost(boolean hasBeachPost) {
        this.hasBeachPost = hasBeachPost;
    }
}