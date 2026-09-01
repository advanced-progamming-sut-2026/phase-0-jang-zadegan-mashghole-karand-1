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
        render(batch, assets, hud, chapter, sunAmount, sunAmount, worldHeight, 1280f, selectedPlantName,
                selectedConveyorIndex, conveyorAnimator, hudTopReserve, null);
    }

    public void render(SpriteBatch batch, AssetContext assets, HudViewState hud,
            ChapterType chapter, int sunAmount, float worldHeight, String selectedPlantName,
            int selectedConveyorIndex, ConveyorTrayAnimator conveyorAnimator, float hudTopReserve,
            Set<PlantType> boostedPlants) {
        render(batch, assets, hud, chapter, sunAmount, sunAmount, worldHeight, 1280f, selectedPlantName,
                selectedConveyorIndex, conveyorAnimator, hudTopReserve, boostedPlants);
    }

    public void render(SpriteBatch batch, AssetContext assets, HudViewState hud,
            ChapterType chapter, int leftSun, int rightSun, float worldHeight, float worldWidth,
            String selectedPlantName, int selectedConveyorIndex, ConveyorTrayAnimator conveyorAnimator,
            float hudTopReserve, Set<PlantType> boostedPlants) {
        if (batch == null || assets == null || hud == null) {
            return;
        }

        if (hud.trayIsConveyorRow) {
            drawConveyor(batch, assets, hud, conveyorAnimator, worldHeight, hudTopReserve,
                    selectedConveyorIndex, boostedPlants);
            return;
        }

        TextureRegion back = assets.region(SeedPacketDefs.worldBack(chapter));
        TextureRegion empty = assets.region(SeedPacketDefs.EMPTY);
        TextureRegion frameSample = back != null ? back : empty;
        float packetH = Math.min(PACKET_MAX_H, worldHeight * PACKET_H_FRAC);
        float packetW = SeedPacketCardPainter.packetWidth(packetH, frameSample);
        float gap = Math.max(4f, packetH * 0.06f);
        float top = trayTop(worldHeight, hudTopReserve);

        if (!hud.traySlots.isEmpty()) {
            drawTrayColumn(batch, assets, hud.traySlots, chapter, leftSun, selectedPlantName,
                    boostedPlants, 10f, top, packetW, packetH, gap, hud.showSun);
        }
        if (hud.rightTraySlots != null && !hud.rightTraySlots.isEmpty()) {
            float rightX = Math.max(10f, worldWidth - packetW - 10f);
            drawTrayColumn(batch, assets, hud.rightTraySlots, chapter, rightSun, selectedPlantName,
                    boostedPlants, rightX, top, packetW, packetH, gap, hud.showSun);
        }
        batch.setColor(Color.WHITE);
    }

    private void drawTrayColumn(SpriteBatch batch, AssetContext assets, List<HudViewState.TraySlot> slots,
            ChapterType chapter, int sunAmount, String selectedPlantName, Set<PlantType> boostedPlants,
            float x, float top, float packetW, float packetH, float gap, boolean showSun) {
        float y = top - packetH;
        for (HudViewState.TraySlot slot : slots) {
            PlantType type = PlantType.fromName(slot.name);
            boolean boosted = boostedPlants != null && type != null && boostedPlants.contains(type);
            SeedPacketCardView card = SeedPacketCardView.fromTraySlot(slot, showSun, sunAmount,
                    selectedPlantName, boosted);
            painter.draw(batch, assets, card, chapter, x, y, packetW, packetH);

            y -= packetH + gap;
            if (y + packetH < 8f) {
                break;
            }
        }
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
        return hitTest(hud, assets, worldX, worldY, worldHeight, sunAmount, sunAmount, 1280f, true,
                conveyorAnimator, hudTopReserve);
    }

    public String hitTest(HudViewState hud, AssetContext assets,
            float worldX, float worldY, float worldHeight, int sunAmount, boolean requireSelectable,
            ConveyorTrayAnimator conveyorAnimator, float hudTopReserve) {
        return hitTest(hud, assets, worldX, worldY, worldHeight, sunAmount, sunAmount, 1280f,
                requireSelectable, conveyorAnimator, hudTopReserve);
    }

    public String hitTest(HudViewState hud, AssetContext assets,
            float worldX, float worldY, float worldHeight, int leftSun, int rightSun, float worldWidth,
            boolean requireSelectable, ConveyorTrayAnimator conveyorAnimator, float hudTopReserve) {
        if (hud == null || assets == null) {
            return null;
        }

        if (hud.trayIsConveyorRow) {
            ConveyorTrayHit hit = hitTestConveyor(hud, assets, worldX, worldY, worldHeight, leftSun,
                    requireSelectable, conveyorAnimator, hudTopReserve);
            if (!hit.isHit()) {
                return null;
            }
            if (hit.isSlot()) {
                return hud.traySlots.get(hit.slotIndex()).name;
            }
            return "";
        }

        TextureRegion back = assets.region(SeedPacketDefs.worldBack(null));
        TextureRegion empty = assets.region(SeedPacketDefs.EMPTY);
        TextureRegion frameSample = back != null ? back : empty;
        float packetH = Math.min(PACKET_MAX_H, worldHeight * PACKET_H_FRAC);
        float packetW = SeedPacketCardPainter.packetWidth(packetH, frameSample);
        float gap = Math.max(4f, packetH * 0.06f);
        float top = trayTop(worldHeight, hudTopReserve);

        String leftHit = hitTestColumn(hud.traySlots, 10f, top, packetW, packetH, gap,
                worldX, worldY, leftSun, requireSelectable, hud.showSun);
        if (leftHit != null) {
            return leftHit;
        }
        if (hud.rightTraySlots != null && !hud.rightTraySlots.isEmpty()) {
            float rightX = Math.max(10f, worldWidth - packetW - 10f);
            return hitTestColumn(hud.rightTraySlots, rightX, top, packetW, packetH, gap,
                    worldX, worldY, rightSun, requireSelectable, hud.showSun);
        }
        return null;
    }

    private static String hitTestColumn(List<HudViewState.TraySlot> slots, float x, float top,
            float packetW, float packetH, float gap, float worldX, float worldY, int sunAmount,
            boolean requireSelectable, boolean showSun) {
        if (slots == null || slots.isEmpty()) {
            return null;
        }
        float y = top - packetH;
        for (HudViewState.TraySlot slot : slots) {
            if (contains(x, y, packetW, packetH, worldX, worldY)) {
                if (!requireSelectable || isSelectable(showSun, slot, sunAmount)) {
                    return slot.name;
                }
                return "";
            }
            y -= packetH + gap;
            if (y + packetH < 8f) {
                break;
            }
        }
        return null;
    }

    public static boolean isSelectable(HudViewState hud, HudViewState.TraySlot slot, int sunAmount) {
        return isSelectable(hud != null && hud.showSun, slot, sunAmount);
    }

    public static boolean isSelectable(boolean showSun, HudViewState.TraySlot slot, int sunAmount) {
        if (slot == null || !slot.ready) {
            return false;
        }
        if (showSun && sunAmount < slot.cost) {
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
            int selectedConveyorIndex, Set<PlantType> boostedPlants) {
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
                        slot.ready, selectedConveyorIndex == i, slot.level, boostedPlants);
            }
        } else {
            for (ConveyorTrayAnimator.AnimatedPacket packet : animated) {
                if (packet.y + layout.packetH < layout.beltY - layout.packetH) {
                    continue;
                }
                drawConveyorPacket(batch, assets, layout, packet.name, packet.y,
                        packet.ready, selectedConveyorIndex == packet.slotIndex, packet.level, boostedPlants);
            }
        }
        batch.setColor(Color.WHITE);
    }

    private void drawConveyorPacket(SpriteBatch batch, AssetContext assets,
            ConveyorTrayAnimator.ConveyorLayout layout, String plantName, float y,
            boolean ready, boolean selected, int level, Set<PlantType> boostedPlants) {
        PlantType type = PlantType.fromName(plantName);
        boolean boosted = boostedPlants != null && type != null && boostedPlants.contains(type);
        SeedPacketCardView card = new SeedPacketCardView(
                plantName, 0, level, false, ready, 0f, false, selected, boosted, false, true,
                false, false, false);
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
