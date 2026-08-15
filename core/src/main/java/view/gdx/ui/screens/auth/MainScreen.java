package view.gdx.ui.screens.auth;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import controller.ControllerManager;
import view.gdx.AssetContext;
import view.gdx.ui.UiScreen;
import view.gdx.ui.UiSkin;
import view.gdx.ui.UiViewContext;

public final class MainScreen implements UiScreen {
    private final Stage stage;
    private final Image title;
    private final TextButton startGame;
    private final ImageButton settings;
    private final ImageButton news;
    private final ImageButton profile;
    private final TextButton logout;
    private final TextButton quit;

    private ControllerManager controller;
    private AssetContext assets;

    public MainScreen() {
        this.stage = new Stage(new ScreenViewport());
        this.title = new Image();
        this.startGame = AuthWidgets.primary("Play");
        this.settings = new ImageButton(new ImageButton.ImageButtonStyle());
        this.news = new ImageButton(new ImageButton.ImageButtonStyle());
        this.profile = new ImageButton(new ImageButton.ImageButtonStyle());
        this.logout = AuthWidgets.plain("Logout");
        this.quit = AuthWidgets.plain("Quit");

        AuthWidgets.onChange(startGame, () -> AuthWidgets.apply(controller, controller.enterMenu("game")));
        AuthWidgets.onChange(settings, () -> AuthWidgets.apply(controller, controller.enterMenu("settings")));
        AuthWidgets.onChange(news, () -> AuthWidgets.apply(controller, controller.enterMenu("news")));
        AuthWidgets.onChange(profile, () -> AuthWidgets.apply(controller, controller.enterMenu("profile")));
        AuthWidgets.onChange(logout, () -> AuthWidgets.apply(controller, controller.getMainMenuController().logout()));
        AuthWidgets.onChange(quit, () -> controller.quit());

        Table topLeft = new Table();
        topLeft.add(logout).padRight(8f);
        topLeft.add(quit);

        Table root = new Table();
        root.setFillParent(true);
        root.pad(16f);

        root.add(topLeft).left().top();
        root.add().expandX();
        root.add(profile).right().top();
        root.row();

        root.add();
        root.add(title).expand().center();
        root.add();
        root.row();

        root.add(news).size(72f).left().bottom();
        root.add(startGame).width(300f).height(48f).center().bottom().padBottom(8f);
        root.add(settings).right().bottom();

        stage.addActor(root);
    }


    @Override
    public void show(UiViewContext context) {
        this.controller = context.controller;
        this.assets = context.assets;

    }

    @Override
    public void act(float deltaSeconds) {
        if (title.getDrawable() == null && assets != null) {
            var region = assets.region("IMAGE_UI_MAINMENU_PVZ2_LOGO_HORIZONTAL");
            if (region != null) {
                title.setDrawable(new TextureRegionDrawable(region));
                title.invalidateHierarchy();
            }
        }
        if (news.getStyle().imageUp == null && assets != null) {
            var region = assets.region("IMAGE_UI_HUD_NEWSBUTTON_BUTTONS_HUD_NEWS_SELECTED_COPY_2");
            if (region != null) {
                ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(news.getStyle());
                style.imageUp = new TextureRegionDrawable(region);
                news.setStyle(style);
            }
        }
        if (settings.getStyle().imageUp == null && assets != null) {
            var region = assets.region("IMAGE_UI_HUD_SETTINGSBUTTON_BUTTONS_HUD_SETTINGS_SELECTED");
            if (region != null) {
                ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(settings.getStyle());
                style.imageUp = new TextureRegionDrawable(region);
                settings.setStyle(style);
            }
        }
        if (profile.getStyle().imageUp == null && assets != null) {
            var region = assets.region("IMAGE_UI_MAINMENU_MM_PLAYERICON");
            if (region != null) {
                ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(profile.getStyle());
                style.imageUp = new TextureRegionDrawable(region);
                profile.setStyle(style);
            }
        }
        stage.act(deltaSeconds);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width,height,true);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    @Override
    public Stage stage() {
        return stage;
    }
}
