package view.gdx.lawn;

import java.util.Set;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import model.data.content.chapter.ChapterType;
import model.data.plant.PlantType;
import model.service.HudViewState;
import view.gdx.AssetContext;

public final class SeedTrayRenderer {
    private static final float PACKET_MAX_H = 68f;
    private static final float PACKET_H_FRAC = 0.088f;

    private final SeedPacketCardPainter painter = new SeedPacketCardPainter();

    public void render(SpriteBatch batch, AssetContext assets, HudViewState hud,
            ChapterType chapter, int sunAmount, float worldHeight, String selectedPlantName) {
        render(batch, assets, hud, chapter, sunAmount, worldHeight, selectedPlantName, null);
    }

    public void render(SpriteBatch batch, AssetContext assets, HudViewState hud,
            ChapterType chapter, int sunAmount, float worldHeight, String selectedPlantName,
            Set<PlantType> boostedPlants) {
        if (batch == null || assets == null || hud == null || hud.traySlots.isEmpty()) {
            return;
        }

        TextureRegion back = assets.region(SeedPacketDefs.worldBack(chapter));
        TextureRegion empty = assets.region(SeedPacketDefs.EMPTY);
        TextureRegion frameSample = back != null ? back : empty;
        float packetH = Math.min(PACKET_MAX_H, worldHeight * PACKET_H_FRAC);
        float packetW = SeedPacketCardPainter.packetWidth(packetH, frameSample);
        float gap = Math.max(4f, packetH * 0.06f);
        float x = 10f;
        float top = worldHeight - 14f;

        if (hud.trayIsConveyorRow) {
            drawConveyor(batch, assets, hud, x, top, packetW, packetH, gap, worldHeight, selectedPlantName);
            return;
        }

        float y = top - packetH;
        for (HudViewState.TraySlot slot : hud.traySlots) {
            PlantType type = PlantType.fromName(slot.name);
            boolean boosted = boostedPlants != null && type != null && boostedPlants.contains(type);
            SeedPacketCardView card = SeedPacketCardView.fromTraySlot(slot, hud.showSun, sunAmount,
                    selectedPlantName, boosted);
            painter.draw(batch, assets, card, chapter, x, y, packetW, packetH);

            y -= packetH + gap;
            if (y + packetH < 8f) {
                break;
            }
        }
        batch.setColor(Color.WHITE);
    }

    public String hitTest(HudViewState hud, AssetContext assets,
            float worldX, float worldY, float worldHeight, int sunAmount) {
        return hitTest(hud, assets, worldX, worldY, worldHeight, sunAmount, true);
    }

    public String hitTest(HudViewState hud, AssetContext assets,
            float worldX, float worldY, float worldHeight, int sunAmount, boolean requireSelectable) {
        if (hud == null || hud.traySlots.isEmpty() || assets == null) {
            return null;
        }
        TextureRegion back = assets.region(SeedPacketDefs.worldBack(null));
        TextureRegion empty = assets.region(SeedPacketDefs.EMPTY);
        TextureRegion frameSample = back != null ? back : empty;
        float packetH = Math.min(PACKET_MAX_H, worldHeight * PACKET_H_FRAC);
        float packetW = SeedPacketCardPainter.packetWidth(packetH, frameSample);
        float gap = Math.max(4f, packetH * 0.06f);
        float x = 10f;
        float top = worldHeight - 14f;

        if (hud.trayIsConveyorRow) {
            float beltW = packetW * 1.18f;
            float packetX = x + (beltW - packetW) * 0.5f;
            float beltH = Math.min(worldHeight - 24f,
                    Math.max(1, hud.traySlots.size()) * (packetH + gap) + packetH * 0.4f);
            float beltY = top - beltH;
            float y = top - packetH - 8f;
            for (HudViewState.TraySlot slot : hud.traySlots) {
                if (contains(packetX, y, packetW, packetH, worldX, worldY)
                        && (!requireSelectable || isSelectable(hud, slot, sunAmount))) {
                    return slot.name;
                }
                y -= packetH + gap;
                if (y + packetH < beltY + 4f) {
                    break;
                }
            }
            return null;
        }

        float y = top - packetH;
        for (HudViewState.TraySlot slot : hud.traySlots) {
            if (contains(x, y, packetW, packetH, worldX, worldY)
                    && (!requireSelectable || isSelectable(hud, slot, sunAmount))) {
                return slot.name;
            }
            y -= packetH + gap;
            if (y + packetH < 8f) {
                break;
            }
        }
        return null;
    }

    public static boolean isSelectable(HudViewState hud, HudViewState.TraySlot slot, int sunAmount) {
        if (slot == null || !slot.ready) {
            return false;
        }
        if (hud != null && hud.showSun && sunAmount < slot.cost) {
            return false;
        }
        return true;
    }

    private static boolean contains(float x, float y, float w, float h, float px, float py) {
        return px >= x && px <= x + w && py >= y && py <= y + h;
    }

    private void drawConveyor(SpriteBatch batch, AssetContext assets, HudViewState hud,
            float x, float top, float packetW, float packetH, float gap, float worldHeight,
            String selectedPlantName) {
        TextureRegion belt = assets.region(SeedPacketDefs.CONVEYOR_BELT);
        TextureRegion side = assets.region(SeedPacketDefs.CONVEYOR_SIDE);
        TextureRegion topCap = assets.region(SeedPacketDefs.CONVEYOR_TOP);

        int slots = Math.max(1, hud.traySlots.size());
        float beltH = Math.min(worldHeight - 24f, slots * (packetH + gap) + packetH * 0.4f);
        float beltW = packetW * 1.18f;
        float beltX = x;
        float beltY = top - beltH;

        if (belt != null) {
            batch.setColor(Color.WHITE);
            batch.draw(belt, beltX, beltY, beltW, beltH);
        }
        if (side != null) {
            float sideW = beltW * 0.12f;
            batch.draw(side, beltX, beltY, sideW, beltH);
            batch.draw(side, beltX + beltW - sideW, beltY, sideW, beltH);
        }
        if (topCap != null) {
            float capH = packetH * 0.28f;
            batch.draw(topCap, beltX, top - capH, beltW, capH);
        }

        float packetX = beltX + (beltW - packetW) * 0.5f;
        float y = top - packetH - 8f;
        for (HudViewState.TraySlot slot : hud.traySlots) {
            SeedPacketCardView card = SeedPacketCardView.fromTraySlot(slot, false, 0, selectedPlantName, false);
            painter.draw(batch, assets, card, null, packetX, y, packetW, packetH);
            y -= packetH + gap;
            if (y + packetH < beltY + 4f) {
                break;
            }
        }
        batch.setColor(Color.WHITE);
    }
}
