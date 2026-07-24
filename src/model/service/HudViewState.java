package model.service;

import model.core.GameLoop;
import model.core.ReadOnlyGameState;
import model.data.content.minigame.IZombieShop;
import model.data.content.minigame.MiniGameType;
import model.data.content.specialLevel.SpecialLevelType;
import model.data.content.specialLevel.TimedWarConfig;
import model.data.content.specialLevel.TimedWarMode;
import model.data.plant.PlantStats;
import model.data.plant.PlantType;
import model.data.zombie.ZombieType;
import model.rule.ConveyorState;
import model.rule.LevelRule;
import model.rule.SessionConfig;
import model.rule.SessionContext;
import model.rule.rules.specialLevel.DeadlineRules;
import model.rule.rules.specialLevel.SaveOurSeedsRules;
import model.rule.rules.specialLevel.TimedWarRules;
import model.storage.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HudViewState {

    public enum Mode {
        NORMAL,
        BRAINS,
        VASE_BREAKER,
        CONVEYOR,
        TIMED_WAR,
        DEADLINE,
        SAVE_OUR_SEEDS
    }

    public static final class TraySlot {
        public final String name;
        public final int cost;
        public final int cooldownSeconds;
        public final boolean ready;
        public final boolean highlighted;
        public final int count;

        public TraySlot(String name, int cost, int cooldownSeconds, boolean ready, boolean highlighted, int count) {
            this.name = name;
            this.cost = cost;
            this.cooldownSeconds = Math.max(0, cooldownSeconds);
            this.ready = ready;
            this.highlighted = highlighted;
            this.count = count;
        }
    }

    public final Mode mode;
    public final String modeLabel;
    public final boolean showSun;
    public final boolean showWave;
    public final boolean showPlantFood;

    public final String conveyorOfferName;
    public final int conveyorRemaining;
    public final int conveyorSecondsUntilNext;

    public final String timedWarGoalLabel;
    public final int timedWarProgress;
    public final int timedWarGoal;
    public final int timedWarSecondsLeft;

    public final int deadlineColumn;

    public final int protectedAlive;
    public final int protectedTotal;
    public final int protectedCol;

    public final int heldSeedTypes;

    public final List<TraySlot> traySlots;
    public final List<String> helpLines;
    public final boolean trayIsConveyorRow;

    public HudViewState(
            Mode mode,
            String modeLabel,
            boolean showSun,
            boolean showWave,
            boolean showPlantFood,
            String conveyorOfferName,
            int conveyorRemaining,
            int conveyorSecondsUntilNext,
            String timedWarGoalLabel,
            int timedWarProgress,
            int timedWarGoal,
            int timedWarSecondsLeft,
            int deadlineColumn,
            int protectedAlive,
            int protectedTotal,
            int protectedCol,
            int heldSeedTypes,
            List<TraySlot> traySlots,
            List<String> helpLines,
            boolean trayIsConveyorRow) {
        this.mode = mode != null ? mode : Mode.NORMAL;
        this.modeLabel = modeLabel != null ? modeLabel : "";
        this.showSun = showSun;
        this.showWave = showWave;
        this.showPlantFood = showPlantFood;
        this.conveyorOfferName = conveyorOfferName;
        this.conveyorRemaining = conveyorRemaining;
        this.conveyorSecondsUntilNext = conveyorSecondsUntilNext;
        this.timedWarGoalLabel = timedWarGoalLabel != null ? timedWarGoalLabel : "";
        this.timedWarProgress = timedWarProgress;
        this.timedWarGoal = timedWarGoal;
        this.timedWarSecondsLeft = timedWarSecondsLeft;
        this.deadlineColumn = deadlineColumn;
        this.protectedAlive = protectedAlive;
        this.protectedTotal = protectedTotal;
        this.protectedCol = protectedCol;
        this.heldSeedTypes = heldSeedTypes;
        this.traySlots = traySlots != null ? List.copyOf(traySlots) : List.of();
        this.helpLines = helpLines != null ? List.copyOf(helpLines) : List.of();
        this.trayIsConveyorRow = trayIsConveyorRow;
    }

    public static HudViewState empty() {
        return new HudViewState(
                Mode.NORMAL, "", true, true, true,
                null, 0, 0,
                "", 0, 0, 0,
                -1, 0, 0, -1, 0,
                List.of(), List.of(), false);
    }

    public static HudViewState fromSession(SessionContext context, ReadOnlyGameState state) {
        return fromSession(context, state, null);
    }

    public static HudViewState fromSession(SessionContext context, ReadOnlyGameState state, User user) {
        if (context == null || context.getConfig() == null) {
            if (state != null && state.isBrainsMode()) {
                return brainsHud();
            }
            return empty();
        }

        SessionConfig config = context.getConfig();
        MiniGameType mini = config.miniGameType;
        SpecialLevelType special = config.specialLevelType;

        if ((state != null && state.isBrainsMode()) || mini == MiniGameType.I_ZOMBIE) {
            return brainsHud();
        }
        if (mini == MiniGameType.VASE_BREAKER) {
            return vaseBreakerHud(context, state);
        }
        if (mini == MiniGameType.WALLNUT_BOWLING
                || special == SpecialLevelType.CONVEYOR_BELT
                || context.isConveyorMode()) {
            return conveyorHud(context, mini);
        }
        if (special == SpecialLevelType.TIMED_WAR) {
            return timedWarHud(context, user);
        }
        if (special == SpecialLevelType.DEAD_LINE) {
            return deadlineHud(context, user);
        }
        if (special == SpecialLevelType.SAVE_OUR_SEEDS) {
            return saveOurSeedsHud(context, user);
        }

        return normalHud(context, specialLabel(special), user, special);
    }

    private static HudViewState brainsHud() {
        List<TraySlot> slots = new ArrayList<>();
        for (Map.Entry<ZombieType, Integer> entry : IZombieShop.getCosts().entrySet()) {
            slots.add(new TraySlot(entry.getKey().name, entry.getValue(), 0, true, false, 1));
        }
        return new HudViewState(
                Mode.BRAINS, "I, Zombie", true, false, false,
                null, 0, 0,
                "", 0, 0, 0,
                -1, 0, 0, -1, 0,
                slots,
                List.of(
                        "place zombie -t <type> -l (r,c)",
                        "show available zombies",
                        "show sun amount",
                        "advance time -t <n> ticks",
                        "menu exit"),
                false);
    }

    private static HudViewState vaseBreakerHud(SessionContext context, ReadOnlyGameState state) {
        int heldTotal = 0;
        List<TraySlot> slots = new ArrayList<>();
        if (context.getHeldSeeds() != null) {
            for (Map.Entry<PlantType, Integer> entry : context.getHeldSeeds().entrySet()) {
                int count = entry.getValue() != null ? entry.getValue() : 0;
                heldTotal += count;
                slots.add(new TraySlot(entry.getKey().name, 0, 0, count > 0, false, count));
            }
        }
        int vases = state != null ? state.getVases().size() : 0;
        return new HudViewState(
                Mode.VASE_BREAKER, "Vase Breaker", false, false, false,
                null, vases, 0,
                "", 0, 0, 0,
                -1, 0, 0, -1, heldTotal,
                slots,
                List.of(
                        "break vase -l (r,c)",
                        "collect seed -l (r,c)",
                        "plant plant -t <type> -l (r,c)",
                        "show held seeds",
                        "pluck plant -l (r,c)",
                        "advance time -t <n> ticks",
                        "menu exit"),
                false);
    }

    private static HudViewState conveyorHud(SessionContext context, MiniGameType mini) {
        String label = mini == MiniGameType.WALLNUT_BOWLING
                ? "Wall-nut Bowling"
                : "Conveyor Belt";
        int seconds = 0;
        List<TraySlot> slots = new ArrayList<>();
        ConveyorState conveyor = context.getConveyorState();
        if (conveyor != null) {
            seconds = conveyor.getSecondsUntilNextOffer();
            PlantType offer = conveyor.getCurrentOffer();
            if (offer != null) {
                slots.add(new TraySlot(offer.name, 0, 0, true, true, 1));
            }
            for (PlantType upcoming : conveyor.getUpcomingQueue()) {
                slots.add(new TraySlot(upcoming.name, 0, 0, false, false, 1));
            }
        }
        return new HudViewState(
                Mode.CONVEYOR, label, false, true, false,
                null, 0, seconds,
                "", 0, 0, 0,
                -1, 0, 0, -1, 0,
                slots,
                List.of(
                        "plant conveyor -l (r,c)",
                        "pluck plant -l (r,c)",
                        "feed plant -l (r,c)",
                        "show plants status",
                        "advance time -t <n> ticks",
                        "menu exit"),
                true);
    }

    private static HudViewState timedWarHud(SessionContext context, User user) {
        TimedWarRules rules = findRule(context, TimedWarRules.class);
        TimedWarConfig cfg = rules != null ? rules.getConfig() : TimedWarConfig.kills(25, 150);
        int progress = rules != null ? rules.getProgress() : 0;
        int ticksLeft = rules != null ? rules.getTicksRemaining() : cfg.timeLimitTicks;
        String goalLabel = cfg.mode == TimedWarMode.SUN ? "Sun" : "Kills";
        return withPlantTray(context, user,
                Mode.TIMED_WAR, "Timed War", cfg.mode == TimedWarMode.SUN, false, true,
                goalLabel, progress, cfg.goalAmount,
                Math.max(0, ticksLeft / GameLoop.TICKS_PER_SECOND),
                -1, 0, 0, -1,
                normalHelp(true));
    }

    private static HudViewState deadlineHud(SessionContext context, User user) {
        DeadlineRules rules = findRule(context, DeadlineRules.class);
        int col = rules != null ? rules.getDeadlineColumn() : DeadlineRules.DEFAULT_DEADLINE_COLUMN;
        return withPlantTray(context, user,
                Mode.DEADLINE, "Deadline", true, true, true,
                "", 0, 0, 0,
                col, 0, 0, -1,
                normalHelp(true));
    }

    private static HudViewState saveOurSeedsHud(SessionContext context, User user) {
        SaveOurSeedsRules rules = findRule(context, SaveOurSeedsRules.class);
        int alive = rules != null ? rules.getProtectedAliveCount() : 0;
        int total = rules != null ? rules.getProtectedTotalSlots() : SaveOurSeedsRules.PROTECTED_ROWS.length;
        int col = rules != null ? rules.getProtectedCol() : SaveOurSeedsRules.PROTECTED_COL;
        return withPlantTray(context, user,
                Mode.SAVE_OUR_SEEDS, "Save Our Seeds", true, true, true,
                "", 0, 0, 0,
                -1, alive, total, col,
                normalHelp(true));
    }

    private static HudViewState normalHud(SessionContext context, String label, User user, SpecialLevelType special) {
        List<String> help = normalHelp(true);
        if (special == SpecialLevelType.PLANT_WHAT_YOU_GET) {
            help = new ArrayList<>(help);
            help.add(0, "start zombie waves");
        }
        return withPlantTray(context, user,
                Mode.NORMAL, label, true, true, true,
                "", 0, 0, 0,
                -1, 0, 0, -1,
                help);
    }

    private static HudViewState withPlantTray(
            SessionContext context,
            User user,
            Mode mode,
            String label,
            boolean showSun,
            boolean showWave,
            boolean showPlantFood,
            String timedWarGoalLabel,
            int timedWarProgress,
            int timedWarGoal,
            int timedWarSecondsLeft,
            int deadlineColumn,
            int protectedAlive,
            int protectedTotal,
            int protectedCol,
            List<String> help) {
        return new HudViewState(
                mode, label, showSun, showWave, showPlantFood,
                null, 0, 0,
                timedWarGoalLabel, timedWarProgress, timedWarGoal, timedWarSecondsLeft,
                deadlineColumn, protectedAlive, protectedTotal, protectedCol, 0,
                buildSelectedPlantSlots(context, user),
                help,
                false);
    }

    private static List<TraySlot> buildSelectedPlantSlots(SessionContext context, User user) {
        List<TraySlot> slots = new ArrayList<>();
        if (context == null || context.getConfig() == null || context.getConfig().selectedPlants == null) {
            return slots;
        }
        for (PlantType type : context.getConfig().selectedPlants) {
            if (type == null) {
                continue;
            }
            int level = user != null ? user.getPlantLevel(type) : PlantStats.DEFAULT_LEVEL;
            PlantStats stats = PlantStats.forLevel(type, level);
            int cdTicks = context.getPlantingCooldownTicks(type);
            int cdSec = (int) Math.ceil(cdTicks / (double) GameLoop.TICKS_PER_SECOND);
            boolean ready = cdTicks <= 0;
            slots.add(new TraySlot(type.name, stats.cost, cdSec, ready, false, 1));
        }
        return slots;
    }

    private static List<String> normalHelp(boolean includeSun) {
        List<String> help = new ArrayList<>();
        help.add("plant plant -t <type> -l (r,c)");
        if (includeSun) {
            help.add("collect sun -l (r,c)");
        }
        help.add("pluck plant -l (r,c)");
        help.add("feed plant -l (r,c)");
        help.add("show plants status");
        help.add("show tile status -l (r,c)");
        help.add("advance time -t <n> ticks");
        help.add("menu exit");
        return help;
    }

    private static String specialLabel(SpecialLevelType special) {
        if (special == null) {
            return "";
        }
        return switch (special) {
            case LOCKED_PLANTS -> "Locked Plants";
            case NIGHT_OPS -> "Night Ops";
            case LOVE_YOUR_PLANTS -> "Love Your Plants";
            case PLANT_WHAT_YOU_GET -> "Plant What You Get";
            default -> special.name().replace('_', ' ');
        };
    }

    private static <T extends LevelRule> T findRule(SessionContext context, Class<T> type) {
        if (context == null || context.getRuleEngine() == null) {
            return null;
        }
        List<LevelRule> rules = context.getRuleEngine().getActiveRules();
        if (rules == null) {
            return null;
        }
        for (LevelRule rule : rules) {
            if (type.isInstance(rule)) {
                return type.cast(rule);
            }
        }
        return null;
    }
}
