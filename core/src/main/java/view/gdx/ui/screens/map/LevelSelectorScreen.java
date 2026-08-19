package view.gdx.ui.screens.map;

import com.badlogic.gdx.scenes.scene2d.Stage;
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

    private void buildChapterPhase(GameNavigationState nav) {
        root.pad(12f);
        ChapterWorldMap map = new ChapterWorldMap();
        map.bind(controller, assets, nav);
        activeMap = map;

        TextButton travelLog = UiWidgets.secondary("Travel Log");
        TextButton collection = UiWidgets.plain("Collection");
        TextButton greenhouse = UiWidgets.plain("Greenhouse");
        TextButton leaderboard = UiWidgets.plain("Leaderboard");
        TextButton back = UiWidgets.plain("Back");

        UiWidgets.onChange(travelLog, () -> UiWidgets.apply(controller, controller.enterMenu("travel-log")));
        UiWidgets.onChange(collection, () -> UiWidgets.apply(controller, controller.enterMenu("collection")));
        UiWidgets.onChange(greenhouse, () -> UiWidgets.apply(controller, controller.enterMenu("greenhouse")));
        UiWidgets.onChange(leaderboard, () -> UiWidgets.apply(controller, controller.enterMenu("leaderboard")));
        UiWidgets.onChange(back, () -> UiWidgets.apply(controller, controller.exitMenu()));

        Table chrome = new Table();
        chrome.add(travelLog).width(180f).height(40f).padRight(8f);
        chrome.add(collection).width(180f).height(40f).padRight(8f);
        chrome.add(greenhouse).width(180f).height(40f).padRight(8f);
        chrome.add(leaderboard).width(180f).height(40f).padRight(8f);
        chrome.add(back).width(140f).height(40f);

        root.add(UiWidgets.title("Select a World")).padBottom(8f).row();
        root.add(map).grow().padBottom(10f).row();
        root.add(chrome).bottom();
    }

    private void buildLevelPhase(GameNavigationState nav) {
        root.pad(12f);
        LevelWorldMap map = new LevelWorldMap();
        map.bind(controller, assets, nav);
        activeMap = map;

        TextButton back = UiWidgets.plain("Back");
        UiWidgets.onChange(back, () -> UiWidgets.apply(controller, controller.exitMenu()));

        String chapterName = ChapterCommands.displayName(nav.selectedChapter);
        root.add(UiWidgets.title(chapterName)).padBottom(8f).row();
        root.add(map).grow().padBottom(10f).row();
        root.add(back).width(160f).height(40f).bottom();
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

        TextButton back = UiWidgets.plain("Back");
        UiWidgets.onChange(back, () -> UiWidgets.apply(controller, controller.exitMenu()));

        root.add(list).padBottom(12f).row();
        root.add(back).width(420f).height(44f).row();
    }

    private void buildPlantPhase(GameNavigationState nav) {
        root.center();
        root.add(UiWidgets.title("Pick Plants")).padBottom(8f).row();
        root.add(UiWidgets.body("Selected " + nav.selectedPlants.size() + "/8")).padBottom(12f).row();

        Table selected = new Table();
        if (nav.selectedPlants.isEmpty()) {
            selected.add(UiWidgets.body("(none)")).row();
        } else {
            for (PlantType plant : nav.selectedPlants) {
                TextButton remove = UiWidgets.plain("Remove " + plant.name);
                UiWidgets.onChange(remove, () -> UiWidgets.apply(controller,
                        controller.getPickPlantsController().removePlant(plant)));
                selected.add(remove).width(420f).height(40f).padBottom(6f).row();
            }
        }

        Table unlocked = new Table();
        for (PlantType plant : nav.unlockedPlants) {
            if (nav.selectedPlants.contains(plant) || plant == PlantType.Imitater) {
                continue;
            }
            TextButton add = UiWidgets.secondary("Add " + plant.name);
            UiWidgets.onChange(add, () -> UiWidgets.apply(controller,
                    controller.getPickPlantsController().addPlant(plant, null)));
            unlocked.add(add).width(420f).height(40f).padBottom(6f).row();
        }

        TextButton start = UiWidgets.primary("Start Game");
        TextButton back = UiWidgets.plain("Back");
        UiWidgets.onChange(start, () -> UiWidgets.apply(controller,
                controller.getPickPlantsController().startGame()));
        UiWidgets.onChange(back, () -> UiWidgets.apply(controller, controller.exitMenu()));

        root.add(scroll(selected)).width(440f).height(140f).padBottom(8f).row();
        root.add(scroll(unlocked)).width(440f).height(180f).padBottom(12f).row();
        root.add(start).width(420f).height(48f).padBottom(8f).row();
        root.add(back).width(420f).height(44f).row();
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
