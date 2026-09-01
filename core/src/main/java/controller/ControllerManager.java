package controller;

import java.util.ArrayList;
import java.util.List;

import controller.CommandResult.CommandResult;
import model.ModelManager;
import model.core.EventBus;
import model.core.GameLoop;
import model.data.content.chapter.ChapterType;
import model.data.wave.LevelConfig;
import model.quest.QuestAssigner;
import model.rule.SessionConfig;
import model.rule.SessionContext;
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
    private final RankedChallengeController rankedChallengeController;

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

    private final List<String> dialogueSpeakers = new ArrayList<>();
    private final List<String> dialogueTexts = new ArrayList<>();
    private int dialogueIndex = -1;

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
        this.rankedChallengeController = new RankedChallengeController(this, storage, gameNavigation);
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
        storage.loadProgress();
        if (networkAuth != null) {
            networkAuth.restoreSession();
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
        if (screen != ScreenType.MAIN) {
            currentMenu = MenuType.NONE;
        }
        if (screen == ScreenType.GAME) {
            sessionLifecycleController.onSessionStart();
            clearDialogue();
            showAnnouncement("Get ready to defend your lawn!");
            startLevelDialogueIfNeeded();
        } else {
            clearDialogue();
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

    public void showAnnouncement(String message) {
        if (view != null) {
            view.showAnnouncement(message);
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

    public boolean isDialogueActive() {
        return dialogueIndex >= 0 && dialogueIndex < dialogueTexts.size();
    }

    public String currentDialogueSpeaker() {
        return isDialogueActive() ? dialogueSpeakers.get(dialogueIndex) : "";
    }

    public String currentDialogueText() {
        return isDialogueActive() ? dialogueTexts.get(dialogueIndex) : "";
    }

    public void advanceDialogue() {
        if (!isDialogueActive()) {
            return;
        }
        dialogueIndex++;
        if (!isDialogueActive()) {
            clearDialogue();
        }
        refreshView();
    }

    public void startZombossEndDialogue(boolean won) {
        SessionContext context = model.getPlayContext();
        if (context == null || context.getConfig() == null || context.getConfig().levelConfig == null) {
            return;
        }
        if (context.getConfig().levelConfig.levelNumber != 5) {
            return;
        }
        dialogueSpeakers.clear();
        dialogueTexts.clear();
        if (won) {
            addDialogueLine("Penny", "Zomboss has been defeated.");
            addDialogueLine("Crazy Dave", "We did it! Victory tacos for everyone!");
            addDialogueLine("Penny", "Well done. The lawn is safe for now.");
        } else {
            addDialogueLine("Crazy Dave", "Zomboss got us this time.");
            addDialogueLine("Penny", "Regroup and try again.");
            addDialogueLine("Crazy Dave", "Next round, we win for sure!");
        }
        dialogueIndex = 0;
    }

    private void startLevelDialogueIfNeeded() {
        SessionContext context = model.getPlayContext();
        if (context == null || context.getConfig() == null) {
            return;
        }
        SessionConfig config = context.getConfig();
        if (config.isMinigame() || config.levelConfig == null) {
            return;
        }
        LevelConfig level = config.levelConfig;
        ChapterType chapter = level.chapterType;
        int number = level.levelNumber;
        dialogueSpeakers.clear();
        dialogueTexts.clear();
        if (number == 1) {
            addDialogueLine("Penny", "Welcome to " + chapterLabel(chapter) + ".");
            addDialogueLine("Crazy Dave", "Time to plant some defenses!");
            addDialogueLine("Penny", "Collect sun and stop the zombies.");
        } else if (number == 5) {
            addDialogueLine("Penny", "Zomboss is near. Prepare for the boss fight.");
            addDialogueLine("Crazy Dave", "This is gonna be wild!");
        } else if (number == 2 || number == 3 || number == 4) {
            addDialogueLine("Penny", "New threats ahead in " + chapterLabel(chapter) + ".");
            addDialogueLine("Crazy Dave", "Let's keep those zombies off the lawn!");
        }
        dialogueIndex = dialogueTexts.isEmpty() ? -1 : 0;
    }

    private void addDialogueLine(String speaker, String text) {
        dialogueSpeakers.add(speaker);
        dialogueTexts.add(text);
    }

    private void clearDialogue() {
        dialogueSpeakers.clear();
        dialogueTexts.clear();
        dialogueIndex = -1;
    }

    private static String chapterLabel(ChapterType chapter) {
        if (chapter == null) {
            return "this world";
        }
        return switch (chapter) {
            case ANCIENT_EGYPT -> "Ancient Egypt";
            case FROSTBITE_CAVES -> "Frostbite Caves";
            case BIG_WAVE_BEACH -> "Big Wave Beach";
            case DARK_AGES -> "Dark Ages";
        };
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

    public RankedChallengeController getRankedChallengeController() {
        return rankedChallengeController;
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

    public GameLoop getGameLoop() {
        return gameLoop;
    }

    public ModelManager getModel() {
        return model;
    }
}
