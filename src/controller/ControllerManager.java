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
    private final GameMenuController gameMenuController = new GameMenuController();
    private final MainMenuController mainMenuController = new MainMenuController();
    private final SettingController settingController = new SettingController();
    private final NewsMenuController newsMenuController = new NewsMenuController();
    private final ProfileController profileController = new ProfileController();
    private final PickPlantsController pickPlantsController = new PickPlantsController();
    private final CollectionController collectionController = new CollectionController();
    private final GameMechanismController gameMechanismController;
    private final GreenhouseController greenhouseController = new GreenhouseController();
    private ShopController shopController;
    private final QuestMenuController questMenuController = new QuestMenuController();

    public ControllerManager(ModelManager model,
            EventBus eventBus, GameLoop gameLoop) {
        this.model = model;
        this.eventBus = eventBus;
        this.gameLoop = gameLoop;

        this.gameLoop.setOnTickHandler(() -> {
            model.tick();
            view.render(model.getStateView());
        });

        gameMechanismController = new GameMechanismController(gameLoop, model.getState());
        // shopController = new ShopController(shop);
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

    public GameMenuController getGameMenuController() {
        return gameMenuController;
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

    public GameMechanismController getGameMechanismController() {
        return gameMechanismController;
    }

    public GreenhouseController getGreenhouseController() {
        return greenhouseController;
    }

    public ShopController getShopController() {
        return shopController;
    }

    public QuestMenuController getQuestMenuController() {
        return questMenuController;
    }
}