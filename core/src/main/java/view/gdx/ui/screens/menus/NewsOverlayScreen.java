package view.gdx.ui.screens.menus;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import controller.ControllerManager;
import controller.NewsMenuController;
import view.gdx.ui.UiScreen;
import view.gdx.ui.UiViewContext;
import view.gdx.ui.widgets.UiWidgets;

public final class NewsOverlayScreen implements UiScreen {
    private final Stage stage;
    private final Texture brownTexture;
    private final Texture panelTexture;
    private ControllerManager controllerManager;
    private Label newsTitleLabel;
    private Label newsContentLabel;
    private TextButton back;
    private TextButton filterButton;


    public NewsOverlayScreen() {
        stage = new Stage(new ScreenViewport());

        Pixmap brownPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        brownPixmap.setColor(new Color(0.36f, 0.18f, 0.07f, 1f)); // dark brown
        brownPixmap.fill();
        brownTexture = makeRoundedRect(new Color(0.36f, 0.18f, 0.07f, 1f), 256, 256, 12);
        brownPixmap.dispose();

        Pixmap panelPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        panelPixmap.setColor(new Color(0.75f, 0.55f, 0.30f, 1f)); // warm tan
        panelPixmap.fill();
        panelTexture = makeRoundedRect(new Color(0.75f, 0.55f, 0.30f, 1f), 256, 256, 8);
        panelPixmap.dispose();

        Label title = UiWidgets.title("News");
        newsTitleLabel = UiWidgets.body("");
        newsContentLabel = UiWidgets.body("");
        ScrollPane newsScrollPane = new ScrollPane(newsContentLabel);
        newsScrollPane.setFadeScrollBars(false);
        newsScrollPane.setScrollingDisabled(true, false);

        back = UiWidgets.plain("Back");
        UiWidgets.onChange(back, () -> UiWidgets.apply(controllerManager, controllerManager.exitMenu()));

        filterButton = UiWidgets.plain("Filter");
        filterButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                toggleFilter();
            }
        }); 

        newsContentLabel.setWrap(true);

        Table panel = new Table();
        panel.setBackground(new TextureRegionDrawable(new TextureRegion(panelTexture)));
        panel.pad(20f);

        panel.add(title).padBottom(8f).row();
        panel.add(filterButton).padBottom(8f).row();
        panel.add(newsScrollPane)
                .width(380f)
                .height(200f)
                .padBottom(8f)
                .row();
        panel.add(back).width(160f).height(40f);

        Table brownOuter = new Table();
        brownOuter.setBackground(new TextureRegionDrawable(new TextureRegion(brownTexture)));
        brownOuter.setTouchable(Touchable.enabled);
        brownOuter.pad(8f);
        brownOuter.add(panel);

        Table root = new Table();
        root.setFillParent(true);
        root.center();
        root.add(brownOuter);

        stage.addActor(root);
    }

    @Override
    public void show(UiViewContext context) {
        controllerManager = context.controller;
        loadNews();
    }

    @Override
    public void act(float deltaSeconds) {
        stage.act(deltaSeconds);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        brownTexture.dispose();
        panelTexture.dispose();
    }

    @Override
    public Stage stage() {
        return stage;
    }

    private void loadNews() {
        newsTitleLabel.setText("News");
        NewsMenuController newsMenuController = controllerManager.getNewsMenuController();
        StringBuilder sb = new StringBuilder();
        for(String news : newsMenuController.getViewState().messages){
            sb.append(news).append("\n");
        }
        newsContentLabel.setText(sb.toString());
    }

    private void toggleFilter() {
        NewsMenuController.NewsFilter filter = controllerManager.getNewsMenuController().getFilter();
        boolean isALL = filter == NewsMenuController.NewsFilter.ALL;
        if (isALL) {
            filterButton.setText("UNREAD");
            controllerManager.getNewsMenuController().setFilter(NewsMenuController.NewsFilter.UNREAD);
        } else {
            filterButton.setText("ALL");
            controllerManager.getNewsMenuController().setFilter(NewsMenuController.NewsFilter.ALL);
        }
        loadNews();
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

