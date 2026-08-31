package view.gdx.lawn;

import java.util.Map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;

import model.board.Tile;
import model.board.TileType;
import model.core.ReadOnlyGameState;
import model.data.Barrel.Barrel;
import model.data.plant.Plant;
import model.data.vfx.LawnEffect;
import model.data.zombie.Zombie;
import pvz.libpvz.pam.ClipRef;
import pvz.libpvz.pam.PamPlayer;
import view.gdx.AssetContext;
import view.gdx.VisibilityResolver;
import view.gdx.anim.AnimStateStore;
import view.gdx.anim.EntityAnimState;
import view.gdx.catalog.*;

public final class LawnRenderer {
    private static final boolean RENDER_ZOMBIES = true;
    private static final float PAM_CANVAS = 390f;
    private static final float ENTITY_HEIGHT_IN_CELLS = 2f;
    private static final String FIRE_TILE_PAM = "768/FULL/BACKGROUNDS/FIRETILE/FIRETILE.PAM";

    private final VisualCatalog catalog;
    private final LawnLayout layout;
    private final AnimStateStore animStates;
    private final VisibilityResolver visibilityResolver;
    private final Matrix4 savedTransform = new Matrix4();
    private final Matrix4 entityTransform = new Matrix4();

    public LawnRenderer(VisualCatalog catalog, LawnLayout layout, AnimStateStore animStates,
            VisibilityResolver visibilityResolver) {
        this.catalog = catalog;
        this.layout = layout;
        this.animStates = animStates;
        this.visibilityResolver = visibilityResolver;
    }

    public void render(SpriteBatch batch, AssetContext assets, ReadOnlyGameState state, float deltaSeconds) {
        if (state == null || assets.pamPlayer() == null) {
            return;
        }
        animStates.advanceAll(deltaSeconds);
        PamPlayer player = assets.pamPlayer();

        drawFireTiles(batch, assets, player, state);

        if (RENDER_ZOMBIES) {
            for (Zombie zombie : state.getZombies()) {
                if (zombie.type != null && zombie.type.isZomboss()) {
                    drawZombie(batch, assets, player, zombie);
                }
            }
        }
        for (Plant plant : state.getPlants()) {
            drawPlant(batch, assets, player, plant);
        }
        if (RENDER_ZOMBIES) {
            for (Zombie zombie : state.getZombies()) {
                if (zombie.type == null || !zombie.type.isZomboss()) {
                    drawZombie(batch, assets, player, zombie);
                }
            }
        }
        for (Barrel barrel : state.getBarrels()) {
            drawBarrel(batch, assets, player, barrel);
        }
        drawLawnEffects(batch, assets, player, state);
    }

    private float entityScale() {
        return (layout.cellHeight() * ENTITY_HEIGHT_IN_CELLS) / PAM_CANVAS;
    }

    private void drawPlant(SpriteBatch batch, AssetContext assets, PamPlayer player, Plant plant) {
        PlantVisualDef visual = catalog.plant(plant.type);
        if (visual == null) {
            return;
        }
        String desiredClip = visual.idleClip;
        if (plant.isPlantFoodActive && visual.plantFoodClip != null) {
            desiredClip = visual.plantFoodClip;
        } else if (isPlayingAction(plant) && visual.attackClip != null) {
            desiredClip = visual.attackClip;
        }
        if (desiredClip != null && desiredClip.endsWith("_stage")) {
            desiredClip = desiredClip + plant.getGrowthStage();
        }

        EntityAnimState anim = animStates.getOrCreate(animKey(1, plant.instanceId), visual.idleClip);
        if (!desiredClip.equals(anim.clipName)) {
            anim.clipName = desiredClip;
            anim.stateTime = 0f;
        }
        ClipRef clip = assets.clip(visual.pamPath, anim.clipName);
        if (clip == null) {
            return;
        }
        float x = layout.cellCenterX(plant.col);
        float y = layout.cellCenterY(plant.row);
        drawPam(batch, player, clip, anim.stateTime, x, y, true, null);
    }

    private boolean isPlayingAction(Plant plant) {
        return plant.isAttacking();
    }

    private void drawZombie(SpriteBatch batch, AssetContext assets, PamPlayer player, Zombie zombie) {
        ZombieVisualDef visual = catalog.zombie(zombie.type);
        if (visual == null) {
            return;
        }
        String defaultClip;
        boolean loop = true;
        if (zombie.animClip != null) {
            defaultClip = zombie.animClip;
            loop = zombie.animClipLoop;
        } else if (zombie.stunned) {
            defaultClip = visual.idleClip;
        } else if (zombie.forceWalkAnim) {
            defaultClip = visual.walkClip;
        } else if (zombie.type != null && zombie.type.isZomboss()) {
            defaultClip = visual.idleClip;
        } else if (zombie.isEating) {
            defaultClip = visual.eatClip;
        } else {
            defaultClip = visual.walkClip;
        }
        if (defaultClip == null) {
            defaultClip = visual.idleClip;
        }
        EntityAnimState anim = animStates.getOrCreate(animKey(2, zombie.instanceId), defaultClip);
        if (!defaultClip.equals(anim.clipName)) {
            anim.clipName = defaultClip;
            anim.stateTime = 0f;
        }
        ClipRef clip = assets.clip(visual.pamPath, anim.clipName);
        if (clip == null) {
            return;
        }
        float x = layout.worldX(zombie.position);
        float y = layout.worldYForRow(zombie.row, zombie.position);
        if (zombie.rowSpan() > 1) {
            y = (layout.cellCenterY(zombie.row)
                    + layout.cellCenterY(zombie.row + zombie.rowSpan() - 1)) * 0.5f;
        }
        Map<String, Boolean> visibility = visibilityResolver.forZombie(zombie, visual);
        beginEntityScale(batch, x, y);
        try {
            if (!(visual.companions == null || visual.companions.isEmpty())) {
                for (int i = 0; i < visual.companions.size(); i++) {
                    CompanionVisual c = visual.companions.get(i);
                    if (c.onlyWhileArmored && (zombie.armor == null || !zombie.armor.isIntact)) {
                        continue;
                    }
                    ClipRef prop = assets.clip(c.pamPath, c.clipName);
                    if (prop == null) {
                        continue;
                    }
                    long key = animKey(4 + i, zombie.instanceId);
                    EntityAnimState propAnim = animStates.getOrCreate(key, c.clipName);
                    player.draw(batch, prop, propAnim.stateTime, x + c.offsetX, y + c.offsetY, true);
                }
            }
            if (visibility.isEmpty()) {
                player.draw(batch, clip, anim.stateTime, x, y, loop);
            } else {
                player.draw(batch, clip, anim.stateTime, x, y, loop, visibility);
            }
        } finally {
            endEntityScale(batch);
        }
    }

    private void drawBarrel(SpriteBatch batch, AssetContext assets, PamPlayer player, Barrel barrel) {
        if (!barrel.ownerDead()) {
            return;
        }
        BarrelVisualDef visual = catalog.barrel();
        if (visual == null) {
            return;
        }
        String clipName = visual.rollClip;
        EntityAnimState animState = animStates.getOrCreate(animKey(3, System.identityHashCode(barrel)), clipName);
        ClipRef clip = assets.clip(visual.pamPath, clipName);
        if (clip == null) {
            return;
        }
        float x = layout.cellCenterX(barrel.col);
        float y = layout.cellCenterY(barrel.row);
        drawPam(batch, player, clip, animState.stateTime, x, y, true, null);
    }

    private void drawFireTiles(SpriteBatch batch, AssetContext assets, PamPlayer player,
            ReadOnlyGameState state) {
        ClipRef fireClip = assets.clip(FIRE_TILE_PAM, "firetile_up");
        TextureRegion fill = assets.region("IMAGE_UI_HUD_INGAME_PROGRESS_METER_FILL");
        for (int r = 0; r < ReadOnlyGameState.GRID_ROWS; r++) {
            for (int c = 0; c < ReadOnlyGameState.GRID_COLS; c++) {
                Tile tile = state.getBoard().getTile(r, c);
                if (tile == null || tile.getType() != TileType.FIRE) {
                    continue;
                }
                float x = layout.cellCenterX(c);
                float y = layout.cellCenterY(r);
                if (fireClip != null) {
                    EntityAnimState anim = animStates.getOrCreate(animKey(8, r * 16 + c), "firetile_up");
                    drawPam(batch, player, fireClip, anim.stateTime, x, y, true, null);
                } else if (fill != null) {
                    batch.setColor(1f, 0.35f, 0.05f, 0.45f);
                    batch.draw(fill, layout.cellLeft(c), layout.cellBottom(r),
                            layout.cellWidth(), layout.cellHeight());
                    batch.setColor(Color.WHITE);
                }
            }
        }
    }

    private void drawLawnEffects(SpriteBatch batch, AssetContext assets, PamPlayer player,
            ReadOnlyGameState state) {
        for (LawnEffect effect : state.getLawnEffects()) {
            ClipRef clip = assets.clip(effect.pamPath, effect.clipName);
            if (clip == null) {
                continue;
            }
            EntityAnimState anim = animStates.getOrCreate(animKey(9, effect.id), effect.clipName);
            if (!effect.clipName.equals(anim.clipName)) {
                anim.clipName = effect.clipName;
                anim.stateTime = 0f;
            }
            float x = layout.cellCenterX(effect.col);
            float y = layout.cellCenterY(effect.row);
            drawPam(batch, player, clip, anim.stateTime, x, y, effect.loop, null);
        }
    }

    private void drawPam(SpriteBatch batch, PamPlayer player, ClipRef clip, float time,
            float x, float y, boolean loop, Map<String, Boolean> visibility) {
        beginEntityScale(batch, x, y);
        try {
            if (visibility == null) {
                player.draw(batch, clip, time, x, y, loop);
            } else {
                player.draw(batch, clip, time, x, y, loop, visibility);
            }
        } finally {
            endEntityScale(batch);
        }
    }

    private void beginEntityScale(SpriteBatch batch, float x, float y) {
        float s = entityScale();
        savedTransform.set(batch.getTransformMatrix());
        entityTransform.set(savedTransform);
        entityTransform.translate(x, y, 0f).scale(s, s, 1f).translate(-x, -y, 0f);
        batch.setTransformMatrix(entityTransform);
    }

    private void endEntityScale(SpriteBatch batch) {
        batch.setTransformMatrix(savedTransform);
    }

    private static long animKey(int kind, int instanceId) {
        return (((long) kind) << 32) | (instanceId & 0xffffffffL);
    }
}
