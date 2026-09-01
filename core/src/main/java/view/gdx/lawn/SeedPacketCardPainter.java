package view.gdx.lawn;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import model.data.content.chapter.ChapterType;
import model.data.content.minigame.IZombieShop;
import model.data.zombie.ZombieType;
import pvz.libpvz.pam.ClipRef;
import pvz.libpvz.pam.PamPlayer;
import view.gdx.AssetContext;
import view.gdx.VisibilityResolver;
import view.gdx.catalog.DefaultVisualCatalog;
import view.gdx.catalog.VisualCatalog;
import view.gdx.catalog.ZombieVisualDef;
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
    private static final float ZOMBIE_ICON_PAM_SCALE = 0.34f;
    private static final VisualCatalog ZOMBIE_VISUALS = new DefaultVisualCatalog();
    private static final VisibilityResolver ZOMBIE_VISIBILITY = new VisibilityResolver();

    private final GlyphLayout glyphLayout = new GlyphLayout();
    private final Matrix4 savedTransform = new Matrix4();
    private final Matrix4 iconTransform = new Matrix4();
    private BitmapFont costFont;
    private BitmapFont levelFont;

    public void draw(SpriteBatch batch, AssetContext assets, SeedPacketCardView card,
            ChapterType chapter, float x, float y, float packetW, float packetH) {
        if (batch == null || assets == null || card == null) {
            return;
        }
        ensureFonts();

        if (card.isEmpty()) {
            drawEmptyPacket(batch, assets, x, y, packetW, packetH);
            return;
        }

        TextureRegion boostFrame = resolveBoostFrame(assets);
        if (card.boosted && boostFrame != null) {
            drawBoostedPacket(batch, assets, card, x, y, packetW, packetH, boostFrame);
            return;
        }

        drawNormalPacket(batch, assets, card, chapter, x, y, packetW, packetH);
    }

    private static TextureRegion resolveBoostFrame(AssetContext assets) {
        TextureRegion boostFrame = assets.region(BOOST_FRAME);
        if (boostFrame == null) {
            boostFrame = assets.region(BOOST_FRAME_FALLBACK);
        }
        return boostFrame;
    }

    private void drawEmptyPacket(SpriteBatch batch, AssetContext assets,
            float x, float y, float packetW, float packetH) {
        TextureRegion empty = assets.region(SeedPacketDefs.EMPTY);
        if (empty != null) {
            batch.setColor(Color.WHITE);
            batch.draw(empty, x, y, packetW, packetH);
        }
        batch.setColor(Color.WHITE);
    }

    private void drawBoostedPacket(SpriteBatch batch, AssetContext assets, SeedPacketCardView card,
            float x, float y, float packetW, float packetH, TextureRegion boostFrame) {
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
    }

    private void drawNormalPacket(SpriteBatch batch, AssetContext assets, SeedPacketCardView card,
            ChapterType chapter, float x, float y, float packetW, float packetH) {
        TextureRegion empty = assets.region(SeedPacketDefs.EMPTY);
        TextureRegion back = assets.region(SeedPacketDefs.worldBack(chapter));
        TextureRegion frame = back != null ? back : empty;

        Color tint = !card.ready ? AFFORDABLE : (card.affordable ? AFFORDABLE : UNAFFORDABLE);

        if (frame != null) {
            batch.setColor(tint);
            batch.draw(frame, x, y, packetW, packetH);
        }

        String packetId = SeedPacketDefs.packetId(card.plantName);
        TextureRegion plant = packetId != null ? assets.region(packetId) : null;
        if (plant != null) {
            Color plantTint = card.locked ? new Color(0.65f, 0.65f, 0.65f, 1f) : tint;
            drawPlantIcon(batch, plant, x, y, packetW, packetH, plantTint);
        } else {
            ZombieType zombieType = ZombieType.fromName(card.plantName);
            if (zombieType != null && IZombieShop.isPurchasable(zombieType)) {
                Color zombieTint = card.locked ? new Color(0.65f, 0.65f, 0.65f, 1f) : tint;
                drawZombiePamIcon(batch, assets, zombieType, x, y, packetW, packetH, zombieTint);
            }
        }

        drawDecorations(batch, assets, card, x, y, packetW, packetH);
    }

    private void drawZombiePamIcon(SpriteBatch batch, AssetContext assets, ZombieType zombieType,
            float x, float y, float packetW, float packetH, Color tint) {
        PamPlayer player = assets.pamPlayer();
        if (player == null) {
            return;
        }
        ZombieVisualDef visual = ZOMBIE_VISUALS.zombie(zombieType);
        if (visual == null || visual.idleClip == null) {
            return;
        }
        ClipRef clip = assets.clip(visual.pamPath, visual.idleClip);
        if (clip == null) {
            return;
        }
        java.util.Map<String, Boolean> visibility = ZOMBIE_VISIBILITY.intactArmor(visual);
        float centerX = x + packetW * 0.5f;
        float centerY = y + packetH * 0.52f;
        float scale = ZOMBIE_ICON_PAM_SCALE * (packetH / 68f);
        Color previous = batch.getColor();
        batch.setColor(tint);
        savedTransform.set(batch.getTransformMatrix());
        iconTransform.set(savedTransform);
        iconTransform.translate(centerX, centerY, 0f).scale(scale, scale, 1f).translate(-centerX, -centerY, 0f);
        batch.setTransformMatrix(iconTransform);
        try {
            if (visibility.isEmpty()) {
                player.draw(batch, clip, 0f, centerX, centerY, false);
            } else {
                player.draw(batch, clip, 0f, centerX, centerY, false, visibility);
            }
        } finally {
            batch.setTransformMatrix(savedTransform);
            batch.setColor(previous);
        }
    }

    public void drawDecorations(SpriteBatch batch, AssetContext assets, SeedPacketCardView card,
            float x, float y, float packetW, float packetH) {
        if (batch == null || assets == null || card == null || card.isEmpty()) {
            return;
        }
        ensureFonts();

        drawCooldownDecoration(batch, assets, card, x, y, packetW, packetH);
        drawSelectionDecoration(batch, assets, card, x, y, packetW, packetH);
        drawLockDecoration(batch, assets, card, x, y, packetW, packetH);
        drawLevel(batch, card.level, x, y, packetW, packetH);

        if (card.stackCount > 1) {
            drawStackCount(batch, card.stackCount, x, y, packetW, packetH);
        }

        if (card.showCost) {
            TextureRegion priceTab = assets.region(SeedPacketDefs.PRICE_TAB);
            boolean usable = card.ready && card.affordable;
            drawCost(batch, priceTab, card.cost, x, y, packetW, usable);
        }

        drawReadyBarDecoration(batch, assets, card, x, y, packetW, packetH);
    }

    private void drawCooldownDecoration(SpriteBatch batch, AssetContext assets, SeedPacketCardView card,
            float x, float y, float packetW, float packetH) {
        if (card.ready || card.cooldownFraction <= 0f) {
            return;
        }
        TextureRegion cooldown = assets.region(SeedPacketDefs.COOLDOWN);
        drawCooldownWipe(batch, cooldown, x, y, packetW, packetH, card.cooldownFraction);
    }

    private void drawSelectionDecoration(SpriteBatch batch, AssetContext assets, SeedPacketCardView card,
            float x, float y, float packetW, float packetH) {
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
    }

    private void drawLockDecoration(SpriteBatch batch, AssetContext assets, SeedPacketCardView card,
            float x, float y, float packetW, float packetH) {
        if (!card.locked) {
            return;
        }
        TextureRegion lock = assets.region(LOCK_ICON);
        if (lock != null) {
            float lockW = packetW * 0.45f;
            float lockH = packetH * 0.45f;
            batch.setColor(Color.WHITE);
            batch.draw(lock, x + (packetW - lockW) * 0.5f, y + (packetH - lockH) * 0.35f, lockW, lockH);
        }
    }

    private void drawReadyBarDecoration(SpriteBatch batch, AssetContext assets, SeedPacketCardView card,
            float x, float y, float packetW, float packetH) {
        if (!card.showReadyBar || card.boosted) {
            return;
        }
        TextureRegion readyBar = assets.region(SeedPacketDefs.READY);
        if (readyBar != null) {
            float barH = packetH * 0.14f;
            batch.setColor(Color.WHITE);
            batch.draw(readyBar, x, y + packetH - barH, packetW, barH);
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

    private void drawStackCount(SpriteBatch batch, int count, float x, float y,
            float packetW, float packetH) {
        if (costFont == null || count <= 1) {
            return;
        }
        String text = "x" + count;
        glyphLayout.setText(costFont, text);
        float textX = Math.round(x + packetW - glyphLayout.width - PRICE_TAB_PAD);
        float textY = Math.round(y + PRICE_TAB_PAD + glyphLayout.height);
        drawOutlined(batch, costFont, text, textX, textY, COST_COLOR);
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
        if (skin != null && loadFontsFromSkin(skin)) {
            return;
        }
        costFont = new BitmapFont();
        prepareFont(costFont);
        levelFont = costFont;
    }

    private boolean loadFontsFromSkin(Skin skin) {
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
            return true;
        }
        try {
            costFont = skin.get(BitmapFont.class);
            prepareFont(costFont);
            if (levelFont == null) {
                levelFont = costFont;
            }
            return true;
        } catch (RuntimeException ignored) {
            return false;
        }
    }

    private static void prepareFont(BitmapFont font) {
        font.getData().setScale(1f);
        font.setUseIntegerPositions(true);
    }
}
