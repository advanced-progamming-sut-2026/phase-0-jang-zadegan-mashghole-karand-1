package model.data.sun;

import model.core.GameState;
import model.core.Position;
import model.data.plant.Plant;

public class Sun {
    public final int id;
    public final int row;
    public Position position;
    public int amount;
    public SunType type;
    public int age = 0;
    public boolean isFalling = true;
    public float targetY;
    public Plant generatorPlant;

    public boolean hasExploded = false;

    private static int nextId = 0;

    public Sun(int row, Position position, int amount, SunType type) {
        this.id = nextId++;
        this.row = row;
        this.position = position;
        this.amount = amount;
        this.type = type;
        this.targetY = row * GameState.CELL_HEIGHT + GameState.CELL_HEIGHT / 2;
    }

    public Sun(int row, Position position, int amount, Plant plant) {
        this.id = nextId++;
        this.row = row;
        this.position = position;
        this.amount = amount;
        this.type = SunType.NORMAL;
        this.generatorPlant = plant;
        this.targetY = row * GameState.CELL_HEIGHT + GameState.CELL_HEIGHT / 2;
    }

    public boolean isAtPosition(int x, int y) {
        return (int) (position.x) == x && (int) (position.y) == y;
    }
}