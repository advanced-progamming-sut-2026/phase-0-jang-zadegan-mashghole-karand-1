package view.gdx.ui.screens.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import controller.ChapterCommands;
import controller.ControllerManager;
import model.data.content.chapter.ChapterType;
import model.service.GameNavigationState;
import view.gdx.AssetContext;
import view.gdx.ui.widgets.UiWidgets;

final class ChapterWorldMap extends WorldMapPane {
    private static final ChapterType[] CHAPTERS = ChapterType.values();
    private static final float SIDE_SCALE = 0.58f;
    private static final float CENTER_SCALE = 1f;
    private static final float KEY_REPEAT = 0.22f;

    private final IslandSlot[] slots = new IslandSlot[CHAPTERS.length];
    private final Image[] dots = new Image[CHAPTERS.length];

    private ControllerManager controller;
    private GameNavigationState navigation;
    private int selectedIndex;
    private float keyCooldown;

    ChapterWorldMap() {
        panEnabled = false;
        refMapWidth = 1600f;
        for (int i = 0; i < CHAPTERS.length; i++) {
            slots[i] = new IslandSlot(CHAPTERS[i]);
        }
    }

    void bind(ControllerManager controller, AssetContext assets, GameNavigationState navigation) {
        this.controller = controller;
        this.assets = assets;
        this.navigation = navigation;
        if (navigation != null) {
            for (int i = 0; i < CHAPTERS.length; i++) {
                if (navigation.unlockedChapters.contains(CHAPTERS[i])) {
                    selectedIndex = i;
                    break;
                }
            }
        }
        built = false;
        ensureBuilt();
        layoutSelection();
    }

    @Override
    protected void rebuild() {
        content.clearChildren();
        float midY = mapHeight * 0.46f;
        for (int i = 0; i < slots.length; i++) {
            IslandSlot slot = slots[i];
            float boxH = viewHeight() * 0.62f;
            float boxW = boxH * 0.75f;
            Group island = addIslandArt(
                    WorldMapDefs.IslandArt.single(WorldMapDefs.universeIcon(slot.chapter)),
                    mapWidth * 0.5f, midY, boxW, boxH, 0.4f);
            Label name = UiWidgets.body(ChapterCommands.displayName(slot.chapter));
            name.setAlignment(Align.center);
            float labelWidth = Math.max(180f, viewHeight() * 0.34f);
            name.setWidth(labelWidth);

            Image lock = createLockIconForIsland(island);

            final int index = i;
            ClickListener click = new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (index == selectedIndex) {
                        tryEnter(slot.chapter);
                    } else {
                        selectedIndex = index;
                        layoutSelection();
                    }
                }
            };
            if (island != null) {
                island.addListener(click);
            }

            slot.root = island;
            slot.name = name;
            slot.lock = lock;
            content.addActor(name);
        }
        buildDots();
        layoutSelection();
    }

    private void buildDots() {
        float dotSize = Math.max(5f, viewHeight() * 0.008f);
        float gap = dotSize * 2.6f;
        float total = dots.length * gap;
        float startX = (mapWidth - total) * 0.5f + gap * 0.5f;
        float y = mapHeight * 0.08f;
        for (int i = 0; i < dots.length; i++) {
            Image dot = addCircle(dotSize, new Color(1f, 1f, 1f, 0.35f));
            dot.setTouchable(Touchable.enabled);
            final int index = i;
            dot.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    selectedIndex = index;
                    layoutSelection();
                }
            });
            dot.setPosition(startX + i * gap - dotSize * 0.5f, y);
            dots[i] = dot;
            content.addActor(dot);
        }
    }

    private void layoutSelection() {
        float midX = mapWidth * 0.5f;
        float midY = mapHeight * 0.46f;
        float sideGap = mapWidth * 0.28f;
        for (int i = 0; i < slots.length; i++) {
            IslandSlot slot = slots[i];
            if (slot.root == null) {
                continue;
            }
            int offset = i - selectedIndex;
            boolean center = offset == 0;
            boolean visible = Math.abs(offset) <= 1;
            slot.root.setVisible(visible);
            slot.name.setVisible(visible);
            if (!visible) {
                continue;
            }
            float scale = center ? CENTER_SCALE : SIDE_SCALE;
            float targetH = viewHeight() * 0.62f * scale;
            float targetW = targetH * 0.75f;
            resizeIsland(slot, targetW, targetH);
            float x = midX + offset * sideGap;
            slot.root.setPosition(x - slot.root.getWidth() * 0.5f, midY - slot.root.getHeight() * 0.4f);
            slot.name.setPosition(x - slot.name.getWidth() * 0.5f, slot.root.getY() - viewHeight() * 0.045f);
            slot.baseY = slot.root.getY();
            applyLockVisual(slot);
            if (center) {
                slot.root.toFront();
                slot.name.toFront();
            }
        }
        for (int i = 0; i < dots.length; i++) {
            if (dots[i] == null) {
                continue;
            }
            boolean selected = i == selectedIndex;
            dots[i].setColor(selected ? new Color(1f, 1f, 1f, 1f) : new Color(1f, 1f, 1f, 0.35f));
            dots[i].toFront();
        }
    }

    private void resizeIsland(IslandSlot slot, float boxW, float boxH) {
        Group root = slot.root;
        if (root.getChildren().size == 0) {
            return;
        }
        Actor first = root.getChild(0);
        if (!(first instanceof Image island)) {
            return;
        }
        TextureRegion region = assets.region(WorldMapDefs.universeIcon(slot.chapter));
        if (region == null || region.getRegionWidth() <= 0 || region.getRegionHeight() <= 0) {
            return;
        }
        float scale = Math.min(boxW / region.getRegionWidth(), boxH / region.getRegionHeight());
        float w = region.getRegionWidth() * scale;
        float h = region.getRegionHeight() * scale;
        island.setSize(w, h);
        island.setPosition(0f, 0f);
        root.setSize(w, h);
        if (slot.lock != null) {
            float lockSize = Math.max(36f, h * 0.18f);
            TextureRegion lockRegion = assets.region(WorldMapDefs.LOCK_ICON);
            if (lockRegion != null && lockRegion.getRegionHeight() > 0) {
                float lockScale = lockSize / lockRegion.getRegionHeight();
                slot.lock.setSize(lockRegion.getRegionWidth() * lockScale, lockSize);
            } else {
                slot.lock.setSize(lockSize, lockSize);
            }
            slot.lock.setPosition((w - slot.lock.getWidth()) * 0.5f, (h - slot.lock.getHeight()) * 0.5f);
        }
    }

    private void applyLockVisual(IslandSlot slot) {
        boolean unlocked = navigation != null && navigation.unlockedChapters.contains(slot.chapter);
        applyIslandLock(slot.root, slot.lock, unlocked);
        slot.name.setColor(unlocked ? Color.WHITE : new Color(0.75f, 0.75f, 0.8f, 1f));
    }

    private void tryEnter(ChapterType chapter) {
        if (controller == null || navigation == null) {
            return;
        }
        if (!navigation.unlockedChapters.contains(chapter)) {
            return;
        }
        UiWidgets.apply(controller,
                controller.getGameMenuController().enterChapter(ChapterCommands.commandName(chapter)));
    }

    private void moveSelection(int delta) {
        int next = MathUtils.clamp(selectedIndex + delta, 0, slots.length - 1);
        if (next != selectedIndex) {
            selectedIndex = next;
            layoutSelection();
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        ensureBuilt();
        keyCooldown = Math.max(0f, keyCooldown - delta);
        if (keyCooldown <= 0f) {
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
                moveSelection(-1);
                keyCooldown = KEY_REPEAT;
            } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
                moveSelection(1);
                keyCooldown = KEY_REPEAT;
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)
                    || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                tryEnter(slots[selectedIndex].chapter);
            }
        }
        bobTime += delta;
        IslandSlot center = slots[selectedIndex];
        if (center.root != null && center.root.isVisible()) {
            float bob = MathUtils.sin(bobTime * 1.3f) * (viewHeight() * 0.008f);
            center.root.setY(center.baseY + bob);
        }
    }

    private static final class IslandSlot {
        final ChapterType chapter;
        Group root;
        Label name;
        Image lock;
        float baseY;

        IslandSlot(ChapterType chapter) {
            this.chapter = chapter;
        }
    }
}
