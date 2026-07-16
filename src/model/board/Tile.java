package model.board;

import model.data.Grave.Grave;
import model.data.plant.Plant;
import model.data.plant.PlantType;

public class Tile {
    private final int row;
    private final int col;
    private TileType type;

    private IceDirection direction; // for ice tiles
    private boolean isWater;
    private boolean hasBeachPost;
    private Plant plant;
    private Plant lilyPad;

    private Grave grave; // null if doesn't have grave

    public Tile(int row, int col) {
        this.row = row;
        this.col = col;
        this.type = TileType.NORMAL;
        this.direction = IceDirection.NONE;
        this.isWater = false;
        this.hasBeachPost = false;
        this.grave = null;
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

    public void removePlant() {
        this.plant = null;
    }

    public void setLilyPad(Plant lilyPad) {
        this.lilyPad = lilyPad;
    }

    public void removeLilyPad() {
        this.lilyPad = null;
    }

    public boolean hasGrave() {
        return grave != null;
    }

    public Grave getGrave() {
        return grave;
    }

    public void setGrave(Grave grave) {
        this.grave = grave;
    }

    public void removeGrave() {
        this.grave = null;
    }

    public boolean isPlantable(PlantType plantType) {
        if (hasPlant())
            return false;
        if (type == TileType.WATER) {
            // uncomment when lily pad is added

            // if (plantType == PlantType.LilyPad && hasLilyPad()) {
            // return false;
            // }
            // if (plantType != PlantType.LilyPad && !hasLilyPad()) {
            // return false;
            // }
        }
        if (hasGrave())
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