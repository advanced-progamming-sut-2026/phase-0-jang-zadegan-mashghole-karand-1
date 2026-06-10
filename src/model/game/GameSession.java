package pvz.model.game;

import pvz.model.sun.SunManager;
import pvz.model.zombie.WaveManager;

public class GameSession {

    private GameStatus status;
    private int sunAmount;
    private GameBoard board;
    private WaveManager waveManager;
    private SunManager sunManager;
}
