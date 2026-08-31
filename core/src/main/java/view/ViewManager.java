package view;

import controller.ControllerManager;
import controller.InputHandler;
import model.core.ReadOnlyGameState;
import model.service.*;
import model.storage.user.User;
import view.renderer.Renderer;

public class ViewManager implements ViewFacade {
    private final Renderer renderer;
    private final InputListener inputListener;

    public ViewManager(Renderer renderer, InputHandler inputHandler) {
        this.renderer = renderer;
        this.inputListener = new InputListener(inputHandler, renderer);
    }

    public void showMessage(String message) {
        renderer.renderMessage(message);
    }

    public void showAnnouncement(String message) {
        renderer.renderMessage(message);
    }

    public void showError(String message) {
        renderer.renderError(message);
    }

    public boolean scrollMessages(int olderDelta) {
        return renderer.scrollMessages(olderDelta);
    }

    public void render(ReadOnlyGameState state, ScreenType currentScreen, MenuType currentMenu,
            AuthState authState, GameNavigationState gameNavigation, ProfileViewState profileViewState,
            NewsViewState newsViewState, SettingsViewState settingsViewState, LeaderboardViewState leaderboardViewState,
            CollectionViewState collectionViewState, QuestViewState questViewState, HudViewState hudViewState,
            ControllerManager controllerManager, boolean hasUnreadNews) {
        String screenKey = buildScreenKey(currentScreen, currentMenu, gameNavigation, questViewState,
                leaderboardViewState, collectionViewState, state);
        renderer.prepareScreen(screenKey);
        renderScreenContent(state, currentScreen, currentMenu, authState, gameNavigation, profileViewState,
                newsViewState, settingsViewState, leaderboardViewState, collectionViewState, questViewState,
                hudViewState, controllerManager, hasUnreadNews);

        if (currentMenu != MenuType.NONE && currentScreen != ScreenType.MAIN
                && !(currentScreen == ScreenType.LEVEL_SELECTOR && currentMenu == MenuType.TRAVEL_LOG)) {
            renderMenuOverlay(currentMenu, profileViewState, newsViewState, settingsViewState, leaderboardViewState,
                    questViewState);
        }
    }

    private String buildScreenKey(ScreenType currentScreen, MenuType currentMenu,
            GameNavigationState gameNavigation, QuestViewState questViewState,
            LeaderboardViewState leaderboardViewState, CollectionViewState collectionViewState,
            ReadOnlyGameState state) {
        String screenKey = currentScreen.name();
        if (currentScreen == ScreenType.LEVEL_SELECTOR) {
            screenKey += "-" + gameNavigation.phase.name();
            if (currentMenu != MenuType.NONE) {
                screenKey += "-" + currentMenu.name();
                if (currentMenu == MenuType.TRAVEL_LOG) {
                    screenKey += "-" + questViewState.filter + "-" + questViewState.totalCount();
                }
            }
        } else if (currentScreen == ScreenType.MAIN && currentMenu != MenuType.NONE) {
            screenKey += "-" + currentMenu.name();
        } else if (currentScreen == ScreenType.LEADERBOARD) {
            screenKey += "-" + leaderboardViewState.sortColumn.name()
                    + "-" + leaderboardViewState.sortDirection.name();
        } else if (currentScreen == ScreenType.COLLECTION) {
            screenKey += "-" + collectionViewState.tab.name() + "-" + collectionViewState.mode.name();
            if (collectionViewState.hasDetail()) {
                screenKey += "-detail-" + collectionViewState.detailTitle;
            }
        } else if (currentScreen == ScreenType.GAME) {
            if (state.isLevelComplete()) {
                screenKey += "-WIN";
            } else if (state.isGameOver()) {
                screenKey += "-LOSE";
            }
        }
        return screenKey;
    }

    private void renderScreenContent(ReadOnlyGameState state, ScreenType currentScreen, MenuType currentMenu,
            AuthState authState, GameNavigationState gameNavigation, ProfileViewState profileViewState,
            NewsViewState newsViewState, SettingsViewState settingsViewState,
            LeaderboardViewState leaderboardViewState, CollectionViewState collectionViewState,
            QuestViewState questViewState, HudViewState hudViewState, ControllerManager controllerManager,
            boolean hasUnreadNews) {
        switch (currentScreen) {
            case REGISTER:
                renderer.renderRegisterScreen(authState.questions);
                break;
            case LOGIN:
                renderer.renderLoginScreen(authState.isAwaitingSecurityAnswer, authState.isAwaitingNewPassword,
                        authState.passwordResetQuestion);
                break;
            case MAIN:
                if (currentMenu == MenuType.NONE) {
                    renderer.renderMainScreen(hasUnreadNews);
                } else {
                    renderMenuOverlay(currentMenu, profileViewState, newsViewState, settingsViewState,
                            leaderboardViewState, questViewState);
                }
                break;
            case LEVEL_SELECTOR:
                if (currentMenu == MenuType.TRAVEL_LOG) {
                    renderer.renderQuestsOverlay(questViewState);
                } else {
                    renderer.renderLevelSelectionScreen(gameNavigation);
                }
                break;
            case GAME:
                renderer.renderGameScreen(state, hudViewState != null ? hudViewState : HudViewState.empty());
                break;
            case COLLECTION:
                renderer.renderCollectionScreen(collectionViewState);
                break;
            case LEADERBOARD:
                renderer.renderLeaderboardOverlay(leaderboardViewState);
                break;
            case GREEN_HOUSE:
                renderer.renderGreenHouseScreen(controllerManager.getGreenhouseController());
                break;
            case SHOP:
                renderShopScreen(controllerManager);
                break;
        }
    }

    private void renderShopScreen(ControllerManager controllerManager) {
        User user = controllerManager.getStorage().getCurrentUser();
        if (user == null)
            return;
        renderer.renderShopScreen(user.getCoins(), user.getGems(),
                user.dailyDeal.dailyDealPlant, user.dailyDeal.dailyDealPrice,
                user.dailyDeal.dailyDealPurchased, controllerManager.getShopController().getShopDisplayMode());
    }

    private void renderMenuOverlay(MenuType currentMenu, ProfileViewState profileViewState,
            NewsViewState newsViewState, SettingsViewState settingsViewState,
            LeaderboardViewState leaderboardViewState, QuestViewState questViewState) {
        switch (currentMenu) {
            case PAUSE:
                renderer.renderPauseOverlay();
                break;
            case SETTING:
                renderer.renderSettingOverlay(settingsViewState);
                break;
            case PROFILE:
                renderer.renderProfileOverlay(profileViewState);
                break;
            case NEWS:
                renderer.renderNewsOverlay(newsViewState);
                break;
            case TRAVEL_LOG:
                renderer.renderQuestsOverlay(questViewState);
                break;
            case PLANT_SELECTOR:
                renderer.renderPlantSelectorOverlay();
                break;
            default:
                break;
        }
    }

    public void start() {
        inputListener.start();
    }

    public void initialize() {
        renderer.initialize();
    }

    public void stop() {
        inputListener.stop();
        renderer.stop();
    }
}
