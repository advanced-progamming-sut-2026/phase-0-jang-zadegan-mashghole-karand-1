package view.renderer;

import static view.renderer.ConsoleTheme.BOLD;
import static view.renderer.ConsoleTheme.CYAN;
import static view.renderer.ConsoleTheme.GRAY;
import static view.renderer.ConsoleTheme.GREEN;
import static view.renderer.ConsoleTheme.ORANGE;
import static view.renderer.ConsoleTheme.PURPLE;
import static view.renderer.ConsoleTheme.RED;
import static view.renderer.ConsoleTheme.RESET;
import static view.renderer.ConsoleTheme.SCREEN_WIDTH;
import static view.renderer.ConsoleTheme.WHITE;
import static view.renderer.ConsoleTheme.YELLOW;

import java.util.List;

import model.core.GameLoop;
import model.core.ReadOnlyGameState;
import model.data.brain.Brain;
import model.service.HudViewState;

final class ConsoleGameScreens {

    private final ConsoleRenderEngine engine;
    private final ConsoleGridRenderer gridRenderer;

    ConsoleGameScreens(ConsoleRenderEngine engine, ConsoleGridRenderer gridRenderer) {
        this.engine = engine;
        this.gridRenderer = gridRenderer;
    }

    String getGameScreen(ReadOnlyGameState state, HudViewState hud) {
        HudViewState safeHud = hud != null ? hud : HudViewState.empty();
        StringBuilder sb = new StringBuilder();
        sb.append(getHUD(state, safeHud));
        sb.append("\n");
        sb.append(gridRenderer.getGrid(state, safeHud));
        sb.append("\n");
        sb.append(getPlantTray(safeHud));
        sb.append("\n");
        sb.append(getGameHelp(safeHud));
        if (state.isLevelComplete()) {
            sb.append("\n");
            sb.append(buildLevelCompleteOverlay(state));
        } else if (state.isGameOver()) {
            sb.append("\n");
            sb.append(buildGameOverOverlay(state));
        }
        sb.append("\n");
        sb.append(engine.getMessages());
        return sb.toString();
    }

    private String buildGameOverOverlay(ReadOnlyGameState state) {
        StringBuilder sb = new StringBuilder();
        sb.append(engine.getHeaderBox(" 💀 " + BOLD + "GAME OVER" + RESET, RED));
        sb.append("\n");
        if (state.getGameOverReason() != null) {
            sb.append("  ").append(CYAN).append("Reason:").append(RESET).append(" ")
                    .append(state.getGameOverReason().message).append("\n");
        }
        sb.append("  ").append(CYAN).append("Waves Survived:").append(RESET).append(" ")
                .append(state.getCurrentWave()).append("\n");
        sb.append("\n");
        sb.append("  ").append(CYAN).append("1.").append(RESET).append(" Return to level select: ")
                .append(GREEN).append("menu exit").append(RESET).append("\n");
        return sb.toString();
    }

    private String buildLevelCompleteOverlay(ReadOnlyGameState state) {
        StringBuilder sb = new StringBuilder();
        sb.append(engine.getHeaderBox(" 🎉 " + BOLD + "LEVEL COMPLETE!" + RESET, GREEN));
        sb.append("\n");
        if (state != null && state.hasSessionScore()) {
            sb.append("  ").append(CYAN).append("Score:").append(RESET).append(" ")
                    .append(BOLD).append(state.getSessionScore()).append(RESET);
            if (state.isSessionScoreNewRecord()) {
                sb.append("  ").append(YELLOW).append("(new high score!)").append(RESET);
            }
            sb.append("\n\n");
        }
        sb.append("  ").append(CYAN).append("1.").append(RESET).append(" Return to level select: ")
                .append(GREEN).append("menu exit").append(RESET).append("\n");
        return sb.toString();
    }

    String getHUD(ReadOnlyGameState state, HudViewState hud) {
        String status = state.isGameOver() ? "💀" : state.isLevelComplete() ? "⭐" : "▶️";
        int seconds = state.getTotalTicks() / GameLoop.TICKS_PER_SECOND;
        StringBuilder title = new StringBuilder();

        if (hud.modeLabel != null && !hud.modeLabel.isEmpty()) {
            title.append(BOLD).append(hud.modeLabel).append(RESET).append("  ");
        }

        title.append(switch (hud.mode) {
            case BRAINS -> buildBrainsTitle(state, status, seconds);
            case VASE_BREAKER -> buildVaseBreakerTitle(state, hud, status, seconds);
            case CONVEYOR -> buildConveyorTitle(state, hud, status);
            case TIMED_WAR -> buildTimedWarTitle(state, hud, status);
            case DEADLINE -> buildDeadlineTitle(state, hud, status, seconds);
            case SAVE_OUR_SEEDS -> buildSaveOurSeedsTitle(state, hud, status, seconds);
            case ZOMBOSS -> buildZombossTitle(state, hud, status);
            default -> buildDefaultTitle(state, hud, status, seconds);
        });

        return engine.getHeaderBox(title.toString(), CYAN);
    }

    private String buildBrainsTitle(ReadOnlyGameState state, String status, int seconds) {
        long brains = state.getBrains().stream().filter(Brain::isCollected).count();
        return String.format("%s☀️ : %-4d  %s🧠 : %d/%d  %s🧟 : %-3d  %s⏱️ %-4ds  %s%s%s",
                YELLOW, state.getSunAmount(),
                PURPLE, brains, ReadOnlyGameState.GRID_ROWS,
                RED, state.getZombies().size(),
                WHITE, seconds,
                CYAN, status, RESET);
    }

    private String buildVaseBreakerTitle(ReadOnlyGameState state, HudViewState hud, String status, int seconds) {
        return String.format("%s🏺 : %-3d  %s🌱 : %-3d  %s🧟 : %-3d  %s⏱️ %-4ds  %s%s%s",
                PURPLE, hud.conveyorRemaining,
                GREEN, hud.heldSeedTypes,
                RED, state.getZombies().size(),
                WHITE, seconds,
                CYAN, status, RESET);
    }

    private String buildConveyorTitle(ReadOnlyGameState state, HudViewState hud, String status) {
        return String.format("%s⏳ : %-2ds  %s🌊 : %-3d  %s🧟 : %-3d  %s%s%s",
                YELLOW, hud.conveyorSecondsUntilNext,
                CYAN, state.getCurrentWave(),
                RED, state.getZombies().size(),
                CYAN, status, RESET);
    }

    private String buildZombossTitle(ReadOnlyGameState state, HudViewState hud, String status) {
        return String.format("%s⏳ : %-2ds  %s❤ %d/%d  %s🧟 : %-3d  %s%s%s",
                YELLOW, hud.conveyorSecondsUntilNext,
                RED, hud.timedWarProgress, hud.timedWarGoal,
                RED, state.getZombies().size(),
                CYAN, status, RESET);
    }

    private String buildTimedWarTitle(ReadOnlyGameState state, HudViewState hud, String status) {
        StringBuilder title = new StringBuilder();
        if (hud.showSun) {
            title.append(String.format("%s☀️ : %-4d  ", YELLOW, state.getSunAmount()));
        }
        title.append(String.format("%s🎯 %s : %d/%d  %s⏰ : %-3ds  %s🧟 : %-3d  ",
                ORANGE, hud.timedWarGoalLabel, hud.timedWarProgress, hud.timedWarGoal,
                YELLOW, hud.timedWarSecondsLeft,
                RED, state.getZombies().size()));
        if (hud.showPlantFood) {
            title.append(String.format("%s🌿 : %-2d  ", PURPLE, state.getPlantFoodAmount()));
        }
        title.append(String.format("%s%s%s", CYAN, status, RESET));
        return title.toString();
    }

    private String buildDeadlineTitle(ReadOnlyGameState state, HudViewState hud, String status, int seconds) {
        return String.format(
                "%s☀️ : %-4d  %s⛔ col %-2d  %s🌊 : %-3d  %s🧟 : %-3d  "
                        + "%s🌿 : %-2d  %s⏱️ %-4ds  %s%s%s",
                YELLOW, state.getSunAmount(),
                RED, hud.deadlineColumn,
                CYAN, state.getCurrentWave(),
                RED, state.getZombies().size(),
                PURPLE, state.getPlantFoodAmount(),
                WHITE, seconds,
                CYAN, status, RESET);
    }

    private String buildSaveOurSeedsTitle(ReadOnlyGameState state, HudViewState hud, String status, int seconds) {
        return String.format(
                "%s☀️ : %-4d  %s🛡️ %d/%d @col%d  %s🌊 : %-3d  %s🧟 : %-3d  "
                        + "%s🌿 : %-2d  %s⏱️ %-4ds  %s%s%s",
                YELLOW, state.getSunAmount(),
                GREEN, hud.protectedAlive, hud.protectedTotal, hud.protectedCol,
                CYAN, state.getCurrentWave(),
                RED, state.getZombies().size(),
                PURPLE, state.getPlantFoodAmount(),
                WHITE, seconds,
                CYAN, status, RESET);
    }

    private String buildDefaultTitle(ReadOnlyGameState state, HudViewState hud, String status, int seconds) {
        StringBuilder title = new StringBuilder();
        if (hud.showSun) {
            title.append(String.format("%s☀️ : %-4d  ", YELLOW, state.getSunAmount()));
        }
        if (hud.showWave) {
            title.append(String.format("%s🌊 : %-3d  ", CYAN, state.getCurrentWave()));
        }
        title.append(String.format("%s🧟 : %-3d  ", RED, state.getZombies().size()));
        if (hud.showPlantFood) {
            title.append(String.format("%s🌿 : %-2d  ", PURPLE, state.getPlantFoodAmount()));
        }
        title.append(String.format("%s⏱️ %-4ds  %s%s%s",
                WHITE, seconds,
                CYAN, status, RESET));
        return title.toString();
    }

    private String getPlantTray(HudViewState hud) {
        StringBuilder sb = new StringBuilder();
        String trayTitle = switch (hud.mode) {
            case CONVEYOR, ZOMBOSS -> "Conveyor";
            case BRAINS -> "Zombies";
            case VASE_BREAKER -> "Held Seeds";
            default -> "Plants";
        };
        sb.append("  ").append(BOLD).append(trayTitle).append(":").append(RESET).append("\n");

        if (hud.traySlots == null || hud.traySlots.isEmpty()) {
            sb.append("  ").append(GRAY).append("(none)").append(RESET).append("\n");
            return sb.toString();
        }

        if (hud.trayIsConveyorRow) {
            sb.append("  ");
            for (int i = 0; i < hud.traySlots.size(); i++) {
                if (i > 0) {
                    sb.append(" ");
                }
                sb.append(formatTraySlot(hud.traySlots.get(i), 14));
            }
            sb.append("\n");
            return sb.toString();
        }

        final int cols = 4;
        final int slotWidth = 26;
        for (int i = 0; i < hud.traySlots.size(); i += cols) {
            sb.append("  ");
            for (int c = 0; c < cols && i + c < hud.traySlots.size(); c++) {
                if (c > 0) {
                    sb.append(" ");
                }
                sb.append(formatTraySlot(hud.traySlots.get(i + c), slotWidth));
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private String formatTraySlot(HudViewState.TraySlot slot, int width) {
        StringBuilder body = new StringBuilder();
        if (slot.highlighted) {
            body.append(YELLOW).append("▶").append(RESET);
        } else {
            body.append(" ");
        }
        String nameColor = slot.ready ? GREEN : GRAY;
        body.append(nameColor).append(engine.truncate(slot.name, 12)).append(RESET);
        if (slot.count > 1) {
            body.append(CYAN).append(" x").append(slot.count).append(RESET);
        }
        if (slot.cost > 0) {
            body.append(" ").append(YELLOW).append(slot.cost).append(RESET);
        }
        if (!slot.ready && slot.cooldownSeconds > 0) {
            body.append(" ").append(RED).append(slot.cooldownSeconds).append("s").append(RESET);
        } else if (slot.ready && !slot.highlighted) {
            body.append(" ").append(GREEN).append("ok").append(RESET);
        }
        return engine.padVisible(body.toString(), width);
    }

    private String getGameHelp(HudViewState hud) {
        StringBuilder sb = new StringBuilder();
        sb.append("  ").append(BOLD).append("Commands:").append(RESET).append("\n");
        List<String> lines = hud.helpLines != null ? hud.helpLines : List.of();
        if (lines.isEmpty()) {
            sb.append("  ").append(GRAY).append("menu exit").append(RESET).append("\n");
            return sb.toString();
        }

        int columns = lines.size() <= 4 ? 2 : 3;
        int rows = (lines.size() + columns - 1) / columns;
        int colWidth = Math.max(28, (SCREEN_WIDTH - 4) / columns);

        for (int r = 0; r < rows; r++) {
            sb.append("  ");
            for (int c = 0; c < columns; c++) {
                int idx = c * rows + r;
                if (idx >= lines.size()) {
                    continue;
                }
                String item = CYAN + "• " + RESET + GREEN + engine.truncate(lines.get(idx), colWidth - 4) + RESET;
                sb.append(engine.padVisible(item, colWidth));
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
