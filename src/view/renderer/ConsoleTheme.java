package view.renderer;

final class ConsoleTheme {

    static final int SCREEN_WIDTH = 120;

    static final String RESET = "\u001B[0m";
    static final String RED = "\u001B[31m";
    static final String GREEN = "\u001B[32m";
    static final String YELLOW = "\u001B[33m";
    static final String BLUE = "\u001B[34m";
    static final String PURPLE = "\u001B[35m";
    static final String CYAN = "\u001B[36m";
    static final String WHITE = "\u001B[37m";
    static final String ORANGE = "\u001B[38;5;208m";
    static final String GRAY = "\u001B[38;5;240m";
    static final String BOLD = "\u001B[1m";
    static final String BG_WATER = "\u001B[48;5;24m";
    static final String BG_ICE = "\u001B[48;5;153m";
    static final String BG_NECRO = "\u001B[48;5;54m";

    private ConsoleTheme() {
    }
}
