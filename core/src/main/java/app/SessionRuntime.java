package app;

import com.badlogic.gdx.Gdx;

import controller.ControllerManager;
import model.ModelManager;
import model.core.GameLoop;
import model.core.ReadOnlyGameState;
import model.storage.SqlStorageManager;

public final class SessionRuntime {
    private final ModelManager model;
    private final ControllerManager controller;
    private final GameLoop gameLoop;
    private final SqlStorageManager storage;

    private float tickAccumulator;

    public SessionRuntime(ModelManager model, ControllerManager controller, GameLoop gameLoop,
            SqlStorageManager storage) {
        this.model = model;
        this.controller = controller;
        this.gameLoop = gameLoop;
        this.storage = storage;
    }

    public ReadOnlyGameState state() {
        return model.getStateView();
    }

    public ModelManager model() {
        return model;
    }

    public ControllerManager controller() {
        return controller;
    }

    public void update(float deltaSeconds) {
        if (model.getState().isGameOver() || model.getState().isLevelComplete()) {
            return;
        }
        tickAccumulator += deltaSeconds;
        float tickSeconds = 1f / GameLoop.TICKS_PER_SECOND;
        int guard = 0;
        while (tickAccumulator >= tickSeconds && guard < 5) {
            gameLoop.tick();
            tickAccumulator -= tickSeconds;
            guard++;
        }
    }

    public void dispose() {
        try {
            storage.saveProgress();
        } catch (RuntimeException e) {
            Gdx.app.error("SessionRuntime", "Failed to save progress on dispose", e);
        }
    }
}
