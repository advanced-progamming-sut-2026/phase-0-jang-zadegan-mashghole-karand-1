package view.gdx.ui.screens.map;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import controller.ChapterCommands;
import controller.ControllerManager;
import controller.MiniGameCommands;
import model.data.content.minigame.MiniGameCatalog;
import model.data.content.minigame.MiniGameType;
import model.data.plant.PlantType;
import model.service.GameNavigationState;
import model.service.GameNavigationState.Phase;
import view.gdx.AssetContext;
import view.gdx.ui.UiScreen;
import view.gdx.ui.UiSkin;
import view.gdx.ui.UiViewContext;
import view.gdx.ui.widgets.UiWidgets;

public final class LevelSelectorScreen implements UiScreen {
    private final Stage stage;
    private final Table root;
    private ControllerManager controller;
    private AssetContext assets;
    private WorldMapPane activeMap;
    private PlantType plantSelectionFocus;

    public LevelSelectorScreen() {
        stage = new Stage(new ScreenViewport());
        root = new Table();
        root.setFillParent(true);
        stage.addActor(root);
    }

    @Override
    public void show(UiViewContext context) {
        this.controller = context.controller;
        this.assets = context.assets;
        if (activeMap != null) {
            activeMap.clear();
            activeMap = null;
        }
        root.clearChildren();
        GameNavigationState nav = context.gameNavigation;
        if (nav != null && nav.phase != Phase.PLANT) {
            plantSelectionFocus = null;
        }
        if (nav == null || nav.phase == Phase.CHAPTER || nav.phase == Phase.NONE) {
            buildChapterPhase(nav);
        } else if (nav.phase == Phase.LEVEL) {
            buildLevelPhase(nav);
        } else if (nav.phase == Phase.MINIGAME) {
            buildMinigamePhase(nav);
        } else if (nav.phase == Phase.PLANT) {
            buildPlantPhase(nav);
        }
    }

    private void buildPlantPhase(GameNavigationState nav) {
        if (plantSelectionFocus != null && !nav.unlockedPlants.contains(plantSelectionFocus)
                && !nav.selectedPlants.contains(plantSelectionFocus)) {
            plantSelectionFocus = null;
        }
        PlantSelectionPanel.build(root, controller, assets, nav, plantSelectionFocus,
                focus -> plantSelectionFocus = focus);
    }

    private void buildChapterPhase(GameNavigationState nav) {
        root.pad(12f);
        ChapterWorldMap map = new ChapterWorldMap();
        map.bind(controller, assets, nav);
        activeMap = map;

        ImageButton travelLog = new ImageButton(UiSkin.get(), "hud_quests");
        TextButton collection = UiWidgets.plain("Collection");
        TextButton greenhouse = UiWidgets.plain("Greenhouse");
        TextButton leaderboard = UiWidgets.plain("Leaderboard");

        UiWidgets.onChange(travelLog, () -> UiWidgets.apply(controller, controller.enterMenu("travel-log")));
        UiWidgets.onChange(collection, () -> UiWidgets.apply(controller, controller.enterMenu("collection")));
        UiWidgets.onChange(greenhouse, () -> UiWidgets.apply(controller, controller.enterMenu("greenhouse")));
        UiWidgets.onChange(leaderboard, () -> UiWidgets.apply(controller, controller.enterMenu("leaderboard")));

        Table chrome = new Table();
        chrome.add(travelLog).size(72f).padRight(8f);
        chrome.add(collection).width(180f).height(40f).padRight(8f);
        chrome.add(greenhouse).width(180f).height(40f).padRight(8f);
        chrome.add(leaderboard).width(180f).height(40f).padRight(8f);

        root.add(UiWidgets.title("Select a World")).padBottom(8f).row();
        root.add(map).grow().padBottom(10f).row();
        root.add(chrome).bottom();
    }

    private void buildLevelPhase(GameNavigationState nav) {
        root.pad(12f);
        LevelWorldMap map = new LevelWorldMap();
        map.bind(controller, assets, nav);
        activeMap = map;


        String chapterName = ChapterCommands.displayName(nav.selectedChapter);
        root.add(UiWidgets.title(chapterName)).padBottom(8f).row();
        root.add(map).grow().padBottom(10f).row();
    }

    private void buildMinigamePhase(GameNavigationState nav) {
        root.center();
        root.add(UiWidgets.title("Minigames")).padBottom(18f).row();

        Table list = new Table();
        for (MiniGameType type : MiniGameType.values()) {
            boolean unlocked = nav.unlockedMinigames.contains(type);
            boolean playable = MiniGameCatalog.isPlayable(type);
            TextButton button = UiWidgets.primary(MiniGameCommands.displayName(type));
            button.setDisabled(!unlocked || !playable);
            UiWidgets.onChange(button, () -> UiWidgets.apply(controller,
                    controller.getGameMenuController().selectMinigame(MiniGameCommands.commandName(type))));
            list.add(button).width(420f).height(44f).padBottom(8f).row();
        }


        root.add(list).padBottom(12f).row();
    }

    private ScrollPane scroll(Table table) {
        ScrollPane pane = new ScrollPane(table, UiSkin.get());
        pane.setFadeScrollBars(false);
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
        if (activeMap != null) {
            activeMap.clear();
            activeMap = null;
        }
        stage.dispose();
    }

    @Override
    public Stage stage() {
        return stage;
    }
}
