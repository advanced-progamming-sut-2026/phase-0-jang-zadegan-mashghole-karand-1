package model.core;

import model.gameSetting.GameSetting;

public class GameLoop {
    public static final int TICKS_PER_SECOND = 10;
    public static final int TICK_INTERVAL_MS = 1000 / TICKS_PER_SECOND;

    private Runnable onTickHandler;
    private boolean autoTickEnabled = false;
    private boolean running = false;
    private Thread autoTickThread;

    private int ticksPerformed = 0;
    private int totalTicks = 0;
    private int gameSpeed = GameSetting.DEFAULT_GAME_SPEED;

    public void setOnTickHandler(Runnable onTick) {
        this.onTickHandler = onTick;
    }

    public void setGameSpeed(int gameSpeed) {
        this.gameSpeed = Math.max(GameSetting.MIN_GAME_SPEED,
                Math.min(gameSpeed, GameSetting.MAX_GAME_SPEED));
    }

    public int getGameSpeed() {
        return gameSpeed;
    }

    public int getTickIntervalMs() {
        return Math.max(1, TICK_INTERVAL_MS * GameSetting.DEFAULT_GAME_SPEED / gameSpeed);
    }

    public void tick() {
        performTick();
    }

    public void tick(int count, boolean realTime) {
        if (realTime) {
            for (int i = 0; i < count; i++) {
                performTick();
                try {
                    Thread.sleep(getTickIntervalMs());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        } else {
            for (int i = 0; i < count; i++) {
                performTick();
            }
        }
    }

    public void startAutoTick() {
        if (running && autoTickThread != null && autoTickThread.isAlive()) {
            return;
        }
        if (running) {
            running = false;
            autoTickThread = null;
        }

        if (onTickHandler == null) {
            return;
        }

        autoTickEnabled = true;
        running = true;
        autoTickThread = new Thread(this::autoTickLoop, "AutoTickThread");
        autoTickThread.setDaemon(true);
        autoTickThread.start();
    }

    public void stopAutoTick() {
        if (!running) {
            return;
        }

        running = false;
        autoTickEnabled = false;
        Thread thread = autoTickThread;
        if (thread == null) {
            return;
        }
        if (thread == Thread.currentThread()) {
            return;
        }
        thread.interrupt();
        try {
            thread.join(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        autoTickThread = null;
    }

    public void toggleAutoTick() {
        if (running) {
            stopAutoTick();
        } else {
            startAutoTick();
        }
    }

    public boolean isAutoTickRunning() {
        return running && autoTickThread != null && autoTickThread.isAlive();
    }

    public boolean isAutoTickEnabled() {
        return autoTickEnabled;
    }

    private void autoTickLoop() {
        try {
            while (running) {
                long startTime = System.currentTimeMillis();

                if (!performTick()) {
                    break;
                }

                long elapsed = System.currentTimeMillis() - startTime;
                long sleepTime = getTickIntervalMs() - elapsed;

                try {
                    Thread.sleep(Math.max(1, sleepTime));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        } finally {
            running = false;
            autoTickEnabled = false;
            if (autoTickThread == Thread.currentThread()) {
                autoTickThread = null;
            }
        }
    }

    private boolean performTick() {
        try {
            if (onTickHandler != null) {
                onTickHandler.run();
            }
            ticksPerformed++;
            totalTicks++;
            return true;
        } catch (RuntimeException e) {
            System.err.println("Game tick failed: " + e.getMessage());
            e.printStackTrace(System.err);
            return false;
        }
    }

    public int getTicksPerformed() {
        return ticksPerformed;
    }

    public int getTotalTicks() {
        return totalTicks;
    }

    public void resetStats() {
        ticksPerformed = 0;
        totalTicks = 0;
    }
}