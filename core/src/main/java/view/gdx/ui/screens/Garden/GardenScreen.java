package view.gdx.ui.screens.Garden;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import controller.ControllerManager;
import model.core.Position;
import model.data.plant.PlantType;
import model.greenhouse.Greenhouse;
import model.greenhouse.Pot;
import model.storage.user.User;
import view.gdx.AssetContext;
import view.gdx.catalog.DefaultVisualCatalog;
import view.gdx.catalog.PlantVisualDef;
import view.gdx.catalog.VisualCatalog;
import view.gdx.ui.UiScreen;
import view.gdx.ui.UiSkin;
import view.gdx.ui.UiViewContext;
import view.gdx.ui.widgets.UiWidgets;

public class GardenScreen implements UiScreen {
    private final Stage stage;
    private ControllerManager controller;
    private UiViewContext lastContext;
    public  VisualCatalog catalog = new DefaultVisualCatalog();
    private final ImageButton store;
    private final Table root;

    private static final int SLOT_COL = 4;
    private static final int SLOT_ROW = 3;

    static final float[][] SLOT_X = {
            {626.0f,796.0f,966.0f,1136.0f},
            {626.0f,796.0f,966.0f,1136.0f},
            {626.0f,796.0f,966.0f,1136.0f},
    };
    static final float[][] SLOT_Y = {
            {298.0f ,298.0f ,298.0f, 298.0f },
            {445.0f ,445.0f ,445.0f,445.0f },
            {607.0f ,607.0f ,607.0f,607.0f  },


    };
    static final float[][] PLANT_X = {
            {626.0f,796.0f,966.0f,1136.0f},
            {626.0f,796.0f,966.0f,1136.0f},
            {626.0f,796.0f,966.0f,1136.0f},
    };
    static final float[][] PLANT_Y = {
            {280.0f ,280.0f ,280.0f, 280.0f },
            {430.0f ,430.0f ,430.0f,430.0f },
            {590.0f ,590.0f ,590.0f,590.0f },



    };

    private static final String SLOT_IMAGE =
            "IMAGE_ZEN_GARDEN_GROWING_PLANT_SLOT_GROWING_PLANT_SLOT_184X161_2";
    private static final String LOCKED_IMAGE =
            "IMAGE_ZEN_GARDEN_LOCKED_POT_ICON";
    private static final String EMPTY_POT =
            "IMAGE_ZEN_GARDEN_GROWING_PLANT_SLOT_GROWING_PLANT_SLOT_122X161";
    private static final String GROW_BUTTON =
            "IMAGE_UI_GENERIC_GEMSBUYBUTTON_DOWN";
    private static final String READY_CARD =
            "IMAGE_ZEN_GARDEN_BOOSTCARD_ANIM_BOOSTCARD_ANIM_244X161";
    private static final String SHOP_ICON =
            "IMAGE_UI_HUD_WORLDMAP_BUTTONS_HUD_STORE_NORMAL";
    private AssetContext assets;



    public GardenScreen() {
        stage = new Stage(new ScreenViewport());
        store =  new ImageButton(new ImageButton.ImageButtonStyle());
         root = new Table();
        root.setFillParent(true);
        root.add(store).size(150,150).expand().top().right().padBottom(80f);
        stage.addActor(root);

        UiWidgets.onChange(store, () -> UiWidgets.
                apply(controller, controller.getGreenhouseController().enterShop()));
    }

    public void buildPot(UiViewContext context) {
        User user = context.controller.getGreenhouseController().getUser();
        Greenhouse greenhouse = user.greenhouse;

        float imgW = 1750f, imgH = 774f;
        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();
        float scale = Math.max(screenW / imgW, screenH / imgH);
        float drawX = (screenW - imgW * scale) * 0.5f;
        float drawY = (screenH - imgH * scale) * 0.5f;

        float size = 90f * scale;
        for (int row = 0; row < SLOT_ROW; row++) {
            for (int col = 0; col < SLOT_COL; col++) {
                Pot pot = greenhouse.getPot(new Position(col + 1, row + 1));
                if (pot == null) continue;

                String imageId;
                if (pot.isLocked()) {
                    imageId = LOCKED_IMAGE;
                } else{
                    imageId = SLOT_IMAGE;
                }
                TextureRegion region = assets.region(imageId);
                if (region == null) continue;

                Image img = new Image(region);

                float imgX = SLOT_X[row][col];
                float imgY = SLOT_Y[row][col];

                float screenX = drawX + imgX * scale;
                float screenY = drawY + (imgH - imgY) * scale;
                img.setSize(size, size);
                img.setPosition(screenX - size * 0.5f, screenY - size * 0.5f);
                stage.addActor(img);
            }
        }
    }
    public void buildPlant(ControllerManager controller) {

        User user = controller.getGreenhouseController().getUser();
        if (user == null || user.greenhouse == null || assets == null) {
            return;
        }
        Greenhouse greenhouse = user.greenhouse;

        float imgW = 1750f;
        float imgH = 774f;
        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();
        float scale = Math.max(screenW / imgW, screenH / imgH);
        float drawX = (screenW - imgW * scale) * 0.5f;
        float drawY = (screenH - imgH * scale) * 0.5f;

        float size = 90f * scale;

        for (int row = 0; row < SLOT_ROW; row++) {
            for (int col = 0; col < SLOT_COL; col++) {
                Pot pot = greenhouse.getPot(new Position(col + 1, row + 1));
                if (pot == null || pot.isLocked()) {
                    continue;
                }

                float imgX = PLANT_X[row][col];
                float imgY = PLANT_Y[row][col];
                float screenX = drawX + imgX * scale;
                float screenY = drawY + (imgH - imgY) * scale;

                final Position pos = new Position(col + 1, row + 1);

                if (pot.isEmpty()) {
                    TextureRegion region = assets.region(EMPTY_POT);
                    if (region == null) continue;

                    ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
                    style.imageUp = new TextureRegionDrawable(region);
                    ImageButton btn = new ImageButton(style);
                    btn.setSize(size, size);
                    btn.setPosition(screenX - size * 0.5f, screenY - size * 0.5f);

                    UiWidgets.onChange(btn, () -> UiWidgets.apply(
                            controller,
                            controller.getGreenhouseController().plantPot(pos)));

                    stage.addActor(btn);

                } else if (!pot.isReady()) {
                    String pamPath;
                    String clipName;

                    if (pot.getPlantType() != null) {
                        PlantVisualDef visual = catalog.plant(pot.getPlantType());
                        if (visual == null) continue;
                        pamPath = visual.pamPath;
                        clipName = visual.idleClip;
                        if (clipName != null && clipName.endsWith("_stage")) {
                            clipName = clipName + "1";
                        }
                    } else {
                        pamPath = "768/INITIAL/PLANT/MARIGOLD/MARIGOLD.PAM";
                        clipName = "idle";
                    }

                    stage.addActor(new GardenPamActor(assets, pamPath, clipName, screenX, screenY,0.4f));
                    TextureRegion region = assets.region(GROW_BUTTON);
                    if (region == null) continue;
                    int gemCost = (int) pot.getRemainingHours();
                    String timeText = pot.getRemainingTimeText();
                    float btnW = 80f * scale;
                    float btnH = 40f * scale;
                    float groupH = btnH + 22f * scale;
                    Group group = new Group();
                    group.setSize(btnW, groupH);
                    group.setPosition(screenX - btnW * 0.5f, screenY - groupH * 0.5f - 80f);
                    Label time = new Label(timeText, UiSkin.get(), "default");
                    time.setFontScale(0.75f);
                    time.pack();
                    time.setPosition(
                            (btnW - time.getPrefWidth()) * 0.5f,
                            btnH + 2f * scale);
                    group.addActor(time);
                    ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
                    style.imageUp = new TextureRegionDrawable(region);
                    ImageButton btn = new ImageButton(style);
                    btn.setSize(btnW, btnH);
                    btn.setPosition(0, 0);
                    group.addActor(btn);
                    Label cost = new Label(String.valueOf(gemCost), UiSkin.get(), "default");
                    cost.setFontScale(0.85f);
                    cost.pack();
                    cost.setPosition(
                            (btnW - cost.getPrefWidth()) * 0.5f,
                            (btnH - cost.getPrefHeight()) * 0.5f);
                    group.addActor(cost);
                    UiWidgets.onChange(btn, () -> UiWidgets.apply(
                            controller,
                            controller.getGreenhouseController().grow(pos)));
                    stage.addActor(group);
                }else {
                    float cardW = 100f * scale;
                    float cardH = 70f * scale;
                    float plantSize = 70f * scale;

                    TextureRegion cardRegion = assets.region(READY_CARD);
                    if (cardRegion == null) continue;

                    Group group = new Group();
                    group.setSize(cardW, cardH);
                    group.setPosition(screenX - cardW * 0.5f, screenY - cardH * 0.5f);

                    Image card = new Image(cardRegion);
                    card.setSize(cardW, cardH);
                    card.setPosition(0, 0);
                    group.addActor(card);
                    String packetId = packetImageId(pot.getPlantType());
                    TextureRegion plantRegion = assets.region(packetId);
                    if (plantRegion != null) {
                        Image plant = new Image(plantRegion);
                        plant.setSize(plantSize, plantSize);
                        plant.setPosition(
                                (cardW - plantSize) * 0.5f,
                                (cardH - plantSize) * 0.5f);
                        group.addActor(plant);
                    }

                    ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
                    ImageButton btn = new ImageButton(style);
                    btn.setSize(cardW, cardH);
                    btn.setPosition(0, 0);
                    group.addActor(btn);

                    UiWidgets.onChange(btn, () -> UiWidgets.apply(
                            controller,
                            controller.getGreenhouseController().collect(pos)));

                    stage.addActor(group);
                }
            }
        }
    }
    @Override
    public void show(UiViewContext context) {
        this.controller = context.controller;
        this.assets = context.assets;
        this.lastContext = context;
        rebuild();
    }

    @Override
    public void act(float deltaSeconds) {
        if (store.getStyle().imageUp == null && assets != null) {
            var region = assets.region(SHOP_ICON);
            if (region != null) {
                ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(store.getStyle());
                style.imageUp = new TextureRegionDrawable(region);
                store.setStyle(style);
            }
        }
        stage.act(deltaSeconds);

    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        if (lastContext != null && assets != null) {
            rebuild();
        }
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    @Override
    public Stage stage() {
        return stage;
    }
    private static String packetImageId(PlantType type) {
        if (type == null) {
            return "IMAGE_UI_PACKETS_MARIGOLD";
        }
        String key = type.name().replace("_", "").toUpperCase();
        if (type == PlantType.MegaGatlingPea) key = "MEGAGATLING";
        return "IMAGE_UI_PACKETS_" + key;
    }
    private void rebuild() {
        stage.clear();
        stage.addActor(root);
        buildPot(lastContext);
        buildPlant(controller);
    }
}

