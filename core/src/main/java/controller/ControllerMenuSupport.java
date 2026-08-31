package controller;

import controller.CommandResult.CommandResult;
import model.service.GameNavigationState;
import model.service.GameNavigationState.Phase;
import network.NetworkSession;
import view.MenuType;
import view.ScreenType;

final class ControllerMenuSupport {

    CommandResult enterMenu(ControllerManager manager, String menuName) {
        String name = menuName.trim().toLowerCase();

        if (manager.currentScreen == ScreenType.REGISTER && name.equals("login")) {
            manager.setScreen(ScreenType.LOGIN);
            return new CommandResult("Entered login menu.", true);
        }

        if (manager.currentScreen == ScreenType.MAIN) {
            CommandResult mainResult = enterFromMainMenu(manager, name);
            if (mainResult != null) {
                return mainResult;
            }
        }

        if (manager.currentScreen == ScreenType.LEVEL_SELECTOR
                && manager.getGameNavigation().phase == Phase.CHAPTER) {
            CommandResult levelSelectorResult = enterFromLevelSelector(manager, name);
            if (levelSelectorResult != null) {
                return levelSelectorResult;
            }
        }

        if (manager.currentScreen == ScreenType.LEVEL_SELECTOR
                && manager.currentMenu == MenuType.TRAVEL_LOG
                && (name.equals("minigames") || name.equals("minigame"))) {
            return manager.getGameMenuController().enterMinigames();
        }

        if (manager.currentScreen == ScreenType.GAME) {
            CommandResult gameres = enterFromGame(manager, name);
            if (gameres != null) {
                return gameres;
            }
        }

        return new CommandResult("Cannot enter menu from here.", false);
    }

    private CommandResult enterFromMainMenu(ControllerManager manager, String name) {
        if (name.equals("game")) {
            if (!manager.getStorage().isLoggedIn()) {
                return new CommandResult("You must be logged in to play.", false);
            }
            if (manager.currentMenu != MenuType.NONE) {
                return new CommandResult("Close the current menu first.", false);
            }
            manager.getGameNavigation().reset();
            manager.getGameNavigation().phase = Phase.CHAPTER;
            manager.setScreen(ScreenType.LEVEL_SELECTOR);
            return new CommandResult("Entered game menu. Select a chapter.", true);
        }
        if (name.equals("settings") || name.equals("setting")) {
            return openMainMenu(manager, MenuType.SETTING, "settings");
        }
        if (name.equals("news")) {
            return openMainMenu(manager, MenuType.NEWS, "news");
        }
        if (name.equals("profile")) {
            return openMainMenu(manager, MenuType.PROFILE, "profile");
        }
        return null;
    }

    private CommandResult enterFromLevelSelector(ControllerManager manager, String name) {
        if (name.equals("travel-log") || name.equals("travel log")) {
            return manager.openTravelLogMenu();
        }
        if (name.equals("collection")) {
            CommandResult openCheck = manager.getCollectionController().requireCanOpenCollection();
            if (openCheck != null) {
                return openCheck;
            }
            manager.getCollectionController().onOpened();
            manager.setScreen(ScreenType.COLLECTION);
            return new CommandResult("Opened collection. Default tab: plants.", true);
        }
        if (name.equals("leaderboard")) {
            CommandResult loggedInCheck = manager.requireLoggedIn();
            if (loggedInCheck != null) {
                return loggedInCheck;
            }
            manager.setScreen(ScreenType.LEADERBOARD);
            return new CommandResult("Opened leaderboard.", true);
        }
        if (name.equals("greenhouse") || name.equals("green-house") || name.equals("green house")) {
            CommandResult loggedInCheck = manager.requireLoggedIn();
            if (loggedInCheck != null) {
                return loggedInCheck;
            }
            manager.getGreenhouseController().hidePots();
            manager.setScreen(ScreenType.GREEN_HOUSE);
            return new CommandResult("Opened greenhouse.", true);
        }
        return null;
    }

    private CommandResult openMainMenu(ControllerManager manager, MenuType menu, String label) {
        CommandResult screenCheck = manager.requireScreen(ScreenType.MAIN);
        if (screenCheck != null) {
            return screenCheck;
        }
        CommandResult loggedInCheck = manager.requireLoggedIn();
        if (loggedInCheck != null) {
            return loggedInCheck;
        }
        if (manager.currentMenu != MenuType.NONE && manager.currentMenu != menu) {
            return new CommandResult("Close the current menu first.", false);
        }
        if (menu == MenuType.NEWS) {
            manager.getNewsMenuController().onMenuOpened();
        }
        manager.currentMenu = menu;
        return new CommandResult("Opened " + label + " menu.", true);
    }

    CommandResult exitMenu(ControllerManager manager) {
        return switch (manager.currentScreen) {
            case MAIN -> exitMainMenu(manager);
            case LOGIN -> exitLoginMenu(manager);
            case LEVEL_SELECTOR -> exitLevelSelectorMenu(manager);
            case SHOP -> null;
            case GREEN_HOUSE -> {
                if (manager.currentMenu == MenuType.SHOP) {
                    manager.currentMenu = MenuType.NONE;
                    manager.refreshView();
                    yield new CommandResult("Closed shop.", true);
                }
                yield exitToLevelSelector(manager, "Returned to game menu.");
            }
            case COLLECTION -> exitToLevelSelector(manager, "Returned to game menu.");
            case LEADERBOARD -> exitToLevelSelector(manager, "Returned to game menu.");
            case GAME -> {
                if (manager.currentMenu == MenuType.PAUSE
                        || manager.currentMenu == MenuType.QUICK_MESSAGES) {
                    manager.currentMenu = MenuType.NONE;
                    manager.refreshView();
                    yield new CommandResult("Resumed", true);
                }
                if (manager.currentMenu == MenuType.MATCH_RESTART
                        || manager.currentMenu == MenuType.MATCH_RESTART_WAIT) {
                    NetworkSession net = manager.getNetworkSession();
                    if (net != null) {
                        net.socket().cancelRestart();
                    }
                    manager.currentMenu = MenuType.NONE;
                    manager.refreshView();
                    yield new CommandResult("Cancelled restart", true);
                }
                if (manager.currentMenu == MenuType.MATCH_RESULT) {
                    yield manager.getSessionLifecycleController().returnToLevelSelect();
                }
                if (manager.isDialogueActive()) {
                    manager.advanceDialogue();
                    yield new CommandResult("Dialogue advanced.", true);
                }
                yield manager.getSessionLifecycleController().returnToLevelSelect();
            }
            default -> new CommandResult("Cannot exit this menu.", false);
        };
    }

    private CommandResult exitMainMenu(ControllerManager manager) {
        if (manager.currentMenu != MenuType.NONE) {
            if (manager.currentMenu == MenuType.NEWS) {
                manager.getNewsMenuController().onMenuClosed();
            }
            manager.currentMenu = MenuType.NONE;
            manager.refreshView();
            return new CommandResult("Returned to main menu.", true);
        }
        return new CommandResult("Cannot exit this menu.", false);
    }

    private CommandResult exitLoginMenu(ControllerManager manager) {
        manager.getAuthController().clearPasswordResetState();
        manager.getAuthController().clearPendingRegistration();
        manager.setScreen(ScreenType.REGISTER);
        return new CommandResult("Returned to register menu.", true);
    }

    private CommandResult exitLevelSelectorMenu(ControllerManager manager) {
        if (manager.currentMenu != MenuType.NONE) {
            manager.currentMenu = MenuType.NONE;
            manager.refreshView();
            return new CommandResult("Returned to game menu.", true);
        }
        var gameNavigation = manager.getGameNavigation();
        if (gameNavigation.phase == Phase.PLANT) {
            return exitPlantPhase(manager, gameNavigation);
        }
        if (gameNavigation.phase == Phase.LEVEL || gameNavigation.phase == Phase.MINIGAME) {
            return exitToChapterSelection(manager, gameNavigation);
        }
        gameNavigation.reset();
        manager.setScreen(ScreenType.MAIN);
        return new CommandResult("Returned to main menu.", true);
    }

    private CommandResult exitPlantPhase(ControllerManager manager, GameNavigationState gameNavigation) {
        if (gameNavigation.pendingMiniGame != null) {
            gameNavigation.phase = Phase.MINIGAME;
            gameNavigation.pendingMiniGame = null;
            gameNavigation.pendingLevel = null;
            gameNavigation.pendingSpecialLevel = null;
            gameNavigation.selectedPlants.clear();
            manager.refreshView();
            return new CommandResult("Returned to minigame selection.", true);
        }
        gameNavigation.phase = Phase.LEVEL;
        gameNavigation.selectedPlants.clear();
        manager.refreshView();
        return new CommandResult("Returned to level selection.", true);
    }

    private CommandResult exitToChapterSelection(ControllerManager manager, GameNavigationState gameNavigation) {
        gameNavigation.phase = Phase.CHAPTER;
        gameNavigation.selectedChapter = null;
        gameNavigation.selectedLevel = 0;
        gameNavigation.pendingLevel = null;
        gameNavigation.pendingSpecialLevel = null;
        gameNavigation.pendingMiniGame = null;
        manager.refreshView();
        return new CommandResult("Returned to chapter selection.", true);
    }

    private CommandResult exitShopMenu(ControllerManager manager) {
        ShopController shopController = manager.getShopController();
        if (shopController != null) {
            shopController.setShopDisplayMode(ShopController.ShopDisplayMode.MENU);
        }
        manager.setScreen(ScreenType.GREEN_HOUSE);
        return new CommandResult("Returned to greenhouse.", true);
    }

    private CommandResult exitToLevelSelector(ControllerManager manager, String message) {
        manager.getGameNavigation().phase = Phase.CHAPTER;
        manager.setScreen(ScreenType.LEVEL_SELECTOR);
        return new CommandResult(message, true);
    }

    private CommandResult enterFromGame(ControllerManager manager, String name) {
        if (!name.equals("pause")) {
            return null;
        }

        if (manager.currentMenu != MenuType.NONE && manager.currentMenu != MenuType.PAUSE) {
            return new CommandResult("Close the current menu first.", false);
        }
        manager.currentMenu = MenuType.PAUSE;
        return new CommandResult("Paused", true);
    }
}
