package model.service;

import java.util.ArrayList;
import java.util.List;

import model.data.content.chapter.ChapterType;
import model.data.content.specialLevel.SpecialLevelType;
import model.data.content.specialLevel.TimedWarConfig;
import model.data.content.specialLevel.TimedWarMode;
import model.data.wave.LevelConfig;
import model.rule.SessionConfig;

public final class LevelObjectives {
    private LevelObjectives() {
    }

    public static List<String> from(SessionConfig config) {
        if (config == null || config.isMinigame()) {
            return List.of();
        }

        List<String> lines = new ArrayList<>();
        lines.add("Don't let zombies reach your house");

        SpecialLevelType special = config.specialLevelType;
        LevelConfig level = config.levelConfig;
        if (special == null && level != null) {
            special = level.specialLevelType;
        }

        if (special != null) {
            switch (special) {
                case DEAD_LINE -> lines.add("Don't let zombies cross the line");
                case SAVE_OUR_SEEDS -> lines.add("Protect the endangered plants");
                case LOVE_YOUR_PLANTS -> lines.add("Don't let 5 plants die");
                case CONVEYOR_BELT -> lines.add("Use plants from the conveyor");
                case LOCKED_PLANTS -> lines.add("Survive with a limited plant set");
                case NIGHT_OPS -> lines.add("No sun falls from the sky");
                case PLANT_WHAT_YOU_GET -> lines.add("Plant only what you get");
                case ZOMBOSS -> lines.add("Defeat Dr. Zomboss");
                case TIMED_WAR -> lines.add(timedWarLine(level));
            }
        }

        if (level != null && level.chapterType == ChapterType.DARK_AGES
                && special != SpecialLevelType.NIGHT_OPS) {
            lines.add("No sun falls from the sky");
        }

        return List.copyOf(lines);
    }

    private static String timedWarLine(LevelConfig level) {
        TimedWarConfig timed = level != null ? level.timedWarConfig : null;
        if (timed == null) {
            return "Reach the goal before time runs out";
        }
        if (timed.mode == TimedWarMode.KILLS) {
            return "Defeat " + timed.goalAmount + " zombies before time runs out";
        }
        return "Collect " + timed.goalAmount + " sun before time runs out";
    }
}
