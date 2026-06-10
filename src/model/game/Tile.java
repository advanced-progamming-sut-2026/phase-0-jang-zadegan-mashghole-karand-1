package pvz.model.game;

import pvz.model.plant.Plant;
import pvz.model.zombie.Zombie;

import java.util.List;

public class Tile {

    private Position position;
    private TileType tileType;
    private List<Plant> plants;
    private List<Zombie> zombies;
}
