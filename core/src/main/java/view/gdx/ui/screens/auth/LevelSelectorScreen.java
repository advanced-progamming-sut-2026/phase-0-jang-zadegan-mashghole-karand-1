package view.gdx.ui.screens.auth;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import controller.ChapterCommands;
import controller.ControllerManager;
import controller.MiniGameCommands;
import model.data.content.chapter.ChapterCatalog;
import model.data.content.chapter.ChapterType;
import model.data.content.minigame.MiniGameCatalog;
import model.data.content.minigame.MiniGameType;
import model.data.plant.PlantType;
import model.service.GameNavigationState;
import model.service.GameNavigationState.Phase;
import view.gdx.ui.UiScreen;
import view.gdx.ui.UiSkin;
import view.gdx.ui.UiViewContext;

public final class LevelSelectorScreen implements UiScreen {
    private final Stage stage;
    private final Table root;
    private ControllerManager controller;

    public LevelSelectorScreen() {
        stage = new Stage(new ScreenViewport());
        root = new Table();
        root.setFillParent(true);
        root.center();
        stage.addActor(root);
    }

    @Override
    public void show(UiViewContext context) {
        this.controller = context.controller;
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
        root.add(AuthWidgets.title("Chapters")).padBottom(18f).row();

        Table list = new Table();
        for (ChapterType chapter : ChapterType.values()) {
            boolean unlocked = nav != null && nav.unlockedChapters.contains(chapter);
            TextButton button = AuthWidgets.primary(ChapterCommands.displayName(chapter));
            button.setDisabled(!unlocked);
            AuthWidgets.onChange(button, () -> AuthWidgets.apply(controller,
                    controller.getGameMenuController().enterChapter(ChapterCommands.commandName(chapter))));
            list.add(button).width(420f).height(44f).padBottom(8f).row();
        }

        TextButton travelLog = AuthWidgets.secondary("Travel Log");
        TextButton collection = AuthWidgets.plain("Collection");
        TextButton greenhouse = AuthWidgets.plain("Greenhouse");
        TextButton leaderboard = AuthWidgets.plain("Leaderboard");
        TextButton back = AuthWidgets.plain("Back");

        AuthWidgets.onChange(travelLog, () -> AuthWidgets.apply(controller, controller.enterMenu("travel-log")));
        AuthWidgets.onChange(collection, () -> AuthWidgets.apply(controller, controller.enterMenu("collection")));
        AuthWidgets.onChange(greenhouse, () -> AuthWidgets.apply(controller, controller.enterMenu("greenhouse")));
        AuthWidgets.onChange(leaderboard, () -> AuthWidgets.apply(controller, controller.enterMenu("leaderboard")));
        AuthWidgets.onChange(back, () -> AuthWidgets.apply(controller, controller.exitMenu()));

        root.add(scroll(list)).width(440f).height(240f).padBottom(12f).row();
        root.add(travelLog).width(420f).height(44f).padBottom(8f).row();
        root.add(collection).width(420f).height(44f).padBottom(8f).row();
        root.add(greenhouse).width(420f).height(44f).padBottom(8f).row();
        root.add(leaderboard).width(420f).height(44f).padBottom(8f).row();
        root.add(back).width(420f).height(44f).padBottom(8f).row();

    }

    private void buildLevelPhase(GameNavigationState nav) {
        String chapterName = ChapterCommands.displayName(nav.selectedChapter);
        root.add(AuthWidgets.title(chapterName)).padBottom(18f).row();

        Table list = new Table();
        for (int i = 1; i <= ChapterCatalog.LEVELS_PER_CHAPTER; i++) {
            boolean unlocked = nav.isLevelUnlocked(nav.selectedChapter, i);
            int highScore = nav.getLevelHighScore(nav.selectedChapter, i);
            String label = "Level " + i;
            if (highScore > 0) {
                label += "  (high " + highScore + ")";
            }
            TextButton button = AuthWidgets.primary(label);
            button.setDisabled(!unlocked);
            final int levelNumber = i;
            AuthWidgets.onChange(button, () -> AuthWidgets.apply(controller,
                    controller.getGameMenuController().selectLevel(levelNumber)));
            list.add(button).width(420f).height(44f).padBottom(8f).row();
        }

        TextButton back = AuthWidgets.plain("Back");
        AuthWidgets.onChange(back, () -> AuthWidgets.apply(controller, controller.exitMenu()));

        root.add(list).padBottom(12f).row();
        root.add(back).width(420f).height(44f).row();
    }

    private void buildMinigamePhase(GameNavigationState nav) {
        root.add(AuthWidgets.title("Minigames")).padBottom(18f).row();

        Table list = new Table();
        for (MiniGameType type : MiniGameType.values()) {
            boolean unlocked = nav.unlockedMinigames.contains(type);
            boolean playable = MiniGameCatalog.isPlayable(type);
            TextButton button = AuthWidgets.primary(MiniGameCommands.displayName(type));
            button.setDisabled(!unlocked || !playable);
            AuthWidgets.onChange(button, () -> AuthWidgets.apply(controller,
                    controller.getGameMenuController().selectMinigame(MiniGameCommands.commandName(type))));
            list.add(button).width(420f).height(44f).padBottom(8f).row();
        }

        TextButton back = AuthWidgets.plain("Back");
        AuthWidgets.onChange(back, () -> AuthWidgets.apply(controller, controller.exitMenu()));

        root.add(list).padBottom(12f).row();
        root.add(back).width(420f).height(44f).row();
    }

    private void buildPlantPhase(GameNavigationState nav) {
        root.add(AuthWidgets.title("Pick Plants")).padBottom(8f).row();
        root.add(AuthWidgets.body("Selected " + nav.selectedPlants.size() + "/8")).padBottom(12f).row();

        Table selected = new Table();
        if (nav.selectedPlants.isEmpty()) {
            selected.add(AuthWidgets.body("(none)")).row();
        } else {
            for (PlantType plant : nav.selectedPlants) {
                TextButton remove = AuthWidgets.plain("Remove " + plant.name);
                AuthWidgets.onChange(remove, () -> AuthWidgets.apply(controller,
                        controller.getPickPlantsController().removePlant(plant)));
                selected.add(remove).width(420f).height(40f).padBottom(6f).row();
            }
        }

        Table unlocked = new Table();
        for (PlantType plant : nav.unlockedPlants) {
            if (nav.selectedPlants.contains(plant) || plant == PlantType.Imitater) {
                continue;
            }
            TextButton add = AuthWidgets.secondary("Add " + plant.name);
            AuthWidgets.onChange(add, () -> AuthWidgets.apply(controller,
                    controller.getPickPlantsController().addPlant(plant, null)));
            unlocked.add(add).width(420f).height(40f).padBottom(6f).row();
        }

        TextButton start = AuthWidgets.primary("Start Game");
        TextButton back = AuthWidgets.plain("Back");
        AuthWidgets.onChange(start, () -> AuthWidgets.apply(controller,
                controller.getPickPlantsController().startGame()));
        AuthWidgets.onChange(back, () -> AuthWidgets.apply(controller, controller.exitMenu()));

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
        stage.dispose();
    }

    @Override
    public Stage stage() {
        return stage;
    }
}