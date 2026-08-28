package view.gdx.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import model.service.HudViewState;
import view.gdx.AssetContext;

public final class HudOverlayRenderer {
    private static final String PF_BANK = "IMAGE_UI_HUD_INGAME_PLANTFOOD_BANK";
    private static final String PF_SLOT = "IMAGE_UI_HUD_INGAME_PLANTFOOD_BANK_FILLED_SLOT";
    private static final String PF_BUTTON = "IMAGE_UI_HUD_INGAME_PLANTFOOD_BUTTON";

    private static final int PF_MAX_SLOTS = 3;

    private final BitmapFont font = new BitmapFont();
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
        if (hud == null) return;
        if ( hud.showPlantFood){
            drawPlantFood(batch,assets,plantFoodAmount,worldWidth,worldHeight);
        }
        if (hud.showWave){
            drawWave(batch,assets,progress, totalWaves,worldWidth,worldHeight);
        }
        if (hud.showSun){
            drawSun(batch,assets,sunAmount,worldWidth,worldHeight);
        }
    }
    private void drawPlantFood(SpriteBatch batch, AssetContext assets,
                               int plantFoodAmount, float worldWidth, float worldHeight) {
        TextureRegion bank = assets.region(PF_BANK);
        if (bank == null) {
            return;
        }

        float bankH = Math.min(50f, worldHeight * 0.1f);
        float bankW = bankH * (bank.getRegionWidth() / (float) bank.getRegionHeight());
        float x = 120f;
        float y = worldHeight - bankH - 16f;

        batch.setColor(Color.WHITE);
        batch.draw(bank, x, y, bankW, bankH);

        TextureRegion slot = assets.region(PF_SLOT);
        if (slot != null) {
            int filled = Math.max(0, Math.min(PF_MAX_SLOTS, plantFoodAmount));
            float slotH = bankH * 0.42f;
            float slotW = slotH * (slot.getRegionWidth() / (float) slot.getRegionHeight());
            float gap = slotW * 0.35f;
            float startX = x + bankW * 0.18f;
            float slotY = y + (bankH - slotH) * 0.55f;

            for (int i = 0; i < filled; i++) {
                batch.draw(slot, startX + i * (slotW + gap), slotY, slotW, slotH);
            }
        }

        TextureRegion button = assets.region(PF_BUTTON);
        if (button != null) {
            float btnH = bankH * 0.85f;
            float btnW = btnH * (button.getRegionWidth() / (float) button.getRegionHeight());
            batch.draw(button, x + bankW - btnW * 0.35f, y + (bankH - btnH) * 0.5f, btnW, btnH);
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
    private void drawSun(SpriteBatch batch, AssetContext assets,
                         int sunAmount , float worldWidth, float worldHeight){
        TextureRegion backGround = assets.region("IMAGE_UI_HUD_INGAME_BACKGROUND_3SLICE");
        if (backGround == null){
            return;
        }
        float backGroundH = 30f;
        float backGroundW = 85f;
        float x = 300f;
        float y = worldHeight - backGroundH - 25f;
        batch.setColor(Color.WHITE);
        batch.draw(backGround, x, y, backGroundW, backGroundH);
        TextureRegion sun = assets.region("IMAGE_UI_HUD_INGAME_SUN");
        float sunH = 50f;
        float sunW = 30f;
        float sunX = 290f;
        float sunY = worldHeight - backGroundH - 38f;
        batch.setColor(Color.WHITE);
        batch.draw(sun, sunX, sunY, sunW, sunH);
        font.setColor(Color.WHITE);
        font.draw(batch, String.valueOf(sunAmount), x + 35f, y + 21f);
    }
}
