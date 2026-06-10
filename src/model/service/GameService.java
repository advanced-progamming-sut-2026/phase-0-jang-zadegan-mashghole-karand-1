package pvz.model.service;

import pvz.model.game.GameSession;
import pvz.model.plant.PlantFactory;
import pvz.model.world.Chapter;
import pvz.model.world.Level;
import pvz.model.zombie.ZombieFactory;

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
