package view.gdx;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;

import app.DesktopApp;
import view.gdx.anim.AnimStateStore;
import view.gdx.catalog.DefaultVisualCatalog;
import view.gdx.catalog.VisualCatalog;
import view.gdx.lawn.LawnLayout;
import view.gdx.lawn.LawnRenderer;
import view.gdx.ui.UiNavigator;

public final class GraphicsApp extends ApplicationAdapter {
    private OrthographicCamera camera;
    private FitViewport worldViewport;
    private SpriteBatch batch;

    private DesktopApp app;
    private UiNavigator ui;
    private VisualCatalog catalog;
    private AssetContext assets;
    private LawnLayout lawnLayout;
    private LawnRenderer lawnRenderer;
    private AnimStateStore animStates;
    private VisibilityResolver visibilityResolver;

    @Override
    public void create() {
        camera = new OrthographicCamera();
        worldViewport = new FitViewport(1280, 720, camera);
        batch = new SpriteBatch();

        app = DesktopApp.create();
        ui = app.navigator();

        catalog = new DefaultVisualCatalog();
        assets = new AssetContext(catalog);
        lawnLayout = new LawnLayout();
        animStates = new AnimStateStore();
        visibilityResolver = new VisibilityResolver();
        lawnRenderer = new LawnRenderer(catalog, lawnLayout, animStates, visibilityResolver);

        Gdx.app.log("GraphicsApp", "ready assets=" + assets.status()
                + " screen=" + app.controller().getCurrentScreen());
    }

    @Override
    public void render() {
        float dt = Gdx.graphics.getDeltaTime();
        assets.update();
        ui.act(dt);

        Gdx.gl.glClearColor(0.08f, 0.1f, 0.14f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (app.isGameScreen()) {
            worldViewport.apply();
            batch.setProjectionMatrix(camera.combined);
            batch.begin();
            lawnRenderer.render(batch, assets, app.gameState(), dt);
            batch.end();
        }

        ui.draw();
    }

    @Override
    public void resize(int width, int height) {
        worldViewport.update(width, height, true);
        ui.resize(width, height);
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
