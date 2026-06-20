package controller;

import model.ModelManager;
import model.core.EventBus;
import model.core.GameLoop;
import view.ViewManager;

public class ControllerManager {
    private ModelManager model;
    private ViewManager view;
    private EventBus eventBus;
    private GameLoop gameLoop;
    private final AuthController authController = new AuthController();
    private final LoginController loginController = new LoginController();
    private final GameController gameController = new GameController();
    private final MainMenuController mainMenuController = new MainMenuController();
    private final SettingController settingController = new SettingController();
    private final NewsMenuController newsMenuController = new NewsMenuController();
    private final ProfileController profileController = new ProfileController();
    private final PickPlantsController pickPlantsController = new PickPlantsController();
    private final CollectionController collectionController = new CollectionController();

    public ControllerManager(ModelManager model,
            EventBus eventBus, GameLoop gameLoop) {
        this.model = model;
        this.eventBus = eventBus;
        this.gameLoop = gameLoop;

        this.gameLoop.setOnTickHandler(() -> {
            model.tick();
            view.render(model.getStateView());
        });
    }

    public void setView(ViewManager view) {
        this.view = view;
    }

    public void start() {
        view.render(model.getStateView());
        gameLoop.startAutoTick();
    }

    public void tick() {
        view.render(model.getState());
    }

    public void sendMessage(String message) {
        view.showMessage(message);
    }

    public AuthController getAuthController() {
        return authController;
    }

    public LoginController getLoginController() {
        return loginController;
    }

    public GameController getGameController() {
        return gameController;
    }

    public MainMenuController getMainMenuController() {
        return mainMenuController;
    }

    public SettingController getSettingController() {
        return settingController;
    }

    public NewsMenuController getNewsMenuController() {
        return newsMenuController;
    }

    public ProfileController getProfileController() {
        return profileController;
    }

    public PickPlantsController getPickPlantsController() {
        return pickPlantsController;
    }

    public CollectionController getCollectionController() {
        return collectionController;
    }
}