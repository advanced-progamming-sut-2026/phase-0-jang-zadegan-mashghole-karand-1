package model.data.plant;

import java.util.ArrayList;
import java.util.List;

public class Plant {
    public final int id;
    public final int row;
    public final int col;
    public final String name;
    public int health;
    public int cost;

    public List<PlantAbility> abilities = new ArrayList<>();

    private static int nextId = 0;

    public Plant(int row, int col, String name, int health, int cost) {
        this.id = nextId++;
        this.row = row;
        this.col = col;
        this.name = name;
        this.health = health;
        this.cost = cost;
    }

    public void addAbility(PlantAbility ability) {
        abilities.add(ability);
        ability.onAttach(this);
    }

    // public float getWorldX() {
    // return col * 80;
    // }

    // public float getWorldY() {
    // return row * 100;
    // }
}