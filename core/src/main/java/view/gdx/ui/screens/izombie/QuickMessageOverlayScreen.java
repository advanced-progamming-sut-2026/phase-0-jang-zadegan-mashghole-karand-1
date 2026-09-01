package view.gdx.ui.screens.izombie;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import controller.ControllerManager;
import model.rule.SessionConfig;
import model.rule.SessionContext;
import model.storage.user.User;
import network.NetworkSession;
import shared.izombie.MatchRole;
import shared.message.QuickMessageId;
import view.gdx.AssetContext;
import view.gdx.lawn.QuickMessageHud;
import view.gdx.ui.UiScreen;
import view.gdx.ui.UiViewContext;
import view.gdx.ui.widgets.PamPreviewActor;
import view.gdx.ui.widgets.UiWidgets;

public final class QuickMessageOverlayScreen implements UiScreen {
    private static final float TEXT_W = 96f;
    private static final float TEXT_H = 38f;
    private static final float ICON_CELL = 80f;
    private static final float CELL_PAD = 10f;
    private static final float EMOJI_H = 36f;
    private static final float ANIM_SCALE = 0.22f;

    private final Stage stage;
    private final Texture brownTexture;
    private final Texture panelTexture;
    private final QuickMessageHud quickMessageHud;

    private ControllerManager controller;
    private AssetContext assets;

    public QuickMessageOverlayScreen(QuickMessageHud quickMessageHud) {
        this.quickMessageHud = quickMessageHud;
        stage = new Stage(new ScreenViewport());
        brownTexture = makeRoundedRect(new Color(0.36f, 0.18f, 0.07f, 1f), 256, 256, 12);
        panelTexture = makeRoundedRect(new Color(0.75f, 0.55f, 0.30f, 1f), 256, 256, 8);
        rebuildGrid();
    }

    private void rebuildGrid() {
        stage.clear();

        Table panel = new Table();
        panel.setBackground(new TextureRegionDrawable(new TextureRegion(panelTexture)));
        panel.pad(24f);
        panel.add(UiWidgets.title("Quick Message")).padBottom(12f).colspan(3).row();

        int col = 0;
        for (QuickMessageId id : QuickMessageId.catalog()) {
            Actor cell = buildCell(id);
            if (id.kind == QuickMessageId.Kind.TEXT) {
                panel.add(cell).width(TEXT_W).height(TEXT_H).pad(CELL_PAD);
            } else {
                panel.add(cell).size(ICON_CELL).pad(CELL_PAD);
            }
            col++;
            if (col % 3 == 0) {
                panel.row();
            }
        }
        TextButton close = UiWidgets.plain("Close");
        UiWidgets.onChange(close, () -> {
            controller.clearCurrentMenu();
            controller.refreshView();
        });
        panel.row();
        panel.add(close).colspan(3).width(200f).height(40f).padTop(8f);

        Table brownOuter = new Table();
        brownOuter.setBackground(new TextureRegionDrawable(new TextureRegion(brownTexture)));
        brownOuter.setTouchable(Touchable.enabled);
        brownOuter.pad(16f);
        brownOuter.add(panel);

        Table root = new Table();
        root.setFillParent(true);
        root.center();
        root.add(brownOuter);
        stage.addActor(root);
    }

    private Actor buildCell(QuickMessageId id) {
        Table cell = new Table();
        cell.setTouchable(Touchable.enabled);
        cell.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                send(id);
            }
        });

        switch (id.kind) {
            case TEXT -> {
                TextButton button = UiWidgets.plain(id.display);
                button.setTouchable(Touchable.disabled);
                cell.add(button).width(TEXT_W - 8f).height(TEXT_H - 4f);
            }
            case EMOJI -> {
                TextureRegion region = assets != null ? assets.region(id.imageId) : null;
                if (region != null) {
                    Image image = new Image(new TextureRegionDrawable(region));
                    cell.add(image).height(EMOJI_H).width(scaledW(region, EMOJI_H));
                } else {
                    cell.add(UiWidgets.body("?"));
                }
            }
            case ANIMATED -> {
                if (assets != null) {
                    PamPreviewActor preview = new PamPreviewActor(assets, id.pamPath, id.pamClip, ANIM_SCALE);
                    cell.add(preview).size(ICON_CELL - 12f);
                } else {
                    cell.add(UiWidgets.body("..."));
                }
            }
            default -> cell.add(UiWidgets.body("?"));
        }
        return cell;
    }

    private void send(QuickMessageId id) {
        if (controller == null) {
            return;
        }
        NetworkSession net = controller.getNetworkSession();
        if (net != null) {
            net.socket().quickMessage(id.name());
        }
        MatchRole role = localRole();
        if (role != null && quickMessageHud != null) {
            quickMessageHud.show(role, localDisplayName(), id);
        }
        controller.clearCurrentMenu();
        controller.refreshView();
    }

    private MatchRole localRole() {
        SessionContext ctx = controller.getModel().getPlayContext();
        SessionConfig cfg = ctx != null ? ctx.getConfig() : null;
        return cfg != null ? cfg.localMatchRole : null;
    }

    private String localDisplayName() {
        User user = controller.getStorage().getCurrentUser();
        if (user == null) {
            return "You";
        }
        if (user.nickname != null && !user.nickname.isBlank()) {
            return user.nickname;
        }
        return user.username;
    }

    @Override
    public void show(UiViewContext context) {
        this.controller = context.controller;
        this.assets = context.assets;
        rebuildGrid();
    }

    @Override
    public void act(float deltaSeconds) {
        stage.act(deltaSeconds);
    }

    @Override
    public Stage stage() {
        return stage;
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        brownTexture.dispose();
        panelTexture.dispose();
    }

    private static float scaledW(TextureRegion region, float targetH) {
        if (region == null || region.getRegionHeight() <= 0) {
            return targetH;
        }
        return targetH * (region.getRegionWidth() / (float) region.getRegionHeight());
    }

    private static Texture makeRoundedRect(Color color, int w, int h, int radius) {
        Pixmap pixmap = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fillRectangle(radius, 0, w - 2 * radius, h);
        pixmap.fillRectangle(0, radius, w, h - 2 * radius);
        pixmap.fillCircle(radius, radius, radius);
        pixmap.fillCircle(w - radius - 1, radius, radius);
        pixmap.fillCircle(radius, h - radius - 1, radius);
        pixmap.fillCircle(w - radius - 1, h - radius - 1, radius);
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }
}
