package view.gdx;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.HashSet;
import java.util.Set;

import app.DesktopApp;
import model.data.content.chapter.ChapterType;
import model.data.plant.PlantType;
import model.core.ReadOnlyGameState;
import model.rule.SessionContext;
import model.service.HudViewState;
import view.MenuType;
import view.gdx.anim.AnimStateStore;
import view.gdx.catalog.DefaultVisualCatalog;
import view.gdx.catalog.VisualCatalog;
import view.gdx.lawn.ConveyorTrayAnimator;
import view.gdx.lawn.LawnBackgroundRenderer;
import view.gdx.lawn.LawnGridDebugOverlay;
import view.gdx.lawn.LawnLayout;
import view.gdx.lawn.LawnPlantInput;
import view.gdx.lawn.LawnRenderer;
import view.gdx.lawn.PlantPlacementFeedbackRenderer;
import view.gdx.lawn.QuickMessageHud;
import view.gdx.lawn.SeedTrayRenderer;
import view.gdx.ui.HudOverlayRenderer;
import view.gdx.ui.MenuBackdrop;
import view.gdx.ui.PlantFoodCollectFeedback;
import view.gdx.ui.UiNavigator;
import view.gdx.ui.screens.GlobalTopBar;

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
    private PlantPlacementFeedbackRenderer placementFeedback;
    private SeedTrayRenderer seedTray;
    private ConveyorTrayAnimator conveyorAnimator;
    private LawnPlantInput plantInput;
    private LawnGridDebugOverlay lawnGridDebug;
    private AnimStateStore animStates;
    private VisibilityResolver visibilityResolver;
    private HudOverlayRenderer hudOverlay;
    private PlantFoodCollectFeedback plantFoodFeedback;
    private QuickMessageHud quickMessageHud;

    @Override
    public void create() {
        camera = new OrthographicCamera();
        worldViewport = new ExtendViewport(1280, 720, camera);
        batch = new SpriteBatch();

        catalog = new DefaultVisualCatalog();
        assets = new AssetContext(catalog);
        menuBackdrop = new MenuBackdrop();

        app = DesktopApp.create(assets);
        ui = app.navigator();
        quickMessageHud = app.quickMessageHud();

        menuBackdrop.bind(assets, app.controller().getCurrentScreen());

        lawnLayout = new LawnLayout();
        lawnBackground = new LawnBackgroundRenderer();
        animStates = new AnimStateStore();
        visibilityResolver = new VisibilityResolver();
        lawnRenderer = new LawnRenderer(catalog, lawnLayout, animStates, visibilityResolver);
        placementFeedback = new PlantPlacementFeedbackRenderer(catalog, lawnLayout, animStates);
        seedTray = new SeedTrayRenderer();
        conveyorAnimator = new ConveyorTrayAnimator();
        hudOverlay = new HudOverlayRenderer();
        plantFoodFeedback = new PlantFoodCollectFeedback();
        plantInput = new LawnPlantInput(worldViewport, lawnLayout, seedTray, hudOverlay);
        lawnGridDebug = new LawnGridDebugOverlay();

        ui.setGameWorldInput(plantInput);

        plantFoodFeedback.register(app.model().getEventBus());

        Gdx.app.log("GraphicsApp", "ready assets=" + assets.status()
                + " backdrop=" + menuBackdrop.ready()
                + " screen=" + app.controller().getCurrentScreen());
    }

    @Override
    public void render() {
        float dt = Gdx.graphics.getDeltaTime();
        assets.update();
        menuBackdrop.bind(assets, app.controller().getCurrentScreen());
        ui.act(dt);
        if (quickMessageHud != null) {
            quickMessageHud.update(dt);
        }

        Gdx.gl.glClearColor(0.08f, 0.1f, 0.14f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (app.isGameScreen()) {
            renderGameScreen(dt);
        } else {
            renderMenuScreen();
        }

        ui.draw();
    }

    private void renderGameScreen(float dt) {
        worldViewport.apply();
        ChapterType chapter = resolveChapter();
        syncLawnLayout(chapter);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        lawnBackground.render(batch);

        boolean onlineMatch = isOnlineMatch();
        boolean paused = pausesWorld(app.controller().getCurrentMenu(), onlineMatch)
                || app.controller().isDialogueActive();
        float worldDt = paused ? 0f : dt;
        SessionContext session = app.model().getPlayContext();
        lawnRenderer.render(batch, assets, app.gameState(), worldDt, chapter);
        HudViewState hud = HudViewState.fromSession(
                session,
                app.gameState(),
                app.controller().getStorage().getCurrentUser());
        renderGameHud(dt, session, hud, chapter, paused);
        batch.end();
        syncGroundWebbingOverlay();
        lawnGridDebug.render(camera, batch, lawnLayout, lawnBackground);
    }

    private void syncGroundWebbingOverlay() {
        var user = app.controller().getStorage().getCurrentUser();
        LawnGridDebugOverlay.ENABLED = user != null
                && user.preferredSetting != null
                && user.preferredSetting.isShowGroundWebbing();
    }

    private void syncLawnLayout(ChapterType chapter) {
        lawnBackground.bind(assets, chapter, worldViewport.getWorldWidth(), worldViewport.getWorldHeight());
        if (lawnBackground.ready()) {
            lawnLayout.syncFromBackground(
                    lawnBackground.drawX(), lawnBackground.drawY(),
                    lawnBackground.leftW(), lawnBackground.centerW(), lawnBackground.stripH(),
                    lawnBackground.scale());
        }
    }

    private void renderGameHud(float dt, SessionContext session, HudViewState hud,
            ChapterType chapter, boolean paused) {
        int sun = resolveTraySun(hud);
        int plantSun = app.gameState() != null ? app.gameState().getPlantSun() : sun;
        int zombieSun = app.gameState() != null ? app.gameState().getZombieSun() : sun;
        boolean dualSun = app.gameState() != null && app.gameState().isDualSunMode();
        float worldHeight = worldViewport.getWorldHeight();
        float worldWidth = worldViewport.getWorldWidth();
        float hudTopReserve = GlobalTopBar.reservedScreenHeight()
                * (worldHeight / Math.max(1, Gdx.graphics.getHeight()));
        if (!paused) {
            conveyorAnimator.update(dt, hud, worldHeight, hudTopReserve);
        }
        int[] traySuns = resolveTraySuns(hud, dualSun, sun, plantSun, zombieSun);
        float mapRightX = lawnLayout.originX + ReadOnlyGameState.GRID_COLS * lawnLayout.cellWidth();
        plantInput.bind(app.controller(), assets, hud, traySuns[0], traySuns[1], worldWidth,
                worldViewport::getWorldHeight, conveyorAnimator, hudTopReserve, mapRightX);
        plantInput.refreshHoverFromPointer();
        renderPlacementFeedback(batch);
        seedTray.render(batch, assets, hud, chapter, traySuns[0], traySuns[1], worldHeight, worldWidth,
                plantInput.selectedPlantName(), plantInput.selectedConveyorIndex(),
                conveyorAnimator, hudTopReserve, boostedPlants(session));
        int pf = app.gameState() != null ? app.gameState().getPlantFoodAmount() : 0;
        if (!paused) {
            plantFoodFeedback.update(dt, pf, lawnLayout, assets, worldWidth, worldHeight);
        }
        renderHudOverlay(session, hud, plantSun, dualSun, zombieSun, pf, worldWidth, worldHeight, mapRightX);
        plantFoodFeedback.render(batch, assets);
        if (dualSun && quickMessageHud != null) {
            quickMessageHud.render(batch, assets, worldWidth, worldHeight, hudTopReserve);
        }
        renderPlantFoodCursor(batch);
        renderShovelCursor(batch);
    }

    private int[] resolveTraySuns(HudViewState hud, boolean dualSun, int sun,
            int plantSun, int zombieSun) {
        int leftSun = sun;
        int rightSun = sun;
        if (dualSun) {
            boolean hasRightTray = hud.rightTraySlots != null && !hud.rightTraySlots.isEmpty();
            if (hasRightTray) {
                leftSun = plantSun;
                rightSun = zombieSun;
            } else {
                leftSun = resolveTraySun(hud);
                rightSun = leftSun;
            }
        }
        return new int[] {leftSun, rightSun};
    }

    private void renderHudOverlay(SessionContext session, HudViewState hud, int plantSun,
            boolean dualSun, int zombieSun, int pf, float worldWidth, float worldHeight, float mapRightX) {
        float progress = 0f;
        int totalWaves = 0;
        if (session != null && session.getWaveManager() != null) {
            totalWaves = session.getWaveManager().getTotalWaves();
            if (app.gameState() != null) {
                progress = session.getWaveManager().getLevelProgress(app.model().getState());
            }
        }
        hudOverlay.setPlantFoodMode(plantInput.isPlantFoodMode());
        hudOverlay.setShovelMode(plantInput.isShovelMode());
        hudOverlay.setPlantFoodPulse(plantFoodFeedback.bankPulse());
        hudOverlay.render(batch, assets, hud, plantSun, dualSun ? zombieSun : -1, pf, progress,
                totalWaves, worldWidth, worldHeight, mapRightX);
    }

    private void renderMenuScreen() {
        plantInput.clearSelection();
        plantInput.clearPlantFoodMode();
        plantInput.clearShovelMode();
        plantInput.clearHover();
        conveyorAnimator.reset();
        plantFoodFeedback.reset();
        if (quickMessageHud != null) {
            quickMessageHud.clear();
        }
        menuBackdrop.render(batch, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void resize(int width, int height) {
        worldViewport.update(width, height, true);
        ui.resize(width, height);
    }

    private static Set<PlantType> boostedPlants(SessionContext session) {
        if (session == null || session.getConfig() == null || session.getConfig().selectedPlants == null) {
            return null;
        }
        Set<PlantType> boosted = new HashSet<>();
        for (PlantType type : session.getConfig().selectedPlants) {
            if (session.isBoosted(type)) {
                boosted.add(type);
            }
        }
        return boosted.isEmpty() ? null : boosted;
    }

    private void renderPlacementFeedback(SpriteBatch batch) {
        if (placementFeedback == null || plantInput == null) {
            return;
        }
        if (plantInput.isPlantFoodMode()) {
            if (plantInput.hoverPlantFoodTargetValid()) {
                placementFeedback.renderPlantFoodTarget(
                        batch, assets, plantInput.hoverRow(), plantInput.hoverCol());
            }
            return;
        }
        if (plantInput.isShovelMode()) {
            if (plantInput.hoverShovelTargetValid()) {
                placementFeedback.renderShovelTarget(
                        batch, assets, plantInput.hoverRow(), plantInput.hoverCol());
            }
            return;
        }
        if (plantInput.hasZombieCursor()) {
            placementFeedback.renderZombieTarget(batch, assets, plantInput.selectedZombie(),
                    plantInput.zombieCursorRow(), plantInput.zombieCursorCol());
            return;
        }
        String selected = plantInput.selectedPlantName();
        if (selected == null || !plantInput.hasHoverCell()) {
            return;
        }
        PlantType plantType = PlantType.fromName(selected);
        if (plantType == null) {
            return;
        }
        placementFeedback.render(batch, assets, plantType, plantInput.hoverRow(), plantInput.hoverCol());
    }

    private void renderPlantFoodCursor(SpriteBatch batch) {
        if (plantInput == null || !plantInput.isPlantFoodMode() || !plantInput.hasPointerWorld()) {
            return;
        }
        TextureRegion leaf = assets.region(HudOverlayRenderer.PF_LEAF);
        if (leaf == null || leaf.getRegionHeight() <= 0) {
            return;
        }
        float size = 36f;
        float w = size * (leaf.getRegionWidth() / (float) leaf.getRegionHeight());
        float x = plantInput.pointerWorldX() - w * 0.5f;
        float y = plantInput.pointerWorldY() - size * 0.5f;
        batch.setColor(1f, 1f, 1f, 0.95f);
        batch.draw(leaf, x, y, w, size);
        batch.setColor(1f, 1f, 1f, 1f);
    }

    private void renderShovelCursor(SpriteBatch batch) {
        if (plantInput == null || !plantInput.isShovelMode() || !plantInput.hasPointerWorld()) {
            return;
        }
        TextureRegion shovel = assets.region(HudOverlayRenderer.SHOVEL_ICON);
        if (shovel == null || shovel.getRegionHeight() <= 0) {
            shovel = assets.region(HudOverlayRenderer.SHOVEL_BUTTON);
        }
        if (shovel == null || shovel.getRegionHeight() <= 0) {
            return;
        }
        float size = 40f;
        float w = size * (shovel.getRegionWidth() / (float) shovel.getRegionHeight());
        float x = plantInput.pointerWorldX() - w * 0.5f;
        float y = plantInput.pointerWorldY() - size * 0.5f;
        batch.setColor(1f, 1f, 1f, 0.95f);
        batch.draw(shovel, x, y, w, size);
        batch.setColor(1f, 1f, 1f, 1f);
    }

    private ChapterType resolveChapter() {
        SessionContext session = app.model().getPlayContext();
        if (session == null || session.getConfig() == null || session.getConfig().levelConfig == null) {
            return null;
        }
        return session.getConfig().levelConfig.chapterType;
    }

    private int resolveTraySun(HudViewState hud) {
        if (app.gameState() == null) {
            return 0;
        }
        if (app.gameState().isDualSunMode()) {
            SessionContext session = app.model().getPlayContext();
            if (session != null && session.getConfig() != null
                    && session.getConfig().localMatchRole == shared.izombie.MatchRole.PLANTS) {
                return app.gameState().getPlantSun();
            }
            if (session != null && session.getConfig() != null
                    && session.getConfig().iZombiePlayMode == shared.izombie.IZombiePlayMode.COUCH) {
                return app.gameState().getPlantSun();
            }
            return app.gameState().getZombieSun();
        }
        return app.gameState().getSunAmount();
    }

    private boolean isOnlineMatch() {
        return app.controller().getSessionLifecycleController().isOnlineMatch();
    }

    private static boolean pausesWorld(MenuType menu, boolean onlineMatch) {
        if (onlineMatch && (menu == MenuType.PAUSE || menu == MenuType.QUICK_MESSAGES)) {
            return false;
        }
        return menu == MenuType.PAUSE
                || menu == MenuType.MATCH_RESTART
                || menu == MenuType.MATCH_RESTART_WAIT
                || menu == MenuType.MATCH_RESULT
                || menu == MenuType.QUICK_MESSAGES
                || menu == MenuType.LEVEL_OBJECTIVES;
    }

    @Override
    public void dispose() {
        if (app != null) {
            app.dispose();
        }
        if (assets != null) {
            assets.dispose();
        }
        if (lawnGridDebug != null) {
            lawnGridDebug.dispose();
        }
        if (batch != null) {
            batch.dispose();
        }
    }
}
