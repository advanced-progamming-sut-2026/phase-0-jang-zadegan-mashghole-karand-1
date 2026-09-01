package view.gdx.ui.screens.menus;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import controller.ControllerManager;
import view.MenuType;
import view.gdx.AssetContext;
import view.gdx.ui.UiScreen;
import view.gdx.ui.UiViewContext;
import view.gdx.ui.widgets.UiWidgets;

public class PauseOverlayScreen implements UiScreen{
    private static final String WINDOWTOPPER = "IMAGE_UI_PAUSEMENU_WINDOWTOPPER";
    private static final String ZOMBOSS_TOPPER =
            "IMAGE_UI_PAUSEMENU_ZOMBOSS_TOPPER_ANIM_ZOMBOSS_TOPPER_ANIM_134X211";
    private static final float MENU_WIDTH = 520f;

    
    
    private Stage stage;
    private final Texture dimTexture;
    private final Texture brownTexture;
    private final Texture panelTexture;
    private final Texture tipTexture;
    
    private final Group topper = new Group();
    private final Image windowTopper = new Image();
    private final Image zombossTopper = new Image();

    private ControllerManager controller;
    private AssetContext assets;
    private boolean toppersStyled;

    public PauseOverlayScreen(){
        stage = new Stage(new ScreenViewport());
        dimTexture = solid(new Color(0f, 0f, 0f, 0.55f), 1, 1);
        brownTexture = makeRoundedRect(new Color(0.36f, 0.18f, 0.07f, 1f), 256, 256, 12);
        panelTexture = makeRoundedRect(new Color(0.75f, 0.55f, 0.30f, 1f), 256, 256, 8);
        tipTexture = makeRoundedRect(new Color(0.93f, 0.86f, 0.55f, 1f), 256, 256, 8);

        topper.addActor(windowTopper);
        topper.addActor(zombossTopper);
        topper.setTouchable(Touchable.disabled);

        stage.addActor(buildRoot());
    }

    private Table buildRoot() {
        Label title = UiWidgets.title("GAME PAUSED");
        Table buttons = buildPauseButtons();
        Table panel = buildPausePanel(title, buttons);
        Table brownOuter = buildBrownOuter(panel);
        Table panelLayer = new Table();
        panelLayer.add(brownOuter).padTop(70f);
        Table topperLayer = new Table();
        topperLayer.top();
        topperLayer.add(topper);
        Stack stack = new Stack();
        stack.add(panelLayer);
        stack.add(topperLayer);
        Table root = new Table();
        root.setFillParent(true);
        root.setBackground(new TextureRegionDrawable(new TextureRegion(dimTexture)));
        root.setTouchable(Touchable.enabled);
        root.center();
        root.add(stack);
        return root;
    }

    private Table buildPauseButtons() {
        TextButton exit = UiWidgets.secondary("Exit level");
        TextButton restart = UiWidgets.secondary("Restart level");
        TextButton resume = UiWidgets.primary("Resume");

        UiWidgets.onChange(exit, () -> UiWidgets.apply(controller,
                controller.getSessionLifecycleController().returnToLevelSelect()));

        UiWidgets.onChange(restart, () -> {
            if (controller.getSessionLifecycleController().isOnlineMatch()) {
                var net = controller.getNetworkSession();
                if (net != null && net.socket().requestRestart()) {
                    controller.openMenu(MenuType.MATCH_RESTART_WAIT);
                    return;
                }
            }
            UiWidgets.apply(controller,
                    controller.getSessionLifecycleController().restartLevel());
        });

        UiWidgets.onChange(resume, () -> UiWidgets.apply(controller,
            controller.exitMenu()
        ));

        Table buttons = new Table();
        buttons.add(exit).width(160f).height(44f).padRight(8f);
        buttons.add(restart).width(140f).height(44f).padRight(8f);
        buttons.add(resume).width(140f).height(44f);
        return buttons;
    }

    private Table buildPausePanel(Label title, Table buttons) {
        Table panel = new Table();
        panel.setBackground(new TextureRegionDrawable(new TextureRegion(panelTexture)));
        panel.pad(24f,28f,24f,28f);
        panel.add(title).padBottom(16f).row();
        panel.add(buttons);
        return panel;
    }

    private Table buildBrownOuter(Table panel) {
        Table brownOuter = new Table();
        brownOuter.setBackground(new TextureRegionDrawable(new TextureRegion(brownTexture)));
        brownOuter.setTouchable(Touchable.enabled);
        brownOuter.pad(10f);
        brownOuter.add(panel);
        return brownOuter;
    }

        @Override
    public void show(UiViewContext context) {
        controller = context.controller;
        assets = context.assets;
        toppersStyled = false;
    }
    @Override
    public void act(float deltaSeconds) {
        if (!toppersStyled && assets != null) {
            styleToppers(assets);
        }
        stage.act(deltaSeconds);
    }
    private void styleToppers(AssetContext assets) {
        var window = assets.region(WINDOWTOPPER);
        var zomboss = assets.region(ZOMBOSS_TOPPER);
        if (window == null) {
            return;
        }
        float scale = MENU_WIDTH / window.getRegionWidth();
        float windowW = window.getRegionWidth() * scale;
        float windowH = window.getRegionHeight() * scale;
        windowTopper.setDrawable(new TextureRegionDrawable(window));
        windowTopper.setSize(windowW, windowH);
        windowTopper.setPosition(0f, 0f);
        topper.setSize(windowW, windowH);
        topper.setSize(windowW, windowH);
        if (topper.getParent() instanceof Table parent) {
            parent.invalidateHierarchy();
        }
        if (zomboss != null) {
            float zW = zomboss.getRegionWidth() * scale;
            float zH = zomboss.getRegionHeight() * scale;
            zombossTopper.setDrawable(new TextureRegionDrawable(zomboss));
            zombossTopper.setSize(zW, zH);
            zombossTopper.setPosition((windowW - zW) * 0.5f, (windowH - zH) * 0.5f);
        }
        toppersStyled = true;
    }
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }
    @Override
    public void dispose() {
        stage.dispose();
        dimTexture.dispose();
        brownTexture.dispose();
        panelTexture.dispose();
        tipTexture.dispose();
    }
    @Override
    public Stage stage() {
        return stage;
    }

    private static Texture solid(Color color, int w, int h) {
        Pixmap px = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        px.setColor(color);
        px.fill();
        Texture tex = new Texture(px);
        px.dispose();
        return tex;
    }


    private Texture makeRoundedRect(Color color, int w, int h, int radius) {
        Pixmap px = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        px.setColor(0, 0, 0, 0);
        px.fill();

        px.setColor(color);

        px.fillRectangle(radius, 0, w - 2 * radius, h);

        px.fillRectangle(0, radius, w, h - 2 * radius);

        px.fillCircle(radius,         radius,         radius);
        px.fillCircle(w - radius - 1, radius,         radius);
        px.fillCircle(radius,         h - radius - 1, radius);
        px.fillCircle(w - radius - 1, h - radius - 1, radius);

        Texture tex = new Texture(px);
        px.dispose();
        return tex;
    }
}
