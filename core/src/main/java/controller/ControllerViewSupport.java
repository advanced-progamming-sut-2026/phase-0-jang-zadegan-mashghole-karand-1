package controller;

import model.service.*;
import view.MenuType;
import view.ScreenType;
import view.ViewFacade;

final class ControllerViewSupport {

    void refreshView(ControllerManager manager) {
        ViewFacade view = manager.view;
        if (view == null) {
            return;
        }
        updateAuthState(manager);
        if (manager.getStorage().isLoggedIn()) {
            refreshLoggedInState(manager);
        } else {
            clearLoggedOutState(manager);
        }
        view.render(manager.getModel().getState(), manager.currentScreen, manager.currentMenu,
                manager.authState, manager.getGameNavigation(), manager.profileViewState,
                manager.newsViewState, manager.settingsViewState, manager.leaderboardViewState,
                manager.collectionViewState, manager.questViewState, manager.hudViewState,
                manager, manager.hasUnreadNews);
    }

    private void updateAuthState(ControllerManager manager) {
        AuthController authController = manager.getAuthController();
        manager.authState.questions = authController.getQuestions();
        manager.authState.isAwaitingSecurityAnswer = authController.isAwaitingSecurityAnswer();
        manager.authState.isAwaitingNewPassword = authController.isAwaitingNewPassword();
        manager.authState.passwordResetQuestion = authController.getPasswordResetQuestion();
    }

    private void refreshLoggedInState(ControllerManager manager) {
        var storage = manager.getStorage();
        var gameNavigation = manager.getGameNavigation();
        gameNavigation.unlockedChapters = storage.getUnlockedChapters();
        gameNavigation.unlockedPlants = storage.getUnlockedPlants().stream()
                .filter(p -> p != null && !p.isBowlingExclusive())
                .toList();
        gameNavigation.unlockedMinigames = storage.getUnlockedMinigames();
        gameNavigation.levelHighScores = new java.util.HashMap<>(storage.getLevelHighScores());
        gameNavigation.completedLevelIds = new java.util.HashSet<>(
                storage.getCurrentUser().gameProgress.getCompletedLevelIds());
        manager.profileViewState = ProfileViewState.fromUser(storage.getCurrentUser());
        manager.settingsViewState = SettingsViewState.fromUser(storage.getCurrentUser());
        manager.leaderboardViewState = manager.getLeaderboardMenuController().getViewState();
        manager.hasUnreadNews = storage.getCurrentUser().newsFeed.hasUnread();
        manager.newsViewState = manager.currentMenu == MenuType.NEWS
                ? manager.getNewsMenuController().getViewState()
                : NewsViewState.empty();
        manager.questViewState = manager.currentMenu == MenuType.TRAVEL_LOG
                ? manager.getQuestMenuController().getViewState()
                : QuestViewState.empty();
        manager.collectionViewState = manager.currentScreen == ScreenType.COLLECTION
                ? manager.getCollectionController().getViewState()
                : CollectionViewState.empty();
        manager.hudViewState = manager.currentScreen == ScreenType.GAME
                ? HudViewState.fromSession(
                        manager.getModel().getPlayContext(), manager.getModel().getState(),
                        storage.getCurrentUser())
                : HudViewState.empty();
    }

    private void clearLoggedOutState(ControllerManager manager) {
        manager.profileViewState = ProfileViewState.empty();
        manager.newsViewState = NewsViewState.empty();
        manager.settingsViewState = SettingsViewState.empty();
        manager.collectionViewState = CollectionViewState.empty();
        manager.leaderboardViewState = LeaderboardViewState.empty();
        manager.questViewState = QuestViewState.empty();
        manager.hudViewState = HudViewState.empty();
        manager.hasUnreadNews = false;
    }
}
