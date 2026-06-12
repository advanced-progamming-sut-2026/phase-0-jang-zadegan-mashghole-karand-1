package model.service;

import model.game.GameSession;
import model.data.plant.PlantFactory;
import model.world.Chapter;
import model.world.Level;
import model.data.zombie.ZombieFactory;

public class GameService {

    private PlantFactory plantFactory;
    private ZombieFactory zombieFactory;

    public GameSession startLevel(Level level) {
        return null;
    }

    public void advanceTime(GameSession session, int ticks) {
    }

    public Chapter getChapter(String name) {
        return null;
    }
}
