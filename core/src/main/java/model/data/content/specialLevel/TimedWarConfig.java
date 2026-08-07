package model.data.content.specialLevel;

import model.core.GameLoop;

public final class TimedWarConfig {
    public final TimedWarMode mode;
    public final int goalAmount;
    public final int timeLimitTicks;

    public TimedWarConfig(TimedWarMode mode, int goalAmount, int timeLimitTicks) {
        this.mode = mode;
        this.goalAmount = goalAmount;
        this.timeLimitTicks = timeLimitTicks;
    }

    public static TimedWarConfig kills(int goalAmount, int timeLimitSeconds) {
        return new TimedWarConfig(TimedWarMode.KILLS, goalAmount, timeLimitSeconds * GameLoop.TICKS_PER_SECOND);
    }

    public static TimedWarConfig sun(int goalAmount, int timeLimitSeconds) {
        return new TimedWarConfig(TimedWarMode.SUN, goalAmount, timeLimitSeconds * GameLoop.TICKS_PER_SECOND);
    }
}
