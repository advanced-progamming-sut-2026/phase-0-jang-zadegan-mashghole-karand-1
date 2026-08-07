package view.gdx;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;

import app.GameSessionBridge;
import view.gdx.anim.AnimStateStore;
import view.gdx.catalog.DefaultVisualCatalog;
import view.gdx.catalog.VisualCatalog;
import view.gdx.lawn.DemoLawnPreview;
import view.gdx.lawn.LawnLayout;
import view.gdx.lawn.LawnRenderer;
import view.gdx.ui.HudStage;

public final class GraphicsApp extends ApplicationAdapter {
    private OrthographicCamera camera;
    private FitViewport worldViewport;
    private SpriteBatch batch;

    private VisualCatalog catalog;
    private AssetContext assets;
    private LawnLayout lawnLayout;
    private LawnRenderer lawnRenderer;
    private DemoLawnPreview demoPreview;
    private HudStage hud;
    private GameSessionBridge session;
    private AnimStateStore animStates;
    private VisibilityResolver visibilityResolver;

    @Override
    public void create() {
        camera = new OrthographicCamera();
        worldViewport = new FitViewport(1280, 720, camera);
        batch = new SpriteBatch();

        catalog = new DefaultVisualCatalog();
        assets = new AssetContext(catalog);
        lawnLayout = new LawnLayout();
        animStates = new AnimStateStore();
        visibilityResolver = new VisibilityResolver();
        lawnRenderer = new LawnRenderer(catalog, lawnLayout, animStates, visibilityResolver);
        demoPreview = new DemoLawnPreview(catalog, lawnLayout);
        session = new GameSessionBridge();
        session.startDevSession();
        hud = new HudStage(assets.status());

        Gdx.app.log("GraphicsApp", "ready assets=" + assets.status()
                + " skin=" + hud.skinStatus()
                + " session=" + session.status());
    }

    @Override
    public void render() {
        float dt = Gdx.graphics.getDeltaTime();
        session.tick(dt);
        assets.update();
        hud.act(dt);

        Gdx.gl.glClearColor(0.12f, 0.38f, 0.16f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        worldViewport.apply();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        if (session.state() != null) {
            lawnRenderer.render(batch, assets, session.state(), dt);
            int plants = session.state().getPlants().size();
            int zombies = session.state().getZombies().size();
            hud.setStatusText(session.status()
                    + " | plants=" + plants
                    + " zombies=" + zombies
                    + " sun=" + session.state().getSunAmount()
                    + " | " + assets.status());
        } else {
            demoPreview.update(dt);
            demoPreview.render(batch, assets);
            hud.setStatusText("Fallback demo | " + session.status() + " | " + assets.status());
        }
        batch.end();

        hud.draw();
    }

    @Override
    public void resize(int width, int height) {
        worldViewport.update(width, height, true);
        hud.resize(width, height);
    }

    @Override
    public void dispose() {
        if (session != null) {
            session.dispose();
        }
        if (hud != null) {
            hud.dispose();
        }
        if (assets != null) {
            assets.dispose();
        }
        if (batch != null) {
            batch.dispose();
        }
    }
}
