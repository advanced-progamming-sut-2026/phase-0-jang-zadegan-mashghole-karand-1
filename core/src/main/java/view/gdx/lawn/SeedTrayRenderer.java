package view.gdx.lawn;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import model.data.content.chapter.ChapterType;
import model.service.HudViewState;
import view.gdx.AssetContext;
import view.gdx.ui.UiSkin;

public final class SeedTrayRenderer {
    private static final Color AFFORDABLE = Color.WHITE;
    private static final Color UNAFFORDABLE = new Color(0.55f, 0.55f, 0.6f, 1f);
    private static final Color COST_COLOR = Color.WHITE;
    private static final Color COST_UNAVAILABLE = new Color(0.75f, 0.75f, 0.8f, 1f);
    private static final Color LEVEL_COLOR = Color.WHITE;
    private static final Color OUTLINE_COLOR = Color.BLACK;

    private static final float PACKET_MAX_H = 68f;
    private static final float PACKET_H_FRAC = 0.088f;
    private static final float FALLBACK_PACKET_ASPECT = 119f / 75f;

    private static final float PRICE_TAB_PAD = 3f;
    private static final float COST_TEXT_NUDGE_X = 4f;
    private static final float COST_TEXT_NUDGE_Y = -3f;
    private static final float TEXT_OUTLINE = 1f;
    private static final float LEVEL_SCALE = 0.8f;

    private final GlyphLayout glyphLayout = new GlyphLayout();
    private BitmapFont costFont;
    private BitmapFont levelFont;

    public void render(SpriteBatch batch, AssetContext assets, HudViewState hud,
            ChapterType chapter, int sunAmount, float worldHeight, String selectedPlantName) {
        if (batch == null || assets == null || hud == null || hud.traySlots.isEmpty()) {
            return;
        }
        ensureFonts();

        TextureRegion back = assets.region(SeedPacketDefs.worldBack(chapter));
        TextureRegion empty = assets.region(SeedPacketDefs.EMPTY);
        TextureRegion frameSample = back != null ? back : empty;
        float packetH = Math.min(PACKET_MAX_H, worldHeight * PACKET_H_FRAC);
        float packetW = packetH * aspectOf(frameSample, FALLBACK_PACKET_ASPECT);
        float gap = Math.max(4f, packetH * 0.06f);
        float x = 10f;
        float top = worldHeight - 14f;

        if (hud.trayIsConveyorRow) {
            drawConveyor(batch, assets, hud, x, top, packetW, packetH, gap, worldHeight, selectedPlantName);
            return;
        }

        TextureRegion cooldown = assets.region(SeedPacketDefs.COOLDOWN);
        TextureRegion priceTab = assets.region(SeedPacketDefs.PRICE_TAB);
        TextureRegion selectFx = assets.region(SeedPacketDefs.SELECT);

        float y = top - packetH;
        for (HudViewState.TraySlot slot : hud.traySlots) {
            boolean affordable = !hud.showSun || sunAmount >= slot.cost;
            boolean usable = slot.ready && affordable;
            Color tint = !slot.ready ? AFFORDABLE : (affordable ? AFFORDABLE : UNAFFORDABLE);
            boolean selected = usable && isSelected(slot, selectedPlantName);

            TextureRegion frame = back != null ? back : empty;
            if (frame != null) {
                batch.setColor(tint);
                batch.draw(frame, x, y, packetW, packetH);
            }

            TextureRegion plant = assets.region(SeedPacketDefs.packetId(slot.name));
            if (plant != null) {
                drawPlantIcon(batch, plant, x, y, packetW, packetH, tint);
            }

            if (!slot.ready && slot.cooldownFraction > 0f) {
                drawCooldownWipe(batch, cooldown, x, y, packetW, packetH, slot.cooldownFraction);
            }

            drawLevel(batch, slot.level, x, y, packetW, packetH);

            if (hud.showSun) {
                drawCost(batch, priceTab, slot.cost, x, y, packetW, usable);
            }

            if (selected || slot.highlighted) {
                if (selectFx != null) {
                    batch.setColor(Color.WHITE);
                    batch.draw(selectFx, x, y, packetW, packetH);
                }
            }

            y -= packetH + gap;
            if (y + packetH < 8f) {
                break;
            }
        }
        batch.setColor(Color.WHITE);
    }

    private static void drawCooldownWipe(SpriteBatch batch, TextureRegion cooldown,
            float x, float y, float packetW, float packetH, float fraction) {
        float coverH = packetH * Math.max(0f, Math.min(1f, fraction));
        if (coverH <= 0.5f || cooldown == null) {
            return;
        }
        float coverY = y;
        batch.setColor(0f, 0f, 0f, 0.5f);
        batch.draw(cooldown, x, coverY, packetW, coverH);
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
        float packetW = packetH * aspectOf(frameSample, FALLBACK_PACKET_ASPECT);
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

    private static boolean isSelected(HudViewState.TraySlot slot, String selectedPlantName) {
        return selectedPlantName != null && selectedPlantName.equals(slot.name);
    }

    private void drawConveyor(SpriteBatch batch, AssetContext assets, HudViewState hud,
            float x, float top, float packetW, float packetH, float gap, float worldHeight,
            String selectedPlantName) {
        TextureRegion belt = assets.region(SeedPacketDefs.CONVEYOR_BELT);
        TextureRegion side = assets.region(SeedPacketDefs.CONVEYOR_SIDE);
        TextureRegion topCap = assets.region(SeedPacketDefs.CONVEYOR_TOP);
        TextureRegion empty = assets.region(SeedPacketDefs.EMPTY);

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
            Color tint = slot.highlighted || slot.ready ? AFFORDABLE : UNAFFORDABLE;
            if (empty != null) {
                batch.setColor(tint);
                batch.draw(empty, packetX, y, packetW, packetH);
            }
            TextureRegion plant = assets.region(SeedPacketDefs.packetId(slot.name));
            if (plant != null) {
                drawPlantIcon(batch, plant, packetX, y, packetW, packetH, tint);
            }
            drawLevel(batch, slot.level, packetX, y, packetW, packetH);
            if (isSelected(slot, selectedPlantName) || slot.highlighted) {
                TextureRegion selected = assets.region(SeedPacketDefs.SELECT);
                if (selected != null) {
                    batch.setColor(Color.WHITE);
                    batch.draw(selected, packetX, y, packetW, packetH);
                }
            }
            y -= packetH + gap;
            if (y + packetH < beltY + 4f) {
                break;
            }
        }
        batch.setColor(Color.WHITE);
    }

    private static void drawPlantIcon(SpriteBatch batch, TextureRegion plant,
            float x, float y, float packetW, float packetH, Color tint) {
        float insetX = packetW * 0.16f;
        float insetY = packetH * 0.22f;
        batch.setColor(tint);
        batch.draw(plant, x + insetX, y + insetY,
                packetW - insetX * 2f, packetH - insetY * 1.6f);
    }

    private void drawCost(SpriteBatch batch, TextureRegion priceTab, int cost,
            float x, float y, float packetW, boolean usable) {
        if (priceTab != null) {
            float tabW = priceTab.getRegionWidth();
            float tabH = priceTab.getRegionHeight();
            float tabX = x + packetW - tabW - PRICE_TAB_PAD;
            float tabY = y + PRICE_TAB_PAD;
            batch.setColor(Color.WHITE);
            batch.draw(priceTab, tabX, tabY, tabW, tabH);

            if (costFont == null) {
                return;
            }
            String text = String.valueOf(cost);
            glyphLayout.setText(costFont, text);
            float textX = Math.round(tabX + (tabW - glyphLayout.width) * 0.5f + COST_TEXT_NUDGE_X);
            float textY = Math.round(tabY + (tabH + glyphLayout.height) * 0.5f + COST_TEXT_NUDGE_Y);
            drawOutlined(batch, costFont, text, textX, textY,
                    usable ? COST_COLOR : COST_UNAVAILABLE);
            return;
        }

        if (costFont == null) {
            return;
        }
        String text = String.valueOf(cost);
        glyphLayout.setText(costFont, text);
        float textX = Math.round(x + packetW - glyphLayout.width - PRICE_TAB_PAD + COST_TEXT_NUDGE_X);
        float textY = Math.round(y + PRICE_TAB_PAD + glyphLayout.height + COST_TEXT_NUDGE_Y);
        drawOutlined(batch, costFont, text, textX, textY,
                usable ? COST_COLOR : COST_UNAVAILABLE);
    }

    private void drawLevel(SpriteBatch batch, int level, float x, float y,
            float packetW, float packetH) {
        if (levelFont == null) {
            return;
        }
        String text = "LVL " + level;
        float prevScaleX = levelFont.getScaleX();
        float prevScaleY = levelFont.getScaleY();
        levelFont.getData().setScale(LEVEL_SCALE);
        try {
            glyphLayout.setText(levelFont, text);
            float padRight = Math.max(PRICE_TAB_PAD, packetW * 0.06f);
            float padTop = Math.max(PRICE_TAB_PAD, packetH * 0.08f);
            float textX = Math.round(x + packetW - glyphLayout.width - padRight);
            float textY = Math.round(y + packetH - padTop);
            drawOutlined(batch, levelFont, text, textX, textY, LEVEL_COLOR);
        } finally {
            levelFont.getData().setScale(prevScaleX, prevScaleY);
        }
    }

    private void drawOutlined(SpriteBatch batch, BitmapFont font, String text,
            float x, float y, Color fill) {
        font.setColor(OUTLINE_COLOR);
        for (int ox = -1; ox <= 1; ox++) {
            for (int oy = -1; oy <= 1; oy++) {
                if (ox == 0 && oy == 0) {
                    continue;
                }
                font.draw(batch, text, x + ox * TEXT_OUTLINE, y + oy * TEXT_OUTLINE);
            }
        }
        font.setColor(fill);
        font.draw(batch, text, x, y);
    }

    private static float aspectOf(TextureRegion region, float fallback) {
        if (region == null || region.getRegionHeight() <= 0) {
            return fallback;
        }
        return region.getRegionWidth() / (float) region.getRegionHeight();
    }

    private void ensureFonts() {
        if (costFont != null && levelFont != null) {
            return;
        }
        Skin skin = UiSkin.get();
        if (skin != null) {
            if (costFont == null && skin.has("medium", Label.LabelStyle.class)) {
                Label.LabelStyle style = skin.get("medium", Label.LabelStyle.class);
                if (style != null && style.font != null) {
                    costFont = style.font;
                    prepareFont(costFont);
                }
            }
            if (levelFont == null && skin.has("default", Label.LabelStyle.class)) {
                Label.LabelStyle style = skin.get("default", Label.LabelStyle.class);
                if (style != null && style.font != null) {
                    levelFont = style.font;
                    prepareFont(levelFont);
                }
            }
            if (costFont == null && skin.has("default", Label.LabelStyle.class)) {
                Label.LabelStyle style = skin.get("default", Label.LabelStyle.class);
                if (style != null && style.font != null) {
                    costFont = style.font;
                    prepareFont(costFont);
                }
            }
            if (costFont == null && skin.has("default-font", BitmapFont.class)) {
                costFont = skin.getFont("default-font");
                prepareFont(costFont);
            }
            if (levelFont == null) {
                levelFont = costFont;
            }
            if (costFont != null) {
                return;
            }
            try {
                costFont = skin.get(BitmapFont.class);
                prepareFont(costFont);
                if (levelFont == null) {
                    levelFont = costFont;
                }
                return;
            } catch (RuntimeException ignored) {
            }
        }
        costFont = new BitmapFont();
        prepareFont(costFont);
        levelFont = costFont;
    }

    private static void prepareFont(BitmapFont font) {
        font.getData().setScale(1f);
        font.setUseIntegerPositions(true);
    }
}
