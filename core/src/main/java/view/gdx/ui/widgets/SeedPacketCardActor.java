package view.gdx.ui.widgets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import model.data.content.chapter.ChapterType;
import view.gdx.AssetContext;
import view.gdx.lawn.SeedPacketCardPainter;
import view.gdx.lawn.SeedPacketCardView;
import view.gdx.lawn.SeedPacketDefs;


public final class SeedPacketCardActor extends Group {
    private static final float DEFAULT_PACKET_H = 68f;
    private static final float FALLBACK_PACKET_ASPECT = 119f / 75f;

    private final SeedPacketCardPainter painter = new SeedPacketCardPainter();
    private final Image frameImage = new Image();
    private final Image plantImage = new Image();
    private final DecorationLayer decorations = new DecorationLayer();

    private AssetContext assets;
    private ChapterType chapter;
    private SeedPacketCardView card = SeedPacketCardView.empty();
    private Runnable clickAction;
    private float packetWidth;
    private float packetHeight;

    public SeedPacketCardActor(AssetContext assets, ChapterType chapter) {
        this(assets, chapter, DEFAULT_PACKET_H);
    }

    public SeedPacketCardActor(AssetContext assets, ChapterType chapter, float packetHeight) {
        this.assets = assets;
        this.chapter = chapter;
        addActor(frameImage);
        addActor(plantImage);
        addActor(decorations);
        resizePacket(packetHeight);
        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (clickAction != null) {
                    clickAction.run();
                }
            }
        });
    }

    public void bind(AssetContext assets, ChapterType chapter) {
        this.assets = assets;
        this.chapter = chapter;
        refreshImages();
    }

    public void setCard(SeedPacketCardView card) {
        this.card = card != null ? card : SeedPacketCardView.empty();
        refreshImages();
    }

    public void setClickAction(Runnable clickAction) {
        this.clickAction = clickAction;
    }

    public SeedPacketCardView card() {
        return card;
    }

    public void resizePacket(float packetHeight) {
        this.packetHeight = packetHeight;
        float aspect = FALLBACK_PACKET_ASPECT;
        if (assets != null) {
            TextureRegion frame = assets.region(SeedPacketDefs.worldBack(chapter));
            if (frame == null) {
                frame = assets.region(SeedPacketDefs.EMPTY);
            }
            aspect = SeedPacketCardPainter.aspectOf(frame, FALLBACK_PACKET_ASPECT);
        }
        this.packetWidth = packetHeight * aspect;
        setSize(packetWidth, packetHeight);
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        this.packetWidth = width;
        this.packetHeight = height;
        refreshImages();
    }

    public float getPrefWidth() {
        return packetWidth;
    }

    public float getPrefHeight() {
        return packetHeight;
    }

    public float getMinWidth() {
        return packetWidth;
    }

    public float getMinHeight() {
        return packetHeight;
    }

    private void refreshImages() {
        frameImage.setVisible(false);
        plantImage.setVisible(false);
        decorations.setSize(packetWidth, packetHeight);
        decorations.setPosition(0f, 0f);

        if (assets == null) {
            return;
        }

        if (card.isEmpty()) {
            showEmptyCard();
            return;
        }

        String packetId = SeedPacketDefs.packetId(card.plantName);
        TextureRegion plantRegion = assets.region(packetId);
        TextureRegion boostFrame = resolveBoostFrame();

        if (card.boosted && boostFrame != null) {
            showBoostedCard(boostFrame, plantRegion);
            return;
        }

        if (plantRegion != null) {
            showPlantOnlyCard(plantRegion);
            return;
        }

        showFrameFallbackCard();
    }

    private TextureRegion resolveBoostFrame() {
        TextureRegion boostFrame = assets.region(SeedPacketCardPainter.BOOST_FRAME);
        if (boostFrame == null) {
            boostFrame = assets.region(SeedPacketCardPainter.BOOST_FRAME_FALLBACK);
        }
        return boostFrame;
    }

    private void showEmptyCard() {
        setDrawable(frameImage, assets.region(SeedPacketDefs.EMPTY));
        frameImage.setSize(packetWidth, packetHeight);
        frameImage.setPosition(0f, 0f);
        frameImage.setVisible(true);
    }

    private void showBoostedCard(TextureRegion boostFrame, TextureRegion plantRegion) {
        setDrawable(frameImage, boostFrame);
        frameImage.setSize(packetWidth, packetHeight);
        frameImage.setPosition(0f, 0f);
        frameImage.setVisible(true);
        layoutPlantInset(plantRegion);
        plantImage.setVisible(plantRegion != null);
    }

    private void showPlantOnlyCard(TextureRegion plantRegion) {
        setDrawable(plantImage, plantRegion);
        plantImage.setSize(packetWidth, packetHeight);
        plantImage.setPosition(0f, 0f);
        plantImage.setColor(card.locked ? new Color(0.65f, 0.65f, 0.65f, 1f) : Color.WHITE);
        plantImage.setVisible(true);
    }

    private void showFrameFallbackCard() {
        TextureRegion frame = assets.region(SeedPacketDefs.worldBack(chapter));
        if (frame == null) {
            frame = assets.region(SeedPacketDefs.EMPTY);
        }
        setDrawable(frameImage, frame);
        frameImage.setSize(packetWidth, packetHeight);
        frameImage.setPosition(0f, 0f);
        frameImage.setVisible(frame != null);
    }

    private void layoutPlantInset(TextureRegion plantRegion) {
        if (plantRegion == null) {
            plantImage.setVisible(false);
            return;
        }
        float insetX = packetWidth * 0.16f;
        float insetY = packetHeight * 0.22f;
        float w = packetWidth - insetX * 2f;
        float h = packetHeight - insetY * 1.6f;
        setDrawable(plantImage, plantRegion);
        plantImage.setSize(w, h);
        plantImage.setPosition(insetX, insetY);
        plantImage.setColor(card.locked ? new Color(0.65f, 0.65f, 0.65f, 1f) : Color.WHITE);
        plantImage.setVisible(true);
    }

    private static void setDrawable(Image image, TextureRegion region) {
        if (region != null) {
            image.setDrawable(new TextureRegionDrawable(region));
        } else {
            image.setDrawable(null);
        }
    }

    private final class DecorationLayer extends com.badlogic.gdx.scenes.scene2d.ui.Widget {
        @Override
        public void draw(Batch batch, float parentAlpha) {
            if (assets == null || card.isEmpty() || parentAlpha <= 0f
                    || !(batch instanceof SpriteBatch spriteBatch)) {
                return;
            }
            Color color = getColor();
            batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
            batch.flush();
            painter.drawDecorations(spriteBatch, assets, card, 0f, 0f, getWidth(), getHeight());
            batch.setColor(Color.WHITE);
        }
    }
}
