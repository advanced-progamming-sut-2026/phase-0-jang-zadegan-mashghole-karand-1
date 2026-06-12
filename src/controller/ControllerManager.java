package controller;

import model.ModelManager;
import model.core.EventBus;
import model.core.GameLoop;
import view.ViewManager;

public class ControllerManager {
    private ModelManager model;
    private ViewManager view;
    private EventBus eventBus;
    private GameLoop gameLoop;
    private InputHandler inputHandler;

    public ControllerManager(ModelManager model, ViewManager view,
            EventBus eventBus, GameLoop gameLoop) {
        this.model = model;
        this.view = view;
        this.eventBus = eventBus;
        this.gameLoop = gameLoop;
        this.inputHandler = new InputHandler(model, view);

        setupEventSubscriptions();
    }

    private void setupEventSubscriptions() {
        // eventBus.subscribe(SunChangedEvent.class, e -> {
        // view.updateSunDisplay(model.getSunAmount());
        // });

        // eventBus.subscribe(GameOverEvent.class, e -> {
        // view.showGameOverScreen(e.won);
        // });

        // eventBus.subscribe(LevelCompleteEvent.class, e -> {
        // view.showLevelCompleteScreen(model.getCurrentWave());
        // });

        // eventBus.subscribe(ZombieDiedEvent.class, e -> {
        // view.addKillEffect(e.zombie);
        // });
    }

    public void start() {
        // view.showMainMenu();
    }
}