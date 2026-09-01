package view.gdx.ui.screens.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import view.gdx.AssetContext;

abstract class WorldMapPane extends WidgetGroup {
    static final String PATH_IMAGE = "IMAGE_WORLDMAP_MAP_PATH_MAP_PATH_135X16";
    static final String LEVEL_BADGE = "IMAGE_WORLDMAP_LEVEL_NODE_LEVEL_NODE_246X263";

    private static final float SCROLL_SPEED = 520f;
    private static final float REF_HEIGHT = 720f;

    protected final Group content = new Group();
    protected AssetContext assets;
    protected float refMapWidth = 2400f;
    protected float mapWidth = 2400f;
    protected float mapHeight = 720f;
    protected float panX;
    protected float bobTime;
    protected boolean built;
    protected boolean panEnabled = true;
    private float lastLayoutHeight = -1f;
    private Texture dimTexture;
    private Texture circleTexture;

    WorldMapPane() {
        setTouchable(Touchable.enabled);
        addActor(content);
        content.setSize(mapWidth, mapHeight);
        addListener(new DragListener() {
            @Override
            public void drag(InputEvent event, float x, float y, int pointer) {
                if (!panEnabled) {
                    return;
                }
                panX += getDeltaX();
                clampPan();
                applyPan();
            }
        });
    }

    protected abstract void rebuild();

    protected float viewHeight() {
        float height = getHeight();
        return height > 1f ? height : getPrefHeight();
    }

    protected void syncMapSize() {
        mapHeight = viewHeight();
        mapWidth = refMapWidth * (mapHeight / REF_HEIGHT);
        content.setSize(mapWidth, mapHeight);
    }

    protected float mapX(float normalizedX) {
        return normalizedX * mapWidth;
    }

    protected float mapY(float normalizedY) {
        return normalizedY * mapHeight;
    }

    protected float islandHeight(float depthScale) {
        return viewHeight() * 0.40f * depthScale;
    }

    protected float islandBoxWidth(float depthScale) {
        return islandHeight(depthScale) * 1.15f;
    }

    protected float badgeHeight(float depthScale) {
        return viewHeight() * 0.12f * depthScale;
    }

    protected float pathThickness(float depthScale) {
        return Math.max(4f, viewHeight() * 0.014f * depthScale);
    }

    protected void ensureBuilt() {
        if (assets == null) {
            return;
        }
        float height = viewHeight();
        if (built && Math.abs(height - lastLayoutHeight) <= 1f) {
            return;
        }
        syncMapSize();
        rebuild();
        built = true;
        lastLayoutHeight = height;
        panX = MathUtils.clamp(panX, Math.min(0f, getWidth() - mapWidth), 0f);
        applyPan();
    }

    protected Image addPath(float x1, float y1, float x2, float y2, float depthScale) {
        TextureRegion pathRegion = assets.region(PATH_IMAGE);
        if (pathRegion == null) {
            return null;
        }
        float dx = x2 - x1;
        float dy = y2 - y1;
        float length = (float) Math.sqrt(dx * dx + dy * dy);
        float angle = MathUtils.atan2(dy, dx) * MathUtils.radiansToDegrees;
        float thickness = pathThickness(depthScale);
        Image path = new Image(new TextureRegionDrawable(pathRegion));
        path.setSize(length, thickness);
        path.setOrigin(0f, thickness * 0.5f);
        path.setPosition(x1, y1 - thickness * 0.5f);
        path.setRotation(angle);
        path.setColor(1f, 1f, 1f, 0.85f);
        path.setTouchable(Touchable.disabled);
        content.addActor(path);
        return path;
    }

    protected Image addImageFitBox(String imageId, float x, float y, float boxW, float boxH, float anchorY) {
        TextureRegion region = assets.region(imageId);
        Image image = new Image();
        if (region != null && region.getRegionWidth() > 0 && region.getRegionHeight() > 0) {
            image.setDrawable(new TextureRegionDrawable(region));
            float scale = Math.min(boxW / region.getRegionWidth(), boxH / region.getRegionHeight());
            image.setSize(region.getRegionWidth() * scale, region.getRegionHeight() * scale);
        } else {
            image.setSize(boxW * 0.8f, boxH * 0.8f);
        }
        image.setPosition(x - image.getWidth() * 0.5f, y - image.getHeight() * anchorY);
        content.addActor(image);
        return image;
    }

    protected Group addIslandArt(WorldMapDefs.IslandArt art, float x, float y, float boxW, float boxH, float anchorY) {
        if (art == null || art.layers.length == 0) {
            return null;
        }
        if (!art.isLayered()) {
            return wrapSingleLayerIsland(art.layers[0], x, y, boxW, boxH, anchorY);
        }
        TextureRegion base = assets.region(art.layers[0]);
        if (base == null || base.getRegionWidth() <= 0 || base.getRegionHeight() <= 0) {
            return addIslandArt(WorldMapDefs.IslandArt.single(art.layers[0]), x, y, boxW, boxH, anchorY);
        }
        return addLayeredIslandArt(art, x, y, boxW, boxH, anchorY, base);
    }

    private Group wrapSingleLayerIsland(String imageId, float x, float y, float boxW, float boxH, float anchorY) {
        Image image = addImageFitBox(imageId, x, y, boxW, boxH, anchorY);
        Group wrap = new Group();
        wrap.setSize(image.getWidth(), image.getHeight());
        wrap.setPosition(image.getX(), image.getY());
        image.setPosition(0f, 0f);
        content.removeActor(image);
        wrap.addActor(image);
        content.addActor(wrap);
        return wrap;
    }

    private Group addLayeredIslandArt(WorldMapDefs.IslandArt art, float x, float y, float boxW, float boxH,
            float anchorY, TextureRegion base) {
        float scale = Math.min(boxW / base.getRegionWidth(), boxH / base.getRegionHeight());
        float baseW = base.getRegionWidth() * scale;
        float baseH = base.getRegionHeight() * scale;
        Group group = new Group();
        group.setSize(baseW, baseH * 1.35f);
        Image island = new Image(new TextureRegionDrawable(base));
        island.setSize(baseW, baseH);
        island.setPosition(0f, 0f);
        group.addActor(island);
        addIslandLayer(group, art, 1, scale, baseW, baseH, 0.42f);
        addIslandLayer(group, art, 2, scale, baseW, baseH, 0.72f);
        group.setPosition(x - group.getWidth() * 0.5f, y - baseH * anchorY);
        content.addActor(group);
        return group;
    }

    private void addIslandLayer(Group group, WorldMapDefs.IslandArt art, int layerIndex, float scale,
            float baseW, float baseH, float yFactor) {
        if (art.layers.length <= layerIndex) {
            return;
        }
        TextureRegion region = assets.region(art.layers[layerIndex]);
        if (region == null) {
            return;
        }
        float layerScale = scale * 0.92f;
        Image image = new Image(new TextureRegionDrawable(region));
        image.setSize(region.getRegionWidth() * layerScale, region.getRegionHeight() * layerScale);
        image.setPosition((baseW - image.getWidth()) * 0.5f, baseH * yFactor);
        group.addActor(image);
    }

    protected Image addDimOverlay(float width, float height) {
        return addSolidOverlay(width, height, new Color(0f, 0f, 0f, 0.55f));
    }

    protected Image addSolidOverlay(float width, float height, Color color) {
        if (dimTexture == null) {
            Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pixmap.setColor(Color.WHITE);
            pixmap.fill();
            dimTexture = new Texture(pixmap);
            pixmap.dispose();
        }
        Image overlay = new Image(new TextureRegionDrawable(new TextureRegion(dimTexture)));
        overlay.setSize(width, height);
        overlay.setColor(color);
        overlay.setTouchable(Touchable.disabled);
        return overlay;
    }

    protected void applyIslandLock(Group island, Image lockIcon, boolean unlocked) {
        if (island == null) {
            return;
        }
        island.setColor(Color.WHITE);
        float alpha = unlocked ? 1f : 0.38f;
        for (com.badlogic.gdx.scenes.scene2d.Actor child : island.getChildren()) {
            if (child != lockIcon) {
                child.setColor(child.getColor().r, child.getColor().g, child.getColor().b, alpha);
            }
        }
        if (lockIcon != null) {
            lockIcon.setVisible(!unlocked);
            lockIcon.toFront();
        }
    }

    protected Image createLockIconForIsland(Group island) {
        if (island == null) {
            return null;
        }
        TextureRegion lockRegion = assets.region(WorldMapDefs.LOCK_ICON);
        Image lockImg = new Image();
        if (lockRegion != null && lockRegion.getRegionHeight() > 0) {
            lockImg.setDrawable(new TextureRegionDrawable(lockRegion));
            float lockH = Math.max(32f, island.getHeight() * 0.22f);
            float lockScale = lockH / lockRegion.getRegionHeight();
            lockImg.setSize(lockRegion.getRegionWidth() * lockScale, lockH);
        } else {
            float lockH = Math.max(32f, island.getHeight() * 0.22f);
            lockImg.setSize(lockH, lockH);
        }
        lockImg.setPosition((island.getWidth() - lockImg.getWidth()) * 0.5f,
                (island.getHeight() - lockImg.getHeight()) * 0.5f);
        lockImg.setTouchable(Touchable.disabled);
        lockImg.setVisible(false);
        island.addActor(lockImg);
        return lockImg;
    }

    protected void tickPan(float delta) {
        if (!panEnabled) {
            return;
        }
        float scroll = 0f;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            scroll += SCROLL_SPEED * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            scroll -= SCROLL_SPEED * delta;
        }
        if (scroll != 0f) {
            panX += scroll;
            clampPan();
            applyPan();
        }
    }

    protected void clampPan() {
        float minPan = Math.min(0f, getWidth() - mapWidth);
        panX = MathUtils.clamp(panX, minPan, 0f);
    }

    protected void applyPan() {
        if (!panEnabled) {
            content.setPosition((getWidth() - mapWidth) * 0.5f, (getHeight() - mapHeight) * 0.5f);
            return;
        }
        content.setPosition(panX, (getHeight() - mapHeight) * 0.5f);
    }

    @Override
    protected void sizeChanged() {
        if (assets != null) {
            built = false;
            ensureBuilt();
        } else {
            clampPan();
            applyPan();
        }
    }

    @Override
    public float getPrefWidth() {
        return 1280f;
    }

    @Override
    public float getPrefHeight() {
        return 560f;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.flush();
        if (clipBegin()) {
            super.draw(batch, parentAlpha);
            batch.flush();
            clipEnd();
        }
    }

    protected Image addCircle(float diameter, Color color) {
        if (circleTexture == null) {
            int size = 64;
            Pixmap pm = new Pixmap(size, size, Pixmap.Format.RGBA8888);
            pm.setColor(0f, 0f, 0f, 0f);
            pm.fill();
            pm.setColor(Color.WHITE);
            pm.fillCircle(size / 2, size / 2, size / 2 - 1);
            circleTexture = new Texture(pm);
            circleTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            pm.dispose();
        }
        Image circle = new Image(new TextureRegionDrawable(new TextureRegion(circleTexture)));
        circle.setSize(diameter, diameter);
        circle.setColor(color);
        circle.setTouchable(Touchable.disabled);
        return circle;
    }

    @Override
    public void clear() {
        super.clear();
        if (dimTexture != null) {
            dimTexture.dispose();
            dimTexture = null;
        }
        if (circleTexture != null) {
            circleTexture.dispose();
            circleTexture = null;
        }
    }
}
