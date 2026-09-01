package view.gdx.ui.screens.menus;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.LinkedHashMap;
import java.util.Map;

import controller.ControllerManager;
import model.service.LeaderboardViewState;
import model.service.LeaderboardViewState.Entry;
import model.service.LeaderboardViewState.SortColumn;
import model.service.LeaderboardViewState.SortDirection;
import pvz.skin.PvzSkin;
import view.gdx.ui.UiScreen;
import view.gdx.ui.UiSkin;
import view.gdx.ui.UiViewContext;
import view.gdx.ui.widgets.UiWidgets;

public final class LeaderboardOverlayScreen implements UiScreen {

    private static final SortColumn[] SORT_COLUMNS = {
            SortColumn.SCORE,
            SortColumn.LEVELS,
            SortColumn.MINIGAMES,
    };

    private final Stage stage;
    private final Table root;
    private final Table entryList;
    private final Label sortInfo;
    private final Map<SortColumn, TextButton> sortButtons = new LinkedHashMap<>();

    private TextButton directionButton;
    private ControllerManager controller;

    public LeaderboardOverlayScreen() {
        stage = new Stage(new ScreenViewport());
        entryList = new Table();
        entryList.top().left();
        sortInfo = UiWidgets.body("");
        root = new Table();
        root.setFillParent(true);
        root.add(buildPanel()).width(700f).height(620f).center();
        stage.addActor(root);
    }

    private Table buildPanel() {
        Table panel = new Table();
        panel.setBackground(PvzSkin.get().getDrawable("image_ui_quests_panel_edge_to_edge_ten"));

        Label title = UiWidgets.title("Leaderboard");
        title.setAlignment(Align.center);
        panel.add(title).growX().padTop(12f).padBottom(8f).row();
        panel.add(buildSortBar()).width(660f).padBottom(6f).row();
        panel.add(sortInfo).width(660f).padBottom(8f).row();

        ScrollPane scroll = new ScrollPane(entryList, UiSkin.get());
        scroll.setFadeScrollBars(false);
        scroll.setScrollingDisabled(true, false);
        scroll.setForceScroll(true, false);
        panel.add(scroll).width(660f).height(420f).pad(8f).row();
        panel.add(buildFooter()).padTop(12f).padBottom(15f).row();
        return panel;
    }

    private Table buildSortBar() {
        Table bar = new Table();
        bar.add(UiWidgets.body("Sort by:")).padRight(8f);
        for (SortColumn column : SORT_COLUMNS) {
            TextButton button = UiWidgets.plain(labelFor(column));
            sortButtons.put(column, button);
            UiWidgets.onChange(button, () -> {
                if (controller != null) {
                    UiWidgets.apply(controller, controller.getLeaderboardMenuController().sortBy(column));
                }
            });
            bar.add(button).minWidth(100f).height(36f).padRight(4f);
        }
        directionButton = UiWidgets.plain("High to Low");
        UiWidgets.onChange(directionButton, () -> {
            if (controller != null) {
                UiWidgets.apply(controller, controller.getLeaderboardMenuController().toggleSortDirection());
            }
        });
        bar.add(directionButton).minWidth(120f).height(36f).padLeft(8f);
        return bar;
    }

    private Table buildFooter() {
        TextButton close = UiWidgets.plain("Close");
        UiWidgets.onChange(close, () -> {
            if (controller != null) {
                UiWidgets.apply(controller, controller.exitMenu());
            }
        });
        Table footer = new Table();
        footer.add(close).width(160f).height(40f);
        return footer;
    }

    @Override
    public void show(UiViewContext context) {
        controller = context.controller;
        LeaderboardViewState leaderboard = context.leaderboard != null
                ? context.leaderboard
                : LeaderboardViewState.empty();
        updateSortControls(leaderboard);
        rebuildEntryList(leaderboard);
    }

    private void updateSortControls(LeaderboardViewState leaderboard) {
        SortColumn active = leaderboard.sortColumn;
        var activeStyle = UiSkin.get().get("green", TextButton.TextButtonStyle.class);
        var inactiveStyle = UiSkin.get().get("default", TextButton.TextButtonStyle.class);
        for (Map.Entry<SortColumn, TextButton> entry : sortButtons.entrySet()) {
            entry.getValue().setStyle(entry.getKey() == active ? activeStyle : inactiveStyle);
        }
        directionButton.setText(leaderboard.sortDirection == SortDirection.HTL ? "High to Low" : "Low to High");
        sortInfo.setText("Sorted by " + labelFor(active) + " ("
                + (leaderboard.sortDirection == SortDirection.HTL ? "highest first" : "lowest first") + ")");
    }

    private void rebuildEntryList(LeaderboardViewState leaderboard) {
        entryList.clearChildren();
        if (leaderboard.entries.isEmpty()) {
            Label empty = UiWidgets.body("No players to show.");
            empty.setAlignment(Align.center);
            entryList.add(empty).width(640f).padTop(24f).row();
            return;
        }

        Table header = new Table();
        header.add(UiWidgets.body("Rank")).width(48f).padRight(8f);
        header.add(UiWidgets.body("User")).width(140f).padRight(8f);
        header.add(UiWidgets.body("Progress")).width(150f).padRight(8f);
        header.add(UiWidgets.body("Score")).width(72f).padRight(8f);
        header.add(UiWidgets.body("Minigames")).width(80f);
        entryList.add(header).width(640f).padBottom(6f).row();

        for (Entry entry : leaderboard.entries) {
            entryList.add(buildEntryRow(entry)).width(640f).padBottom(4f).row();
        }
    }

    private Table buildEntryRow(Entry entry) {
        String progress = "Ch " + entry.chapter + " Lv " + entry.level;
        Table row = new Table();
        row.add(UiWidgets.body(String.valueOf(entry.rank))).width(48f).padRight(8f);
        row.add(UiWidgets.body(entry.username)).width(140f).padRight(8f);
        row.add(UiWidgets.body(progress)).width(150f).padRight(8f);
        row.add(UiWidgets.body(String.valueOf(entry.score))).width(72f).padRight(8f);
        row.add(UiWidgets.body(String.valueOf(entry.minigames))).width(80f);
        return row;
    }

    private static String labelFor(SortColumn column) {
        return switch (column) {
            case SCORE -> "Score";
            case LEVELS -> "Levels";
            case MINIGAMES -> "Minigames";
        };
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
