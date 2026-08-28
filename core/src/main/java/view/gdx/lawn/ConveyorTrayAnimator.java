package view.gdx.lawn;

import java.util.ArrayList;
import java.util.List;

import model.service.HudViewState;

public final class ConveyorTrayAnimator {
    private static final float BELT_SCROLL_SPEED = 48f;
    private static final float PACKET_MOVE_SPEED = 180f;

    private float beltScrollOffset;
    private final List<AnimatedPacket> packets = new ArrayList<>();
    private List<String> lastSlotNames = List.of();
    private ConveyorLayout layout = ConveyorLayout.empty();

    public void reset() {
        beltScrollOffset = 0f;
        packets.clear();
        lastSlotNames = List.of();
        layout = ConveyorLayout.empty();
    }

    public void update(float deltaSeconds, HudViewState hud, float worldHeight, float hudTopReserve) {
        if (hud == null || !hud.trayIsConveyorRow) {
            reset();
            return;
        }

        beltScrollOffset += BELT_SCROLL_SPEED * deltaSeconds;
        layout = ConveyorLayout.compute(worldHeight, hudTopReserve);

        List<String> slotNames = slotNames(hud);
        if (!slotNames.equals(lastSlotNames)) {
            reconcilePackets(hud, slotNames);
            lastSlotNames = slotNames;
        }

        for (AnimatedPacket packet : packets) {
            packet.targetY = layout.slotY(packet.slotIndex);
            float distance = packet.targetY - packet.y;
            if (Math.abs(distance) <= 0.5f) {
                packet.y = packet.targetY;
            } else {
                float step = PACKET_MOVE_SPEED * deltaSeconds;
                packet.y += Math.signum(distance) * Math.min(step, Math.abs(distance));
            }
        }
    }

    public float beltScrollOffset() {
        return beltScrollOffset;
    }

    public ConveyorLayout layout() {
        return layout;
    }

    public List<AnimatedPacket> visiblePackets() {
        return List.copyOf(packets);
    }

    private void reconcilePackets(HudViewState hud, List<String> slotNames) {
        if (lastSlotNames.isEmpty()) {
            spawnAllFromHud(hud, !slotNames.isEmpty());
            return;
        }

        if (slotNames.isEmpty()) {
            packets.clear();
            return;
        }

        int removedIndex = findRemovedIndex(lastSlotNames, slotNames);
        if (removedIndex >= 0) {
            if (removedIndex < packets.size()) {
                packets.remove(removedIndex);
            }
            for (AnimatedPacket packet : packets) {
                if (packet.slotIndex > removedIndex) {
                    packet.slotIndex--;
                }
            }
            syncMetadataFromHud(hud);
            return;
        }

        if (isNewAtBack(lastSlotNames, slotNames)) {
            int newIndex = slotNames.size() - 1;
            HudViewState.TraySlot incoming = hud.traySlots.get(newIndex);
            packets.add(new AnimatedPacket(
                    incoming.name,
                    layout.spawnY(),
                    layout.slotY(newIndex),
                    newIndex,
                    incoming.ready,
                    incoming.highlighted,
                    incoming.level));
            return;
        }

        rebuildFromHud(hud);
    }

    private static int findRemovedIndex(List<String> previous, List<String> current) {
        if (current.size() != previous.size() - 1) {
            return -1;
        }
        for (int i = 0; i < previous.size(); i++) {
            if (!previous.subList(0, i).equals(current.subList(0, Math.min(i, current.size())))) {
                continue;
            }
            if (i < previous.size() - 1
                    && previous.subList(i + 1, previous.size()).equals(current.subList(i, current.size()))) {
                return i;
            }
        }
        return -1;
    }

    private static boolean isNewAtBack(List<String> previous, List<String> current) {
        return !current.isEmpty()
                && current.size() == previous.size() + 1
                && current.subList(0, previous.size()).equals(previous);
    }

    private void rebuildFromHud(HudViewState hud) {
        spawnAllFromHud(hud, false);
    }

    private void spawnAllFromHud(HudViewState hud, boolean animateFromBottom) {
        packets.clear();
        for (int i = 0; i < hud.traySlots.size(); i++) {
            HudViewState.TraySlot slot = hud.traySlots.get(i);
            float targetY = layout.slotY(i);
            float startY = animateFromBottom
                    ? layout.spawnY() - (hud.traySlots.size() - 1 - i) * (layout.packetH + layout.gap)
                    : targetY;
            packets.add(new AnimatedPacket(
                    slot.name, startY, targetY, i, slot.ready, slot.highlighted, slot.level));
        }
    }

    private void syncMetadataFromHud(HudViewState hud) {
        for (int i = 0; i < packets.size() && i < hud.traySlots.size(); i++) {
            HudViewState.TraySlot slot = hud.traySlots.get(i);
            AnimatedPacket packet = packets.get(i);
            packet.name = slot.name;
            packet.ready = slot.ready;
            packet.highlighted = slot.highlighted;
            packet.level = slot.level;
            packet.slotIndex = i;
        }
    }

    private static List<String> slotNames(HudViewState hud) {
        List<String> names = new ArrayList<>(hud.traySlots.size());
        for (HudViewState.TraySlot slot : hud.traySlots) {
            names.add(slot.name);
        }
        return names;
    }

    public static final class AnimatedPacket {
        public String name;
        public float y;
        public float targetY;
        public int slotIndex;
        public boolean ready;
        public boolean highlighted;
        public int level;

        AnimatedPacket(String name, float y, float targetY, int slotIndex,
                boolean ready, boolean highlighted, int level) {
            this.name = name;
            this.y = y;
            this.targetY = targetY;
            this.slotIndex = slotIndex;
            this.ready = ready;
            this.highlighted = highlighted;
            this.level = level;
        }
    }

    public static final class ConveyorLayout {
        private static final float PACKET_MAX_H = 68f;
        private static final float PACKET_H_FRAC = 0.088f;
        private static final float FALLBACK_PACKET_ASPECT = 119f / 75f;
        private static final float BELT_BOTTOM = 0f;

        public final float x;
        public final float hudBottomY;
        public final float packetW;
        public final float packetH;
        public final float gap;
        public final float beltW;
        public final float beltH;
        public final float beltX;
        public final float beltY;
        public final float packetX;
        public final float packetAreaTop;
        public final float topCapH;

        private ConveyorLayout(float x, float hudBottomY, float packetW, float packetH, float gap,
                float beltW, float beltH, float beltX, float beltY, float packetX,
                float packetAreaTop, float topCapH) {
            this.x = x;
            this.hudBottomY = hudBottomY;
            this.packetW = packetW;
            this.packetH = packetH;
            this.gap = gap;
            this.beltW = beltW;
            this.beltH = beltH;
            this.beltX = beltX;
            this.beltY = beltY;
            this.packetX = packetX;
            this.packetAreaTop = packetAreaTop;
            this.topCapH = topCapH;
        }

        static ConveyorLayout empty() {
            return new ConveyorLayout(10f, 0f, 0f, 0f, 0f, 0f, 0f, 10f, 0f, 10f, 0f, 0f);
        }

        static ConveyorLayout compute(float worldHeight, float hudTopReserve) {
            float packetH = Math.min(PACKET_MAX_H, worldHeight * PACKET_H_FRAC);
            float packetW = packetH * FALLBACK_PACKET_ASPECT;
            float gap = Math.max(4f, packetH * 0.06f);
            float x = 10f;
            float hudBottomY = Math.max(packetH * 2f, worldHeight - hudTopReserve);
            float beltH = hudBottomY - BELT_BOTTOM;
            float beltW = packetW * 1.18f;
            float beltX = x;
            float packetX = beltX + (beltW - packetW) * 0.5f;
            float topCapH = packetH * 0.28f;
            float packetAreaTop = hudBottomY - topCapH - 8f;
            return new ConveyorLayout(x, hudBottomY, packetW, packetH, gap, beltW, beltH,
                    beltX, BELT_BOTTOM, packetX, packetAreaTop, topCapH);
        }

        public float slotY(int index) {
            return packetAreaTop - packetH - index * (packetH + gap);
        }

        public float spawnY() {
            return beltY - packetH;
        }
    }
}
