package view.renderer;

import java.util.ArrayList;
import java.util.List;

import controller.PickPlantsController;
import model.core.GameLoop;
import model.core.ReadOnlyGameState;
import model.data.plant.Plant;
import model.data.plant.PlantType;
import model.data.zombie.Zombie;
import model.service.GameNavigationState;
import model.service.GameNavigationState.Phase;
import model.storage.user.SafetyQuestion;
import model.world.ChapterCatalog;
import model.world.ChapterType;

public class ConsoleRenderer implements Renderer {

    private static final int GRID_ROWS = ReadOnlyGameState.GRID_ROWS;
    private static final int GRID_COLS = ReadOnlyGameState.GRID_COLS;
    private static final int CELL_WIDTH = 5;
    private static final int CELL_HEIGHT = 2;
    private static final int MAX_MESSAGES = 4;
    private static final int SCREEN_WIDTH = 59;
    private static final int RENDER_HEIGHT = 40;

    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String PURPLE = "\u001B[35m";
    private static final String CYAN = "\u001B[36m";
    private static final String WHITE = "\u001B[37m";
    private static final String ORANGE = "\u001B[38;5;208m";
    private static final String GRAY = "\u001B[38;5;240m";
    private static final String BOLD = "\u001B[1m";

    private List<String> messages = new java.util.ArrayList<>();

    private String[] lastRenderLines = new String[RENDER_HEIGHT];
    private String currentScreenKey = "";
    private boolean needsFullClear = true;
    private String promptInput = "";
    private static final int PROMPT_LINE = RENDER_HEIGHT + 2;
    private static final int PROMPT_PREFIX_COLUMNS = 2;

    @Override
    public void prepareScreen(String screenKey) {
        if (!screenKey.equals(currentScreenKey)) {
            currentScreenKey = screenKey;
            needsFullClear = true;
            java.util.Arrays.fill(lastRenderLines, null);
        }
    }

    private void render(String content) {
        synchronized (ConsoleRenderer.class) {
            List<String> lines = new ArrayList<>();

            for (String line : content.split("\n")) {
                lines.add(line);
            }

            while (lines.size() < RENDER_HEIGHT) {
                lines.add("");
            }
            if (lines.size() > RENDER_HEIGHT) {
                lines = new ArrayList<>(lines.subList(0, RENDER_HEIGHT));
            }

            boolean changed = needsFullClear;
            if (!changed) {
                for (int i = 0; i < RENDER_HEIGHT; i++) {
                    String line = lines.get(i);
                    if (!java.util.Objects.equals(line, lastRenderLines[i])) {
                        changed = true;
                        break;
                    }
                }
            }

            if (!changed) {
                return;
            }

            if (needsFullClear) {
                System.out.print("\033[2J");
                needsFullClear = false;
            }

            for (int i = 0; i < RENDER_HEIGHT; i++) {
                System.out.printf("\033[%d;1H\033[2K%s", i + 1, lines.get(i));
                lastRenderLines[i] = lines.get(i);
            }
            System.out.printf("\033[%d;1H\033[2K%s", RENDER_HEIGHT + 1, "─".repeat(SCREEN_WIDTH));

            drawCommandPrompt();
            System.out.flush();
        }
    }

    private void drawCommandPrompt() {
        System.out.printf("\033[%d;1H\033[2K", PROMPT_LINE);
        System.out.print(CYAN + "> " + RESET + promptInput);
        int column = PROMPT_PREFIX_COLUMNS + promptInput.length() + 1;
        System.out.printf("\033[%d;%dH", PROMPT_LINE, column);
    }

    private String getRegisterScreen(List<SafetyQuestion> questions) {
        StringBuilder sb = new StringBuilder();
        String title = "🌱  " + BOLD + "PLANTS VS ZOMBIES 2 | Register" + RESET + "  🧟";

        sb.append(getHeaderBox(title, GREEN));
        sb.append("\n");
        sb.append("  " + CYAN + "1." + RESET + " Register: " + GREEN
                + "register -u <username> -p <password> <password_confirm> -n <nickname> -e <email> -g <gender>"
                + RESET + "\n");
        sb.append("  " + CYAN + "2." + RESET + " Pick Security Question: " + GREEN
                + "pick question -q <question_number> -a <answer> -c <answer_confirm>" + RESET + "\n");
        sb.append("  " + CYAN + "3." + RESET + " Login Menu: " + GREEN + "menu enter login" + RESET + "\n");
        sb.append("  " + CYAN + "4." + RESET + " Quit: " + GREEN + "quit" + RESET + "\n");
        sb.append("\n");
        sb.append("  " + BOLD + "Safety Questions:" + RESET + "\n");
        for (int i = 0; i < questions.size(); i++) {
            sb.append("    ").append(CYAN).append(i + 1).append(".").append(RESET).append(" ")
                    .append(questions.get(i).question).append("\n");
        }
        sb.append("\n");
        sb.append(getMessages());

        return sb.toString();
    }

    @Override
    public void renderRegisterScreen(List<SafetyQuestion> questions) {
        render(getRegisterScreen(questions));
    }

    private String getLoginScreen(boolean isAwaitingSecurityAnswer, boolean isAwaitingNewPassword,
            String passwordResetQuestion) {
        StringBuilder sb = new StringBuilder();
        String title = "🌱  " + BOLD + "PLANTS VS ZOMBIES 2 | Login" + RESET + "  🧟";

        sb.append(getHeaderBox(title, GREEN));
        sb.append("\n");
        sb.append("  " + CYAN + "1." + RESET + " Login: " + GREEN
                + "login -u <username> -p <password>" + RESET + "\n");
        sb.append("  " + CYAN + "2." + RESET + " Stay Logged In: " + GREEN
                + "login -u <username> -p <password> -stay-logged-in" + RESET + "\n");
        sb.append("  " + CYAN + "3." + RESET + " Forget Password: " + GREEN
                + "forget password -u <username> -e <email>" + RESET + "\n");
        sb.append("  " + CYAN + "4." + RESET + " Answer Security Question: " + GREEN
                + "answer -a <answer>" + RESET + "\n");
        sb.append("  " + CYAN + "5." + RESET + " Reset Password: " + GREEN
                + "reset password -p <password> <password_confirm>" + RESET + "\n");
        sb.append("  " + CYAN + "6." + RESET + " Register Menu: " + GREEN + "menu exit" + RESET + "\n");
        sb.append("  " + CYAN + "7." + RESET + " Quit: " + GREEN + "quit" + RESET + "\n");

        if (isAwaitingSecurityAnswer && passwordResetQuestion != null) {
            sb.append("\n");
            sb.append("  " + BOLD + "Security Question:" + RESET + " ")
                    .append(passwordResetQuestion).append("\n");
        }
        if (isAwaitingNewPassword) {
            sb.append("\n");
            sb.append("  " + YELLOW + "Enter your new password using reset password." + RESET + "\n");
        }

        sb.append("\n");
        sb.append(getMessages());

        return sb.toString();
    }

    @Override
    public void renderLoginScreen(boolean isAwaitingSecurityAnswer, boolean isAwaitingNewPassword,
            String passwordResetQuestion) {
        render(getLoginScreen(isAwaitingSecurityAnswer, isAwaitingNewPassword, passwordResetQuestion));
    }

    private String getMainScreen() {
        StringBuilder sb = new StringBuilder();
        String title = "🌱  " + BOLD + "PLANTS VS ZOMBIES 2" + RESET + "  🧟";

        sb.append(getHeaderBox(title, GREEN));
        sb.append("\n");
        sb.append("  " + CYAN + "1." + RESET + " Start Game: " + GREEN + "menu enter game" + RESET + "\n");
        sb.append("  " + CYAN + "2." + RESET + " Settings: " + GREEN + "menu enter settings" + RESET + "\n");
        sb.append("  " + CYAN + "3." + RESET + " News: " + GREEN + "menu enter news" + RESET + "\n");
        sb.append("  " + CYAN + "4." + RESET + " Profile: " + GREEN + "menu enter profile" + RESET + "\n");
        sb.append("  " + CYAN + "5." + RESET + " Logout: " + GREEN + "menu logout" + RESET + "\n");
        sb.append("  " + CYAN + "6." + RESET + " Quit: " + GREEN + "quit" + RESET + "\n");
        sb.append("\n");
        sb.append(getMessages());

        return sb.toString();
    }

    @Override
    public void renderMainScreen() {
        render(getMainScreen());
    }

    private String getGameScreen(ReadOnlyGameState state) {
        StringBuilder sb = new StringBuilder();
        sb.append(getHUD(state));
        sb.append("\n");
        sb.append(getGrid(state));
        sb.append("\n");
        sb.append(getMessages());
        return sb.toString();
    }

    @Override
    public void renderGameScreen(ReadOnlyGameState state) {
        render(getGameScreen(state));
    }

    @Override
    public void renderLevelSelectionScreen(GameNavigationState gameNavigation) {
        render(getLevelSelectionScreen(gameNavigation));
    }

    private String getLevelSelectionScreen(GameNavigationState gameNavigation) {
        StringBuilder sb = new StringBuilder();

        if (gameNavigation.phase == Phase.CHAPTER) {
            String title = "🌱  " + BOLD + "PLANTS VS ZOMBIES 2 | Chapters" + RESET + "  🧟";
            sb.append(getHeaderBox(title, GREEN));
            sb.append("\n");
            sb.append("  " + CYAN + "1." + RESET + " Enter Chapter: " + GREEN
                    + "menu enter chapter -c <chaptername>" + RESET + "\n");
            sb.append("  " + CYAN + "2." + RESET + " Back: " + GREEN + "menu exit" + RESET + "\n");
            sb.append("  " + CYAN + "3." + RESET + " Quit: " + GREEN + "quit" + RESET + "\n");
            sb.append("\n");
            sb.append("  " + BOLD + "Chapters:" + RESET + "\n");
            for (ChapterType chapter : ChapterType.values()) {
                boolean unlocked = gameNavigation.unlockedChapters.contains(chapter);
                String status = unlocked ? GREEN + "unlocked" + RESET : RED + "locked" + RESET;
                sb.append("    ").append(CYAN).append(ChapterCatalog.commandName(chapter)).append(RESET)
                        .append(" - ").append(ChapterCatalog.displayName(chapter))
                        .append(" (").append(status).append(")\n");
            }
        } else if (gameNavigation.phase == Phase.LEVEL) {
            String chapterName = ChapterCatalog.displayName(gameNavigation.selectedChapter);
            String title = "🌱  " + BOLD + "PLANTS VS ZOMBIES 2 | " + chapterName + RESET + "  🧟";
            sb.append(getHeaderBox(title, GREEN));
            sb.append("\n");
            sb.append("  " + CYAN + "1." + RESET + " Select Level: " + GREEN
                    + "select level -n <level_number>" + RESET + "\n");
            sb.append("  " + CYAN + "2." + RESET + " Back: " + GREEN + "menu exit" + RESET + "\n");
            sb.append("  " + CYAN + "3." + RESET + " Quit: " + GREEN + "quit" + RESET + "\n");
            sb.append("\n");
            sb.append("  " + BOLD + "Levels:" + RESET + "\n");
            for (int i = 1; i <= ChapterCatalog.LEVELS_PER_CHAPTER; i++) {
                sb.append("    ").append(CYAN).append(i).append(RESET).append("\n");
            }
        } else if (gameNavigation.phase == Phase.PLANT) {
            String title = "🌱  " + BOLD + "PLANTS VS ZOMBIES 2 | Pick Plants" + RESET + "  🧟";
            sb.append(getHeaderBox(title, GREEN));
            sb.append("\n");
            sb.append("  " + CYAN + "1." + RESET + " Show Plants: " + GREEN + "show available plants" + RESET + "\n");
            sb.append("  " + CYAN + "2." + RESET + " Add Plant: " + GREEN + "add plant -t <type>" + RESET + "\n");
            sb.append("  " + CYAN + "3." + RESET + " Remove Plant: " + GREEN + "remove plant -t <type>" + RESET + "\n");
            sb.append("  " + CYAN + "4." + RESET + " Start Game: " + GREEN + "start game" + RESET + "\n");
            sb.append("  " + CYAN + "5." + RESET + " Back: " + GREEN + "menu exit" + RESET + "\n");
            sb.append("  " + CYAN + "6." + RESET + " Quit: " + GREEN + "quit" + RESET + "\n");
            sb.append("\n");
            sb.append("  " + BOLD + "Selected (" + gameNavigation.selectedPlants.size() + "/"
                    + PickPlantsController.MAX_SELECTED_PLANTS + "):" + RESET + "\n");
            if (gameNavigation.selectedPlants.isEmpty()) {
                sb.append("    ").append(GRAY).append("(none)").append(RESET).append("\n");
            } else {
                for (PlantType plant : gameNavigation.selectedPlants) {
                    sb.append("    ").append(GREEN).append(plant.name).append(RESET).append("\n");
                }
            }
            sb.append("\n");
            sb.append("  " + BOLD + "Unlocked Plants:" + RESET + "\n");
            for (PlantType plant : gameNavigation.unlockedPlants) {
                sb.append("    ").append(CYAN).append(plant.name).append(RESET).append("\n");
            }
        } else {
            sb.append(getHeaderBox("🌱  " + BOLD + "PLANTS VS ZOMBIES 2 | Game" + RESET + "  🧟", GREEN));
        }

        sb.append("\n");
        sb.append(getMessages());
        return sb.toString();
    }

    @Override
    public void renderGreenHouseScreen() {
    }

    @Override
    public void renderShopScreen() {
    }

    @Override
    public void renderCollectionScreen() {
    }

    @Override
    public void renderSettingOverlay() {
        render(getMenuOverlay("Settings", YELLOW));
    }

    @Override
    public void renderNewsOverlay() {
        render(getMenuOverlay("News", BLUE));
    }

    @Override
    public void renderProfileOverlay() {
        render(getMenuOverlay("Profile", PURPLE));
    }

    private String getMenuOverlay(String menuName, String color) {
        StringBuilder sb = new StringBuilder();
        String title = "🌱  " + BOLD + "PLANTS VS ZOMBIES 2 | " + menuName + RESET + "  🧟";

        sb.append(getHeaderBox(title, color));
        sb.append("\n");
        sb.append("  " + CYAN + "1." + RESET + " Back: " + GREEN + "menu exit" + RESET + "\n");
        sb.append("\n");
        sb.append(getMessages());
        return sb.toString();
    }

    @Override
    public void renderPauseOverlay() {
    }

    @Override
    public void renderQuestsOverlay() {
    }

    @Override
    public void renderPlantSelectorOverlay() {
    }

    @Override
    public void renderGameOverOverlay(boolean won, int score, int wavesSurvived) {
        // if (won) {
        // renderHeaderBox(" 🎉 " + BOLD + "LEVEL COMPLETE!", GREEN);
        // } else {
        // renderHeaderBox(" 💀 " + BOLD + "GAME OVER", RED);
        // }
        // System.out.println();
        // System.out.println(" " + CYAN + "Score:" + RESET + " " + score);
        // System.out.println(" " + CYAN + "Waves Survived:" + RESET + " " +
        // wavesSurvived);
        // System.out.println();
        // // suggest commands for each option
        // System.out.println(" " + CYAN + "1." + RESET + " Restart");
        // System.out.println(" " + CYAN + "2." + RESET + " Main Menu");
        // System.out.println();
    }

    @Override
    public void renderLevelCompleteOverlay(int stars, int score) {
    }

    private String getHUD(ReadOnlyGameState state) {
        String status = state.isGameOver() ? "💀" : state.isLevelComplete() ? "⭐" : "▶️";
        String title = String.format("%s☀️ : %-4d  " +
                "%s🌊 : %-3d  " +
                "%s🧟 : %-3d  " +
                "%s🌿 : %-2d  " +
                "%s⏱️ %-4ds  " +
                CYAN + "%s" + RESET +
                CYAN + " %2s" + RESET,
                YELLOW, state.getSunAmount(),
                CYAN, state.getCurrentWave(),
                RED, state.getZombies().size(),
                PURPLE, state.getPlantFoodAmount(),
                WHITE, state.getTotalTicks() / GameLoop.TICKS_PER_SECOND,
                status, "");
        return (getHeaderBox(title, CYAN));
    }

    @Override
    public void renderHUD(ReadOnlyGameState state) {
    }

    private String getGrid(ReadOnlyGameState state) {
        StringBuilder sb = new StringBuilder();
        sb.append("     ");
        for (int col = 0; col < GRID_COLS; col++) {
            sb.append(String.format("  %d   ", col));
        }
        sb.append("\n");

        sb.append("    ┌─────┬─────┬─────┬─────┬─────┬─────┬─────┬─────┬─────┐\n");

        for (int row = 0; row < GRID_ROWS; row++) {
            for (int layer = 0; layer < CELL_HEIGHT; layer++) {
                if (layer == 0) {
                    sb.append(String.format(" %d  │", row));
                } else {
                    sb.append("    │");
                }

                for (int col = 0; col < GRID_COLS; col++) {
                    Plant plant = state.getPlantAt(row, col);
                    Zombie zombie = findZombieAt(state, row, col);
                    boolean hasProjectile = hasProjectileInCell(state, row, col);
                    boolean hasSun = hasSunInCell(state, row, col);

                    String cellContent = getCellLayer(plant, zombie, hasProjectile, hasSun, layer);
                    sb.append(String.format(" %s │", cellContent));
                }
                sb.append("\n");
            }

            if (row < GRID_ROWS - 1) {
                sb.append("    ├─────┼─────┼─────┼─────┼─────┼─────┼─────┼─────┼─────┤\n");
            }
        }

        sb.append("    └─────┴─────┴─────┴─────┴─────┴─────┴─────┴─────┴─────┘");

        return sb.toString();
    }

    @Override
    public void renderGrid(ReadOnlyGameState state) {
    }

    private String getCellLayer(Plant plant, Zombie zombie, boolean hasProjectile, boolean hasSun, int layer) {
        if (plant != null) {
            return getPlantLayer(plant, layer);
        } else if (zombie != null) {
            return getZombieLayer(zombie, layer);
        } else if (hasProjectile) {
            return getProjectileLayer(layer);
        } else if (hasSun) {
            return getSunLayer(layer);
        } else {
            return getEmptyLayer(layer);
        }
    }

    private String getPlantLayer(Plant plant, int layer) {
        int healthPercent = Math.min(100, (plant.hp * 100) / plant.totalHP);

        switch (layer) {
            case 0:
                return getPlantSymbol(plant);
            case 1:
                return getHealthBar(healthPercent);
            default:
                return "   ";
        }
    }

    private String getZombieLayer(Zombie zombie, int layer) {
        int healthPercent = Math.min(100, (zombie.hp * 100) / zombie.type.baseStats.hp);

        switch (layer) {
            case 0:
                return getZombieSymbol(zombie);
            case 1:
                return getHealthBar(healthPercent);
            default:
                return "   ";
        }
    }

    private String getProjectileLayer(int layer) {
        switch (layer) {
            case 0:
                return GREEN + " ● " + RESET;
            case 1:
                return "   ";
            default:
                return "   ";
        }
    }

    private String getSunLayer(int layer) {
        switch (layer) {
            case 0:
                return YELLOW + "☀️ " + RESET;
            case 1:
                return "   ";
            default:
                return "   ";
        }
    }

    private String getEmptyLayer(int layer) {
        return GRAY + " · " + RESET;
    }

    private String getPlantSymbol(Plant plant) {
        switch (plant.type.name) {
            case "Sunflower":
                return "🌻 ";
            case "Peashooter":
                return "🌱 ";
            case "Snow Pea":
                return "❄️ ";
            case "Wall-nut":
                return "🧱 ";
            case "Repeater":
                return "🌿 ";
            case "Cherry Bomb":
                return "💥 ";
            default:
                return "🌿 ";
        }
    }

    private String getZombieSymbol(Zombie zombie) {
        return "🧟 ";
    }

    private String getHealthBar(int percent) {
        if (percent > 66)
            return GREEN + "███" + RESET;
        else if (percent > 33)
            return YELLOW + "██ " + RESET;
        else
            return RED + "█  " + RESET;
    }

    @Override
    public void renderSunDrops(ReadOnlyGameState state) {
    }

    @Override
    public void renderZombieDetails(ReadOnlyGameState state) {
        // if (state.zombies.isEmpty())
        // return;

        // System.out.println("┌" + "─".repeat(70) + "┐");
        // System.out.printf("│ %s🧟 Zombies (%d)%s" + " ".repeat(55) + "│%n",
        // RED, state.zombies.size(), RESET);

        // int count = 0;
        // for (Zombie z : state.zombies) {
        // if (count >= 3) {
        // System.out.printf("│ ... and %d more%s" + " ".repeat(55) + "│%n",
        // state.zombies.size() - 3, RESET);
        // break;
        // }
        // int healthPercent = Math.min(100, (z.hp * 100) / z.type.baseStats.hp);
        // String healthBar = getHealthBar(healthPercent);
        // System.out.printf("│ %s %-15s HP: %-4d Row: %d X: %-5.1f %s" + " ".repeat(15)
        // + "│%n",
        // healthBar,
        // z.type.name,
        // z.hp,
        // z.row,
        // z.position.x,
        // RESET);
        // count++;
        // }
        // System.out.println("└" + "─".repeat(70) + "┘");
    }

    @Override
    public void renderPlantDetails(ReadOnlyGameState state) {
        // if (state.plants.isEmpty())
        // return;

        // System.out.println("┌" + "─".repeat(70) + "┐");
        // System.out.printf("│ %s🌱 Plants (%d)%s" + " ".repeat(55) + "│%n",
        // GREEN, state.plants.size(), RESET);

        // int count = 0;
        // for (Plant p : state.plants) {
        // if (count >= 3) {
        // System.out.printf("│ ... and %d more%s" + " ".repeat(55) + "│%n",
        // state.plants.size() - 3, RESET);
        // break;
        // }
        // int healthPercent = Math.min(100, (p.hp * 100) / p.type.baseStats.hp);
        // String healthBar = getHealthBar(healthPercent);
        // System.out.printf("│ %s %-15s HP: %-4d Row: %d Col: %d %s" + " ".repeat(15) +
        // "│%n",
        // healthBar,
        // p.type.name,
        // p.hp,
        // p.row,
        // p.col,
        // RESET);
        // count++;
        // }
        // System.out.println("└" + "─".repeat(70) + "┘");
    }

    @Override
    public void renderProjectiles(ReadOnlyGameState state) {
    }

    @Override
    public void renderPlantSelectorOverlay(List<PlantType> availablePlants, int selectedIndex, int sunAmount) {
        // if (availablePlants == null || availablePlants.isEmpty())
        // return;

        // System.out.print(GREEN + "🌱 " + RESET);
        // for (int i = 0; i < availablePlants.size(); i++) {
        // PlantType type = availablePlants.get(i);
        // String prefix = (i == selectedIndex) ? "▶ " : " ";
        // boolean canAfford = sunAmount >= type.baseStats.cost;
        // String color = canAfford ? GREEN : RED;
        // System.out.printf("%s%s%s (%d)%s",
        // prefix,
        // color,
        // type.name,
        // type.baseStats.cost,
        // RESET);
        // if (i < availablePlants.size() - 1) {
        // System.out.print(" | ");
        // }
        // }
        // System.out.println();
    }

    @Override
    public void renderMessage(String message) {
        messages.add(message);
        if (messages.size() > MAX_MESSAGES) {
            messages.remove(0);
        }
    }

    @Override
    public void renderError(String error) {
        messages.add(RED + error + RESET);
        if (messages.size() > MAX_MESSAGES) {
            messages.remove(0);
        }
    }

    public String getMessages() {
        StringBuilder sb = new StringBuilder();
        int MESSAGE_BOX_WIDTH = SCREEN_WIDTH - 4;
        sb.append("╔" + "═".repeat(SCREEN_WIDTH - 2) + "╗\n");

        int start = Math.max(0, messages.size() - MAX_MESSAGES);
        List<String> lines = new ArrayList<>();

        for (int i = start; i < messages.size(); i++) {
            String msg = messages.get(i);
            String plainMsg = stripAnsi(msg);

            if (plainMsg.length() > MESSAGE_BOX_WIDTH) {
                int pos = 0;
                while (pos < plainMsg.length()) {
                    int end = Math.min(pos + MESSAGE_BOX_WIDTH, plainMsg.length());
                    if (end < plainMsg.length() && plainMsg.charAt(end) != ' ') {
                        int lastSpace = plainMsg.lastIndexOf(' ', end);
                        if (lastSpace > pos) {
                            end = lastSpace;
                        }
                    }
                    lines.add(plainMsg.substring(pos, end).trim());
                    pos = end;
                }
            } else {
                lines.add(msg);
            }
        }

        int lineStart = Math.max(0, lines.size() - MAX_MESSAGES);
        for (int i = lineStart; i < lines.size(); i++) {
            String line = lines.get(i);
            int plainLength = stripAnsi(line).length();
            int padding = Math.max(0, MESSAGE_BOX_WIDTH - plainLength);
            sb.append("║ ").append(line).append(" ".repeat(padding)).append(" ║\n");
        }

        for (int i = 0; i < MAX_MESSAGES - (lines.size() - lineStart); i++) {
            sb.append("║ ").append(" ".repeat(MESSAGE_BOX_WIDTH)).append(" ║\n");
        }

        sb.append("╚" + "═".repeat(SCREEN_WIDTH - 2) + "╝\n");

        return sb.toString();
    }

    @Override
    public void renderMessages() {
    }

    @Override
    public void renderCommandPrompt(String input) {
        synchronized (ConsoleRenderer.class) {
            promptInput = input != null ? input : "";
            drawCommandPrompt();
            System.out.flush();
        }
    }

    @Override
    public void clearScreen() {
        needsFullClear = true;
        System.out.print("\033[2J\033[H");
        System.out.flush();
    }

    @Override
    public void initialize() {
        clearScreen();
        messages.clear();
    }

    @Override
    public void shutdown() {
    }

    @Override
    public boolean isRunning() {
        return true;
    }

    @Override
    public void stop() {
    }

    private Zombie findZombieAt(ReadOnlyGameState state, int row, int col) {
        int cellCenterX = col * 80 + 40;
        return state.getZombies().stream()
                .filter(z -> z.row == row && Math.abs(z.position.x - cellCenterX) < 40)
                .findFirst()
                .orElse(null);
    }

    private boolean hasProjectileInCell(ReadOnlyGameState state, int row, int col) {
        int cellStartX = col * 80;
        int cellEndX = (col + 1) * 80;
        return state.getProjectiles().stream()
                .anyMatch(p -> p.row == row &&
                        p.position.x >= cellStartX &&
                        p.position.x < cellEndX);
    }

    private boolean hasSunInCell(ReadOnlyGameState state, int row, int col) {
        int cellStartX = col * 80;
        int cellEndX = (col + 1) * 80;
        return state.getSunDrops().stream()
                .anyMatch(s -> s.row == row &&
                        s.position.x >= cellStartX &&
                        s.position.x < cellEndX);
    }

    private String getUpperBorder(String color) {
        return (color + "╔" + "═".repeat(SCREEN_WIDTH - 2) + "╗\n" + RESET);
    }

    private String getLowerBorder(String color) {
        return (color + "╚" + "═".repeat(SCREEN_WIDTH - 2) + "╝" + RESET);
    }

    private String getBoxTitle(String title, String color) {
        String strippedTitle = stripAnsi(title);
        String displayTitle = title;

        if (strippedTitle.length() > SCREEN_WIDTH - 2) {
            // Truncate and add "..."
            int truncateLength = Math.min(SCREEN_WIDTH - 5, strippedTitle.length());
            displayTitle = title.substring(0, truncateLength) + "...";
            strippedTitle = stripAnsi(displayTitle);
        }

        int leftPadding = (SCREEN_WIDTH - 2 - strippedTitle.length()) / 2;
        return (color + "║" + RESET + " ".repeat(leftPadding) + title
                + " ".repeat(SCREEN_WIDTH - 2 - strippedTitle.length() - leftPadding) + color
                + "║\n" + RESET);
    }

    private String getHeaderBox(String title, String color) {
        StringBuilder sb = new StringBuilder();

        sb.append(getUpperBorder(color));
        sb.append(getBoxTitle(title, color));
        sb.append(getLowerBorder(color));

        return sb.toString();
    }

    private String stripAnsi(String str) {
        String ANSI_REGEX = "\u001B\\[[;\\d]*[mK]";
        return str.replaceAll(ANSI_REGEX, "");
    }
}