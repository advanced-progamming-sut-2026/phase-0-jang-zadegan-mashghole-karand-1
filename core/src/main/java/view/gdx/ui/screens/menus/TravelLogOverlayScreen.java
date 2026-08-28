package view.gdx.ui.screens.menus;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import controller.ControllerManager;
import model.service.QuestViewState;
import model.service.QuestViewState.Entry;
import pvz.skin.PvzSkin;
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
            {"active", "Active"},
            {"completed", "Completed"},
    };

    private final Stage stage;
    private final Table root;
    private final Table questList;
    private final Map<String, TextButton> tabButtons = new LinkedHashMap<>();

    private ControllerManager controller;

    public TravelLogOverlayScreen() {
        stage = new Stage(new ScreenViewport());

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
            tabRow.add(btn).height(36f).padRight(4f);
        }

        ScrollPane tabScroll = new ScrollPane(tabRow, UiSkin.get());
        tabScroll.setFadeScrollBars(false);
        tabScroll.setScrollingDisabled(false, true);
        tabScroll.setScrollbarsVisible(true);
        panel.add(tabScroll).width(660f).height(44f).padBottom(8f).row();

        questList = new Table();
        questList.top().left();

        ScrollPane questScroll = scroll(questList);
        questScroll.setScrollingDisabled(true, false);
        questScroll.setForceScroll(true, false);
        panel.add(questScroll).width(660f).height(520f).pad(8f).row();

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
        card.pad(10f);

        String status = entry.completed
                ? "Done"
                : entry.progress + "/" + entry.target;

        Label title = UiWidgets.body(entry.name + "  (" + status + ")  " + entry.rewardLabel);
        title.setAlignment(Align.left);
        title.setWrap(true);
        if (entry.completed) {
            title.setColor(0.55f, 0.55f, 0.55f, 1f);
        }

        Label description = UiWidgets.body(entry.description);
        description.setAlignment(Align.left);
        description.setWrap(true);
        if (entry.completed) {
            description.setColor(0.55f, 0.55f, 0.55f, 1f);
        }

        card.add(title).growX().padBottom(4f).row();
        card.add(description).growX().row();
        return card;
    }

    private static ScrollPane scroll(Table table) {
        ScrollPane pane = new ScrollPane(table, UiSkin.get());
        pane.setFadeScrollBars(false);
        pane.setScrollbarsVisible(true);
        pane.setOverscroll(false, false);
        return pane;
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
    }

    @Override
    public Stage stage() {
        return stage;
    }
}
