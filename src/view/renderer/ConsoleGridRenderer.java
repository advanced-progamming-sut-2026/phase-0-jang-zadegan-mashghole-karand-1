package view.renderer;

import static view.renderer.ConsoleTheme.BG_ICE;
import static view.renderer.ConsoleTheme.BG_NECRO;
import static view.renderer.ConsoleTheme.BG_WATER;
import static view.renderer.ConsoleTheme.BLUE;
import static view.renderer.ConsoleTheme.CYAN;
import static view.renderer.ConsoleTheme.GREEN;
import static view.renderer.ConsoleTheme.PURPLE;
import static view.renderer.ConsoleTheme.RED;
import static view.renderer.ConsoleTheme.RESET;
import static view.renderer.ConsoleTheme.YELLOW;

import model.board.IceDirection;
import model.board.Tile;
import model.core.ReadOnlyGameState;
import model.data.Barrel.Barrel;
import model.data.Grave.Grave;
import model.data.brain.Brain;
import model.data.plant.Plant;
import model.data.plant.PlantType;
import model.data.plant.stuns.PlantStun;
import model.data.vase.Vase;
import model.data.zombie.Zombie;
import model.lawnmower.LawnMower;
import model.service.HudViewState;

final class ConsoleGridRenderer {

    private static final int GRID_ROWS = ReadOnlyGameState.GRID_ROWS;
    private static final int GRID_COLS = ReadOnlyGameState.GRID_COLS;
    private static final int CELL_HEIGHT = 3;
    private static final int CELL_INNER_WIDTH = 10;
    private static final int MOWER_INNER_WIDTH = 2;

    private static final String CELL_DASH = "─".repeat(CELL_INNER_WIDTH);
    private static final String MOWER_DASH = "─".repeat(MOWER_INNER_WIDTH);
    private static final String RED_DASH = RED + CELL_DASH + RESET;
    private static final String RED_TEE_TOP = RED + "┬" + RESET;
    private static final String RED_TEE_MID = RED + "┼" + RESET;
    private static final String RED_TEE_BOT = RED + "┴" + RESET;
    private static final String RED_BAR = RED + "│" + RESET;

    private final ConsoleRenderEngine engine;

    ConsoleGridRenderer(ConsoleRenderEngine engine) {
        this.engine = engine;
    }

    String getGrid(ReadOnlyGameState state, HudViewState hud) {
        int deadlineCol = (hud != null && hud.mode == HudViewState.Mode.DEADLINE) ? hud.deadlineColumn : -1;
        StringBuilder sb = new StringBuilder();
        sb.append(buildGridHeader());
        sb.append(buildGridTopBorder(deadlineCol));
        sb.append(buildGridBody(state, deadlineCol));
        sb.append(buildGridBottomBorder(deadlineCol));
        return sb.toString();
    }

    private String buildGridHeader() {
        StringBuilder sb = new StringBuilder();
        sb.append("      ");
        sb.append(centerLabel("M", MOWER_INNER_WIDTH));
        for (int col = 0; col < GRID_COLS; col++) {
            sb.append(" ").append(centerLabel(String.valueOf(col), CELL_INNER_WIDTH));
        }
        sb.append("\n");
        return sb.toString();
    }

    private String buildGridTopBorder(int deadlineCol) {
        StringBuilder sb = new StringBuilder();
        sb.append("    ┌").append(MOWER_DASH);
        for (int col = 0; col < GRID_COLS; col++) {
            boolean deadline = col == deadlineCol;
            sb.append(deadline ? RED_TEE_TOP : "┬");
            sb.append(deadline ? RED_DASH : CELL_DASH);
        }
        sb.append(deadlineCol == GRID_COLS - 1 ? RED + "┐" + RESET : "┐").append("\n");
        return sb.toString();
    }

    private String buildGridBody(ReadOnlyGameState state, int deadlineCol) {
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < GRID_ROWS; row++) {
            sb.append(buildGridRow(state, row, deadlineCol));
            if (row < GRID_ROWS - 1) {
                sb.append(buildGridSeparator(deadlineCol));
            }
        }
        return sb.toString();
    }

    private String buildGridRow(ReadOnlyGameState state, int row, int deadlineCol) {
        StringBuilder sb = new StringBuilder();
        for (int layer = 0; layer < CELL_HEIGHT; layer++) {
            if (layer == 0) {
                sb.append(String.format(" %d  │", row));
            } else {
                sb.append("    │");
            }
            sb.append(engine.padVisible(getMowerGutter(state, row, layer), MOWER_INNER_WIDTH));

            for (int col = 0; col < GRID_COLS; col++) {
                boolean deadline = col == deadlineCol;
                sb.append(deadline ? RED_BAR : "│");
                sb.append(renderCellLayer(state, row, col, layer));
            }
            sb.append(deadlineCol == GRID_COLS - 1 ? RED_BAR : "│");
            sb.append("\n");
        }
        return sb.toString();
    }

    private String buildGridSeparator(int deadlineCol) {
        StringBuilder sb = new StringBuilder();
        sb.append("    ├").append(MOWER_DASH);
        for (int col = 0; col < GRID_COLS; col++) {
            boolean deadline = col == deadlineCol;
            sb.append(deadline ? RED_TEE_MID : "┼");
            sb.append(deadline ? RED_DASH : CELL_DASH);
        }
        sb.append(deadlineCol == GRID_COLS - 1 ? RED + "┤" + RESET : "┤").append("\n");
        return sb.toString();
    }

    private String buildGridBottomBorder(int deadlineCol) {
        StringBuilder sb = new StringBuilder();
        sb.append("    └").append(MOWER_DASH);
        for (int col = 0; col < GRID_COLS; col++) {
            boolean deadline = col == deadlineCol;
            sb.append(deadline ? RED_TEE_BOT : "┴");
            sb.append(deadline ? RED_DASH : CELL_DASH);
        }
        sb.append(deadlineCol == GRID_COLS - 1 ? RED + "┘" + RESET : "┘");
        return sb.toString();
    }

    private String centerLabel(String text, int width) {
        int pad = Math.max(0, width - text.length());
        int left = pad / 2;
        return " ".repeat(left) + text + " ".repeat(pad - left);
    }

    private String getMowerGutter(ReadOnlyGameState state, int row, int layer) {
        if (layer != 0) {
            return " ".repeat(MOWER_INNER_WIDTH);
        }
        if (state.isBrainsMode()) {
            Brain brain = state.getBrains().stream().filter(b -> b.row == row).findFirst().orElse(null);
            if (brain != null && !brain.isCollected()) {
                return "🧠";
            }
            return "··";
        }
        LawnMower mower = state.getBoard().getLawnMowers(row);
        if (mower != null && mower.isActive()) {
            return "🚜";
        }
        return "··";
    }

    private String renderCellLayer(ReadOnlyGameState state, int row, int col, int layer) {
        Tile tile = state.getBoard().getTile(row, col);
        Plant plant = tile != null ? tile.getPlant() : null;
        Plant lily = tile != null ? tile.getLilyPad() : null;
        if (plant == null && lily != null) {
            plant = lily;
            lily = null;
        }
        Zombie zombie = findZombieAt(state, row, col);
        boolean hasProjectile = hasProjectileInCell(state, row, col);
        boolean hasSun = hasSunInCell(state, row, col);
        boolean hasSeed = state.getSeedDropAt(row, col) != null;
        Vase vase = state.getVaseAt(row, col);
        Barrel barrel = state.getBarrelAt(row, col);
        Grave grave = state.getGraveAt(row, col);

        String content = switch (layer) {
            case 0 -> buildEntityRow(plant, zombie, hasProjectile);
            case 1 -> buildHealthRow(plant, zombie);
            default -> buildGroundRow(tile, lily, hasSun, hasSeed, vase, barrel, grave);
        };
        content = engine.padVisible(content, CELL_INNER_WIDTH);
        return applyTerrainBg(tile, content);
    }

    private String buildEntityRow(Plant plant, Zombie zombie, boolean hasProjectile) {
        StringBuilder row = new StringBuilder();
        if (plant != null) {
            row.append(getPlantSymbol(plant));
            row.append(getPlantStatusSymbol(plant));
        } else {
            row.append("    ");
        }
        if (hasProjectile) {
            row.append(GREEN).append("●").append(RESET);
        } else {
            row.append(" ");
        }
        if (zombie != null) {
            row.append(getZombieSymbol(zombie));
            row.append(getZombieStatusSymbol(zombie));
        }
        return row.toString();
    }

    private String buildHealthRow(Plant plant, Zombie zombie) {
        String left = plant != null
                ? getCompactHealthBar(Math.min(100, (plant.hp * 100) / Math.max(1, plant.totalHP)))
                : "   ";
        String right = zombie != null
                ? getCompactHealthBar(Math.min(100, (zombie.hp * 100) / Math.max(1, zombie.type.baseStats.hp)))
                : "   ";
        return engine.padVisible(left, 4) + "  " + engine.padVisible(right, 4);
    }

    private String buildGroundRow(Tile tile, Plant lily, boolean hasSun, boolean hasSeed,
            Vase vase, Barrel barrel, Grave grave) {
        StringBuilder row = new StringBuilder();
        if (hasSeed) {
            row.append(GREEN).append("🌱").append(RESET);
        } else if (hasSun) {
            row.append(YELLOW).append("☀️").append(RESET);
        } else {
            row.append("  ");
        }

        if (vase != null) {
            row.append(getVaseSymbol(vase));
        } else if (barrel != null) {
            row.append("🛢️");
        } else if (grave != null) {
            row.append("🪦");
        } else if (tile != null && tile.hasBeachPost()) {
            row.append("⚓");
        } else if (lily != null) {
            row.append("🪷");
        } else {
            row.append("  ");
        }

        if (tile != null && tile.isIce()) {
            IceDirection dir = tile.getDirection();
            if (dir == IceDirection.UP) {
                row.append(CYAN).append("↑").append(RESET);
            } else if (dir == IceDirection.DOWN) {
                row.append(CYAN).append("↓").append(RESET);
            } else {
                row.append(CYAN).append("*").append(RESET);
            }
        } else if (tile != null && tile.isWater()) {
            row.append(BLUE).append("~").append(RESET);
        } else if (tile != null && tile.isNecromancy()) {
            row.append(PURPLE).append("‡").append(RESET);
        } else {
            row.append(" ");
        }
        return row.toString();
    }

    private String applyTerrainBg(Tile tile, String content) {
        if (tile == null) {
            return content;
        }
        String bg = switch (tile.getType()) {
            case WATER -> BG_WATER;
            case ICE -> BG_ICE;
            case NECROMANCY -> BG_NECRO;
            default -> "";
        };
        if (bg.isEmpty()) {
            return content;
        }
        return bg + content.replace(RESET, RESET + bg) + RESET;
    }

    private String getPlantStatusSymbol(Plant plant) {
        if (plant.isPlantFoodActive) {
            return "✨";
        }
        if (plant.isFrostbiteFreezeActive()) {
            return "🧊";
        }
        PlantStun stun = plant.getActiveStun();
        if (stun != null) {
            return switch (stun.getKind()) {
                case CAT -> "🐱";
                case OCTOPUS -> "🐙";
                case FROZEN -> "🧊";
            };
        }
        return "  ";
    }

    private String getZombieStatusSymbol(Zombie zombie) {
        if (zombie.isHypnotized) {
            return "💜";
        }
        if (zombie.isFrozen || zombie.isIced() || zombie.frozenTicks > 0) {
            return "🧊";
        }
        if (zombie.stunned) {
            return "💫";
        }
        if (zombie.isEating) {
            return "🍴";
        }
        if (zombie.armor != null && zombie.armor.isIntact()) {
            return "🛡️";
        }
        if (zombie.hasSandstorm()) {
            return "🌪️";
        }
        return "  ";
    }

    private String getVaseSymbol(Vase vase) {
        return switch (vase.vaseType) {
            case PLANT -> "🪴";
            case ZOMBIE -> "💀";
            case NORMAL -> "🏺";
        };
    }

    private String getPlantSymbol(Plant plant) {
        if (plant.type == PlantType.Lily_Pad) {
            return "🪷";
        }
        return switch (plant.type.name) {
            case "Sunflower" -> "🌻";
            case "Peashooter" -> "🌱";
            case "Snow Pea" -> "❄️";
            case "Wall-nut" -> "🧱";
            case "Bowling Wall-nut" -> "🎳";
            case "Bowling Explode-o-nut" -> "💣";
            case "Giant Bowling Wall-nut" -> "⬤ ";
            case "Repeater" -> "🌿";
            case "Cherry Bomb" -> "💥";
            case "Torchwood" -> "🔥";
            case "Potato-Mine", "Primal_Potato-Mine" -> "🥔";
            case "Grave Buster" -> "⛏️";
            default -> "🌿";
        };
    }

    private String getZombieSymbol(Zombie zombie) {
        return "🧟";
    }

    private String getCompactHealthBar(int percent) {
        if (percent > 66) {
            return GREEN + "███" + RESET;
        }
        if (percent > 33) {
            return YELLOW + "██ " + RESET;
        }
        return RED + "█  " + RESET;
    }

    private Zombie findZombieAt(ReadOnlyGameState state, int row, int col) {
        return state.getZombies().stream()
                .filter(z -> z != null && z.isAlive && z.row == row && z.col == col)
                .min(java.util.Comparator.comparingDouble(z -> z.position.x))
                .orElseGet(() -> state.getZombies().stream()
                        .filter(z -> z != null && z.isAlive && z.row == row
                                && z.position.x >= col * ReadOnlyGameState.CELL_WIDTH
                                && z.position.x < (col + 1) * ReadOnlyGameState.CELL_WIDTH)
                        .min(java.util.Comparator.comparingDouble(z -> z.position.x))
                        .orElse(null));
    }

    private boolean hasProjectileInCell(ReadOnlyGameState state, int row, int col) {
        int cellStartX = col * ReadOnlyGameState.CELL_WIDTH;
        int cellEndX = (col + 1) * ReadOnlyGameState.CELL_WIDTH;
        return state.getProjectiles().stream()
                .anyMatch(p -> p.row == row &&
                        p.position.x >= cellStartX &&
                        p.position.x < cellEndX);
    }

    private boolean hasSunInCell(ReadOnlyGameState state, int row, int col) {
        int cellStartX = col * ReadOnlyGameState.CELL_WIDTH;
        int cellEndX = (col + 1) * ReadOnlyGameState.CELL_WIDTH;
        return state.getSunDrops().stream()
                .anyMatch(s -> s.row == row &&
                        s.position.x >= cellStartX &&
                        s.position.x < cellEndX);
    }
}
