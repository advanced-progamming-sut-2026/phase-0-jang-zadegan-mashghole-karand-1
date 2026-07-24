package controller;

import controller.CommandResult.CommandResult;
import model.ModelManager;
import model.core.EventBus;
import model.core.GameLoop;
import model.quest.QuestAssigner;
import model.service.*;
import model.service.GameNavigationState.Phase;
import model.shop.Shop;
import model.shop.ShopItems;
import model.storage.StorageManager;
import model.storage.user.User;
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

    private ScreenType currentScreen = ScreenType.REGISTER;
    private MenuType currentMenu = MenuType.NONE;
    private final AuthState authState = new AuthState();
    private final GameNavigationState gameNavigation = new GameNavigationState();
    private ProfileViewState profileViewState = ProfileViewState.empty();
    private NewsViewState newsViewState = NewsViewState.empty();
    private SettingsViewState settingsViewState = SettingsViewState.empty();
    private LeaderboardViewState leaderboardViewState = LeaderboardViewState.empty();
    private CollectionViewState collectionViewState = CollectionViewState.empty();
    private QuestViewState questViewState = QuestViewState.empty();
    private HudViewState hudViewState = HudViewState.empty();
    private boolean hasUnreadNews = false;
    private Shop shop;

    public ControllerManager(ModelManager model,
            EventBus eventBus, GameLoop gameLoop, StorageManager storage) {
        this.model = model;
        this.eventBus = eventBus;
        this.gameLoop = gameLoop;
        this.storage = storage;
        this.greenhouseController = new GreenhouseController(this, storage);
        this.authController = new AuthController(this, storage);
        this.mainMenuController = new MainMenuController(this, storage);
        this.profileController = new ProfileController(this, storage);
        this.settingController = new SettingController(this, storage);
        this.newsMenuController = new NewsMenuController(this, storage);
        this.questMenuController = new QuestMenuController(this);
        new AppEventHandler(eventBus, storage).register();
        this.gameMenuController = new GameMenuController(this, model, storage, gameNavigation);
        this.pickPlantsController = new PickPlantsController(this, model, storage, gameNavigation);
        this.collectionController = new CollectionController(this, storage);
        this.leaderboardMenuController = new LeaderboardMenuController(this, storage);
        this.sessionLifecycleController = new SessionLifecycleController(this, eventBus, gameLoop, model);
        this.sessionLifecycleController.register();
        this.gameLoop.setOnTickHandler(() -> {
            model.tick();
            tick();
            model.getState().totalTicks++;
        });

        gameMechanismController = new GameMechanismController(this, gameLoop, model);
    }

    public void setView(ViewManager view) {
        this.view = view;
    }

    public void start() {
        storage.loadProgress();
        initShopForCurrentUser();
        initQuestsForCurrentUser();
        currentScreen = storage.isLoggedIn() ? ScreenType.MAIN : ScreenType.REGISTER;
        refreshView();
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
            if (storage.isLoggedIn()) {
                gameNavigation.unlockedChapters = storage.getUnlockedChapters();
                gameNavigation.unlockedPlants = storage.getUnlockedPlants();
                gameNavigation.unlockedMinigames = storage.getUnlockedMinigames();
                profileViewState = ProfileViewState.fromUser(storage.getCurrentUser());
                settingsViewState = SettingsViewState.fromUser(storage.getCurrentUser());
                leaderboardViewState = leaderboardMenuController.getViewState();
                hasUnreadNews = storage.getCurrentUser().newsFeed.hasUnread();
                if (currentMenu == MenuType.NEWS) {
                    newsViewState = newsMenuController.getViewState();
                } else {
                    newsViewState = NewsViewState.empty();
                }
                if (currentMenu == MenuType.TRAVEL_LOG) {
                    questViewState = questMenuController.getViewState();
                } else {
                    questViewState = QuestViewState.empty();
                }
                if (currentScreen == ScreenType.COLLECTION) {
                    collectionViewState = collectionController.getViewState();
                } else {
                    collectionViewState = CollectionViewState.empty();
                }
                if (currentScreen == ScreenType.GAME) {
                    hudViewState = HudViewState.fromSession(
                            model.getPlayContext(), model.getState(), storage.getCurrentUser());
                } else {
                    hudViewState = HudViewState.empty();
                }
            } else {
                profileViewState = ProfileViewState.empty();
                newsViewState = NewsViewState.empty();
                settingsViewState = SettingsViewState.empty();
                collectionViewState = CollectionViewState.empty();
                leaderboardViewState = LeaderboardViewState.empty();
                questViewState = QuestViewState.empty();
                hudViewState = HudViewState.empty();
                hasUnreadNews = false;
            }
            view.render(model.getState(), currentScreen, currentMenu, authState, gameNavigation,
                    profileViewState, newsViewState, settingsViewState, leaderboardViewState, collectionViewState,
                    questViewState, hudViewState, this, hasUnreadNews);
        }
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
        String name = menuName.trim().toLowerCase();

        if (currentScreen == ScreenType.REGISTER && name.equals("login")) {
            setScreen(ScreenType.LOGIN);
            return new CommandResult("Entered login menu.", true);
        }

        if (currentScreen == ScreenType.MAIN) {
            if (name.equals("game")) {
                if (!storage.isLoggedIn()) {
                    return new CommandResult("You must be logged in to play.", false);
                }
                if (currentMenu != MenuType.NONE) {
                    return new CommandResult("Close the current menu first.", false);
                }
                gameNavigation.reset();
                gameNavigation.phase = Phase.CHAPTER;
                setScreen(ScreenType.LEVEL_SELECTOR);
                return new CommandResult("Entered game menu. Select a chapter.", true);
            }
            if (name.equals("settings") || name.equals("setting")) {
                return openMainMenu(MenuType.SETTING, "settings");
            }
            if (name.equals("news")) {
                return openMainMenu(MenuType.NEWS, "news");
            }
            if (name.equals("profile")) {
                return openMainMenu(MenuType.PROFILE, "profile");
            }
        }

        if (currentScreen == ScreenType.LEVEL_SELECTOR
                && gameNavigation.phase == Phase.CHAPTER) {
            if (name.equals("travel-log") || name.equals("travel log")) {
                return openTravelLogMenu();
            }
            if (name.equals("collection")) {
                CommandResult openCheck = collectionController.requireCanOpenCollection();
                if (openCheck != null) {
                    return openCheck;
                }
                collectionController.onOpened();
                setScreen(ScreenType.COLLECTION);
                return new CommandResult("Opened collection. Default tab: plants.", true);
            }
            if (name.equals("leaderboard")) {
                CommandResult loggedInCheck = requireLoggedIn();
                if (loggedInCheck != null) {
                    return loggedInCheck;
                }
                setScreen(ScreenType.LEADERBOARD);
                return new CommandResult("Opened leaderboard.", true);
            }
            if (name.equals("greenhouse") || name.equals("green-house") || name.equals("green house")) {
                CommandResult loggedInCheck = requireLoggedIn();
                if (loggedInCheck != null) {
                    return loggedInCheck;
                }
                greenhouseController.hidePots();
                setScreen(ScreenType.GREEN_HOUSE);
                return new CommandResult("Opened greenhouse.", true);
            }
        }

        if (currentScreen == ScreenType.LEVEL_SELECTOR
                && currentMenu == MenuType.TRAVEL_LOG
                && (name.equals("minigames") || name.equals("minigame"))) {
            return gameMenuController.enterMinigames();
        }

        return new CommandResult("Cannot enter menu from here.", false);
    }

    private CommandResult openMainMenu(MenuType menu, String label) {
        CommandResult screenCheck = requireScreen(ScreenType.MAIN);
        if (screenCheck != null) {
            return screenCheck;
        }
        CommandResult loggedInCheck = requireLoggedIn();
        if (loggedInCheck != null) {
            return loggedInCheck;
        }
        if (currentMenu != MenuType.NONE && currentMenu != menu) {
            return new CommandResult("Close the current menu first.", false);
        }
        if (menu == MenuType.NEWS) {
            newsMenuController.onMenuOpened();
        }
        currentMenu = menu;
        return new CommandResult("Opened " + label + " menu.", true);
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

    public MenuType getCurrentMenu() {
        return currentMenu;
    }

    public CommandResult exitMenu() {
        switch (currentScreen) {
            case MAIN:
                if (currentMenu != MenuType.NONE) {
                    if (currentMenu == MenuType.NEWS) {
                        newsMenuController.onMenuClosed();
                    }
                    currentMenu = MenuType.NONE;
                    refreshView();
                    return new CommandResult("Returned to main menu.", true);
                }
                return new CommandResult("Cannot exit this menu.", false);
            case LOGIN:
                authController.clearPasswordResetState();
                authController.clearPendingRegistration();
                setScreen(ScreenType.REGISTER);
                return new CommandResult("Returned to register menu.", true);
            case LEVEL_SELECTOR:
                if (currentMenu != MenuType.NONE) {
                    currentMenu = MenuType.NONE;
                    refreshView();
                    return new CommandResult("Returned to game menu.", true);
                }
                if (gameNavigation.phase == Phase.PLANT) {
                    if (gameNavigation.pendingMiniGame != null) {
                        gameNavigation.phase = Phase.MINIGAME;
                        gameNavigation.pendingMiniGame = null;
                        gameNavigation.pendingLevel = null;
                        gameNavigation.pendingSpecialLevel = null;
                        gameNavigation.selectedPlants.clear();
                        refreshView();
                        return new CommandResult("Returned to minigame selection.", true);
                    }
                    gameNavigation.phase = Phase.LEVEL;
                    gameNavigation.selectedPlants.clear();
                    refreshView();
                    return new CommandResult("Returned to level selection.", true);
                }
                if (gameNavigation.phase == Phase.LEVEL || gameNavigation.phase == Phase.MINIGAME) {
                    gameNavigation.phase = Phase.CHAPTER;
                    gameNavigation.selectedChapter = null;
                    gameNavigation.selectedLevel = 0;
                    gameNavigation.pendingLevel = null;
                    gameNavigation.pendingSpecialLevel = null;
                    gameNavigation.pendingMiniGame = null;
                    refreshView();
                    return new CommandResult("Returned to chapter selection.", true);
                }
                gameNavigation.reset();
                setScreen(ScreenType.MAIN);
                return new CommandResult("Returned to main menu.", true);
            case SHOP:
                if (shopController != null) {
                    shopController.setShopDisplayMode(ShopController.ShopDisplayMode.MENU);
                }
                setScreen(ScreenType.GREEN_HOUSE);
                return new CommandResult("Returned to greenhouse.", true);
            case GREEN_HOUSE:
                gameNavigation.phase = Phase.CHAPTER;
                setScreen(ScreenType.LEVEL_SELECTOR);
                return new CommandResult("Returned to game menu.", true);
            case COLLECTION:
                gameNavigation.phase = Phase.CHAPTER;
                setScreen(ScreenType.LEVEL_SELECTOR);
                return new CommandResult("Returned to game menu.", true);
            case LEADERBOARD:
                gameNavigation.phase = Phase.CHAPTER;
                setScreen(ScreenType.LEVEL_SELECTOR);
                return new CommandResult("Returned to game menu.", true);
            case GAME:
                return sessionLifecycleController.returnToLevelSelect();
            default:
                return new CommandResult("Cannot exit this menu.", false);
        }
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
        if (user == null) return;
        QuestAssigner.ensureAssigned(user);
        storage.loadQuestProgress(user);
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
