package view.gdx.lawn;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import model.core.ReadOnlyGameState;
import model.data.seed.PlantSeedDrop;
import view.gdx.AssetContext;

public final class SeedDropRenderer {
    private final LawnLayout layout;

    public SeedDropRenderer(LawnLayout layout) {
        this.layout = layout;
    }

    public void render(SpriteBatch batch, AssetContext assets, ReadOnlyGameState state) {
        if (state == null || assets == null || layout == null) {
            return;
        }
        for (PlantSeedDrop drop : state.getSeedDrops()) {
            String packetId = SeedPacketDefs.packetId(drop.plantType.name);
            TextureRegion packet = packetId != null ? assets.region(packetId) : null;
            if (packet == null) {
                continue;
            }
            float alpha = ttlAlpha(drop);
            float x = layout.worldX(drop.position);
            float y = layout.worldY(drop.position);
            float w = layout.cellWidth() * 0.55f;
            float h = w * (packet.getRegionHeight() / (float) packet.getRegionWidth());
            Color previous = batch.getColor();
            batch.setColor(1f, 1f, 1f, alpha);
            batch.draw(packet, x - w * 0.5f, y - h * 0.5f, w, h);
            batch.setColor(previous);
        }
    }

    private static float ttlAlpha(PlantSeedDrop drop) {
        int remaining = Math.max(0, PlantSeedDrop.TTL_TICKS - drop.age);
        float fraction = remaining / (float) PlantSeedDrop.TTL_TICKS;
        return 0.45f + 0.55f * fraction;
    }
}
