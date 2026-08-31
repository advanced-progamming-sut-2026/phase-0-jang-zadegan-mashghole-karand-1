package view.gdx.ui.screens.menus;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import controller.ControllerManager;
import model.quest.QuestPriority;
import model.service.QuestViewState;
import model.service.QuestViewState.Entry;
import model.service.QuestViewState.RewardKind;
import pvz.skin.PvzSkin;
import view.gdx.AssetContext;
import view.gdx.ui.UiScreen;
import view.gdx.ui.UiSkin;
import view.gdx.ui.UiViewContext;
import view.gdx.ui.widgets.UiWidgets;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class TravelLogOverlayScreen implements UiScreen {

    private static final String[][] TABS = {
            {"all", "All"},
            {"critical", "Critical"},
            {"high", "High"},
            {"daily", "Daily"},
            {"main", "Main"},
            {"epic", "Epic"},
            {"active", "Active"},
            {"completed", "Completed"},
    };

    private static final String COIN_ICON = "IMAGE_EFFECTS_COIN_GOLD_COIN_GOLD_98X95";
    private static final String GEM_ICON = "IMAGE_EFFECTS_COIN_DIAMOND_COIN_DIAMOND_141X146";
    private static final String PACK_ICON = "IMAGE_UI_PACKETS_EMPTY_PACKET";

    private static final Color CARD_BG = new Color(0.82f, 0.62f, 0.36f, 1f);
    private static final Color TRACK = new Color(0.28f, 0.14f, 0.06f, 1f);
    private static final Color FILL = new Color(0.42f, 0.72f, 0.22f, 1f);
    private static final Color FILL_DONE = new Color(0.55f, 0.55f, 0.55f, 1f);
    private static final Color MUTED = new Color(0.55f, 0.55f, 0.55f, 1f);

    private final Stage stage;
    private final Table root;
    private final Table questList;
    private final Map<String, TextButton> tabButtons = new LinkedHashMap<>();

    private final Texture cardTexture;
    private final Texture trackTexture;
    private final Texture fillTexture;
    private final Texture fillDoneTexture;
    private final TextureRegionDrawable cardBg;
    private final TextureRegionDrawable trackBg;
    private final TextureRegionDrawable fillBg;
    private final TextureRegionDrawable fillDoneBg;

    private ControllerManager controller;
    private AssetContext assets;

    public TravelLogOverlayScreen() {
        stage = new Stage(new ScreenViewport());

        cardTexture = solid(CARD_BG);
        trackTexture = solid(TRACK);
        fillTexture = solid(FILL);
        fillDoneTexture = solid(FILL_DONE);
        cardBg = new TextureRegionDrawable(new TextureRegion(cardTexture));
        trackBg = new TextureRegionDrawable(new TextureRegion(trackTexture));
        fillBg = new TextureRegionDrawable(new TextureRegion(fillTexture));
        fillDoneBg = new TextureRegionDrawable(new TextureRegion(fillDoneTexture));

        root = new Table();
        root.setFillParent(true);

        Table panel = new Table();
        panel.setBackground(
                PvzSkin.get().getDrawable("image_ui_quests_panel_edge_to_edge_ten")
        );

        Label title = UiWidgets.title("Travel Log");
        title.setAlignment(Align.center);
        panel.add(title).growX().padTop(12f).padBottom(8f).row();

        Table tabRow = new Table();
        for (String[] tab : TABS) {
            String filterKey = tab[0];
            String label = tab[1];
            TextButton btn = UiWidgets.plain(label);
            tabButtons.put(filterKey, btn);
            UiWidgets.onChange(btn, () -> {
                if (controller != null) {
                    UiWidgets.apply(controller, controller.getQuestMenuController().enterPage(filterKey));
                }
            });
            tabRow.add(btn).minWidth(88f).height(36f).padRight(4f);
        }

        ScrollPane tabScroll = new ScrollPane(tabRow, UiSkin.get());
        tabScroll.setFadeScrollBars(false);
        tabScroll.setScrollingDisabled(false, true);
        tabScroll.setScrollbarsVisible(true);
        panel.add(tabScroll).width(660f).height(48f).padBottom(8f).row();

        questList = new Table();
        questList.top().left();

        ScrollPane questScroll = scroll(questList);
        questScroll.setScrollingDisabled(true, false);
        questScroll.setForceScroll(true, false);
        panel.add(questScroll).width(660f).height(500f).pad(8f).row();

        TextButton minigames = UiWidgets.secondary("Minigames");
        TextButton close = UiWidgets.plain("Close");
        UiWidgets.onChange(minigames, () -> {
            if (controller != null) {
                UiWidgets.apply(controller, controller.enterMenu("minigames"));
            }
        });
        UiWidgets.onChange(close, () -> {
            if (controller != null) {
                UiWidgets.apply(controller, controller.exitMenu());
            }
        });

        Table footer = new Table();
        footer.add(minigames).width(160f).height(40f).padRight(12f);
        footer.add(close).width(160f).height(40f);
        panel.add(footer).padTop(12f).padBottom(15f).row();

        root.add(panel).width(700f).height(780f).center().top();
        stage.addActor(root);
    }

    @Override
    public void show(UiViewContext context) {
        controller = context.controller;
        assets = context.assets;
        QuestViewState quests = context.quests != null ? context.quests : QuestViewState.empty();
        updateTabs(quests.filter);
        rebuildQuestList(quests);
    }

    private void updateTabs(String activeFilter) {
        String normalized = activeFilter == null ? "all" : activeFilter.trim().toLowerCase();
        var activeStyle = UiSkin.get().get("green", TextButton.TextButtonStyle.class);
        var inactiveStyle = UiSkin.get().get("default", TextButton.TextButtonStyle.class);
        for (Map.Entry<String, TextButton> entry : tabButtons.entrySet()) {
            entry.getValue().setStyle(entry.getKey().equals(normalized) ? activeStyle : inactiveStyle);
        }
    }

    private void rebuildQuestList(QuestViewState quests) {
        questList.clearChildren();

        if (quests.isEmpty()) {
            Label empty = UiWidgets.body("No quests to show.");
            empty.setAlignment(Align.center);
            questList.add(empty).width(640f).padTop(24f).row();
            return;
        }

        addSection("CRITICAL — story / unlock progress", quests.critical);
        addSection("HIGH — Epic challenges (Gem rewards)", quests.high);
        addSection("MEDIUM / LOW — daily & repeatable", quests.mediumAndLow);
    }

    private void addSection(String header, List<Entry> entries) {
        if (entries == null || entries.isEmpty()) {
            return;
        }

        Label sectionTitle = UiWidgets.body(header);
        sectionTitle.setAlignment(Align.left);
        questList.add(sectionTitle).width(640f).padTop(8f).padBottom(6f).row();

        for (Entry entry : entries) {
            questList.add(buildQuestCard(entry)).width(640f).padBottom(8f).row();
        }
    }

    private Table buildQuestCard(Entry entry) {
        Table card = new Table();
        card.setBackground(cardBg);
        card.pad(10f, 12f, 10f, 12f);

        Label name = UiWidgets.body(entry.name);
        name.setWrap(true);
        name.setAlignment(Align.left);

        String statusText = entry.completed ? "Done" : entry.progress + " / " + entry.target;
        Label status = UiWidgets.body(statusText);
        status.setAlignment(Align.right);

        Label badges = UiWidgets.body(entry.priority.name() + "  ·  " + entry.category.name());
        badges.setColor(priorityColor(entry.priority));
        badges.setAlignment(Align.left);

        Label description = UiWidgets.body(entry.description);
        description.setWrap(true);
        description.setAlignment(Align.left);

        if (entry.completed) {
            name.setColor(MUTED);
            status.setColor(MUTED);
            description.setColor(MUTED);
            badges.setColor(MUTED);
        }

        Table header = new Table();
        header.add(name).growX().left();
        header.add(status).right().padLeft(8f);

        card.add(header).growX().padBottom(2f).row();
        card.add(badges).left().padBottom(4f).row();
        card.add(description).growX().left().padBottom(6f).row();

        ProgressBarActor bar = new ProgressBarActor(trackBg, entry.completed ? fillDoneBg : fillBg);
        bar.setRatio(progressRatio(entry));
        card.add(bar).growX().height(10f).padBottom(8f).row();

        card.add(buildRewardRow(entry)).growX().left();
        return card;
    }

    private Table buildRewardRow(Entry entry) {
        Table row = new Table();
        Image icon = rewardIcon(entry);
        if (icon != null) {
            row.add(icon).size(22f).padRight(6f);
        }
        Label reward = UiWidgets.body("Reward: " + entry.rewardLabel);
        if (entry.completed) {
            reward.setColor(MUTED);
        }
        row.add(reward).left();
        return row;
    }

    private Image rewardIcon(Entry entry) {
        String id = iconId(entry);
        if (id == null || assets == null) {
            return null;
        }
        TextureRegion region = assets.region(id);
        if (region == null) {
            return null;
        }
        Image image = new Image(new TextureRegionDrawable(region));
        image.setSize(22f, 22f);
        return image;
    }

    private static String iconId(Entry entry) {
        if (entry.rewardKind == RewardKind.UNLOCKABLE) {
            return PACK_ICON;
        }
        if (entry.rewardKind == RewardKind.INVENTORY) {
            return PACK_ICON;
        }
        String label = entry.rewardLabel == null ? "" : entry.rewardLabel.toLowerCase();
        if (label.contains("gem")) {
            return GEM_ICON;
        }
        return COIN_ICON;
    }

    private static float progressRatio(Entry entry) {
        if (entry.completed) {
            return 1f;
        }
        if (entry.target <= 0) {
            return 0f;
        }
        return Math.max(0f, Math.min(1f, entry.progress / (float) entry.target));
    }

    private static Color priorityColor(QuestPriority priority) {
        return switch (priority) {
            case CRITICAL -> new Color(0.90f, 0.28f, 0.22f, 1f);
            case HIGH -> new Color(0.95f, 0.72f, 0.18f, 1f);
            case MEDIUM -> new Color(0.30f, 0.72f, 0.82f, 1f);
            case LOW -> new Color(0.50f, 0.70f, 0.45f, 1f);
        };
    }

    private static ScrollPane scroll(Table table) {
        ScrollPane pane = new ScrollPane(table, UiSkin.get());
        pane.setFadeScrollBars(false);
        pane.setScrollbarsVisible(true);
        pane.setOverscroll(false, false);
        return pane;
    }

    private static Texture solid(Color color) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    @Override
    public void act(float deltaSeconds) {
        stage.act(deltaSeconds);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        cardTexture.dispose();
        trackTexture.dispose();
        fillTexture.dispose();
        fillDoneTexture.dispose();
    }

    @Override
    public Stage stage() {
        return stage;
    }

    private static final class ProgressBarActor extends Actor {
        private final TextureRegionDrawable track;
        private final TextureRegionDrawable fill;
        private float ratio;

        ProgressBarActor(TextureRegionDrawable track, TextureRegionDrawable fill) {
            this.track = track;
            this.fill = fill;
        }

        void setRatio(float ratio) {
            this.ratio = Math.max(0f, Math.min(1f, ratio));
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            Color c = getColor();
            batch.setColor(c.r, c.g, c.b, c.a * parentAlpha);
            track.draw(batch, getX(), getY(), getWidth(), getHeight());
            float width = getWidth() * ratio;
            if (width >= 1f) {
                fill.draw(batch, getX(), getY(), width, getHeight());
            }
        }
    }
}
