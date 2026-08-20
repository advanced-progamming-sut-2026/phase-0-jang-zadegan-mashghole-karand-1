package view.gdx;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import app.DesktopApp;
import model.data.content.chapter.ChapterType;
import model.rule.SessionContext;
import view.gdx.anim.AnimStateStore;
import view.gdx.catalog.DefaultVisualCatalog;
import view.gdx.catalog.VisualCatalog;
import view.gdx.lawn.LawnBackgroundRenderer;
import view.gdx.lawn.LawnLayout;
import view.gdx.lawn.LawnRenderer;
import view.gdx.ui.MenuBackdrop;
import view.gdx.ui.UiNavigator;

public final class GraphicsApp extends ApplicationAdapter {
    private OrthographicCamera camera;
    private Viewport worldViewport;
    private SpriteBatch batch;

    private DesktopApp app;
    private UiNavigator ui;
    private VisualCatalog catalog;
    private AssetContext assets;
    private MenuBackdrop menuBackdrop;
    private LawnLayout lawnLayout;
    private LawnBackgroundRenderer lawnBackground;
    private LawnRenderer lawnRenderer;
    private AnimStateStore animStates;
    private VisibilityResolver visibilityResolver;

    @Override
    public void create() {
        camera = new OrthographicCamera();
        worldViewport = new ExtendViewport(1280, 720, camera);
        batch = new SpriteBatch();

        catalog = new DefaultVisualCatalog();
        assets = new AssetContext(catalog);
        menuBackdrop = new MenuBackdrop();
        menuBackdrop.bind(assets);

        app = DesktopApp.create(assets);
        ui = app.navigator();

        lawnLayout = new LawnLayout();
        lawnBackground = new LawnBackgroundRenderer();
        animStates = new AnimStateStore();
        visibilityResolver = new VisibilityResolver();
        lawnRenderer = new LawnRenderer(catalog, lawnLayout, animStates, visibilityResolver);

        Gdx.app.log("GraphicsApp", "ready assets=" + assets.status()
                + " backdrop=" + menuBackdrop.ready()
                + " screen=" + app.controller().getCurrentScreen());
    }

    @Override
    public void render() {
        float dt = Gdx.graphics.getDeltaTime();
        assets.update();
        if (!menuBackdrop.ready()) {
            menuBackdrop.bind(assets);
        }
        ui.act(dt);

        Gdx.gl.glClearColor(0.08f, 0.1f, 0.14f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (app.isGameScreen()) {
            worldViewport.apply();
            lawnBackground.bind(assets, resolveChapter(), worldViewport.getWorldWidth(), worldViewport.getWorldHeight());
            batch.setProjectionMatrix(camera.combined);
            batch.begin();
            lawnBackground.render(batch);
            lawnRenderer.render(batch, assets, app.gameState(), dt);
            batch.end();
        } else {
            menuBackdrop.render(batch, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }

        ui.draw();
    }

    @Override
    public void resize(int width, int height) {
        worldViewport.update(width, height, true);
        ui.resize(width, height);
    }

    private ChapterType resolveChapter() {
        SessionContext session = app.model().getPlayContext();
        if (session == null || session.getConfig() == null || session.getConfig().levelConfig == null) {
            return null;
        }
        return session.getConfig().levelConfig.chapterType;
    }

    @Override
    public void dispose() {
        if (app != null) {
            app.dispose();
        }
        if (assets != null) {
            assets.dispose();
        }
        if (batch != null) {
            batch.dispose();
        }
    }
}
