package controller;

import controller.CommandResult.CommandResult;
import model.core.Position;
import model.data.zombie.Zombie;
import model.storage.user.User;
import view.ScreenType;

public class GreenhouseController {
    ControllerManager controllerManager;

    public GreenhouseController(ControllerManager controllerManager) {
        this.controllerManager = controllerManager;
    }

    public CommandResult showGreenhouse(User user) {
        return null;
    }

    public CommandResult plantPot(User user, Position position) {
        return null;
    }

    public CommandResult collect(User user, Position position) {
        return null;
    }

    public CommandResult grow(User user, Position position) {
        return null;
    }

    public CommandResult enterShop() {
        controllerManager.getShopController().setShopDisplayMode(ShopController.ShopDisplayMode.MENU);
        controllerManager.setScreen(ScreenType.SHOP);
        return success("Entered shop");
    }
    private CommandResult success(String message) {
        return new CommandResult(message, true);
    }

}
