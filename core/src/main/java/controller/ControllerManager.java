package controller;

import controller.CommandResult.CommandResult;
import model.ModelManager;
import model.core.EventBus;
import model.core.GameLoop;
import model.quest.QuestAssigner;
import model.service.*;
import model.service.GameNavigationState.Phase;
import model.shop.Shop;
import model.storage.StorageManager;
import model.storage.user.User;
import network.NetworkAuthBridge;
import network.NetworkSession;
import view.MenuType;
import view.ScreenType;
import view.ViewFacade;

public class ControllerManager {
    private ModelManager model;
    ViewFacade view;
    private GameLoop gameLoop;
    private final StorageManager storage;
    private NetworkAuthBridge networkAuth;
    private NetworkSession networkSession;

    private final AuthController authController;
    private final GameMenuController gameMenuController;
    private final MainMenuController mainMenuController;
    private final SettingController settingController;
    private final NewsMenuController newsMenuController;
    private final ProfileController profileController;
    private final PickPlantsController pickPlantsController;
    private final CollectionController collectionController;
    private final GameMechanismController gameMechanismController;
    private final SessionLifecycleController sessionLifecycleController;
    private final GreenhouseController greenhouseController;
    private ShopController shopController;
    private final QuestMenuController questMenuController;
    private final LeaderboardMenuController leaderboardMenuController;

    ScreenType currentScreen = ScreenType.REGISTER;
    MenuType currentMenu = MenuType.NONE;
    final AuthState authState = new AuthState();
    private final GameNavigationState gameNavigation = new GameNavigationState();
    ProfileViewState profileViewState = ProfileViewState.empty();
    NewsViewState newsViewState = NewsViewState.empty();
    SettingsViewState settingsViewState = SettingsViewState.empty();
    LeaderboardViewState leaderboardViewState = LeaderboardViewState.empty();
    CollectionViewState collectionViewState = CollectionViewState.empty();
    QuestViewState questViewState = QuestViewState.empty();
    HudViewState hudViewState = HudViewState.empty();
    boolean hasUnreadNews = false;

    private final ControllerViewSupport viewSupport = new ControllerViewSupport();
    private final ControllerMenuSupport menuSupport = new ControllerMenuSupport();
    private Shop shop;

    public ControllerManager(ModelManager model,
            EventBus eventBus, GameLoop gameLoop, StorageManager storage) {
        this.model = model;
        this.gameLoop = gameLoop;
        this.storage = storage;
        this.greenhouseController = new GreenhouseController(this, storage);
        this.authController = new AuthController(this, storage);
        this.mainMenuController = new MainMenuController(this, storage);
        this.profileController = new ProfileController(this, storage);
        this.settingController = new SettingController(this, storage);
        this.newsMenuController = new NewsMenuController(this, storage);
        this.questMenuController = new QuestMenuController(this);
        new AppEventHandler(eventBus, storage, this).register();
        this.gameMenuController = new GameMenuController(this, model, storage, gameNavigation);
        this.pickPlantsController = new PickPlantsController(this, model, storage, gameNavigation);
        this.collectionController = new CollectionController(this, storage);
        this.leaderboardMenuController = new LeaderboardMenuController(this, storage);
        this.sessionLifecycleController = new SessionLifecycleController(this, eventBus, gameLoop, model);
        this.sessionLifecycleController.register();
        this.gameLoop.setOnTickHandler(() -> {
            model.tick();
            model.getState().totalTicks++;
        });

        gameMechanismController = new GameMechanismController(this, gameLoop, model);
    }

    public void setNetworkAuth(NetworkAuthBridge networkAuth) {
        this.networkAuth = networkAuth;
        this.networkSession = networkAuth != null ? networkAuth.session() : null;
        authController.setNetworkAuth(networkAuth);
    }

    public NetworkAuthBridge getNetworkAuth() {
        return networkAuth;
    }

    public NetworkSession getNetworkSession() {
        return networkSession;
    }

    public void setView(ViewFacade view) {
        this.view = view;
    }

    public void start() {
        if (networkAuth == null) {
            storage.loadProgress();
        }
        initShopForCurrentUser();
        initQuestsForCurrentUser();
        currentScreen = storage.isLoggedIn() ? ScreenType.MAIN : ScreenType.LOGIN;
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
        viewSupport.refreshView(this);
    }

    public void setScreen(ScreenType screen) {
        this.currentScreen = screen;
        if (screen == ScreenType.GAME) {
            sessionLifecycleController.onSessionStart();
        }
        if (screen != ScreenType.MAIN) {
            currentMenu = MenuType.NONE;
        }
        refreshView();
    }

    public ScreenType getCurrentScreen() {
        return currentScreen;
    }

    public CommandResult requireScreen(ScreenType screen) {
        if (currentScreen != screen) {
            return new CommandResult(
                    "This command is only available on the " + screenLabel(screen) + " screen.", false);
        }
        return null;
    }

    public CommandResult requireLoggedIn() {
        if (!storage.isLoggedIn()) {
            return new CommandResult("You must be logged in to use this command.", false);
        }
        return null;
    }

    public CommandResult requireNotLoggedIn() {
        if (storage.isLoggedIn()) {
            return new CommandResult("You are already logged in.", false);
        }
        return null;
    }

    private String screenLabel(ScreenType screen) {
        return switch (screen) {
            case REGISTER -> "Register";
            case LOGIN -> "Login";
            case MAIN -> "Main";
            case LEVEL_SELECTOR -> "Level Selection";
            case GAME -> "Game";
            case COLLECTION -> "Collection";
            case LEADERBOARD -> "Leaderboard";
            case GREEN_HOUSE -> "Greenhouse";
            case SHOP -> "Shop";
        };
    }

    public GameNavigationState getGameNavigation() {
        return gameNavigation;
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
        return menuSupport.enterMenu(this, menuName);
    }

    public CommandResult openTravelLogMenu() {
        CommandResult screenCheck = requireScreen(ScreenType.LEVEL_SELECTOR);
        if (screenCheck != null) {
            return screenCheck;
        }
        if (gameNavigation.phase != Phase.CHAPTER) {
            return new CommandResult("Open the travel log from the game menu.", false);
        }
        CommandResult loggedInCheck = requireLoggedIn();
        if (loggedInCheck != null) {
            return loggedInCheck;
        }
        if (currentMenu != MenuType.NONE && currentMenu != MenuType.TRAVEL_LOG) {
            return new CommandResult("Close the current menu first.", false);
        }
        currentMenu = MenuType.TRAVEL_LOG;
        return new CommandResult("Opened travel log.", true);
    }

    public void clearCurrentMenu() {
        currentMenu = MenuType.NONE;
    }

    public void openMenu(MenuType menu) {
        currentMenu = menu == null ? MenuType.NONE : menu;
        refreshView();
    }

    public MenuType getCurrentMenu() {
        return currentMenu;
    }

    public CommandResult exitMenu() {
        return menuSupport.exitMenu(this);
    }

    public CommandResult showCurrentMenu() {
        if (currentScreen == ScreenType.MAIN && currentMenu != MenuType.NONE) {
            return new CommandResult(
                    "Current screen: main (" + currentMenu.name().toLowerCase() + " menu open).", true);
        }
        if (currentScreen == ScreenType.LEVEL_SELECTOR && currentMenu == MenuType.TRAVEL_LOG) {
            return new CommandResult("Current screen: game (travel log open).", true);
        }
        return new CommandResult("Current screen: " + currentScreen.name().toLowerCase(), true);
    }

    public void initShopForCurrentUser() {
        if (!storage.isLoggedIn()) {
            shop = null;
            shopController = null;
            return;
        }

        User user = storage.getCurrentUser();
        shop = new Shop(user);
        shop.ensureDailyFresh();
        shopController = new ShopController(shop, storage);
    }

    public void initQuestsForCurrentUser() {
        User user = storage.getCurrentUser();
        if (user == null)
            return;
        QuestAssigner.ensureAssigned(user);
        storage.loadQuestProgress(user);
    }

    public void sendMessage(String message) {
        if (view != null) {
            view.showMessage(message);
        }
    }

    public void showError(String message) {
        if (view != null) {
            view.showError(message);
        }
    }

    public boolean scrollMessages(int olderDelta) {
        if (view == null) {
            return false;
        }
        return view.scrollMessages(olderDelta);
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

    public SessionLifecycleController getSessionLifecycleController() {
        return sessionLifecycleController;
    }

    public GreenhouseController getGreenhouseController() {
        return greenhouseController;
    }

    public LeaderboardMenuController getLeaderboardMenuController() {
        return leaderboardMenuController;
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

    public ModelManager getModel() {
        return model;
    }
}
