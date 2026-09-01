package view.gdx.ui;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import model.core.Position;
import model.core.EventBus;
import model.data.Grave.GraveContent;
import model.event.events.GlowingZombieDiedEvent;
import model.event.events.GraveDestroyedEvent;
import view.gdx.AssetContext;
import view.gdx.lawn.LawnLayout;

public final class PlantFoodCollectFeedback {
    private static final float FLY_DURATION = 0.55f;
    private static final float PULSE_DURATION = 0.4f;
    private static final float LEAF_SIZE = 28f;

    private static final class Source {
        final float modelX;
        final float modelY;
        final int row;
        final int col;
        final boolean graveSource;

        Source(float modelX, float modelY, int row, int col, boolean graveSource) {
            this.modelX = modelX;
            this.modelY = modelY;
            this.row = row;
            this.col = col;
            this.graveSource = graveSource;
        }
    }

    private static final class Flight {
        float startX;
        float startY;
        float endX;
        float endY;
        float elapsed;
    }

    private final List<Source> pendingSources = new ArrayList<>();
    private final List<Flight> flights = new ArrayList<>();
    private float pulseTimer;
    private int prevPlantFoodAmount = -1;
    private boolean registered;

    public void register(EventBus eventBus) {
        if (registered || eventBus == null) {
            return;
        }
        registered = true;
        eventBus.subscribe(GlowingZombieDiedEvent.class, this::onGlowingZombieDied);
        eventBus.subscribe(GraveDestroyedEvent.class, this::onGraveDestroyed);
    }

    public void reset() {
        pendingSources.clear();
        flights.clear();
        pulseTimer = 0f;
        prevPlantFoodAmount = -1;
    }

    public float bankPulse() {
        if (pulseTimer <= 0f) {
            return 0f;
        }
        return pulseTimer / PULSE_DURATION;
    }

    public void update(float deltaSeconds, int plantFoodAmount, LawnLayout layout,
            AssetContext assets, float worldWidth, float worldHeight) {
        if (deltaSeconds > 0f && pulseTimer > 0f) {
            pulseTimer = Math.max(0f, pulseTimer - deltaSeconds);
        }

        if (prevPlantFoodAmount >= 0 && plantFoodAmount > prevPlantFoodAmount && !pendingSources.isEmpty()) {
            int targetSlot = Math.max(0, Math.min(HudOverlayRenderer.PF_MAX_SLOTS - 1, plantFoodAmount - 1));
            HudOverlayRenderer.PlantFoodHudLayout hudLayout =
                    HudOverlayRenderer.layoutPlantFood(assets, worldWidth, worldHeight);
            float endX = hudLayout != null
                    ? hudLayout.slotStartX + targetSlot * (hudLayout.slotW + hudLayout.slotGap) + hudLayout.slotW * 0.5f
                    : 160f;
            float endY = hudLayout != null
                    ? hudLayout.slotY + hudLayout.slotH * 0.5f
                    : worldHeight - 40f;

            for (Source source : pendingSources) {
                Flight flight = new Flight();
                flight.startX = sourceX(layout, source);
                flight.startY = sourceY(layout, source);
                flight.endX = endX;
                flight.endY = endY;
                flights.add(flight);
            }
            pulseTimer = PULSE_DURATION;
        }
        pendingSources.clear();

        if (prevPlantFoodAmount < 0) {
            prevPlantFoodAmount = plantFoodAmount;
        } else {
            prevPlantFoodAmount = plantFoodAmount;
        }

        for (int i = flights.size() - 1; i >= 0; i--) {
            Flight flight = flights.get(i);
            flight.elapsed += deltaSeconds;
            if (flight.elapsed >= FLY_DURATION) {
                flights.remove(i);
            }
        }
    }

    public void render(SpriteBatch batch, AssetContext assets) {
        if (batch == null || assets == null || flights.isEmpty()) {
            return;
        }
        TextureRegion leaf = assets.region(HudOverlayRenderer.PF_LEAF);
        if (leaf == null || leaf.getRegionHeight() <= 0) {
            return;
        }

        for (Flight flight : flights) {
            float t = Math.min(1f, flight.elapsed / FLY_DURATION);
            float ease = 1f - (1f - t) * (1f - t);
            float x = flight.startX + (flight.endX - flight.startX) * ease;
            float y = flight.startY + (flight.endY - flight.startY) * ease;
            float size = LEAF_SIZE * (1f - t * 0.15f);
            float w = size * (leaf.getRegionWidth() / (float) leaf.getRegionHeight());
            batch.setColor(1f, 1f, 1f, 1f - t * 0.2f);
            batch.draw(leaf, x - w * 0.5f, y - size * 0.5f, w, size);
        }
        batch.setColor(Color.WHITE);
    }

    private void onGlowingZombieDied(GlowingZombieDiedEvent event) {
        if (event == null || event.zombie == null || event.zombie.position == null) {
            return;
        }
        pendingSources.add(new Source(
                event.zombie.position.x,
                event.zombie.position.y,
                event.zombie.row,
                event.zombie.col,
                false));
    }

    private void onGraveDestroyed(GraveDestroyedEvent event) {
        if (event == null || event.grave == null || event.grave.graveContent != GraveContent.PLANT_FOOD) {
            return;
        }
        pendingSources.add(new Source(
                event.grave.pos.x,
                event.grave.pos.y,
                event.grave.row,
                event.grave.col,
                true));
    }

    private static float sourceX(LawnLayout layout, Source source) {
        if (layout == null) {
            return source.modelX;
        }
        if (source.graveSource) {
            return layout.cellCenterX(source.col);
        }
        return layout.worldX(new Position(source.modelX, source.modelY));
    }

    private static float sourceY(LawnLayout layout, Source source) {
        if (layout == null) {
            return source.modelY;
        }
        if (source.graveSource) {
            return layout.cellCenterY(source.row);
        }
        return layout.worldYForRow(source.row, new Position(source.modelX, source.modelY));
    }
}
