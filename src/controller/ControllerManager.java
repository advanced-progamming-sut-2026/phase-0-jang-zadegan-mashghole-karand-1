package controller;

import controller.CommandResult.CommandResult;
import model.ModelManager;
import model.core.EventBus;
import model.core.GameLoop;
import model.service.AuthState;
import model.storage.StorageManager;
import view.MenuType;
import view.ScreenType;
import view.ViewManager;

public class ControllerManager {
    private ModelManager model;
    private ViewManager view;
    private EventBus eventBus;
    private GameLoop gameLoop;
    private final StorageManager storage;

    private final AuthController authController;
    private final GameMenuController gameMenuController = new GameMenuController();
    private final MainMenuController mainMenuController;
    private final SettingController settingController = new SettingController();
    private final NewsMenuController newsMenuController = new NewsMenuController();
    private final ProfileController profileController = new ProfileController();
    private final PickPlantsController pickPlantsController = new PickPlantsController();
    private final CollectionController collectionController = new CollectionController();
    private final GameMechanismController gameMechanismController;
    private final GreenhouseController greenhouseController = new GreenhouseController();
    private ShopController shopController;
    private final QuestMenuController questMenuController = new QuestMenuController();

    private ScreenType currentScreen = ScreenType.REGISTER;
    private MenuType currentMenu = MenuType.NONE;
    private AuthState authState = new AuthState();

    public ControllerManager(ModelManager model,
            EventBus eventBus, GameLoop gameLoop, StorageManager storage) {
        this.model = model;
        this.eventBus = eventBus;
        this.gameLoop = gameLoop;
        this.storage = storage;

        this.authController = new AuthController(this, storage);
        this.mainMenuController = new MainMenuController(this, storage);

        this.gameLoop.setOnTickHandler(() -> {
            model.tick();
            tick();
        });

        gameMechanismController = new GameMechanismController(gameLoop, model.getState());
    }

    public void setView(ViewManager view) {
        this.view = view;
    }

    public void start() {
        storage.loadProgress();
        currentScreen = storage.isLoggedIn() ? ScreenType.MAIN : ScreenType.REGISTER;
        refreshView();
        gameLoop.startAutoTick();
    }

    public void tick() {
        refreshView();
    }

    public void quit() {
        if (view != null) {
            view.stop();
        }
        gameLoop.stopAutoTick();
        System.exit(0);
    }

    public void refreshView() {
        if (view != null) {
            authState.questions = authController.getQuestions();
            authState.isAwaitingSecurityAnswer = authController.isAwaitingSecurityAnswer();
            authState.isAwaitingNewPassword = authController.isAwaitingNewPassword();
            authState.passwordResetQuestion = authController.getPasswordResetQuestion();
            view.render(model.getState(), currentScreen, currentMenu, authState);
        }
    }

    public void setScreen(ScreenType screen) {
        this.currentScreen = screen;
        refreshView();
    }

    public ScreenType getCurrentScreen() {
        return currentScreen;
    }

    public void handleCommandResult(CommandResult result) {
        if (result == null) {
            return;
        }
        if (result.message != null && !result.message.isEmpty()) {
            if (result.isSuccess()) {
                sendMessage(result.message);
            } else {
                showError(result.message);
            }
        }
        refreshView();
    }

    public CommandResult enterMenu(String menuName) {
        String name = menuName.trim().toLowerCase();

        if (currentScreen == ScreenType.REGISTER && name.equals("login")) {
            setScreen(ScreenType.LOGIN);
            return new CommandResult("Entered login menu.", true);
        }

        return new CommandResult("Cannot enter menu from here.", false);
    }

    public CommandResult exitMenu() {
        switch (currentScreen) {
            case LOGIN:
                authController.clearPasswordResetState();
                setScreen(ScreenType.REGISTER);
                return new CommandResult("Returned to register menu.", true);
            default:
                return new CommandResult("Cannot exit this menu.", false);
        }
    }

    public CommandResult showCurrentMenu() {
        return new CommandResult("Current screen: " + currentScreen.name().toLowerCase(), true);
    }

    public void sendMessage(String message) {
        view.showMessage(message);
    }

    public void showError(String message) {
        view.showError(message);
    }

    public AuthController getAuthController() {
        return authController;
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

    public StorageManager getStorage() {
        return storage;
    }
}
