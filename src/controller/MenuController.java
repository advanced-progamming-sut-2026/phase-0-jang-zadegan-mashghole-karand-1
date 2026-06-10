package pvz.controller;

import pvz.model.menu.MenuManager;
import pvz.view.GameMenuView;
import pvz.view.MainMenuView;

public class MenuController implements Controller {

    private MenuManager menuManager;
    private MainMenuView mainMenuView;
    private GameMenuView gameMenuView;

    @Override
    public void handle(String command) {
    }
}
