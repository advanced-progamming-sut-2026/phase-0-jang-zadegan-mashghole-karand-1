package view.renderer;

import static view.renderer.ConsoleTheme.CYAN;
import static view.renderer.ConsoleTheme.RESET;
import static view.renderer.ConsoleTheme.SCREEN_WIDTH;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

final class ConsoleRenderEngine {

    private static final int MIN_RENDER_HEIGHT = 40;
    private static final int MAX_MESSAGES = 4;
    private static final int PROMPT_PREFIX_COLUMNS = 2;

    private final List<String> messages = new ArrayList<>();
    private int messageScrollOffset = 0;

    private int currentRenderHeight = MIN_RENDER_HEIGHT;
    private String[] lastRenderLines = new String[MIN_RENDER_HEIGHT];
    private String currentScreenKey = "";
    private boolean needsFullClear = true;
    private boolean scrollRenderMode = false;
    private String promptInput = "";

    void prepareScreen(String screenKey) {
        if (!screenKey.equals(currentScreenKey)) {
            currentScreenKey = screenKey;
            needsFullClear = true;
            Arrays.fill(lastRenderLines, null);
        }
    }

    void render(String content) {
        synchronized (ConsoleRenderEngine.class) {
            List<String> lines = splitIntoLines(content);
            int termRows = detectTerminalRows();
            RenderPlan plan = buildRenderPlan(lines, termRows);
            if (!applyRenderPlan(plan)) {
                return;
            }
            if (plan.useScroll) {
                printScrollFrame(plan.lines);
            } else {
                printFixedFrame(plan.lines);
            }
            System.out.flush();
        }
    }

    private List<String> splitIntoLines(String content) {
        List<String> lines = new ArrayList<>();
        for (String line : content.split("\n", -1)) {
            lines.add(line);
        }
        if (!lines.isEmpty() && lines.get(lines.size() - 1).isEmpty()) {
            lines.remove(lines.size() - 1);
        }
        return lines;
    }

    private RenderPlan buildRenderPlan(List<String> lines, int termRows) {
        int contentHeight = lines.size();
        int tuiHeight = Math.max(MIN_RENDER_HEIGHT, contentHeight);
        boolean useScroll = tuiHeight + 2 > termRows;

        int height = useScroll ? Math.max(1, contentHeight) : tuiHeight;
        while (lines.size() < height) {
            lines.add("");
        }
        return new RenderPlan(lines, useScroll);
    }

    private boolean applyRenderPlan(RenderPlan plan) {
        int height = plan.lines.size();
        if (height != currentRenderHeight || plan.useScroll != scrollRenderMode) {
            needsFullClear = true;
            currentRenderHeight = height;
            lastRenderLines = new String[height];
        }
        scrollRenderMode = plan.useScroll;

        boolean changed = needsFullClear;
        if (!changed) {
            for (int i = 0; i < currentRenderHeight; i++) {
                if (!Objects.equals(plan.lines.get(i), lastRenderLines[i])) {
                    changed = true;
                    break;
                }
            }
        }
        return changed;
    }

    private void printScrollFrame(List<String> lines) {
        System.out.print("\033[3J\033[2J\033[H");
        for (int i = 0; i < currentRenderHeight; i++) {
            System.out.println(lines.get(i));
            lastRenderLines[i] = lines.get(i);
        }
        System.out.println("─".repeat(SCREEN_WIDTH));
        needsFullClear = false;
        drawCommandPrompt();
    }

    private void printFixedFrame(List<String> lines) {
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

    private static final class RenderPlan {
        private final List<String> lines;
        private final boolean useScroll;

        private RenderPlan(List<String> lines, boolean useScroll) {
            this.lines = lines;
            this.useScroll = useScroll;
        }
    }

    void drawCommandPrompt() {
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
            boolean finished = process.waitFor(300, TimeUnit.MILLISECONDS);
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

    void renderCommandPrompt(String input) {
        synchronized (ConsoleRenderEngine.class) {
            promptInput = input != null ? input : "";
            drawCommandPrompt();
            System.out.flush();
        }
    }

    void clearScreen() {
        needsFullClear = true;
        System.out.print("\033[3J\033[2J\033[H");
        System.out.flush();
    }

    void initialize() {
        clearScreen();
        messages.clear();
        messageScrollOffset = 0;
    }

    void renderMessage(String message) {
        messages.add(message);
        messageScrollOffset = 0;
    }

    void renderError(String error) {
        messages.add(ConsoleTheme.RED + error + RESET);
        messageScrollOffset = 0;
    }

    boolean scrollMessages(int olderDelta) {
        if (olderDelta == 0) {
            return false;
        }
        List<String> lines = buildMessageLines();
        int maxOffset = Math.max(0, lines.size() - MAX_MESSAGES);
        int next = Math.max(0, Math.min(maxOffset, messageScrollOffset + olderDelta));
        if (next == messageScrollOffset) {
            return false;
        }
        messageScrollOffset = next;
        return true;
    }

    String getMessages() {
        StringBuilder sb = new StringBuilder();
        int messageBoxWidth = SCREEN_WIDTH - 4;
        sb.append("╔" + "═".repeat(SCREEN_WIDTH - 2) + "╗\n");

        List<String> lines = buildMessageLines();
        int maxOffset = Math.max(0, lines.size() - MAX_MESSAGES);
        if (messageScrollOffset > maxOffset) {
            messageScrollOffset = maxOffset;
        }

        int end = lines.size() - messageScrollOffset;
        int start = Math.max(0, end - MAX_MESSAGES);
        int visible = Math.max(0, end - start);

        for (int i = start; i < end; i++) {
            String line = lines.get(i);
            int plainLength = stripAnsi(line).length();
            int padding = Math.max(0, messageBoxWidth - plainLength);
            sb.append("║ ").append(line).append(" ".repeat(padding)).append(" ║\n");
        }

        for (int i = 0; i < MAX_MESSAGES - visible; i++) {
            sb.append("║ ").append(" ".repeat(messageBoxWidth)).append(" ║\n");
        }

        sb.append("╚" + "═".repeat(SCREEN_WIDTH - 2) + "╝\n");

        return sb.toString();
    }

    private List<String> buildMessageLines() {
        int messageBoxWidth = SCREEN_WIDTH - 4;
        List<String> lines = new ArrayList<>();

        for (String msg : messages) {
            String plainMsg = stripAnsi(msg);

            if (plainMsg.length() > messageBoxWidth) {
                int pos = 0;
                while (pos < plainMsg.length()) {
                    int end = Math.min(pos + messageBoxWidth, plainMsg.length());
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
        return lines;
    }

    String stripAnsi(String str) {
        String ansiRegex = "\u001B\\[[;\\d]*[mK]";
        return str.replaceAll(ansiRegex, "");
    }

    int displayWidth(String text) {
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

    String truncate(String text, int maxLen) {
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

    String padVisible(String text, int width) {
        int visible = displayWidth(stripAnsi(text));
        if (visible >= width) {
            return text;
        }
        return text + " ".repeat(width - visible);
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

    String getHeaderBox(String title, String color) {
        StringBuilder sb = new StringBuilder();

        sb.append(getUpperBorder(color));
        sb.append(getBoxTitle(title, color));
        sb.append(getLowerBorder(color));

        return sb.toString();
    }
}
