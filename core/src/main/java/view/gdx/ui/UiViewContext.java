package view.gdx.ui;

import controller.ControllerManager;
import model.core.ReadOnlyGameState;
import model.service.*;
import view.MenuType;
import view.ScreenType;
import view.gdx.AssetContext;

public final class UiViewContext {
    public final ReadOnlyGameState gameState;
    public final ScreenType screen;
    public final MenuType menu;
    public final AuthState authState;
    public final GameNavigationState gameNavigation;
    public final ProfileViewState profile;
    public final NewsViewState news;
    public final SettingsViewState settings;
    public final LeaderboardViewState leaderboard;
    public final CollectionViewState collection;
    public final QuestViewState quests;
    public final HudViewState hud;
    public final ControllerManager controller;
    public final AssetContext assets;
    public final boolean hasUnreadNews;

    public UiViewContext(ReadOnlyGameState gameState, ScreenType screen, MenuType menu,
            AuthState authState, GameNavigationState gameNavigation, ProfileViewState profile,
            NewsViewState news, SettingsViewState settings, LeaderboardViewState leaderboard,
            CollectionViewState collection, QuestViewState quests, HudViewState hud,
            ControllerManager controller, AssetContext assets, boolean hasUnreadNews) {
        this.gameState = gameState;
        this.screen = screen;
        this.menu = menu;
        this.authState = authState;
        this.gameNavigation = gameNavigation;
        this.profile = profile;
        this.news = news;
        this.settings = settings;
        this.leaderboard = leaderboard;
        this.collection = collection;
        this.quests = quests;
        this.hud = hud;
        this.controller = controller;
        this.hasUnreadNews = hasUnreadNews;
        this.assets = assets;
    }
}
