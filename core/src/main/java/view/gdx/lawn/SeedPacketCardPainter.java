package view.gdx.lawn;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import model.data.content.chapter.ChapterType;
import model.data.plant.PlantType;
import view.gdx.AssetContext;
import view.gdx.ui.UiSkin;


public final class SeedPacketCardPainter {
    public static final String BOOST_FRAME = "IMAGE_UI_PACKETS_READY_PREMIUM";
    public static final String BOOST_FRAME_FALLBACK = SeedPacketDefs.READY;
    static final String LOCK_ICON = "IMAGE_UI_CARDS_LOCK_MEDIUM_GOLD";
    private static final Color AFFORDABLE = Color.WHITE;
    private static final Color UNAFFORDABLE = new Color(0.55f, 0.55f, 0.6f, 1f);
    private static final Color COST_COLOR = Color.WHITE;
    private static final Color COST_UNAVAILABLE = new Color(0.75f, 0.75f, 0.8f, 1f);
    private static final Color LEVEL_COLOR = Color.WHITE;
    private static final Color OUTLINE_COLOR = Color.BLACK;

    private static final float PRICE_TAB_PAD = 3f;
    private static final float COST_TEXT_NUDGE_X = 4f;
    private static final float COST_TEXT_NUDGE_Y = -3f;
    private static final float TEXT_OUTLINE = 1f;
    private static final float LEVEL_SCALE = 0.8f;
    private static final float FALLBACK_PACKET_ASPECT = 119f / 75f;

    private final GlyphLayout glyphLayout = new GlyphLayout();
    private BitmapFont costFont;
    private BitmapFont levelFont;

    public void draw(SpriteBatch batch, AssetContext assets, SeedPacketCardView card,
            ChapterType chapter, float x, float y, float packetW, float packetH) {
        if (batch == null || assets == null || card == null) {
            return;
        }
        ensureFonts();

        TextureRegion empty = assets.region(SeedPacketDefs.EMPTY);
        TextureRegion boostFrame = assets.region(BOOST_FRAME);
        if (boostFrame == null) {
            boostFrame = assets.region(BOOST_FRAME_FALLBACK);
        }
        if (card.isEmpty()) {
            if (empty != null) {
                batch.setColor(Color.WHITE);
                batch.draw(empty, x, y, packetW, packetH);
            }
            batch.setColor(Color.WHITE);
            return;
        }

        if (card.boosted && boostFrame != null) {
            batch.setColor(Color.WHITE);
            batch.draw(boostFrame, x, y, packetW, packetH);
            String packetId = SeedPacketDefs.packetId(card.plantName);
            TextureRegion plant = assets.region(packetId);
            if (plant != null) {
                Color plantTint = card.locked ? new Color(0.65f, 0.65f, 0.65f, 1f) : Color.WHITE;
                drawPlantIcon(batch, plant, x, y, packetW, packetH, plantTint);
            }
            drawDecorations(batch, assets, card, x, y, packetW, packetH);
            batch.setColor(Color.WHITE);
            return;
        }

        TextureRegion back = assets.region(SeedPacketDefs.worldBack(chapter));
        TextureRegion frame = back != null ? back : empty;

        Color tint = !card.ready ? AFFORDABLE : (card.affordable ? AFFORDABLE : UNAFFORDABLE);

        if (frame != null) {
            batch.setColor(tint);
            batch.draw(frame, x, y, packetW, packetH);
        }

        String packetId = SeedPacketDefs.packetId(card.plantName);
        TextureRegion plant = assets.region(packetId);
        if (plant != null) {
            Color plantTint = card.locked ? new Color(0.65f, 0.65f, 0.65f, 1f) : tint;
            drawPlantIcon(batch, plant, x, y, packetW, packetH, plantTint);
        }

        drawDecorations(batch, assets, card, x, y, packetW, packetH);
    }

    public void drawDecorations(SpriteBatch batch, AssetContext assets, SeedPacketCardView card,
            float x, float y, float packetW, float packetH) {
        if (batch == null || assets == null || card == null || card.isEmpty()) {
            return;
        }
        ensureFonts();

        if (!card.ready && card.cooldownFraction > 0f) {
            TextureRegion cooldown = assets.region(SeedPacketDefs.COOLDOWN);
            drawCooldownWipe(batch, cooldown, x, y, packetW, packetH, card.cooldownFraction);
        }

        if (card.selected) {
            TextureRegion selectFx = assets.region(SeedPacketDefs.SELECT);
            if (selectFx != null) {
                batch.setColor(Color.WHITE);
                batch.draw(selectFx, x, y, packetW, packetH);
            }
        } else if (card.highlighted) {
            TextureRegion selectFx = assets.region(SeedPacketDefs.SELECT);
            if (selectFx != null) {
                batch.setColor(1f, 1f, 1f, 0.55f);
                batch.draw(selectFx, x, y, packetW, packetH);
                batch.setColor(Color.WHITE);
            }
        }

        if (card.locked) {
            TextureRegion lock = assets.region(LOCK_ICON);
            if (lock != null) {
                float lockW = packetW * 0.45f;
                float lockH = packetH * 0.45f;
                batch.setColor(Color.WHITE);
                batch.draw(lock, x + (packetW - lockW) * 0.5f, y + (packetH - lockH) * 0.35f, lockW, lockH);
            }
        }

        drawLevel(batch, card.level, x, y, packetW, packetH);

        if (card.showCost) {
            TextureRegion priceTab = assets.region(SeedPacketDefs.PRICE_TAB);
            boolean usable = card.ready && card.affordable;
            drawCost(batch, priceTab, card.cost, x, y, packetW, usable);
        }

        if (card.showReadyBar && !card.boosted) {
            TextureRegion readyBar = assets.region(SeedPacketDefs.READY);
            if (readyBar != null) {
                float barH = packetH * 0.14f;
                batch.setColor(Color.WHITE);
                batch.draw(readyBar, x, y + packetH - barH, packetW, barH);
            }
        }

    }

    public static float aspectOf(TextureRegion region, float fallback) {
        if (region == null || region.getRegionHeight() <= 0) {
            return fallback;
        }
        return region.getRegionWidth() / (float) region.getRegionHeight();
    }

    public static float packetWidth(float packetH, TextureRegion frameSample) {
        return packetH * aspectOf(frameSample, FALLBACK_PACKET_ASPECT);
    }

    private static void drawPlantIcon(SpriteBatch batch, TextureRegion plant,
            float x, float y, float packetW, float packetH, Color tint) {
        float insetX = packetW * 0.16f;
        float insetY = packetH * 0.22f;
        batch.setColor(tint);
        batch.draw(plant, x + insetX, y + insetY,
                packetW - insetX * 2f, packetH - insetY * 1.6f);
    }

    private static void drawCooldownWipe(SpriteBatch batch, TextureRegion cooldown,
            float x, float y, float packetW, float packetH, float fraction) {
        float coverH = packetH * Math.max(0f, Math.min(1f, fraction));
        if (coverH <= 0.5f || cooldown == null) {
            return;
        }
        batch.setColor(0f, 0f, 0f, 0.5f);
        batch.draw(cooldown, x, y, packetW, coverH);
        batch.setColor(Color.WHITE);
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
        if (levelFont == null || level <= 0) {
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
