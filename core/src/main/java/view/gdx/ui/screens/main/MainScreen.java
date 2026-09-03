package view.gdx.ui.screens.main;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import controller.ControllerManager;
import controller.InputHandler;
import view.MenuType;
import view.gdx.AssetContext;
import view.gdx.ui.UiScreen;
import view.gdx.ui.UiViewContext;
import view.gdx.ui.widgets.UiWidgets;

public final class MainScreen implements UiScreen {
    private final Stage stage;
    private final Image title;
    private final TextButton startGame;
    private final ImageButton settings;
    private final ImageButton news;
    private final ImageButton profile;
    private final TextButton logout;
    private final TextButton quit;
    private final Table debugBar;
    private Table cheatRow;
    private TextField cheatField;

    private ControllerManager controller;
    private AssetContext assets;
    private InputHandler inputHandler;

    public MainScreen() {
        this.stage = new Stage(new ScreenViewport());
        this.title = new Image();
        this.startGame = UiWidgets.primary("Play");
        this.settings = new ImageButton(new ImageButton.ImageButtonStyle());
        this.news = new ImageButton(new ImageButton.ImageButtonStyle());
        this.profile = new ImageButton(new ImageButton.ImageButtonStyle());
        this.logout = UiWidgets.plain("Logout");
        this.quit = UiWidgets.plain("Quit");

        UiWidgets.onChange(startGame, () -> UiWidgets.apply(controller, controller.enterMenu("game")));
        UiWidgets.onChange(settings, () -> UiWidgets.apply(controller, controller.enterMenu("settings")));
        UiWidgets.onChange(news, () -> UiWidgets.apply(controller, controller.enterMenu("news")));
        UiWidgets.onChange(profile, () -> UiWidgets.apply(controller, controller.enterMenu("profile")));
        UiWidgets.onChange(logout, () -> UiWidgets.apply(controller, controller.getMainMenuController().logout()));
        UiWidgets.onChange(quit, () -> controller.quit());

        debugBar = buildDebugBar();
        stage.addActor(buildRoot());
    }

    private Table buildDebugBar() {
        TextButton cheatsToggle = UiWidgets.plain("Cheats");
        TextButton runCheat = UiWidgets.primary("Run");
        cheatField = UiWidgets.field("menu cheat add 1000 coin", false);
        cheatRow = new Table();
        cheatRow.add(cheatField).width(280f).height(36f).padRight(6f);
        cheatRow.add(runCheat).width(72f).height(36f);
        cheatRow.setVisible(false);
        UiWidgets.onChange(cheatsToggle, this::toggleCheatRow);
        UiWidgets.onChange(runCheat, this::submitCheat);
        cheatField.setTextFieldListener((field, c) -> {
            if (c == '\n' || c == '\r') {
                submitCheat();
            }
        });

        Table bar = new Table();
        bar.setVisible(false);
        bar.add(cheatsToggle).left().width(88f).height(36f).row();
        bar.add(cheatRow).left().padTop(6f);
        return bar;
    }

    private Table buildRoot() {
        Table topLeft = new Table();
        topLeft.add(logout).padRight(8f);
        topLeft.add(quit).row();
        topLeft.add(debugBar).left().colspan(2).padTop(8f);

        Table root = new Table();
        root.setFillParent(true);
        root.setTouchable(Touchable.childrenOnly);
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
        return root;
    }

    @Override
    public void show(UiViewContext context) {
        this.controller = context.controller;
        this.assets = context.assets;
        if (controller != null) {
            inputHandler = new InputHandler(controller);
        }
        boolean debug = context.settings != null && context.settings.debugMode;
        boolean menuClear = context.menu == null || context.menu == MenuType.NONE;
        debugBar.setVisible(debug && menuClear);
        if (!debug || !menuClear) {
            cheatRow.setVisible(false);
            stage.setKeyboardFocus(null);
        }
    }

    private void toggleCheatRow() {
        cheatRow.setVisible(!cheatRow.isVisible());
        if (cheatRow.isVisible()) {
            stage.setKeyboardFocus(cheatField);
        } else {
            stage.setKeyboardFocus(null);
        }
    }

    private void submitCheat() {
        if (controller == null || inputHandler == null) {
            return;
        }
        String command = UiWidgets.text(cheatField);
        if (command.isEmpty()) {
            return;
        }
        inputHandler.handleInput(command);
        cheatField.setText("");
        stage.setKeyboardFocus(null);
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
        stage.getViewport().update(width, height, true);
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
