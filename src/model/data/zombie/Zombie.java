package model.data.zombie;

import model.game.Position;

import java.util.List;

public abstract class Zombie {

    private int hp;
    private float speed;
    private int damageToPlants;
    private List<String> abilities;
    private Position position;
}
