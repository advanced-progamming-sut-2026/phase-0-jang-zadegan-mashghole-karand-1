package model.core;

public class GameLoop {
    public static final int TICKS_PER_SECOND = 10;
    public static final int TICK_INTERVAL_MS = 1000 / TICKS_PER_SECOND;

    private Runnable onTickHandler;
    private boolean autoTickEnabled = false;
    private boolean running = false;
    private Thread autoTickThread;

    private int ticksPerformed = 0;
    private int totalTicks = 0;

    public void setOnTickHandler(Runnable onTick) {
        this.onTickHandler = onTick;
    }

    public void tick() {
        performTick();
    }

    public void tick(int count, boolean realTime) {
        if (realTime) {
            for (int i = 0; i < count; i++) {
                performTick();
                try {
                    Thread.sleep(TICK_INTERVAL_MS);
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
        if (running) {
            return;
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
        if (autoTickThread != null) {
            autoTickThread.interrupt();
            try {
                autoTickThread.join(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            autoTickThread = null;
        }
    }

    public void toggleAutoTick() {
        if (running) {
            stopAutoTick();
        } else {
            startAutoTick();
        }
    }

    public boolean isAutoTickRunning() {
        return running;
    }

    public boolean isAutoTickEnabled() {
        return autoTickEnabled;
    }

    private void autoTickLoop() {
        while (running) {
            long startTime = System.currentTimeMillis();

            performTick();

            long elapsed = System.currentTimeMillis() - startTime;
            long sleepTime = TICK_INTERVAL_MS - elapsed;

            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    private void performTick() {
        if (onTickHandler != null) {
            onTickHandler.run();
        }
        ticksPerformed++;
        totalTicks++;
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