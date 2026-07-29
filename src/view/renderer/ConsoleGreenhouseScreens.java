package view.renderer;

import static view.renderer.ConsoleTheme.BOLD;
import static view.renderer.ConsoleTheme.CYAN;
import static view.renderer.ConsoleTheme.GRAY;
import static view.renderer.ConsoleTheme.GREEN;
import static view.renderer.ConsoleTheme.RESET;
import static view.renderer.ConsoleTheme.WHITE;
import static view.renderer.ConsoleTheme.YELLOW;

import controller.GreenhouseController;
import model.core.Position;
import model.greenhouse.Greenhouse;
import model.greenhouse.Pot;
import model.storage.user.User;

final class ConsoleGreenhouseScreens {

    private static final int GH_ROWS = 4;
    private static final int GH_COLS = 5;
    private static final int POT_WIDTH = 13;
    private static final int POT_HEIGHT = 5;

    private final ConsoleRenderEngine engine;

    ConsoleGreenhouseScreens(ConsoleRenderEngine engine) {
        this.engine = engine;
    }

    String getGreenHouseScreen(GreenhouseController greenhouseController) {
        StringBuilder sb = new StringBuilder();
        User user = greenhouseController.getUser();
        Greenhouse greenhouse = user != null ? user.greenhouse : null;

        sb.append(engine.getHeaderBox("🌱  " + BOLD + "GREENHOUSE" + RESET + "  🧟", GREEN));
        sb.append("\n");

        int coins = user != null ? user.coins : 0;
        int gems = user != null ? user.gems : 0;
        sb.append("  ").append(BOLD).append("Coins:").append(RESET).append(" ").append(coins);
        sb.append("    ").append(BOLD).append("Gems:").append(RESET).append(" ").append(gems).append("\n");
        sb.append("\n");
        sb.append(engine.getMessages());
        sb.append("\n");
        if (!greenhouseController.isPotsVisible()) {
            sb.append("  " + CYAN + "1." + RESET + " Greenhouse: " + GREEN + "show greenhouse" + RESET + "\n");
            sb.append("  " + CYAN + "2." + RESET + " Shop: " + GREEN + "enter shop" + RESET + "\n");
            sb.append("  " + CYAN + "3." + RESET + " Back: " + GREEN + "menu exit" + RESET + "\n");
            sb.append("  " + CYAN + "4." + RESET + " Quit: " + GREEN + "quit" + RESET + "\n");
        } else {
            sb.append(drawGreenhousePanel(greenhouse));
            sb.append("\n");

            sb.append("  ").append(CYAN).append("Commands:").append(RESET).append("\n");
            sb.append("  ").append(GREEN).append("plant pot at (row,col)").append(RESET).append("  ");
            sb.append(GREEN).append("grow (row,col)").append(RESET).append("  ");
            sb.append(GREEN).append("collect (row,col)").append(RESET).append("\n");
            sb.append("  ").append(GREEN).append("enter shop").append(RESET).append("  ");
            sb.append(GREEN).append("menu exit").append(RESET).append("\n");
            sb.append("\n");
        }
        return sb.toString();
    }

    private String drawGreenhousePanel(Greenhouse greenhouse) {
        StringBuilder sb = new StringBuilder();
        int innerWidth = GH_COLS * (POT_WIDTH + 1) - 1;
        sb.append("  ").append(GREEN).append("┌").append("─".repeat(innerWidth + 2)).append("┐").append(RESET)
                .append("\n");

        for (int row = 1; row <= GH_ROWS; row++) {
            String[][] potLines = new String[GH_COLS][POT_HEIGHT];
            for (int col = 1; col <= GH_COLS; col++) {
                Pot pot = greenhouse != null ? greenhouse.getPot(new Position(col, row)) : null;
                potLines[col - 1] = drawPot(pot, row, col);
            }

            for (int line = 0; line < POT_HEIGHT; line++) {
                sb.append("  ").append(GREEN).append("│ ").append(RESET);
                for (int col = 0; col < GH_COLS; col++) {
                    sb.append(potLines[col][line]);
                    if (col < GH_COLS - 1) {
                        sb.append(" ");
                    }
                }
                sb.append(GREEN).append(" │").append(RESET).append("\n");
            }
        }

        sb.append("  ").append(GREEN).append("└").append("─".repeat(innerWidth + 2)).append("┘").append(RESET);
        return sb.toString();
    }

    private String[] drawPot(Pot pot, int row, int col) {
        if (pot == null || pot.isLocked()) {
            return drawLockedPot(row, col);
        }
        if (pot.isEmpty()) {
            return drawEmptyPot(row, col);
        }
        if (pot.isReady()) {
            return drawReadyPot(pot, row, col);
        }
        return drawGrowingPot(pot, row, col);
    }

    private String[] drawLockedPot(int row, int col) {
        String[] lines = new String[POT_HEIGHT];
        lines[0] = potTop();
        lines[1] = potLine("🔒", GRAY);
        lines[2] = potLine("LOCKED", GRAY);
        lines[3] = potBottom();
        lines[4] = potCoord(row, col);
        return lines;
    }

    private String[] drawEmptyPot(int row, int col) {
        String[] lines = new String[POT_HEIGHT];
        lines[0] = potTop();
        lines[1] = potLine("🪴", YELLOW);
        lines[2] = potLine("Empty", WHITE);
        lines[3] = potBottom();
        lines[4] = potCoord(row, col);
        return lines;
    }

    private String[] drawGrowingPot(Pot pot, int row, int col) {
        String[] lines = new String[POT_HEIGHT];
        String plantLabel = getPlantLabel(pot);

        lines[0] = potTop();
        lines[1] = potLine("🌱", GREEN);
        lines[2] = potLine(plantLabel, CYAN);
        lines[3] = potLine(drawTimer(pot), YELLOW);
        lines[4] = potCoord(row, col);
        return lines;
    }

    private String[] drawReadyPot(Pot pot, int row, int col) {
        String[] lines = new String[POT_HEIGHT];
        String icon = getPlantIcon(pot);
        String plantLabel = getPlantLabel(pot);

        lines[0] = potTop();
        lines[1] = potLine(icon, GREEN);
        lines[2] = potLine(plantLabel, CYAN);
        lines[3] = potLine(drawReadyLabel(), GREEN);
        lines[4] = potCoord(row, col);
        return lines;
    }

    private String drawTimer(Pot pot) {
        return pot.getRemainingTimeText();
    }

    private String drawReadyLabel() {
        return "✅ READY";
    }

    private String getPlantIcon(Pot pot) {
        if (pot.getPlantClass() == Pot.PlantClass.NORMAL_PLANT || pot.getPlantType() == null) {
            return "🌼";
        }
        return "🌿";
    }

    private String getPlantLabel(Pot pot) {
        if (pot.getPlantClass() == Pot.PlantClass.NORMAL_PLANT || pot.getPlantType() == null) {
            return "Marigold";
        }
        return engine.truncate(pot.getPlantType().name, POT_WIDTH - 2);
    }

    private String potTop() {
        return GRAY + "┌" + "─".repeat(POT_WIDTH - 2) + "┐" + RESET;
    }

    private String potBottom() {
        return GRAY + "└" + "─".repeat(POT_WIDTH - 2) + "┘" + RESET;
    }

    private String potLine(String text, String color) {
        String plain = engine.truncate(text, POT_WIDTH - 2);
        int pad = POT_WIDTH - 2 - engine.displayWidth(engine.stripAnsi(plain));
        return GRAY + "│" + RESET + color + plain + RESET
                + " ".repeat(Math.max(0, pad)) + GRAY + "│" + RESET;
    }

    private String potCoord(int row, int col) {
        String coord = "(" + col + "," + row + ")";
        int pad = POT_WIDTH - coord.length();
        int left = Math.max(0, pad / 2);
        return " ".repeat(left) + GRAY + coord + RESET
                + " ".repeat(Math.max(0, pad - left));
    }
}
