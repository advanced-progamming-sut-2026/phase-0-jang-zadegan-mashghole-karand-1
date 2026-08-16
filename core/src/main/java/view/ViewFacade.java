package view;

import controller.ControllerManager;
import model.core.ReadOnlyGameState;
import model.service.*;
import view.gdx.AssetContext;

public interface ViewFacade {
    void showMessage(String message);

    void showError(String error);

    boolean scrollMessages(int olderDelta);

    void render(ReadOnlyGameState state, ScreenType currentScreen, MenuType currentMenu,
                AuthState authState, GameNavigationState gameNavigation, ProfileViewState profileViewState,
                NewsViewState newsViewState, SettingsViewState settingsViewState,
                LeaderboardViewState leaderboardViewState, CollectionViewState collectionViewState,
                QuestViewState questViewState, HudViewState hudViewState, ControllerManager controllerManager,
                boolean hasUnreadNews);

    void initialize();

    void start();

    void stop();
}
