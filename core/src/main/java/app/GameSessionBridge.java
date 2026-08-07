package app;

import com.badlogic.gdx.Gdx;

import model.core.ReadOnlyGameState;

public final class GameSessionBridge {
    private SessionRuntime runtime;
    private String status = "not started";

    public void startDevSession() {
        try {
            runtime = DevSessionBootstrap.start();
            status = "live Egypt L1";
        } catch (RuntimeException e) {
            runtime = null;
            status = "session failed: " + e.getMessage();
            Gdx.app.error("GameSessionBridge", status, e);
        }
    }

    public ReadOnlyGameState state() {
        return runtime != null ? runtime.state() : null;
    }

    public SessionRuntime runtime() {
        return runtime;
    }

    public String status() {
        return status;
    }

    public void tick(float deltaSeconds) {
        if (runtime != null) {
            runtime.update(deltaSeconds);
        }
    }

    public void dispose() {
        if (runtime != null) {
            runtime.dispose();
            runtime = null;
        }
    }
}
