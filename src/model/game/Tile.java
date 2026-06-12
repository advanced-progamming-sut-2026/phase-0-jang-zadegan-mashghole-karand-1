package model.game;

import model.data.plant.Plant;
import model.data.zombie.Zombie;

import java.util.List;

public class Tile {

    private Position position;
    private TileType tileType;
    private List<Plant> plants;
    private List<Zombie> zombies;
}
