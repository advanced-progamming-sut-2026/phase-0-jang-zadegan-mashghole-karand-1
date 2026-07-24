package view.renderer;

import java.util.ArrayList;
import java.util.List;

import controller.ChapterCommands;
import controller.GreenhouseController;
import controller.MiniGameCommands;
import controller.PickPlantsController;
import controller.ShopController;
import model.board.IceDirection;
import model.board.Tile;
import model.core.GameLoop;
import model.core.Position;
import model.core.ReadOnlyGameState;
import model.data.Barrel.Barrel;
import model.data.Grave.Grave;
import model.data.brain.Brain;
import model.data.content.chapter.ChapterCatalog;
import model.data.content.chapter.ChapterType;
import model.data.content.minigame.MiniGameCatalog;
import model.data.content.minigame.MiniGameType;
import model.data.plant.Plant;
import model.data.plant.PlantType;
import model.data.plant.stuns.PlantStun;
import model.data.vase.Vase;
import model.data.zombie.Zombie;
import model.greenhouse.Greenhouse;
import model.greenhouse.Pot;
import model.lawnmower.LawnMower;
import model.service.*;
import model.service.GameNavigationState.Phase;
import model.shop.ShopCurrency;
import model.shop.ShopItem;
import model.shop.ShopItems;
import model.storage.user.SafetyQuestion;
import model.storage.user.User;

public class ConsoleRenderer implements Renderer {

    private static final int GRID_ROWS = ReadOnlyGameState.GRID_ROWS;
    private static final int GRID_COLS = ReadOnlyGameState.GRID_COLS;
    private static final int CELL_HEIGHT = 3;
    private static final int CELL_INNER_WIDTH = 10;
    private static final int MOWER_INNER_WIDTH = 2;
    private static final int MAX_MESSAGES = 4;
    private static final int SCREEN_WIDTH = 120;
    private static final int MIN_RENDER_HEIGHT = 40;
    private static final int GH_ROWS = 4;
    private static final int GH_COLS = 5;
    private static final int POT_WIDTH = 13;
    private static final int POT_HEIGHT = 5;

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
    private static final String BG_WATER = "\u001B[48;5;24m";
    private static final String BG_ICE = "\u001B[48;5;153m";
    private static final String BG_NECRO = "\u001B[48;5;54m";

    private List<String> messages = new java.util.ArrayList<>();

    private int currentRenderHeight = MIN_RENDER_HEIGHT;
    private String[] lastRenderLines = new String[MIN_RENDER_HEIGHT];
    private String currentScreenKey = "";
    private boolean needsFullClear = true;
    private boolean scrollRenderMode = false;
    private String promptInput = "";
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
            for (String line : content.split("\n", -1)) {
                lines.add(line);
            }
            if (!lines.isEmpty() && lines.get(lines.size() - 1).isEmpty()) {
                lines.remove(lines.size() - 1);
            }

            int contentHeight = lines.size();
            int termRows = detectTerminalRows();
            int tuiHeight = Math.max(MIN_RENDER_HEIGHT, contentHeight);
            boolean useScroll = tuiHeight + 2 > termRows;

            int height = useScroll ? Math.max(1, contentHeight) : tuiHeight;
            while (lines.size() < height) {
                lines.add("");
            }

            if (height != currentRenderHeight || useScroll != scrollRenderMode) {
                needsFullClear = true;
                currentRenderHeight = height;
                lastRenderLines = new String[height];
            }
            scrollRenderMode = useScroll;

            boolean changed = needsFullClear;
            if (!changed) {
                for (int i = 0; i < currentRenderHeight; i++) {
                    if (!java.util.Objects.equals(lines.get(i), lastRenderLines[i])) {
                        changed = true;
                        break;
                    }
                }
            }
            if (!changed) {
                return;
            }

            if (useScroll) {
                System.out.print("\033[3J\033[2J\033[H");
                for (int i = 0; i < currentRenderHeight; i++) {
                    System.out.println(lines.get(i));
                    lastRenderLines[i] = lines.get(i);
                }
                System.out.println("─".repeat(SCREEN_WIDTH));
                needsFullClear = false;
                drawCommandPrompt();
            } else {
                if (needsFullClear) {
                    System.out.print("\033[3J\033[2J\033[H");
                    needsFullClear = false;
                }
                for (int i = 0; i < currentRenderHeight; i++) {
                    System.out.printf("\033[%d;1H\033[2K%s", i + 1, lines.get(i));
                    lastRenderLines[i] = lines.get(i);
                }
                System.out.printf("\033[%d;1H\033[2K%s", currentRenderHeight + 1, "─".repeat(SCREEN_WIDTH));
                drawCommandPrompt();
            }
            System.out.flush();
        }
    }

    private void drawCommandPrompt() {
        if (scrollRenderMode) {
            System.out.print("\r\033[2K");
            System.out.print(CYAN + "> " + RESET + promptInput);
            return;
        }
        int promptLine = currentRenderHeight + 2;
        System.out.printf("\033[%d;1H\033[2K", promptLine);
        System.out.print(CYAN + "> " + RESET + promptInput);
        int column = PROMPT_PREFIX_COLUMNS + promptInput.length() + 1;
        System.out.printf("\033[%d;%dH", promptLine, column);
    }

    private int detectTerminalRows() {
        try {
            Process process = new ProcessBuilder("sh", "-c", "stty size < /dev/tty").start();
            boolean finished = process.waitFor(300, java.util.concurrent.TimeUnit.MILLISECONDS);
            if (finished && process.exitValue() == 0) {
                String out = new String(process.getInputStream().readAllBytes()).trim();
                String[] parts = out.split("\\s+");
                if (parts.length >= 1) {
                    int rows = Integer.parseInt(parts[0]);
                    if (rows > 0) {
                        return rows;
                    }
                }
            }
        } catch (Exception ignored) {
        }
        try {
            String env = System.getenv("LINES");
            if (env != null && !env.isBlank()) {
                int rows = Integer.parseInt(env.trim());
                if (rows > 0) {
                    return rows;
                }
            }
        } catch (Exception ignored) {
        }
        return MIN_RENDER_HEIGHT;
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
                    .append(questions.get(i).type.question).append("\n");
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

    private String getMainScreen(boolean hasUnreadNews) {
        StringBuilder sb = new StringBuilder();
        String title = "🌱  " + BOLD + "PLANTS VS ZOMBIES 2" + RESET + "  🧟";
        String unreadIndicator = hasUnreadNews ? " " + RED + "● unread" + RESET : "";

        sb.append(getHeaderBox(title, GREEN));
        sb.append("\n");
        sb.append("  " + CYAN + "1." + RESET + " Start Game: " + GREEN + "menu enter game" + RESET + "\n");
        sb.append("  " + CYAN + "2." + RESET + " Settings: " + GREEN + "menu enter settings" + RESET + "\n");
        sb.append("  " + CYAN + "3." + RESET + " News: " + GREEN + "menu enter news" + RESET + unreadIndicator + "\n");
        sb.append("  " + CYAN + "4." + RESET + " Profile: " + GREEN + "menu enter profile" + RESET + "\n");
        sb.append("  " + CYAN + "5." + RESET + " Logout: " + GREEN + "menu logout" + RESET + "\n");
        sb.append("  " + CYAN + "6." + RESET + " Quit: " + GREEN + "quit" + RESET + "\n");
        sb.append("\n");
        sb.append(getMessages());

        return sb.toString();
    }

    @Override
    public void renderMainScreen(boolean hasUnreadNews) {
        render(getMainScreen(hasUnreadNews));
    }

    private String getGameScreen(ReadOnlyGameState state, HudViewState hud) {
        HudViewState safeHud = hud != null ? hud : HudViewState.empty();
        StringBuilder sb = new StringBuilder();
        sb.append(getHUD(state, safeHud));
        sb.append("\n");
        sb.append(getGrid(state, safeHud));
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
        sb.append(getMessages());
        return sb.toString();
    }

    @Override
    public void renderGameScreen(ReadOnlyGameState state) {
        renderGameScreen(state, HudViewState.empty());
    }

    @Override
    public void renderGameScreen(ReadOnlyGameState state, HudViewState hud) {
        render(getGameScreen(state, hud));
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
            sb.append("  " + CYAN + "2." + RESET + " Travel Log: " + GREEN + "menu enter travel-log" + RESET + "\n");
            sb.append("  " + CYAN + "3." + RESET + " Collection: " + GREEN + "menu enter collection" + RESET + "\n");
            sb.append("  " + CYAN + "4." + RESET + " Greenhouse: " + GREEN + "menu enter greenhouse" + RESET + "\n");
            sb.append("  " + CYAN + "5." + RESET + " Leaderboard: " + GREEN + "menu enter leaderboard" + RESET + "\n");
            sb.append("  " + CYAN + "6." + RESET + " Back: " + GREEN + "menu exit" + RESET + "\n");
            sb.append("  " + CYAN + "7." + RESET + " Quit: " + GREEN + "quit" + RESET + "\n");
            sb.append("\n");
            sb.append("  " + BOLD + "Chapters:" + RESET + "\n");
            for (ChapterType chapter : ChapterType.values()) {
                boolean unlocked = gameNavigation.unlockedChapters.contains(chapter);
                String status = unlocked ? GREEN + "unlocked" + RESET : RED + "locked" + RESET;
                sb.append("    ").append(CYAN).append(ChapterCommands.commandName(chapter)).append(RESET)
                        .append(" - ").append(ChapterCommands.displayName(chapter))
                        .append(" (").append(status).append(")\n");
            }
        } else if (gameNavigation.phase == Phase.MINIGAME) {
            String title = "🌱  " + BOLD + "PLANTS VS ZOMBIES 2 | Minigames" + RESET + "  🧟";
            sb.append(getHeaderBox(title, GREEN));
            sb.append("\n");
            sb.append("  " + CYAN + "1." + RESET + " Select Minigame: " + GREEN
                    + "select minigame -m <name>" + RESET + "\n");
            sb.append("  " + CYAN + "2." + RESET + " Back: " + GREEN + "menu exit" + RESET + "\n");
            sb.append("  " + CYAN + "3." + RESET + " Quit: " + GREEN + "quit" + RESET + "\n");
            sb.append("\n");
            sb.append("  " + BOLD + "Minigames:" + RESET + "\n");
            for (MiniGameType miniGame : MiniGameType.values()) {
                boolean unlocked = gameNavigation.unlockedMinigames.contains(miniGame);
                boolean playable = MiniGameCatalog.isPlayable(miniGame);
                String status;
                if (!unlocked) {
                    status = RED + "locked" + RESET;
                } else if (!playable) {
                    status = YELLOW + "coming soon" + RESET;
                } else {
                    status = GREEN + "unlocked" + RESET;
                }
                sb.append("    ").append(CYAN).append(MiniGameCommands.commandName(miniGame)).append(RESET)
                        .append(" - ").append(MiniGameCommands.displayName(miniGame))
                        .append(" (").append(status).append(")\n");
            }
        } else if (gameNavigation.phase == Phase.LEVEL) {
            String chapterName = ChapterCommands.displayName(gameNavigation.selectedChapter);
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
                int highScore = gameNavigation.getLevelHighScore(gameNavigation.selectedChapter, i);
                sb.append("    ").append(CYAN).append(i).append(RESET);
                if (highScore > 0) {
                    sb.append(" - high score: ").append(GREEN).append(highScore).append(RESET);
                } else {
                    sb.append(" - high score: ").append(GRAY).append("-").append(RESET);
                }
                sb.append("\n");
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
    public void renderGreenHouseScreen(GreenhouseController greenhouseController) {
        render(getGreenHouseScreen(greenhouseController));
    }

    private String getGreenHouseScreen(GreenhouseController greenhouseController) {
        StringBuilder sb = new StringBuilder();
        User user = greenhouseController.getUser();
        Greenhouse greenhouse = user != null ? user.greenhouse : null;

        sb.append(getHeaderBox("🌱  " + BOLD + "GREENHOUSE" + RESET + "  🧟", GREEN));
        sb.append("\n");

        int coins = user != null ? user.coins : 0;
        int gems = user != null ? user.gems : 0;
        sb.append("  ").append(BOLD).append("Coins:").append(RESET).append(" ").append(coins);
        sb.append("    ").append(BOLD).append("Gems:").append(RESET).append(" ").append(gems).append("\n");
        sb.append("\n");
        sb.append(getMessages());
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
        return truncate(pot.getPlantType().name, POT_WIDTH - 2);
    }

    private String potTop() {
        return GRAY + "┌" + "─".repeat(POT_WIDTH - 2) + "┐" + RESET;
    }

    private String potBottom() {
        return GRAY + "└" + "─".repeat(POT_WIDTH - 2) + "┘" + RESET;
    }

    private String potLine(String text, String color) {
        String plain = truncate(text, POT_WIDTH - 2);
        int pad = POT_WIDTH - 2 - displayWidth(stripAnsi(plain));
        return GRAY + "│" + RESET + color + plain + RESET
                + " ".repeat(Math.max(0, pad)) + GRAY + "│" + RESET;
    }

    private int displayWidth(String text) {
        int width = 0;
        for (int i = 0; i < text.length();) {
            int cp = text.codePointAt(i);
            i += Character.charCount(cp);
            if (cp == 0xFE0F || cp == 0xFE0E || cp == 0x200D) {
                continue;
            }
            width += isWideGlyph(cp) ? 2 : 1;
        }
        return width;
    }

    private boolean isWideGlyph(int cp) {
        return (cp >= 0x1F300 && cp <= 0x1FAFF)
                || (cp >= 0x1F000 && cp <= 0x1F02F)
                || (cp >= 0x2600 && cp <= 0x27BF)
                || (cp >= 0x2300 && cp <= 0x23FF)
                || (cp >= 0x2B00 && cp <= 0x2BFF)
                || cp == 0x2705 || cp == 0x26A0;
    }

    private String potCoord(int row, int col) {
        String coord = "(" + col + "," + row + ")";
        int pad = POT_WIDTH - coord.length();
        int left = Math.max(0, pad / 2);
        return " ".repeat(left) + GRAY + coord + RESET
                + " ".repeat(Math.max(0, pad - left));
    }

    private String truncate(String text, int maxLen) {
        if (text == null) {
            return "";
        }
        if (text.length() <= maxLen) {
            return text;
        }
        if (maxLen <= 1) {
            return text.substring(0, maxLen);
        }
        return text.substring(0, maxLen - 1) + ".";
    }

    @Override
    public void renderCollectionScreen(CollectionViewState collection) {
        render(getCollectionScreen(collection));
    }

    private String getCollectionScreen(CollectionViewState collection) {
        StringBuilder sb = new StringBuilder();
        boolean plantsTab = collection.tab == CollectionViewState.Tab.PLANTS;
        boolean unlockedMode = collection.mode == CollectionViewState.Mode.UNLOCKED;
        String tabLabel = plantsTab ? "Plants" : "Zombies";
        String modeLabel = unlockedMode ? "Unlocked" : "All";
        String title = "🌱  " + BOLD + "PLANTS VS ZOMBIES 2 | Collection | " + tabLabel
                + " (" + modeLabel + ")" + RESET + "  🧟";

        sb.append(getHeaderBox(title, PURPLE));
        sb.append("\n");

        String plantsTabMark = plantsTab ? GREEN + "[plants]" + RESET : GRAY + "plants" + RESET;
        String zombiesTabMark = !plantsTab ? GREEN + "[zombies]" + RESET : GRAY + "zombies" + RESET;
        sb.append("  Tabs: ").append(plantsTabMark).append("  ").append(zombiesTabMark).append("\n");
        sb.append("\n");

        if (collection.entries.isEmpty()) {
            sb.append("  ").append(GRAY).append("(none)").append(RESET).append("\n");
        } else {
            sb.append(formatCollectionGrid(collection.entries));
        }

        if (collection.hasDetail()) {
            sb.append("\n");
            sb.append("  ").append(BOLD).append("Details: ").append(RESET)
                    .append(CYAN).append(collection.detailTitle).append(RESET).append("\n");
            for (String line : collection.detailLines) {
                sb.append("    ").append(line).append("\n");
            }
        }

        sb.append("\n");
        sb.append("  " + CYAN + "1." + RESET + " Unlocked Plants: " + GREEN
                + "menu collection show-plants" + RESET + "\n");
        sb.append("  " + CYAN + "2." + RESET + " Unlocked Zombies: " + GREEN
                + "menu collection show-zombies" + RESET + "\n");
        sb.append("  " + CYAN + "3." + RESET + " All Plants: " + GREEN
                + "menu collection show-all-plants" + RESET + "\n");
        sb.append("  " + CYAN + "4." + RESET + " All Zombies: " + GREEN
                + "menu collection show-all-zombies" + RESET + "\n");
        sb.append("  " + CYAN + "5." + RESET + " Plant Details: " + GREEN
                + "menu collection show-plant -p <name>" + RESET + "\n");
        sb.append("  " + CYAN + "6." + RESET + " Zombie Details: " + GREEN
                + "menu collection show-zombie -z <name>" + RESET + "\n");
        sb.append("  " + CYAN + "7." + RESET + " Buy Plant (2000 coins): " + GREEN
                + "menu collection purchase-plant -p <name>" + RESET + "\n");
        sb.append("  " + CYAN + "8." + RESET + " Upgrade Plant: " + GREEN
                + "menu collection upgrade-plant -p <name>" + RESET + "\n");
        sb.append("  " + CYAN + "9." + RESET + " Back: " + GREEN + "menu exit" + RESET + "\n");
        sb.append("\n");
        sb.append(getMessages());
        return sb.toString();
    }

    private String formatCollectionGrid(List<CollectionViewState.Entry> entries) {
        final int columns = 3;
        final int colWidth = 18;
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < entries.size(); i++) {
            CollectionViewState.Entry entry = entries.get(i);
            if (i % columns == 0) {
                sb.append("  ");
            }

            String label = entry.name;
            if (label.length() > colWidth - 1) {
                label = label.substring(0, colWidth - 2) + ".";
            }
            String padded = String.format("%-" + colWidth + "s", label);
            if (entry.unlocked) {
                sb.append(CYAN).append(padded).append(RESET);
            } else {
                sb.append(GRAY).append(padded).append(RESET);
            }

            if ((i + 1) % columns == 0 || i == entries.size() - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    @Override
    public void renderSettingOverlay(SettingsViewState settings) {
        render(getSettingsOverlay(settings));
    }

    private String getSettingsOverlay(SettingsViewState settings) {
        StringBuilder sb = new StringBuilder();
        String title = "🌱  " + BOLD + "PLANTS VS ZOMBIES 2 | Settings" + RESET + "  🧟";

        sb.append(getHeaderBox(title, YELLOW));
        sb.append("\n");
        sb.append(getDifficultyBar(settings)).append("\n");
        sb.append("\n");
        sb.append("  " + CYAN + "1." + RESET + " Change Difficulty: " + GREEN
                + "menu settings change-difficulty -l <level>" + RESET + "\n");
        sb.append("  " + CYAN + "2." + RESET + " Back: " + GREEN + "menu exit" + RESET + "\n");
        sb.append("\n");
        sb.append(getMessages());
        return sb.toString();
    }

    private String getDifficultyBar(SettingsViewState settings) {
        int min = settings.minDifficulty;
        int max = settings.maxDifficulty;
        int level = settings.difficultyLevel;
        int segments = max - min + 1;

        StringBuilder bar = new StringBuilder();
        bar.append("  ").append(BOLD).append("Difficulty").append(RESET).append("\n");
        bar.append("  ");

        for (int i = 0; i < segments; i++) {
            int segmentLevel = min + i;
            bar.append(String.format("%-3d", segmentLevel));
        }
        bar.append("\n  ");

        for (int i = 0; i < segments; i++) {
            int segmentLevel = min + i;
            if (segmentLevel <= level) {
                bar.append(getDifficultySegmentColor(segmentLevel, min, max))
                        .append("██ ").append(RESET);
            } else {
                bar.append(GRAY).append("░░ ").append(RESET);
            }
        }

        return bar.toString();
    }

    private String getDifficultySegmentColor(int level, int min, int max) {
        if (max == min) {
            return YELLOW;
        }
        float ratio = (float) (level - min) / (max - min);
        if (ratio <= 0.25f) {
            return GREEN;
        }
        if (ratio <= 0.5f) {
            return YELLOW;
        }
        if (ratio <= 0.75f) {
            return ORANGE;
        }
        return RED;
    }

    @Override
    public void renderNewsOverlay(NewsViewState news) {
        render(getNewsOverlay(news));
    }

    private String getNewsOverlay(NewsViewState news) {
        StringBuilder sb = new StringBuilder();
        String title = "🌱  " + BOLD + "PLANTS VS ZOMBIES 2 | News" + RESET + "  🧟";

        sb.append(getHeaderBox(title, BLUE));
        sb.append("\n");
        if (news.messages.isEmpty()) {
            sb.append("  No news to show.\n");
        } else {
            int index = 1;
            for (String message : news.messages) {
                sb.append("  ").append(CYAN).append(index++).append(".").append(RESET)
                        .append(" ").append(message).append("\n");
            }
        }
        sb.append("\n");
        sb.append("  " + CYAN + "1." + RESET + " Show All: " + GREEN + "menu news show-all" + RESET + "\n");
        sb.append("  " + CYAN + "2." + RESET + " Show Unread: " + GREEN + "menu news show-unread" + RESET + "\n");
        sb.append("  " + CYAN + "3." + RESET + " Back: " + GREEN + "menu exit" + RESET + "\n");
        sb.append("\n");
        sb.append(getMessages());
        return sb.toString();
    }

    @Override
    public void renderLeaderboardOverlay(LeaderboardViewState leaderboardViewState) {
        render(getLeaderboardOverlay(leaderboardViewState));
    }

    private String getLeaderboardOverlay(LeaderboardViewState leaderboard) {
        StringBuilder sb = new StringBuilder();
        String title = "🌱  " + BOLD + "PLANTS VS ZOMBIES 2 | Leaderboard" + RESET + "  🧟";
        sb.append(getHeaderBox(title, YELLOW));
        sb.append("\n");
        if (leaderboard == null || leaderboard.entries.isEmpty()) {
            sb.append("  No players to show.\n");
        } else {
            sb.append("  ").append(GRAY).append("Sorted by ").append(leaderboard.sortColumn.name())
                    .append(" (").append(leaderboard.sortDirection.name()).append(")").append(RESET).append("\n");
            sb.append("\n");
            sb.append(String.format("  %s%-4s %-12s %-18s %-8s %-8s%s%n",
                    BOLD, "Rank", "User", "Progress", "Score", "Minigames", RESET));
            sb.append("  " + GRAY + "─".repeat(52) + RESET + "\n");
            for (LeaderboardViewState.Entry e : leaderboard.entries) {
                String progress = "Ch " + e.chapter + " Lv " + e.level;
                sb.append(String.format("  %s%-4d%s %-12s %-18s %-8d %-8d%n",
                        CYAN, e.rank, RESET,
                        e.username,
                        progress,
                        e.score,
                        e.minigames));
            }
        }
        sb.append("\n");
        sb.append("  " + CYAN + "1." + RESET + " Sort: " + GREEN
                + "menu leaderboard sort -c <SCORE|LEVELS|MINIGAMES> -t <HTL|LTH>" + RESET + "\n");
        sb.append("  " + CYAN + "2." + RESET + " Back: " + GREEN + "menu exit" + RESET + "\n");
        sb.append("\n");
        sb.append(getMessages());
        return sb.toString();
    }

    @Override
    public void renderProfileOverlay(ProfileViewState profile) {
        render(getProfileOverlay(profile));
    }

    private String getProfileOverlay(ProfileViewState profile) {
        StringBuilder sb = new StringBuilder();
        String title = "🌱  " + BOLD + "PLANTS VS ZOMBIES 2 | Profile" + RESET + "  🧟";

        sb.append(getHeaderBox(title, PURPLE));
        sb.append("\n");
        sb.append("  ").append(BOLD).append("Username:").append(RESET).append(" ").append(profile.username)
                .append("\n");
        sb.append("  ").append(BOLD).append("Nickname:").append(RESET).append(" ").append(profile.nickname)
                .append("\n");
        sb.append("  ").append(BOLD).append("Games Played:").append(RESET).append(" ").append(profile.gamesPlayed)
                .append("\n");
        sb.append("  ").append(BOLD).append("Coins:").append(RESET).append(" ").append(profile.coins).append("\n");
        sb.append("  ").append(BOLD).append("Gems:").append(RESET).append(" ").append(profile.gems).append("\n");
        sb.append("  ").append(BOLD).append("Completed Levels:").append(RESET).append(" ")
                .append(profile.completedLevels)
                .append("\n");
        sb.append("  ").append(BOLD).append("Highest Score:").append(RESET).append(" ").append(profile.highestScore)
                .append("\n");
        sb.append("\n");
        sb.append("  " + CYAN + "1." + RESET + " Refresh: " + GREEN + "menu profile show-info" + RESET + "\n");
        sb.append("  " + CYAN + "2." + RESET + " Change Username: " + GREEN
                + "menu profile change-username -u <username>" + RESET + "\n");
        sb.append("  " + CYAN + "3." + RESET + " Change Nickname: " + GREEN
                + "menu profile change-nickname -u <nickname>" + RESET + "\n");
        sb.append("  " + CYAN + "4." + RESET + " Change Email: " + GREEN
                + "menu profile change-email -e <email>" + RESET + "\n");
        sb.append("  " + CYAN + "5." + RESET + " Change Password: " + GREEN
                + "menu profile change-password -p <newpassword> -o <oldpassword>" + RESET + "\n");
        sb.append("  " + CYAN + "6." + RESET + " Back: " + GREEN + "menu exit" + RESET + "\n");
        sb.append("\n");
        sb.append(getMessages());
        return sb.toString();
    }

    @Override
    public void renderShopScreen(int coins, int gems, PlantType dailyPlant, int dailyPrice, boolean dailyPurchased,
            ShopController.ShopDisplayMode mode) {
        render(getShopScreen(coins, gems, dailyPlant, dailyPrice, dailyPurchased, mode));
    }

    private String getShopScreen(int coins, int gems, PlantType dailyPlant, int dailyPrice, boolean dailyPurchased,
            ShopController.ShopDisplayMode mode) {
        StringBuilder sb = new StringBuilder();
        String title = "🌱  " + BOLD + "PLANTS VS ZOMBIES 2 | Shop" + RESET + "  🧟";

        sb.append(getHeaderBox(title, YELLOW));
        sb.append("\n");
        sb.append("  ").append(BOLD).append("Coins:").append(RESET).append(" ").append(coins).append("\n");
        sb.append("  ").append(BOLD).append("Gems:").append(RESET).append(" ").append(gems).append("\n");
        sb.append("\n");
        if (mode == ShopController.ShopDisplayMode.LIST) {
            sb.append("  ").append(BOLD).append("Shop Items:").append(RESET).append("\n");
            for (ShopItems entry : ShopItems.values()) {
                sb.append(formatShopItem(entry.getShopItem()));
            }
        } else if (mode == ShopController.ShopDisplayMode.DAILY) {

            sb.append("\n");
            sb.append("  ").append(BOLD).append("Daily Deal:").append(RESET).append("\n");
            sb.append(formatDailyDeal(dailyPlant, dailyPrice, dailyPurchased));
        }
        sb.append("\n");
        sb.append("  ").append(CYAN).append("1.").append(RESET).append(" List: ")
                .append(GREEN).append("shop list").append(RESET).append("\n");
        sb.append("  ").append(CYAN).append("2.").append(RESET).append(" Daily: ")
                .append(GREEN).append("shop daily").append(RESET).append("\n");
        sb.append("  ").append(CYAN).append("3.").append(RESET).append(" Buy: ")
                .append(GREEN).append("shop buy -i <itemid> -n <count>").append(RESET).append("\n");
        sb.append("  ").append(CYAN).append("4.").append(RESET).append(" Buy with plant: ")
                .append(GREEN).append("shop buy -i <itemid> -n <count> -t <planttype>").append(RESET).append("\n");
        sb.append("  ").append(CYAN).append("5.").append(RESET).append(" Back: ")
                .append(GREEN).append("menu exit").append(RESET).append("\n");
        sb.append("\n");
        sb.append(getMessages());

        return sb.toString();
    }

    private String formatShopItem(ShopItem item) {
        String currency = item.getCurrency() == ShopCurrency.COIN ? "coins" : "gems";

        StringBuilder sb = new StringBuilder();
        sb.append("    ")
                .append(CYAN).append(item.getId()).append(RESET)
                .append(" | ")
                .append(item.getName())
                .append(" | ")
                .append(item.getPrice()).append(" ").append(currency)
                .append(" | unit: ").append(item.getPurchaseUnit())
                .append("\n");
        sb.append("      ").append(GRAY).append(item.getDescription()).append(RESET).append("\n");
        return sb.toString();
    }

    private String formatDailyDeal(PlantType dailyPlant, int dailyPrice, boolean dailyPurchased) {
        if (dailyPlant == null) {
            return "    " + GRAY + "No daily deal available." + RESET + "\n";
        }

        String status = dailyPurchased ? RED + "purchased" + RESET : GREEN + "available" + RESET;

        return "    " + dailyPlant.name
                + " | " + dailyPrice + " coins"
                + " | " + status + "\n";
    }

    @Override
    public void renderPauseOverlay() {
    }

    @Override
    public void renderQuestsOverlay(QuestViewState quests) {
        render(getQuestsOverlay(quests));
    }

    private String getQuestsOverlay(QuestViewState quests) {
        StringBuilder sb = new StringBuilder();
        String title = "🌱  " + BOLD + "PLANTS VS ZOMBIES 2 | Travel Log" + RESET + "  🧟";
        sb.append(getHeaderBox(title, GREEN));
        sb.append("\n");
        sb.append("  ").append(BOLD).append("Filter:").append(RESET).append(" ")
                .append(quests == null ? "all" : quests.filter).append("\n");
        sb.append("  ").append(CYAN).append("Order:").append(RESET)
                .append(" Critical → High (Epic/Gems) → Medium/Low (Daily)\n\n");

        if (quests == null || quests.isEmpty()) {
            sb.append("  No quests to show.\n");
        } else {
            int index = 1;
            index = appendQuestSection(sb, index, "CRITICAL — story / unlock progress",
                    RED, quests.critical);
            index = appendQuestSection(sb, index, "HIGH — Epic challenges (Gem rewards)",
                    YELLOW, quests.high);
            appendQuestSection(sb, index, "MEDIUM / LOW — daily & repeatable",
                    CYAN, quests.mediumAndLow);
        }

        sb.append("\n");
        sb.append("  " + CYAN + "1." + RESET + " All: " + GREEN + "travel log page all" + RESET + "\n");
        sb.append("  " + CYAN + "2." + RESET + " Critical: " + GREEN + "travel log page critical" + RESET + "\n");
        sb.append("  " + CYAN + "3." + RESET + " High: " + GREEN + "travel log page high" + RESET + "\n");
        sb.append("  " + CYAN + "4." + RESET + " Daily: " + GREEN + "travel log page daily" + RESET + "\n");
        sb.append("  " + CYAN + "5." + RESET + " Active: " + GREEN + "travel log page active" + RESET + "\n");
        sb.append("  " + CYAN + "6." + RESET + " Completed: " + GREEN + "travel log page completed" + RESET + "\n");
        sb.append("  " + CYAN + "7." + RESET + " Minigames: " + GREEN + "menu enter minigames" + RESET + "\n");
        sb.append("  " + CYAN + "8." + RESET + " Back: " + GREEN + "menu exit" + RESET + "\n");
        sb.append("\n");
        sb.append(getMessages());
        return sb.toString();
    }

    private int appendQuestSection(StringBuilder sb, int startIndex, String sectionTitle,
            String color, java.util.List<QuestViewState.Entry> entries) {
        if (entries == null || entries.isEmpty()) {
            return startIndex;
        }
        sb.append("  ").append(color).append(BOLD).append(sectionTitle).append(RESET).append("\n");

        final int gap = 2;
        final int colWidth = (SCREEN_WIDTH - 2 - gap) / 2;
        int index = startIndex;

        for (int i = 0; i < entries.size(); i += 2) {
            QuestViewState.Entry left = entries.get(i);
            QuestViewState.Entry right = (i + 1 < entries.size()) ? entries.get(i + 1) : null;

            String[] leftLines = formatQuestCard(index++, left, colWidth);
            String[] rightLines = right != null
                    ? formatQuestCard(index++, right, colWidth)
                    : new String[] { " ".repeat(colWidth), " ".repeat(colWidth) };

            for (int line = 0; line < leftLines.length; line++) {
                sb.append("  ").append(leftLines[line])
                        .append(" ".repeat(gap))
                        .append(rightLines[line])
                        .append("\n");
            }
        }
        sb.append("\n");
        return index;
    }

    private String[] formatQuestCard(int index, QuestViewState.Entry entry, int width) {
        String status = entry.completed
                ? GREEN + "DONE" + RESET
                : CYAN + entry.progress + "/" + entry.target + RESET;
        String reward = GREEN + compactReward(entry) + RESET;

        String title = CYAN + index + "." + RESET + " "
                + BOLD + truncate(entry.name, Math.max(6, width - 22)) + RESET
                + " (" + status + ") " + reward;
        String description = "   " + truncate(entry.description, width - 3);

        return new String[] {
                padVisible(title, width),
                padVisible(description, width)
        };
    }

    private String compactReward(QuestViewState.Entry entry) {
        String label = entry.rewardLabel == null ? "" : entry.rewardLabel;
        if (label.endsWith(" Gems")) {
            return label.substring(0, label.length() - " Gems".length()) + "gem";
        }
        if (label.endsWith(" Coins")) {
            return label.substring(0, label.length() - " Coins".length()) + "coin";
        }
        if (label.startsWith("Unlock")) {
            return "plant";
        }
        if (label.contains("seed pack")) {
            int space = label.indexOf(' ');
            return space > 0 ? label.substring(0, space) + "seeds" : "seeds";
        }
        return truncate(label.replace(' ', '_').toLowerCase(), 12);
    }

    private String padVisible(String text, int width) {
        int visible = displayWidth(stripAnsi(text));
        if (visible >= width) {
            return text;
        }
        return text + " ".repeat(width - visible);
    }

    @Override
    public void renderPlantSelectorOverlay() {
    }

    @Override
    public void renderGameOverOverlay(boolean won, int score, int wavesSurvived) {
        // use getGameScreen
    }

    @Override
    public void renderLevelCompleteOverlay(int stars, int score) {
        // use getGameScreen
    }

    private String buildGameOverOverlay(ReadOnlyGameState state) {
        StringBuilder sb = new StringBuilder();
        sb.append(getHeaderBox(" 💀 " + BOLD + "GAME OVER" + RESET, RED));
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
        sb.append(getHeaderBox(" 🎉 " + BOLD + "LEVEL COMPLETE!" + RESET, GREEN));
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

    private String getHUD(ReadOnlyGameState state, HudViewState hud) {
        String status = state.isGameOver() ? "💀" : state.isLevelComplete() ? "⭐" : "▶️";
        int seconds = state.getTotalTicks() / GameLoop.TICKS_PER_SECOND;
        StringBuilder title = new StringBuilder();

        if (hud.modeLabel != null && !hud.modeLabel.isEmpty()) {
            title.append(BOLD).append(hud.modeLabel).append(RESET).append("  ");
        }

        switch (hud.mode) {
            case BRAINS -> {
                long brains = state.getBrains().stream().filter(Brain::isCollected).count();
                title.append(String.format("%s☀️ : %-4d  %s🧠 : %d/%d  %s🧟 : %-3d  %s⏱️ %-4ds  %s%s%s",
                        YELLOW, state.getSunAmount(),
                        PURPLE, brains, ReadOnlyGameState.GRID_ROWS,
                        RED, state.getZombies().size(),
                        WHITE, seconds,
                        CYAN, status, RESET));
            }
            case VASE_BREAKER -> {
                title.append(String.format("%s🏺 : %-3d  %s🌱 : %-3d  %s🧟 : %-3d  %s⏱️ %-4ds  %s%s%s",
                        PURPLE, hud.conveyorRemaining,
                        GREEN, hud.heldSeedTypes,
                        RED, state.getZombies().size(),
                        WHITE, seconds,
                        CYAN, status, RESET));
            }
            case CONVEYOR -> {
                title.append(String.format("%s⏳ : %-2ds  %s🌊 : %-3d  %s🧟 : %-3d  %s%s%s",
                        YELLOW, hud.conveyorSecondsUntilNext,
                        CYAN, state.getCurrentWave(),
                        RED, state.getZombies().size(),
                        CYAN, status, RESET));
            }
            case TIMED_WAR -> {
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
            }
            case DEADLINE -> {
                title.append(String.format("%s☀️ : %-4d  %s⛔ col %-2d  %s🌊 : %-3d  %s🧟 : %-3d  %s🌿 : %-2d  %s⏱️ %-4ds  %s%s%s",
                        YELLOW, state.getSunAmount(),
                        RED, hud.deadlineColumn,
                        CYAN, state.getCurrentWave(),
                        RED, state.getZombies().size(),
                        PURPLE, state.getPlantFoodAmount(),
                        WHITE, seconds,
                        CYAN, status, RESET));
            }
            case SAVE_OUR_SEEDS -> {
                title.append(String.format("%s☀️ : %-4d  %s🛡️ %d/%d @col%d  %s🌊 : %-3d  %s🧟 : %-3d  %s🌿 : %-2d  %s⏱️ %-4ds  %s%s%s",
                        YELLOW, state.getSunAmount(),
                        GREEN, hud.protectedAlive, hud.protectedTotal, hud.protectedCol,
                        CYAN, state.getCurrentWave(),
                        RED, state.getZombies().size(),
                        PURPLE, state.getPlantFoodAmount(),
                        WHITE, seconds,
                        CYAN, status, RESET));
            }
            default -> {
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
            }
        }

        return getHeaderBox(title.toString(), CYAN);
    }

    private String getPlantTray(HudViewState hud) {
        StringBuilder sb = new StringBuilder();
        String trayTitle = switch (hud.mode) {
            case CONVEYOR -> "Conveyor";
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
        body.append(nameColor).append(truncate(slot.name, 12)).append(RESET);
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
        return padVisible(body.toString(), width);
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
                String item = CYAN + "• " + RESET + GREEN + truncate(lines.get(idx), colWidth - 4) + RESET;
                sb.append(padVisible(item, colWidth));
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    @Override
    public void renderHUD(ReadOnlyGameState state) {
    }

    private String getGrid(ReadOnlyGameState state, HudViewState hud) {
        StringBuilder sb = new StringBuilder();
        String cellDash = "─".repeat(CELL_INNER_WIDTH);
        String mowerDash = "─".repeat(MOWER_INNER_WIDTH);
        int deadlineCol = (hud != null && hud.mode == HudViewState.Mode.DEADLINE) ? hud.deadlineColumn : -1;
        String redDash = RED + cellDash + RESET;
        String redTeeTop = RED + "┬" + RESET;
        String redTeeMid = RED + "┼" + RESET;
        String redTeeBot = RED + "┴" + RESET;
        String redBar = RED + "│" + RESET;

        sb.append("      ");
        sb.append(centerLabel("M", MOWER_INNER_WIDTH));
        for (int col = 0; col < GRID_COLS; col++) {
            sb.append(" ").append(centerLabel(String.valueOf(col), CELL_INNER_WIDTH));
        }
        sb.append("\n");

        sb.append("    ┌").append(mowerDash);
        for (int col = 0; col < GRID_COLS; col++) {
            boolean deadline = col == deadlineCol;
            sb.append(deadline ? redTeeTop : "┬");
            sb.append(deadline ? redDash : cellDash);
        }
        sb.append(deadlineCol == GRID_COLS - 1 ? RED + "┐" + RESET : "┐").append("\n");

        for (int row = 0; row < GRID_ROWS; row++) {
            for (int layer = 0; layer < CELL_HEIGHT; layer++) {
                if (layer == 0) {
                    sb.append(String.format(" %d  │", row));
                } else {
                    sb.append("    │");
                }
                sb.append(padVisible(getMowerGutter(state, row, layer), MOWER_INNER_WIDTH));

                for (int col = 0; col < GRID_COLS; col++) {
                    boolean deadline = col == deadlineCol;
                    sb.append(deadline ? redBar : "│");
                    sb.append(renderCellLayer(state, row, col, layer));
                }
                sb.append(deadlineCol == GRID_COLS - 1 ? redBar : "│");
                sb.append("\n");
            }

            if (row < GRID_ROWS - 1) {
                sb.append("    ├").append(mowerDash);
                for (int col = 0; col < GRID_COLS; col++) {
                    boolean deadline = col == deadlineCol;
                    sb.append(deadline ? redTeeMid : "┼");
                    sb.append(deadline ? redDash : cellDash);
                }
                sb.append(deadlineCol == GRID_COLS - 1 ? RED + "┤" + RESET : "┤").append("\n");
            }
        }

        sb.append("    └").append(mowerDash);
        for (int col = 0; col < GRID_COLS; col++) {
            boolean deadline = col == deadlineCol;
            sb.append(deadline ? redTeeBot : "┴");
            sb.append(deadline ? redDash : cellDash);
        }
        sb.append(deadlineCol == GRID_COLS - 1 ? RED + "┘" + RESET : "┘");
        return sb.toString();
    }

    @Override
    public void renderGrid(ReadOnlyGameState state) {
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
        content = padVisible(content, CELL_INNER_WIDTH);
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
        return padVisible(left, 4) + "  " + padVisible(right, 4);
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

    @Override
    public void renderSunDrops(ReadOnlyGameState state) {
    }

    @Override
    public void renderZombieDetails(ReadOnlyGameState state) {
    }

    @Override
    public void renderPlantDetails(ReadOnlyGameState state) {
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
        System.out.print("\033[3J\033[2J\033[H");
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