package view.gdx.ui;

import controller.ControllerManager;
import model.core.ReadOnlyGameState;
import model.service.*;
import view.MenuType;
import view.ScreenType;
import view.ViewFacade;
import view.gdx.AssetContext;

public final class DesktopViewFacade implements ViewFacade {
    private final UiNavigator navigator;
    private final AssetContext assets;

    public DesktopViewFacade(UiNavigator navigator, AssetContext assets) {
        this.navigator = navigator;
        this.assets = assets;
    }

    @Override
    public void showMessage(String message) {
        navigator.showToast(message, false);
    }

    @Override
    public void showAnnouncement(String message) {
        navigator.announcementOverlay().show(message);
    }

    @Override
    public void showError(String error) {
        navigator.showToast(error, true);
    }

    @Override
    public boolean scrollMessages(int olderDelta) {
        return false;
    }

    @Override
    public void render(ReadOnlyGameState state, ScreenType currentScreen, MenuType currentMenu,
            AuthState authState, GameNavigationState gameNavigation, ProfileViewState profileViewState,
            NewsViewState newsViewState, SettingsViewState settingsViewState,
            LeaderboardViewState leaderboardViewState, CollectionViewState collectionViewState,
            QuestViewState questViewState, HudViewState hudViewState, ControllerManager controllerManager,
            boolean hasUnreadNews) {
        UiViewContext context = new UiViewContext(state, currentScreen, currentMenu, authState, gameNavigation,
                profileViewState, newsViewState, settingsViewState, leaderboardViewState, collectionViewState,
                questViewState, hudViewState, controllerManager, assets, hasUnreadNews);
        navigator.show(context);
    }

    @Override
    public void initialize() {
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }
}
