package view.gdx.lawn;

import java.util.ArrayList;
import java.util.Map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;

import model.core.ReadOnlyGameState;
import model.data.content.chapter.ChapterType;
import model.data.Barrel.Barrel;
import model.data.plant.Plant;
import model.data.plant.PlantType;
import model.data.projectile.Projectile;
import model.data.zombie.Zombie;
import pvz.libpvz.pam.ClipRef;
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

    private final VisualCatalog catalog;
    private final LawnLayout layout;
    private final AnimStateStore animStates;
    private final VisibilityResolver visibilityResolver;
    private final SunRenderer sunRenderer;
    private final MowerRenderer mowerRenderer;
    private final GraveRenderer graveRenderer;
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
    }

    public void render(SpriteBatch batch, AssetContext assets, ReadOnlyGameState state, float deltaSeconds,
            ChapterType chapter) {
        if (state == null || assets.pamPlayer() == null) {
            return;
        }
        animStates.advanceAll(deltaSeconds);
        PamPlayer player = assets.pamPlayer();

        mowerRenderer.render(batch, assets, state, chapter);
        graveRenderer.render(batch, assets, state, chapter);

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
                drawZombie(batch, assets, player, zombie);
            }
        }
        for (Barrel barrel :new ArrayList<>(state.getBarrels())) {
            drawBarrel(batch, assets, player, barrel);
        }
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
        String defaultClip = zombie.isEating ? visual.eatClip : visual.walkClip;
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
                player.draw(batch, clip, anim.stateTime, x, y, true);
            } else {
                player.draw(batch, clip, anim.stateTime, x, y, true, visibility);
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
