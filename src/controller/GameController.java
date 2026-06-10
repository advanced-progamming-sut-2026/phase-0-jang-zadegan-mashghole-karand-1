package pvz.controller;

import pvz.model.service.GameService;
import pvz.view.GameView;

public class GameController implements Controller {

    private GameService gameService;
    private GameView gameView;

    @Override
    public void handle(String command) {
    }
}
