package view.gdx.lawn;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;

import pvz.libpvz.pam.ClipRef;
import shared.izombie.MatchRole;
import shared.message.QuickMessageId;
import view.gdx.AssetContext;

public final class QuickMessageHud implements com.badlogic.gdx.utils.Disposable {
    public static final String PLANT_AVATAR = "IMAGE_UI_JOUST_AVATARS_AVATAR_18";
    public static final String ZOMBIE_AVATAR = "IMAGE_UI_JOUST_AVATARS_AVATAR_4";

    private static final float DISPLAY_SECONDS = 3.5f;
    private static final float AVATAR_H = 40f;
    private static final float EMOJI_H = 30f;
    private static final float ANIM_DRAW_SCALE = 0.22f;
    private static final float PANEL_PAD = 6f;
    private static final float PANEL_W = 118f;

    private final BitmapFont font = new BitmapFont();
    private final GlyphLayout layout = new GlyphLayout();
    private final TextureRegion white;

    private ActiveMessage plantMessage;
    private ActiveMessage zombieMessage;

    public QuickMessageHud() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        white = new TextureRegion(texture);
    }

    public void show(MatchRole role, String playerName, QuickMessageId message) {
        if (role == null || message == null) {
            return;
        }
        String name = playerName == null || playerName.isBlank() ? "Player" : playerName;
        ActiveMessage active = new ActiveMessage(name, message, DISPLAY_SECONDS);
        if (role == MatchRole.PLANTS) {
            plantMessage = active;
        } else {
            zombieMessage = active;
        }
    }

    public void update(float deltaSeconds) {
        plantMessage = tick(plantMessage, deltaSeconds);
        zombieMessage = tick(zombieMessage, deltaSeconds);
    }

    public void clear() {
        plantMessage = null;
        zombieMessage = null;
    }

    public void render(SpriteBatch batch, AssetContext assets, float worldWidth, float worldHeight,
            float topReserve) {
        if (assets == null) {
            return;
        }
        float y = worldHeight - topReserve - 108f;
        if (plantMessage != null) {
            drawPanel(batch, assets, 12f, y, MatchRole.PLANTS, plantMessage, true);
        }
        if (zombieMessage != null) {
            drawPanel(batch, assets, worldWidth - PANEL_W - 12f, y, MatchRole.ZOMBIES, zombieMessage, false);
        }
    }

    private ActiveMessage tick(ActiveMessage message, float deltaSeconds) {
        if (message == null) {
            return null;
        }
        message.timeLeft -= deltaSeconds;
        message.animTime += deltaSeconds;
        return message.timeLeft > 0f ? message : null;
    }

    private void drawPanel(SpriteBatch batch, AssetContext assets, float x, float y, MatchRole role,
            ActiveMessage message, boolean alignLeft) {
        String avatarId = role == MatchRole.PLANTS ? PLANT_AVATAR : ZOMBIE_AVATAR;
        TextureRegion avatar = assets.region(avatarId);
        float panelH = panelHeight(message.message);
        drawPanelBackground(batch, x, y, PANEL_W, panelH, message.timeLeft);

        float avatarX = alignLeft ? x + PANEL_PAD : x + PANEL_W - PANEL_PAD - scaledW(avatar, AVATAR_H);
        float avatarY = y + panelH - PANEL_PAD - AVATAR_H;
        drawRegionFit(batch, avatar, avatarX, avatarY, AVATAR_H, alpha(message.timeLeft));

        font.setColor(1f, 1f, 1f, alpha(message.timeLeft));
        layout.setText(font, message.playerName);
        float nameX = alignLeft ? x + PANEL_PAD + AVATAR_H + 6f : x + PANEL_PAD;
        float nameY = y + panelH - PANEL_PAD - 4f;
        font.draw(batch, message.playerName, nameX, nameY);

        float contentY = y + PANEL_PAD;
        drawMessageContent(batch, assets, message, x + PANEL_W * 0.5f, contentY, message.timeLeft);
        font.setColor(Color.WHITE);
    }

    private void drawMessageContent(SpriteBatch batch, AssetContext assets, ActiveMessage message,
            float centerX, float bottomY, float timeLeft) {
        QuickMessageId id = message.message;
        float alpha = alpha(timeLeft);
        switch (id.kind) {
            case TEXT -> {
                font.setColor(1f, 0.95f, 0.7f, alpha);
                layout.setText(font, id.display);
                font.draw(batch, id.display, centerX - layout.width * 0.5f, bottomY + layout.height);
                font.setColor(Color.WHITE);
            }
            case EMOJI -> {
                TextureRegion region = assets.region(id.imageId);
                float drawX = centerX - scaledW(region, EMOJI_H) * 0.5f;
                drawRegionFit(batch, region, drawX, bottomY, EMOJI_H, alpha);
            }
            case ANIMATED -> drawPam(batch, assets, id, centerX, bottomY + EMOJI_H * 0.5f, alpha, message.animTime);
            default -> {
            }
        }
    }

    private void drawPam(SpriteBatch batch, AssetContext assets, QuickMessageId id,
            float centerX, float centerY, float alpha, float animTime) {
        if (assets.pamPlayer() == null) {
            return;
        }
        ClipRef clip = assets.clip(id.pamPath, id.pamClip);
        if (clip == null) {
            return;
        }
        Matrix4 old = batch.getTransformMatrix().cpy();
        Matrix4 scaled = old.cpy();
        scaled.translate(centerX, centerY, 0f);
        scaled.scale(ANIM_DRAW_SCALE, ANIM_DRAW_SCALE, 1f);
        batch.flush();
        batch.setTransformMatrix(scaled);
        batch.setColor(1f, 1f, 1f, alpha);
        assets.pamPlayer().draw(batch, clip, animTime, 0f, 0f, true);
        batch.flush();
        batch.setTransformMatrix(old);
        batch.setColor(Color.WHITE);
    }

    private void drawPanelBackground(SpriteBatch batch, float x, float y, float w, float h, float timeLeft) {
        float alpha = alpha(timeLeft) * 0.82f;
        batch.setColor(0.08f, 0.1f, 0.14f, alpha);
        batch.draw(white, x, y, w, h);
        batch.setColor(0.75f, 0.55f, 0.3f, alpha * 0.55f);
        batch.draw(white, x, y + h - 2f, w, 2f);
        batch.setColor(Color.WHITE);
    }

    private static float panelHeight(QuickMessageId message) {
        return switch (message.kind) {
            case TEXT -> 78f;
            case EMOJI -> 86f;
            case ANIMATED -> 94f;
        };
    }

    private static float alpha(float timeLeft) {
        return Math.min(1f, timeLeft / 0.4f);
    }

    private static float scaledW(TextureRegion region, float targetH) {
        if (region == null || region.getRegionHeight() <= 0) {
            return targetH;
        }
        return targetH * (region.getRegionWidth() / (float) region.getRegionHeight());
    }

    private static void drawRegionFit(SpriteBatch batch, TextureRegion region, float x, float y,
            float targetH, float alpha) {
        if (region == null || region.getRegionHeight() <= 0) {
            return;
        }
        float w = scaledW(region, targetH);
        batch.setColor(1f, 1f, 1f, alpha);
        batch.draw(region, x, y, w, targetH);
        batch.setColor(Color.WHITE);
    }

    @Override
    public void dispose() {
        if (white.getTexture() != null) {
            white.getTexture().dispose();
        }
        font.dispose();
    }

    private static final class ActiveMessage {
        private final String playerName;
        private final QuickMessageId message;
        private float timeLeft;
        private float animTime;

        private ActiveMessage(String playerName, QuickMessageId message, float timeLeft) {
            this.playerName = playerName;
            this.message = message;
            this.timeLeft = timeLeft;
        }
    }
}
