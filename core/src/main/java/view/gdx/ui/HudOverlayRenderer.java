package view.gdx.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import model.service.HudViewState;
import view.gdx.AssetContext;

public final class HudOverlayRenderer {
    public static final String PF_BANK = "IMAGE_UI_HUD_INGAME_PLANTFOOD_BANK";
    public static final String PF_LEAF = "IMAGE_UI_HUD_INGAME_PLANTFOOD_BANK_FILLED_SLOT";
    public static final String PF_BUTTON = "IMAGE_UI_HUD_INGAME_PLANTFOOD_BUTTON";
    public static final String PF_BUTTON_DOWN = "IMAGE_UI_HUD_INGAME_PLANTFOOD_BUTTON_DOWN";

    public static final int PF_MAX_SLOTS = 3;
    private static final float HUD_TOP_INSET = 25f;
    private static final float SUN_BG_H = 30f;
    private static final float PF_BANK_H = 30f;
    private static final float PF_SLOT_GAP_RATIO = 0.02f;

    private final BitmapFont font = new BitmapFont();
    private final GlyphLayout glyphLayout = new GlyphLayout();
    private boolean plantFoodMode;
    private float plantFoodPulse;

    public void setPlantFoodMode(boolean plantFoodMode) {
        this.plantFoodMode = plantFoodMode;
    }

    public void setPlantFoodPulse(float pulse) {
        this.plantFoodPulse = Math.max(0f, Math.min(1f, pulse));
    }

    public void render(
            SpriteBatch batch,
            AssetContext assets,
            HudViewState hud,
            int sunAmount,
            int plantFoodAmount,
            float progress,
            int totalWaves,
            float worldWidth,
            float worldHeight) {
        render(batch, assets, hud, sunAmount, -1, plantFoodAmount, progress, totalWaves,
                worldWidth, worldHeight);
    }

    public void render(
            SpriteBatch batch,
            AssetContext assets,
            HudViewState hud,
            int sunAmount,
            int zombieSunAmount,
            int plantFoodAmount,
            float progress,
            int totalWaves,
            float worldWidth,
            float worldHeight) {
        if (hud == null) return;
        if (hud.showPlantFood) {
            drawPlantFood(batch, assets, plantFoodAmount, worldWidth, worldHeight);
        }
        if (hud.showWave) {
            drawWave(batch, assets, progress, totalWaves, worldWidth, worldHeight);
        }
        if (hud.mode == HudViewState.Mode.ZOMBOSS) {
            drawBossHp(batch, assets, hud.timedWarProgress, hud.timedWarGoal, worldWidth, worldHeight);
        }
        if (hud.mode == HudViewState.Mode.BRAINS) {
            drawBrainsHud(batch, worldWidth, worldHeight, hud.protectedAlive, hud.protectedTotal,
                    hud.timedWarSecondsLeft);
        }
        if (hud.mode == HudViewState.Mode.VASE_BREAKER) {
            drawVaseCount(batch, assets, hud.conveyorRemaining, worldWidth, worldHeight);
        }
        if (hud.showSun) {
            if (zombieSunAmount >= 0) {
                float leftX = Math.max(220f, worldWidth * 0.22f);
                float rightX = leftX + 110f;
                drawSunCounter(batch, assets, sunAmount, "P", leftX, worldHeight);
                drawSunCounter(batch, assets, zombieSunAmount, "Z", rightX, worldHeight);
            } else {
                drawSunCounter(batch, assets, sunAmount, null,
                        Math.max(220f, worldWidth * 0.22f), worldHeight);
            }
        }
    }

    public boolean hitTestPlantFoodButton(AssetContext assets, float worldX, float worldY,
            float worldWidth, float worldHeight) {
        PlantFoodHudLayout layout = layoutPlantFood(assets, worldWidth, worldHeight);
        if (layout == null) {
            return false;
        }
        return worldX >= layout.buttonX && worldX <= layout.buttonX + layout.buttonW
                && worldY >= layout.buttonY && worldY <= layout.buttonY + layout.buttonH;
    }

    public static PlantFoodHudLayout layoutPlantFood(AssetContext assets, float worldWidth, float worldHeight) {
        if (assets == null) {
            return null;
        }
        TextureRegion bank = assets.region(PF_BANK);
        TextureRegion button = assets.region(PF_BUTTON);
        if (bank == null || button == null || bank.getRegionHeight() <= 0 || button.getRegionHeight() <= 0) {
            return null;
        }

        float bankH = PF_BANK_H;
        float bankW = bankH * (bank.getRegionWidth() / (float) bank.getRegionHeight());
        float x = 120f;
        float y = worldHeight - bankH - HUD_TOP_INSET;

        float btnH = bankH * 1.15f;
        float btnW = btnH * (button.getRegionWidth() / (float) button.getRegionHeight());
        float buttonX = x + bankW - btnW * 0.28f;
        float buttonY = y + (bankH - btnH) * 0.5f;

        float slotH = bankH * 0.72f;
        TextureRegion leaf = assets.region(PF_LEAF);
        float slotW = leaf != null && leaf.getRegionHeight() > 0
                ? slotH * (leaf.getRegionWidth() / (float) leaf.getRegionHeight())
                : slotH;
        float slotGap = -8f;
        float slotStartX = x + bankW * 0.16f + 7f;
        float slotY = y + (bankH - slotH) * 0.5f;

        return new PlantFoodHudLayout(
                x, y, bankW, bankH,
                buttonX, buttonY, btnW, btnH,
                slotStartX, slotY, slotW, slotH, slotGap);
    }

    private void drawPlantFood(SpriteBatch batch, AssetContext assets,
                               int plantFoodAmount, float worldWidth, float worldHeight) {
        PlantFoodHudLayout layout = layoutPlantFood(assets, worldWidth, worldHeight);
        TextureRegion bank = assets.region(PF_BANK);
        if (layout == null || bank == null) {
            return;
        }

        batch.setColor(Color.WHITE);
        batch.draw(bank, layout.bankX, layout.bankY, layout.bankW, layout.bankH);

        if (plantFoodPulse > 0f) {
            float glow = 0.35f + 0.25f * plantFoodPulse;
            batch.setColor(1f, 0.92f, 0.45f, glow);
            float pad = 3f + plantFoodPulse * 2f;
            batch.draw(bank, layout.bankX - pad, layout.bankY - pad,
                    layout.bankW + pad * 2f, layout.bankH + pad * 2f);
            batch.setColor(Color.WHITE);
        }

        TextureRegion leaf = assets.region(PF_LEAF);
        if (leaf != null) {
            int filled = Math.max(0, Math.min(PF_MAX_SLOTS, plantFoodAmount));
            for (int i = 0; i < filled; i++) {
                batch.draw(leaf,
                        layout.slotStartX + i * (layout.slotW + layout.slotGap),
                        layout.slotY,
                        layout.slotW,
                        layout.slotH);
            }
        }

        TextureRegion button = assets.region(plantFoodMode ? PF_BUTTON_DOWN : PF_BUTTON);
        if (button == null) {
            button = assets.region(PF_BUTTON);
        }
        if (button != null) {
            batch.draw(button, layout.buttonX, layout.buttonY, layout.buttonW, layout.buttonH);
        }
    }

    private void drawWave(SpriteBatch batch, AssetContext assets,
                          float progress, int totalWaves, float worldWidth, float worldHeight) {
        TextureRegion meter = assets.region("IMAGE_UI_HUD_INGAME_PROGRESS_METER");
        TextureRegion fill = assets.region("IMAGE_UI_HUD_INGAME_PROGRESS_METER_FILL");
        TextureRegion head = assets.region("IMAGE_UI_HUD_INGAME_PROGRESS_METER_ZOMBIEHEAD");
        TextureRegion flag = assets.region("IMAGE_UI_HUD_INGAME_PROGRESS_METER_FLAG_DEFAULT");
        TextureRegion pole = assets.region("IMAGE_UI_HUD_INGAME_PROGRESS_METER_FLAG_POLE");
        if (meter == null) {
            return;
        }

        float meterH = 28f;
        float meterW = 220f;
        float meterX = worldWidth / 2f - meterW / 2f;
        float meterY = worldHeight - meterH - 16f;

        float padX = meterW * 0.06f;
        float padY = meterH * 0.18f;
        float trackX = meterX + padX;
        float trackY = meterY + padY;
        float trackW = meterW - padX * 2f + 5f;
        float trackH = meterH - padY * 2f;

        batch.setColor(Color.WHITE);
        batch.draw(meter, meterX, meterY, meterW, meterH);

        float p = Math.max(0f, Math.min(1f, progress));
        float edgeX = trackEdgeX(trackX, trackW, p);

        if (fill != null && p > 0f) {
            float fillW = trackW * p;
            float fillX = trackX + trackW - fillW;
            batch.draw(fill, fillX, trackY, fillW, trackH);
        }


        if (totalWaves > 0) {
            for (int w = 2; w <= totalWaves; w++) {
                float waveStartProgress = (w - 1) / (float) totalWaves;
                float flagX = trackEdgeX(trackX, trackW, waveStartProgress);
                drawFlag(batch, flag, pole, flagX, trackY, trackH, trackW);
            }
        }

        if (head != null) {
            float headH = meterH * 1.5f;
            float headW = headH * (head.getRegionWidth() / (float) head.getRegionHeight());
            float headX = edgeX - headW * 0.5f;
            float headY = meterY + (meterH - headH) * 0.5f;
            batch.setColor(Color.WHITE);
            batch.draw(head, headX, headY, headW, headH);
        }
    }

    private void drawBossHp(SpriteBatch batch, AssetContext assets, int hp, int total,
            float worldWidth, float worldHeight) {
        TextureRegion meter = assets.region("IMAGE_UI_HUD_INGAME_PROGRESS_METER");
        TextureRegion fill = assets.region("IMAGE_UI_HUD_INGAME_PROGRESS_METER_FILL");
        if (meter == null) {
            return;
        }

        float meterH = 28f;
        float meterW = 260f;
        float meterX = worldWidth / 2f - meterW / 2f;
        float meterY = worldHeight - meterH - 16f;

        float padX = meterW * 0.06f;
        float padY = meterH * 0.18f;
        float trackX = meterX + padX;
        float trackY = meterY + padY;
        float trackW = meterW - padX * 2f + 5f;
        float trackH = meterH - padY * 2f;

        batch.setColor(Color.WHITE);
        batch.draw(meter, meterX, meterY, meterW, meterH);

        float p = total <= 0 ? 0f : Math.max(0f, Math.min(1f, hp / (float) total));
        if (fill != null && p > 0f) {
            batch.setColor(0.85f, 0.18f, 0.14f, 1f);
            batch.draw(fill, trackX, trackY, trackW * p, trackH);
            batch.setColor(Color.WHITE);
        }

        for (int i = 1; i <= 2; i++) {
            float sx = trackX + trackW * (i / 3f);
            if (fill != null) {
                batch.setColor(0.95f, 0.9f, 0.7f, 1f);
                batch.draw(fill, sx - 1.5f, trackY, 3f, trackH);
            }
        }
        batch.setColor(Color.WHITE);
    }

    private static float trackEdgeX(float trackX, float trackW, float progress) {
        float p = Math.max(0f, Math.min(1f, progress));
        return trackX + trackW * (1f - p);
    }

    private void drawFlag(SpriteBatch batch, TextureRegion flag, TextureRegion pole,
                          float edgeX, float trackY, float trackH, float trackW) {
        float poleH = trackH * 1.6f;
        float poleW = 4f;
        if (pole != null && pole.getRegionHeight() > 0) {
            poleW = Math.min(6f, poleH * pole.getRegionWidth() / (float) pole.getRegionHeight());
            batch.setColor(Color.WHITE);
            batch.draw(pole, edgeX - poleW * 0.5f, trackY, poleW, poleH);
        }

        if (flag != null && flag.getRegionHeight() > 0) {
            float flagH = trackH * 1.3f;
            float flagW = flagH * flag.getRegionWidth() / (float) flag.getRegionHeight();
            flagW = Math.min(flagW, trackW * 0.10f);
            float flagY = trackY + poleH * 0.35f;
            batch.setColor(Color.WHITE);
            batch.draw(flag, edgeX - flagW * 0.05f, flagY, flagW, flagH);
        }
    }

    private void drawSunCounter(SpriteBatch batch, AssetContext assets,
            int sunAmount, String sideLabel, float x, float worldHeight) {
        TextureRegion backGround = assets.region("IMAGE_UI_HUD_INGAME_BACKGROUND_3SLICE");
        if (backGround == null) {
            return;
        }
        float backGroundH = SUN_BG_H;
        float backGroundW = sideLabel != null ? 95f : 85f;
        float y = worldHeight - backGroundH - HUD_TOP_INSET;
        batch.setColor(Color.WHITE);
        batch.draw(backGround, x, y, backGroundW, backGroundH);

        TextureRegion sun = assets.region("IMAGE_UI_HUD_INGAME_SUN");
        if (sun != null && sun.getRegionHeight() > 0) {
            float sunH = 42f;
            float sunW = sunH * (sun.getRegionWidth() / (float) sun.getRegionHeight());
            float sunX = x - sunW * 0.35f;
            float sunY = y + (backGroundH - sunH) * 0.5f;
            batch.setColor(Color.WHITE);
            batch.draw(sun, sunX, sunY, sunW, sunH);
        }

        font.setColor(Color.WHITE);
        String text = sideLabel != null
                ? sideLabel + " " + sunAmount
                : String.valueOf(sunAmount);
        glyphLayout.setText(font, text);
        float textX = x + 28f;
        float textY = y + (backGroundH + glyphLayout.height) * 0.5f;
        font.draw(batch, glyphLayout, textX, textY);
    }

    private void drawBrainsHud(SpriteBatch batch, float worldWidth, float worldHeight,
            int collected, int total, int secondsLeft) {
        font.setColor(Color.WHITE);
        String brainsText = "Brains " + collected + "/" + total;
        glyphLayout.setText(font, brainsText);
        float x = worldWidth * 0.5f - glyphLayout.width * 0.5f;
        float y = worldHeight - HUD_TOP_INSET - 8f;
        font.draw(batch, glyphLayout, x, y);
        if (secondsLeft > 0) {
            String timerText = secondsLeft + "s";
            glyphLayout.setText(font, timerText);
            float timerX = worldWidth * 0.5f - glyphLayout.width * 0.5f;
            float timerY = y - glyphLayout.height - 4f;
            font.draw(batch, glyphLayout, timerX, timerY);
        }
    }

    private void drawVaseCount(SpriteBatch batch, AssetContext assets, int vaseCount,
            float worldWidth, float worldHeight) {
        TextureRegion backGround = assets.region("IMAGE_UI_HUD_INGAME_BACKGROUND_3SLICE");
        if (backGround == null) {
            return;
        }
        float backGroundH = SUN_BG_H;
        float backGroundW = 85f;
        float x = worldWidth * 0.5f - backGroundW * 0.5f;
        float y = worldHeight - backGroundH - HUD_TOP_INSET;
        batch.setColor(Color.WHITE);
        batch.draw(backGround, x, y, backGroundW, backGroundH);
        font.setColor(Color.WHITE);
        String text = "Vases " + vaseCount;
        glyphLayout.setText(font, text);
        float textX = x + (backGroundW - glyphLayout.width) * 0.5f;
        float textY = y + (backGroundH + glyphLayout.height) * 0.5f;
        font.draw(batch, glyphLayout, textX, textY);
    }

    public static final class PlantFoodHudLayout {
        public final float bankX;
        public final float bankY;
        public final float bankW;
        public final float bankH;
        public final float buttonX;
        public final float buttonY;
        public final float buttonW;
        public final float buttonH;
        public final float slotStartX;
        public final float slotY;
        public final float slotW;
        public final float slotH;
        public final float slotGap;

        public PlantFoodHudLayout(
                float bankX, float bankY, float bankW, float bankH,
                float buttonX, float buttonY, float buttonW, float buttonH,
                float slotStartX, float slotY, float slotW, float slotH, float slotGap) {
            this.bankX = bankX;
            this.bankY = bankY;
            this.bankW = bankW;
            this.bankH = bankH;
            this.buttonX = buttonX;
            this.buttonY = buttonY;
            this.buttonW = buttonW;
            this.buttonH = buttonH;
            this.slotStartX = slotStartX;
            this.slotY = slotY;
            this.slotW = slotW;
            this.slotH = slotH;
            this.slotGap = slotGap;
        }
    }
}
