package view.gdx.ui.screens.map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import controller.ControllerManager;
import model.data.content.chapter.ChapterCatalog;
import model.data.content.chapter.ChapterType;
import model.service.GameNavigationState;
import view.gdx.AssetContext;
import view.gdx.ui.widgets.UiWidgets;

final class LevelWorldMap extends WorldMapPane {
    private static final float[] NX = {0.08f, 0.26f, 0.44f, 0.62f, 0.82f};
    private static final float[] NY = {0.35f, 0.50f, 0.32f, 0.48f, 0.34f};
    private static final float[] DEPTH = {0.80f, 0.76f, 0.78f, 0.76f, 0.82f};

    private final LevelSlot[] slots = new LevelSlot[ChapterCatalog.LEVELS_PER_CHAPTER];

    private ControllerManager controller;
    private GameNavigationState navigation;
    private ChapterType chapter;

    LevelWorldMap() {
        refMapWidth = 2800f;
        for (int i = 0; i < slots.length; i++) {
            slots[i] = new LevelSlot(i + 1, NX[i], NY[i], DEPTH[i]);
        }
    }

    void bind(ControllerManager controller, AssetContext assets, GameNavigationState navigation) {
        this.controller = controller;
        this.assets = assets;
        this.navigation = navigation;
        this.chapter = navigation == null ? null : navigation.selectedChapter;
        built = false;
        ensureBuilt();
        refreshLocks();
    }

    @Override
    protected void rebuild() {
        content.clearChildren();
        if (chapter == null) {
            return;
        }
        WorldMapDefs.IslandArt[] islands = WorldMapDefs.levelIslands(chapter);
        String zomboss = WorldMapDefs.zombossIsland(chapter);
        for (int i = 0; i < slots.length; i++) {
            LevelSlot slot = slots[i];
            if (i < islands.length) {
                slot.art = islands[i];
                slot.boss = false;
            } else {
                slot.art = WorldMapDefs.IslandArt.single(zomboss);
                slot.boss = true;
            }
        }
        for (int i = 0; i < slots.length - 1; i++) {
            LevelSlot from = slots[i];
            LevelSlot to = slots[i + 1];
            addPath(mapX(from.nx), mapY(from.ny), mapX(to.nx), mapY(to.ny), (from.depth + to.depth) * 0.5f);
        }
        for (LevelSlot slot : slots) {
            addLevel(slot);
        }
        refreshLocks();
    }

    private void addLevel(LevelSlot slot) {
        float x = mapX(slot.nx);
        float y = mapY(slot.ny);
        float depth = slot.boss ? slot.depth * 1.05f : slot.depth;
        float boxH = islandHeight(depth);
        float boxW = islandBoxWidth(depth);
        Group island = addIslandArt(slot.art, x, y, boxW, boxH, slot.boss ? 0.45f : 0.4f);
        if (island == null) {
            return;
        }
        Image badge = addImageFitBox(LEVEL_BADGE, x, island.getY() + island.getHeight() * 0.78f,
                badgeHeight(slot.depth) * 1.1f, badgeHeight(slot.depth), 0.5f);
        Label number = UiWidgets.title(String.valueOf(slot.levelNumber));
        number.setAlignment(Align.center);
        float numberSize = Math.max(28f, viewHeight() * 0.055f);
        number.setSize(numberSize * 1.6f, numberSize);
        number.setPosition(x - number.getWidth() * 0.5f, badge.getY() + badge.getHeight() * 0.28f);

        ClickListener click = new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (controller == null || navigation == null || chapter == null) {
                    return;
                }
                if (!navigation.isLevelUnlocked(chapter, slot.levelNumber)) {
                    return;
                }
                UiWidgets.apply(controller, controller.getGameMenuController().selectLevel(slot.levelNumber));
            }
        };
        island.addListener(click);
        badge.addListener(click);

        int highScore = navigation == null ? 0 : navigation.getLevelHighScore(chapter, slot.levelNumber);
        Label score = null;
        if (highScore > 0) {
            score = UiWidgets.body("high " + highScore);
            score.setAlignment(Align.center);
            float scoreWidth = Math.max(120f, viewHeight() * 0.22f);
            score.setWidth(scoreWidth);
            score.setPosition(x - scoreWidth * 0.5f, island.getY() - viewHeight() * 0.03f);
            content.addActor(score);
        }

        slot.root = island;
        slot.lock = createLockIconForIsland(island);
        slot.badge = badge;
        slot.number = number;
        slot.score = score;
        slot.baseY = island.getY();
        slot.badgeBaseY = badge.getY();
        slot.numberBaseY = number.getY();
        content.addActor(number);
    }

    private void refreshLocks() {
        if (navigation == null || chapter == null) {
            return;
        }
        for (LevelSlot slot : slots) {
            if (slot.root == null) {
                continue;
            }
            boolean unlocked = navigation.isLevelUnlocked(chapter, slot.levelNumber);
            applyIslandLock(slot.root, slot.lock, unlocked);
            Touchable touch = unlocked ? Touchable.enabled : Touchable.disabled;
            slot.root.setTouchable(touch);
            if (slot.badge != null) {
                slot.badge.setTouchable(touch);
                slot.badge.setColor(unlocked ? Color.WHITE : new Color(1f, 1f, 1f, 0.38f));
            }
            if (slot.number != null) {
                slot.number.setColor(unlocked ? Color.WHITE : new Color(0.75f, 0.75f, 0.8f, 1f));
            }
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        ensureBuilt();
        tickPan(delta);
        bobTime += delta;
        for (int i = 0; i < slots.length; i++) {
            LevelSlot slot = slots[i];
            if (slot.root == null) {
                continue;
            }
            float bob = MathUtils.sin(bobTime * 1.5f + i * 0.9f) * (viewHeight() * 0.008f * slot.depth);
            slot.root.setY(slot.baseY + bob);
            if (slot.badge != null) {
                slot.badge.setY(slot.badgeBaseY + bob);
            }
            if (slot.number != null) {
                slot.number.setY(slot.numberBaseY + bob);
            }
            if (slot.score != null) {
                slot.score.setY(slot.root.getY() - viewHeight() * 0.03f);
            }
        }
    }

    private static final class LevelSlot {
        final int levelNumber;
        final float nx;
        final float ny;
        final float depth;
        WorldMapDefs.IslandArt art;
        boolean boss;
        Group root;
        Image lock;
        Image badge;
        Label number;
        Label score;
        float baseY;
        float badgeBaseY;
        float numberBaseY;

        LevelSlot(int levelNumber, float nx, float ny, float depth) {
            this.levelNumber = levelNumber;
            this.nx = nx;
            this.ny = ny;
            this.depth = depth;
        }
    }
}
