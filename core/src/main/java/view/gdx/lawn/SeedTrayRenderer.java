package view.gdx.lawn;

import java.util.List;
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
    private static final float TRAY_TOP_PAD = 14f;

    private final SeedPacketCardPainter painter = new SeedPacketCardPainter();

    public void render(SpriteBatch batch, AssetContext assets, HudViewState hud,
            ChapterType chapter, int sunAmount, float worldHeight, String selectedPlantName,
            int selectedConveyorIndex, ConveyorTrayAnimator conveyorAnimator, float hudTopReserve) {
        render(batch, assets, hud, chapter, sunAmount, worldHeight, selectedPlantName,
                selectedConveyorIndex, conveyorAnimator, hudTopReserve, null);
    }

    public void render(SpriteBatch batch, AssetContext assets, HudViewState hud,
            ChapterType chapter, int sunAmount, float worldHeight, String selectedPlantName,
            int selectedConveyorIndex, ConveyorTrayAnimator conveyorAnimator, float hudTopReserve,
            Set<PlantType> boostedPlants) {
        if (batch == null || assets == null || hud == null) {
            return;
        }

        if (hud.trayIsConveyorRow) {
            drawConveyor(batch, assets, hud, conveyorAnimator, worldHeight, hudTopReserve,
                    selectedConveyorIndex);
            return;
        }
        if (hud.traySlots.isEmpty()) {
            return;
        }

        TextureRegion back = assets.region(SeedPacketDefs.worldBack(chapter));
        TextureRegion empty = assets.region(SeedPacketDefs.EMPTY);
        TextureRegion frameSample = back != null ? back : empty;
        float packetH = Math.min(PACKET_MAX_H, worldHeight * PACKET_H_FRAC);
        float packetW = SeedPacketCardPainter.packetWidth(packetH, frameSample);
        float gap = Math.max(4f, packetH * 0.06f);
        float x = 10f;
        float top = trayTop(worldHeight, hudTopReserve);

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

    public ConveyorTrayHit hitTestConveyor(HudViewState hud, AssetContext assets,
            float worldX, float worldY, float worldHeight, int sunAmount,
            boolean requireSelectable, ConveyorTrayAnimator conveyorAnimator, float hudTopReserve) {
        ConveyorTrayAnimator.ConveyorLayout layout = conveyorAnimator != null
                ? conveyorAnimator.layout()
                : ConveyorTrayAnimator.ConveyorLayout.compute(worldHeight, hudTopReserve);

        List<ConveyorTrayAnimator.AnimatedPacket> animated = conveyorAnimator != null
                ? conveyorAnimator.visiblePackets()
                : List.of();
        if (!animated.isEmpty()) {
            for (int i = 0; i < animated.size(); i++) {
                ConveyorTrayAnimator.AnimatedPacket packet = animated.get(i);
                HudViewState.TraySlot slot = i < hud.traySlots.size() ? hud.traySlots.get(i) : null;
                if (contains(layout.packetX, packet.y, layout.packetW, layout.packetH, worldX, worldY)
                        && (!requireSelectable || (slot != null && isSelectable(hud, slot, sunAmount)))) {
                    return ConveyorTrayHit.slot(i);
                }
            }
            return containsConveyorArea(layout, worldX, worldY) ? ConveyorTrayHit.beltArea() : ConveyorTrayHit.MISS;
        }

        for (int i = 0; i < hud.traySlots.size(); i++) {
            HudViewState.TraySlot slot = hud.traySlots.get(i);
            if (contains(layout.packetX, layout.slotY(i), layout.packetW, layout.packetH, worldX, worldY)
                    && (!requireSelectable || isSelectable(hud, slot, sunAmount))) {
                return ConveyorTrayHit.slot(i);
            }
        }
        return containsConveyorArea(layout, worldX, worldY) ? ConveyorTrayHit.beltArea() : ConveyorTrayHit.MISS;
    }

    public String hitTest(HudViewState hud, AssetContext assets,
            float worldX, float worldY, float worldHeight, int sunAmount,
            ConveyorTrayAnimator conveyorAnimator, float hudTopReserve) {
        return hitTest(hud, assets, worldX, worldY, worldHeight, sunAmount, true,
                conveyorAnimator, hudTopReserve);
    }

    public String hitTest(HudViewState hud, AssetContext assets,
            float worldX, float worldY, float worldHeight, int sunAmount, boolean requireSelectable,
            ConveyorTrayAnimator conveyorAnimator, float hudTopReserve) {
        if (hud == null || assets == null) {
            return null;
        }

        if (hud.trayIsConveyorRow) {
            ConveyorTrayHit hit = hitTestConveyor(hud, assets, worldX, worldY, worldHeight, sunAmount,
                    requireSelectable, conveyorAnimator, hudTopReserve);
            if (!hit.isHit()) {
                return null;
            }
            if (hit.isSlot()) {
                return hud.traySlots.get(hit.slotIndex()).name;
            }
            return "";
        }
        if (hud.traySlots.isEmpty()) {
            return null;
        }

        TextureRegion back = assets.region(SeedPacketDefs.worldBack(null));
        TextureRegion empty = assets.region(SeedPacketDefs.EMPTY);
        TextureRegion frameSample = back != null ? back : empty;
        float packetH = Math.min(PACKET_MAX_H, worldHeight * PACKET_H_FRAC);
        float packetW = SeedPacketCardPainter.packetWidth(packetH, frameSample);
        float gap = Math.max(4f, packetH * 0.06f);
        float x = 10f;
        float top = trayTop(worldHeight, hudTopReserve);

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

    private static float trayTop(float worldHeight, float hudTopReserve) {
        return Math.max(0f, worldHeight - hudTopReserve - TRAY_TOP_PAD);
    }

    private static boolean contains(float x, float y, float w, float h, float px, float py) {
        return px >= x && px <= x + w && py >= y && py <= y + h;
    }

    private void drawConveyor(SpriteBatch batch, AssetContext assets, HudViewState hud,
            ConveyorTrayAnimator conveyorAnimator, float worldHeight, float hudTopReserve,
            int selectedConveyorIndex) {
        ConveyorTrayAnimator.ConveyorLayout layout = conveyorAnimator != null
                ? conveyorAnimator.layout()
                : ConveyorTrayAnimator.ConveyorLayout.compute(worldHeight, hudTopReserve);

        TextureRegion belt = assets.region(SeedPacketDefs.CONVEYOR_BELT);
        TextureRegion side = assets.region(SeedPacketDefs.CONVEYOR_SIDE);
        TextureRegion topCap = assets.region(SeedPacketDefs.CONVEYOR_TOP);

        float scroll = conveyorAnimator != null ? conveyorAnimator.beltScrollOffset() : 0f;
        drawScrollingBelt(batch, belt, layout.beltX, layout.beltY, layout.beltW, layout.beltH, scroll);

        if (side != null) {
            float sideW = layout.beltW * 0.12f;
            batch.setColor(Color.WHITE);
            batch.draw(side, layout.beltX, layout.beltY, sideW, layout.beltH);
            batch.draw(side, layout.beltX + layout.beltW - sideW, layout.beltY, sideW, layout.beltH);
        }
        if (topCap != null) {
            batch.setColor(Color.WHITE);
            batch.draw(topCap, layout.beltX, layout.hudBottomY - layout.topCapH, layout.beltW, layout.topCapH);
        }

        List<ConveyorTrayAnimator.AnimatedPacket> animated = conveyorAnimator != null
                ? conveyorAnimator.visiblePackets()
                : List.of();
        if (animated.isEmpty() && !hud.traySlots.isEmpty()) {
            for (int i = 0; i < hud.traySlots.size(); i++) {
                HudViewState.TraySlot slot = hud.traySlots.get(i);
                drawConveyorPacket(batch, assets, layout, slot.name, layout.slotY(i),
                        slot.ready, selectedConveyorIndex == i, slot.level);
            }
        } else {
            for (ConveyorTrayAnimator.AnimatedPacket packet : animated) {
                if (packet.y + layout.packetH < layout.beltY - layout.packetH) {
                    continue;
                }
                drawConveyorPacket(batch, assets, layout, packet.name, packet.y,
                        packet.ready, selectedConveyorIndex == packet.slotIndex, packet.level);
            }
        }
        batch.setColor(Color.WHITE);
    }

    private void drawConveyorPacket(SpriteBatch batch, AssetContext assets,
            ConveyorTrayAnimator.ConveyorLayout layout, String plantName, float y,
            boolean ready, boolean selected, int level) {
        SeedPacketCardView card = new SeedPacketCardView(
                plantName, 0, level, false, ready, 0f, false, selected, false, false, true);
        painter.draw(batch, assets, card, null, layout.packetX, y, layout.packetW, layout.packetH);
    }

    private static void drawScrollingBelt(SpriteBatch batch, TextureRegion belt,
            float beltX, float beltY, float beltW, float beltH, float scrollOffset) {
        if (belt == null) {
            return;
        }
        batch.setColor(Color.WHITE);
        float tileH = belt.getRegionHeight();
        if (tileH <= 0f) {
            batch.draw(belt, beltX, beltY, beltW, beltH);
            return;
        }
        float offset = scrollOffset % tileH;
        for (float y = beltY - offset; y < beltY + beltH; y += tileH) {
            float drawH = Math.min(tileH, beltY + beltH - y);
            if (drawH > 0f) {
                batch.draw(belt, beltX, y, beltW, drawH);
            }
        }
    }

    private static boolean containsConveyorArea(ConveyorTrayAnimator.ConveyorLayout layout,
            float worldX, float worldY) {
        return worldX >= layout.beltX && worldX <= layout.beltX + layout.beltW
                && worldY >= layout.beltY && worldY <= layout.hudBottomY;
    }
}
