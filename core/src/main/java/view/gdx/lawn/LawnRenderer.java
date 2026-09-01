package view.gdx.lawn;

import java.util.ArrayList;
import java.util.Map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;

import model.board.Tile;
import model.board.TileType;
import model.core.ReadOnlyGameState;
import model.data.content.chapter.ChapterType;
import model.data.Barrel.Barrel;
import model.data.plant.Plant;
import model.data.plant.PlantType;
import model.data.projectile.Projectile;
import model.data.vfx.LawnEffect;
import model.data.zombie.Zombie;
import pvz.libpvz.pam.ClipRef;
import pvz.libpvz.pam.PamClipTiming;
import pvz.libpvz.pam.PamPlayer;
import pvz.libpvz.pam.ProjectilePamAnchor;
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
    private static final String ICE_BLOCK_ZOMBIE_PAM =
            "768/FULL/EFFECTS/FROSTBITE_ICE_BLOCK_ZOMBIE/FROSTBITE_ICE_BLOCK_ZOMBIE.PAM";
    private static final String ICE_BLOCK_ZOMBIE_BEHIND_PAM =
            "768/FULL/EFFECTS/FROSTBITE_ICE_BLOCK_ZOMBIE_BEHIND/FROSTBITE_ICE_BLOCK_ZOMBIE_BEHIND.PAM";
    private static final String ICE_BLOCK_PLANT_PAM =
            "768/FULL/EFFECTS/FROSTBITE_ICE_BLOCK_PLANT/FROSTBITE_ICE_BLOCK_PLANT.PAM";
    private static final String ICE_BLOCK_PLANT_BEHIND_PAM =
            "768/FULL/EFFECTS/FROSTBITE_ICE_BLOCK_PLANT_BEHIND/FROSTBITE_ICE_BLOCK_PLANT_BEHIND.PAM";
    private static final String CHILL_PLANT_PAM =
            "768/FULL/EFFECTS/FROSTBITE_CHILL_PLANT/FROSTBITE_CHILL_PLANT.PAM";
    private static final String SANDSTORM_TOP_PAM =
            "768/INITIAL/EFFECTS/SANDSTORM_TOP/SANDSTORM_TOP.PAM";
    private static final String SANDSTORM_REAR_PAM =
            "768/INITIAL/EFFECTS/SANDSTORM_REAR/SANDSTORM_REAR.PAM";

    private final VisualCatalog catalog;
    private final LawnLayout layout;
    private final AnimStateStore animStates;
    private final VisibilityResolver visibilityResolver;
    private final SunRenderer sunRenderer;
    private final MowerRenderer mowerRenderer;
    private final GraveRenderer graveRenderer;
    private final ChapterTerrainRenderer chapterTerrainRenderer;
    private final Matrix4 savedTransform = new Matrix4();
    private final Matrix4 entityTransform = new Matrix4();
    private final float[] projectileAnchorDelta = new float[2];

    public LawnRenderer(VisualCatalog catalog, LawnLayout layout, AnimStateStore animStates,
            VisibilityResolver visibilityResolver) {
        this.catalog = catalog;
        this.layout = layout;
        this.animStates = animStates;
        this.visibilityResolver = visibilityResolver;
        this.sunRenderer = new SunRenderer(layout, animStates);
        this.mowerRenderer = new MowerRenderer(layout, animStates);
        this.graveRenderer = new GraveRenderer(layout, animStates);
        this.chapterTerrainRenderer = new ChapterTerrainRenderer(layout, animStates);
    }

    public void render(SpriteBatch batch, AssetContext assets, ReadOnlyGameState state, float deltaSeconds,
            ChapterType chapter) {
        if (state == null || assets.pamPlayer() == null) {
            return;
        }
        animStates.advanceAll(deltaSeconds);
        PamPlayer player = assets.pamPlayer();

        drawFireTiles(batch, assets, player, state);
        chapterTerrainRenderer.render(batch, assets, state, chapter);
        mowerRenderer.render(batch, assets, state, chapter);
        graveRenderer.render(batch, assets, state, chapter);

        if (RENDER_ZOMBIES) {
            for (Zombie zombie : new ArrayList<>(state.getZombies())) {
                if (zombie.type != null && zombie.type.isZomboss()) {
                    drawZombie(batch, assets, player, zombie);
                }
            }
        }
        for (Plant plant : new ArrayList<>(state.getPlants())) {
            if (plant.type != PlantType.Pumpkin) {
                drawPlant(batch, assets, player, plant);
            }
        }
        for (Plant plant : new ArrayList<>(state.getPlants())) {
            if (plant.type == PlantType.Pumpkin) {
                drawPlant(batch, assets, player, plant);
            }
        }
        for (Projectile p : new ArrayList<>(state.getProjectiles())) {
            drawProjectile(batch, assets, player, p);
        }
        if (RENDER_ZOMBIES) {
            for (Zombie zombie : new ArrayList<>(state.getZombies())) {
                if (zombie.type == null || !zombie.type.isZomboss()) {
                    drawZombie(batch, assets, player, zombie);
                }
            }
        }
        for (Barrel barrel : new ArrayList<>(state.getBarrels())) {
            drawBarrel(batch, assets, player, barrel);
        }
        drawLawnEffects(batch, assets, player, state);
        sunRenderer.render(batch, assets, state);
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
        boolean frozen = plant.isFrostbiteFreezeActive();
        int chillLevel = plant.getFrostbiteFreezeLevel();
        if (frozen) {
            drawOverlayPam(batch, assets, player, ICE_BLOCK_PLANT_BEHIND_PAM, "idle",
                    animKey(20, plant.instanceId), x, y, true);
        }
        drawPam(batch, player, clip, anim.stateTime, x, y, true, null);
        if (frozen) {
            drawOverlayPam(batch, assets, player, ICE_BLOCK_PLANT_PAM, "freeze_idle",
                    animKey(21, plant.instanceId), x, y, true);
        } else if (chillLevel == 1) {
            drawOverlayPam(batch, assets, player, CHILL_PLANT_PAM, "chill_stage1",
                    animKey(26, plant.instanceId), x, y, true);
        } else if (chillLevel >= 2) {
            drawOverlayPam(batch, assets, player, CHILL_PLANT_PAM, "chill_stage2",
                    animKey(26, plant.instanceId), x, y, true);
        }
    }

    private boolean isPlayingAction(Plant plant) {
        return plant.isAttacking();
    }

    private void drawZombie(SpriteBatch batch, AssetContext assets, PamPlayer player, Zombie zombie) {
        float x = layout.worldX(zombie.position);
        float y = layout.worldYForRow(zombie.row, zombie.position);
        if (zombie.rowSpan() > 1) {
            y = (layout.cellCenterY(zombie.row)
                    + layout.cellCenterY(zombie.row + zombie.rowSpan() - 1)) * 0.5f;
        }

        // Phase 1 iced zombies: ice block only (no zombie sprite inside).
        if (zombie.isIced() && zombie.isAlive) {
            drawOverlayPam(batch, assets, player, ICE_BLOCK_ZOMBIE_BEHIND_PAM, "idle",
                    animKey(22, zombie.instanceId), x, y, true);
            drawOverlayPam(batch, assets, player, ICE_BLOCK_ZOMBIE_PAM, "idle",
                    animKey(23, zombie.instanceId), x, y, true);
            return;
        }

        ZombieVisualDef visual = catalog.zombie(zombie.type);
        if (visual == null) {
            return;
        }
        String defaultClip = desiredZombieClip(zombie, visual);
        boolean loop = zombieClipLoops(zombie);
        ClipRef clip = assets.clip(visual.pamPath, defaultClip);
        if (clip == null && zombie.animClip != null && zombie.isAlive) {
            zombie.clearAnim();
            defaultClip = desiredZombieClip(zombie, visual);
            loop = true;
            clip = assets.clip(visual.pamPath, defaultClip);
        }
        if (clip == null) {
            if (!zombie.isAlive) {
                zombie.finishDeathAnim();
            }
            return;
        }
        EntityAnimState anim = animStates.getOrCreate(animKey(2, zombie.instanceId), defaultClip);
        if (!defaultClip.equals(anim.clipName)) {
            anim.clipName = defaultClip;
            anim.stateTime = 0f;
        }
        boolean sandstorm = zombie.hasSandstorm();
        if (sandstorm) {
            drawOverlayPam(batch, assets, player, SANDSTORM_REAR_PAM, "loop",
                    animKey(24, zombie.instanceId), x, y, true);
        }
        Map<String, Boolean> visibility = visibilityResolver.forZombie(zombie, visual);
        beginEntityScale(batch, x, y, zombie.isHypnotized);
        try {
            if (zombie.isAlive && !(visual.companions == null || visual.companions.isEmpty())) {
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
            if (zombie.hitFlashTicks > 0) {
                batch.setColor(1f, 0.45f, 0.45f, 1f);
            } else if (zombie.isGlowing) {
                float pulse = 0.7f + 0.3f * (float) Math.sin(anim.stateTime * 8f);
                batch.setColor(0.45f, 1f, 0.35f, pulse); // lime, not Color.GREEN
            }
            if (visibility.isEmpty()) {
                player.draw(batch, clip, anim.stateTime, x, y, loop);
            } else {
                player.draw(batch, clip, anim.stateTime, x, y, loop, visibility);
            }
            batch.setColor(Color.WHITE);
        } finally {
            endEntityScale(batch);
        }
        if (!loop && anim.stateTime >= PamClipTiming.durationSeconds(clip)) {
            if (!zombie.isAlive) {
                zombie.finishDeathAnim();
            } else if (Zombie.HIT_CLIP.equals(zombie.animClip)) {
                zombie.clearAnim();
            }
        }
        if (sandstorm) {
            drawOverlayPam(batch, assets, player, SANDSTORM_TOP_PAM, "loop",
                    animKey(25, zombie.instanceId), x, y, true);
        }
    }

    private static String desiredZombieClip(Zombie zombie, ZombieVisualDef visual) {
        String clip;
        if (!zombie.isAlive) {
            clip = visual.dieClip != null ? visual.dieClip : Zombie.DIE_CLIP;
        } else if (zombie.animClip != null) {
            clip = zombie.animClip;
        } else if (zombie.stunned) {
            clip = visual.idleClip;
        } else if (zombie.forceWalkAnim) {
            clip = visual.walkClip;
        } else if (zombie.type != null && zombie.type.isZomboss()) {
            clip = visual.idleClip;
        } else if (zombie.isEating) {
            clip = visual.eatClip;
        } else {
            clip = visual.walkClip;
        }
        if (clip == null) {
            clip = visual.idleClip;
        }
        return locomotionClip(zombie, clip);
    }

    private static boolean zombieClipLoops(Zombie zombie) {
        if (!zombie.isAlive) {
            return false;
        }
        if (zombie.animClip != null) {
            return zombie.animClipLoop;
        }
        return true;
    }

    private static String locomotionClip(Zombie zombie, String clip) {
        if (clip != null && clip.endsWith("_newspaper")
                && (zombie.armor == null || !zombie.armor.isIntact())) {
            String stripped = clip.substring(0, clip.length() - "_newspaper".length());
            return stripped.isEmpty() ? clip : stripped;
        }
        return clip;
    }

    private void drawOverlayPam(SpriteBatch batch, AssetContext assets, PamPlayer player,
            String pamPath, String clipName, long animKey, float x, float y, boolean loop) {
        ClipRef clip = assets.clip(pamPath, clipName);
        if (clip == null) {
            return;
        }
        EntityAnimState anim = animStates.getOrCreate(animKey, clipName);
        if (!clipName.equals(anim.clipName)) {
            anim.clipName = clipName;
            anim.stateTime = 0f;
        }
        drawPam(batch, player, clip, anim.stateTime, x, y, loop, null);
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

    private void drawProjectile(SpriteBatch batch, AssetContext assets, PamPlayer player, Projectile p) {
        ProjectileVisualDef visual = catalog.projectile(p.type);
        if (visual == null) {
            return;
        }
        String visualClip = visual.clipName;
        EntityAnimState anim = animStates.getOrCreate(
                animKey(5, System.identityHashCode(p)), visualClip);
        ClipRef clip = assets.clip(visual.pamPath, visualClip);
        if (clip == null) {
            return;
        }

        float x = layout.worldX(p.position);
        float y = layout.worldY(p.position);
        float drawX = x;
        float drawY = y;
        if (ProjectilePamAnchor.drawOriginDelta(clip, anim.stateTime, true, projectileAnchorDelta)) {
            drawX += projectileAnchorDelta[0];
            drawY += projectileAnchorDelta[1];
        }
        beginEntityScale(batch, x, y);
        try {
            player.draw(batch, clip, anim.stateTime, drawX, drawY, true);
        } finally {
            endEntityScale(batch);
        }
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
        beginEntityScale(batch, x, y, false);
    }

    private void beginEntityScale(SpriteBatch batch, float x, float y, boolean flipX) {
        float s = entityScale();
        float sx = flipX ? -s : s;
        savedTransform.set(batch.getTransformMatrix());
        entityTransform.set(savedTransform);
        entityTransform.translate(x, y, 0f).scale(sx, s, 1f).translate(-x, -y, 0f);
        batch.setTransformMatrix(entityTransform);
    }

    private void endEntityScale(SpriteBatch batch) {
        batch.setTransformMatrix(savedTransform);
    }

    private static long animKey(int kind, int instanceId) {
        return (((long) kind) << 32) | (instanceId & 0xffffffffL);
    }
}
