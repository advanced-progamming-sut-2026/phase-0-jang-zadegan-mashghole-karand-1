package view.gdx.ui.screens.menus;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import controller.ControllerManager;
import view.ScreenType;
import view.gdx.AssetContext;
import view.gdx.ui.UiSkin;
import view.gdx.ui.UiViewContext;
import view.gdx.ui.widgets.UiWidgets;

public final class GlobalTopBar {
    private final Stage stage = new Stage(new ScreenViewport());

    private final ImageButton back = new ImageButton(new ImageButton.ImageButtonStyle());
    private final Label coinText = new Label("0", UiSkin.get(), "default");
    private final Label gemText = new Label("0", UiSkin.get(), "default");
    private final Group coinBadge = badge(coinText);
    private final Group gemBadge = badge(gemText);

    private ControllerManager controller;
    private AssetContext assets;
    private boolean styled = false;

    public GlobalTopBar() {
        Table bar = new Table();
        bar.setFillParent(true);
        bar.top().pad(10f);

        bar.add(back).size(64f);
        bar.add().expandX();
        bar.add(coinBadge).padRight(20f);
        bar.add(gemBadge).padRight(200f);

        stage.addActor(bar);

        // Register once (NOT in act)
        UiWidgets.onChange(back, () -> {
            if (controller != null) {
                UiWidgets.apply(controller, controller.exitMenu());
            }
        });
    }

    public void bind(UiViewContext ctx) {
        controller = ctx.controller;
        assets = ctx.assets;

        boolean auth = ctx.screen == ScreenType.LOGIN || ctx.screen == ScreenType.REGISTER;
        boolean showBack = !auth && ctx.screen != ScreenType.MAIN;
        boolean showWallet = !auth;

        back.setVisible(showBack);
        coinBadge.setVisible(showWallet);
        gemBadge.setVisible(showWallet);

        int coins = (ctx.profile == null) ? 0 : ctx.profile.coins;
        int gems = (ctx.profile == null) ? 0 : ctx.profile.gems;

        // Correct field names:
        coinText.setText(String.valueOf(coins));
        gemText.setText(String.valueOf(gems));
    }

    public void act(float dt) {
        if (!styled && assets != null) {
            styleBack(assets);
            styleBadge(coinBadge, assets, "IMAGE_UI_GENERIC_BUTTONS_COIN_BUY_NORMAL",null);
            styleBadge(gemBadge, assets, "IMAGE_UI_GENERIC_BUTTONS_PREMIUM_NORMAL", null);
            styled = true;
        }
        stage.act(dt);
    }

    public void draw() {
        stage.getViewport().apply();
        stage.draw();
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public void dispose() {
        stage.dispose();
    }

    public Stage stage() {
        return stage;
    }

    private static Group badge(Label text) {
        Group g = new Group();
        g.setSize(120f, 36f);

        Image bg = new Image();
        bg.setSize(g.getWidth(), g.getHeight());
        g.addActor(bg);

        text.setFontScale(0.85f);
        text.setPosition(34f, 8f);
        g.addActor(text);

        return g;
    }

    private static void styleBadge(Group g, AssetContext assets, String bgId, String iconId) {
        Image bg = (Image) g.getChild(0);

        var bgR = assets.region(bgId);
        var icR = assets.region(iconId);

        if (bgR != null) {
            bg.setDrawable(new TextureRegionDrawable(bgR));
        }

        if (icR != null) {
            Image icon = new Image(icR);
            icon.setSize(24f, 24f);
            icon.setPosition(4f, 6f);
            g.addActorAt(1, icon);
        }
    }

    private void styleBack(AssetContext assets) {
        var up = assets.region("IMAGE_UI_HUD_WORLDMAP_BUTTONS_HUD_BACK_NORMAL");
        var down = assets.region("IMAGE_UI_HUD_WORLDMAP_BUTTONS_HUD_BACK_SELECTED");
        if (up == null) return;

        ImageButton.ImageButtonStyle s = new ImageButton.ImageButtonStyle();
        s.imageUp = new TextureRegionDrawable(up);
        if (down != null) s.imageDown = new TextureRegionDrawable(down);
        back.setStyle(s);
    }
}