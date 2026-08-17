package view.gdx.lawn;

import java.util.Map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import model.core.ReadOnlyGameState;
import model.data.Barrel.Barrel;
import model.data.plant.Plant;
import model.data.plant.abilities.config.PlantAbilityConfig;
import model.data.plant.abilities.runtime.PlantSunProduceAbility;
import model.data.zombie.Zombie;
import pvz.libpvz.pam.ClipRef;
import pvz.libpvz.pam.PamPlayer;
import view.gdx.AssetContext;
import view.gdx.VisibilityResolver;
import view.gdx.anim.AnimStateStore;
import view.gdx.anim.EntityAnimState;
import view.gdx.catalog.*;

public final class LawnRenderer {
    private final VisualCatalog catalog;
    private final LawnLayout layout;
    private final AnimStateStore animStates;
    private final VisibilityResolver visibilityResolver;

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

        for (Plant plant : state.getPlants()) {
            drawPlant(batch, assets, player, plant);
        }
        for (Zombie zombie : state.getZombies()) {
            drawZombie(batch, assets, player, zombie);
        }
        for(Barrel barrel : state.getBarrels()){
            drawBarrel(batch, assets, player, barrel);
        }
    }

    private void drawPlant(SpriteBatch batch, AssetContext assets, PamPlayer player, Plant plant) {
        PlantVisualDef visual = catalog.plant(plant.type);
        if (visual == null) {
            return;
        }
        String desiredClip = visual.idleClip;
        if (plant.isPlantFoodActive && visual.plantFoodClip!=null) {
            desiredClip = visual.plantFoodClip;
        }else if (isPlayingAction(plant)  && visual.attackClip != null) {
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
        player.draw(batch, clip, anim.stateTime, x, y, true);
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
        if(defaultClip == null) {
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
        if(!(visual.companions == null || visual.companions.isEmpty())) {
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
    }

    private void drawBarrel(SpriteBatch batch, AssetContext assets,PamPlayer player ,Barrel barrel) {
        if(!barrel.ownerDead()){
            return;
        }
        BarrelVisualDef visual = catalog.barrel();
        if (visual == null) {
            return;
        }
        String clipName = visual.rollClip; //for now
        EntityAnimState animState = animStates.getOrCreate(animKey(3,System.identityHashCode(barrel)),clipName);
        ClipRef clip = assets.clip(visual.pamPath, clipName);
        if (clip == null) {
            return;
        }
        float x =  layout.cellCenterX(barrel.col);
        float y = layout.cellCenterY(barrel.row);
        player.draw(batch, clip, animState.stateTime, x, y, true);
    }

    private static long animKey(int kind, int instanceId) {
        return (((long) kind) << 32) | (instanceId & 0xffffffffL);
    }
}
