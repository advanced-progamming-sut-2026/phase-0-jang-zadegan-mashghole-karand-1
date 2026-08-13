package view.gdx.ui;

import controller.ControllerManager;
import model.core.ReadOnlyGameState;
import model.service.*;
import view.MenuType;
import view.ScreenType;
import view.ViewFacade;

/** Desktop {@link ViewFacade}: forwards controller refresh to {@link UiNavigator}. */
public final class DesktopViewFacade implements ViewFacade {
    private final UiNavigator navigator;

    public DesktopViewFacade(UiNavigator navigator) {
        this.navigator = navigator;
    }

    @Override
    public void showMessage(String message) {
        navigator.showToast(message);
    }

    @Override
    public void showError(String error) {
        navigator.showToast(error);
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
                questViewState, hudViewState, controllerManager, hasUnreadNews);
        navigator.show(context);
    }

    @Override
    public void initialize() {
        // pvz-skin loads lazily via UiSkin.get()
    }

    @Override
    public void start() {
        // Desktop uses Scene2D input, not stdin InputListener.
    }

    @Override
    public void stop() {
        // No background input thread on desktop.
    }
}
