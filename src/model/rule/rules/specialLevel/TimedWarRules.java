package model.rule.rules.specialLevel;

import model.core.EventBus;
import model.core.GameState;
import model.core.SessionEnd;
import model.data.content.specialLevel.TimedWarConfig;
import model.data.content.specialLevel.TimedWarMode;
import model.data.sun.Sun;
import model.data.wave.LevelConfig;
import model.data.zombie.Zombie;
import model.event.events.GameOverReason;
import model.rule.LevelRule;
import model.rule.SessionContext;

public class TimedWarRules implements LevelRule {
    private static final TimedWarConfig DEFAULT = TimedWarConfig.kills(25, 150);

    private TimedWarConfig config;
    private int progress;
    private int ticksRemaining;

    @Override
    public boolean winsOnWaveClear() {
        return false;
    }

    @Override
    public void onSessionStart(SessionContext context, GameState state, EventBus bus) {
        config = resolveConfig(context);
        progress = 0;
        ticksRemaining = config.timeLimitTicks;
    }

    @Override
    public void postTick(SessionContext context, GameState state, EventBus bus) {
        if (state.gameOver || state.levelComplete || config == null) {
            return;
        }

        ticksRemaining--;
        if (ticksRemaining <= 0) {
            SessionEnd.lose(state, bus, GameOverReason.TIME_UP);
        }
    }

    @Override
    public void onZombieDied(Zombie zombie, GameState state, EventBus bus) {
        if (config == null || config.mode != TimedWarMode.KILLS) {
            return;
        }
        if (state.gameOver || state.levelComplete) {
            return;
        }
        progress++;
        if (progress >= config.goalAmount) {
            SessionEnd.win(state, bus);
        }
    }

    @Override
    public void onSunCollected(Sun sun, GameState state, EventBus bus) {
        if (config == null || config.mode != TimedWarMode.SUN) {
            return;
        }
        if (state.gameOver || state.levelComplete || sun == null) {
            return;
        }
        progress += Math.max(0, sun.amount);
        if (progress >= config.goalAmount) {
            SessionEnd.win(state, bus);
        }
    }

    public int getProgress() {
        return progress;
    }

    public int getTicksRemaining() {
        return ticksRemaining;
    }

    public TimedWarConfig getConfig() {
        return config != null ? config : DEFAULT;
    }

    private TimedWarConfig resolveConfig(SessionContext context) {
        if (context == null || context.getConfig() == null || context.getConfig().levelConfig == null) {
            return DEFAULT;
        }
        LevelConfig level = context.getConfig().levelConfig;
        if (level.timedWarConfig != null) {
            return level.timedWarConfig;
        }
        return DEFAULT;
    }
}
