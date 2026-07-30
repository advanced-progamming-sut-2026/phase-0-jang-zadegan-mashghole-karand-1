package view.renderer;

import static view.renderer.ConsoleTheme.BOLD;
import static view.renderer.ConsoleTheme.CYAN;
import static view.renderer.ConsoleTheme.GRAY;
import static view.renderer.ConsoleTheme.GREEN;
import static view.renderer.ConsoleTheme.RED;
import static view.renderer.ConsoleTheme.RESET;
import static view.renderer.ConsoleTheme.YELLOW;

import controller.ChapterCommands;
import controller.MiniGameCommands;
import controller.PickPlantsController;
import model.data.content.chapter.ChapterCatalog;
import model.data.content.chapter.ChapterType;
import model.data.content.minigame.MiniGameCatalog;
import model.data.content.minigame.MiniGameType;
import model.data.plant.PlantType;
import model.service.GameNavigationState;
import model.service.GameNavigationState.Phase;

final class ConsoleLevelScreens {

    private final ConsoleRenderEngine engine;

    ConsoleLevelScreens(ConsoleRenderEngine engine) {
        this.engine = engine;
    }

    String getLevelSelectionScreen(GameNavigationState gameNavigation) {
        StringBuilder sb = new StringBuilder();

        if (gameNavigation.phase == Phase.CHAPTER) {
            sb.append(buildChapterPhase(gameNavigation));
        } else if (gameNavigation.phase == Phase.MINIGAME) {
            sb.append(buildMinigamePhase(gameNavigation));
        } else if (gameNavigation.phase == Phase.LEVEL) {
            sb.append(buildLevelPhase(gameNavigation));
        } else if (gameNavigation.phase == Phase.PLANT) {
            sb.append(buildPlantPhase(gameNavigation));
        } else {
            sb.append(engine.getHeaderBox("🌱  " + BOLD + "PLANTS VS ZOMBIES 2 | Game" + RESET + "  🧟", GREEN));
        }

        sb.append("\n");
        sb.append(engine.getMessages());
        return sb.toString();
    }

    private String buildChapterPhase(GameNavigationState gameNavigation) {
        StringBuilder sb = new StringBuilder();
        String title = "🌱  " + BOLD + "PLANTS VS ZOMBIES 2 | Chapters" + RESET + "  🧟";
        sb.append(engine.getHeaderBox(title, GREEN));
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
        return sb.toString();
    }

    private String buildMinigamePhase(GameNavigationState gameNavigation) {
        StringBuilder sb = new StringBuilder();
        String title = "🌱  " + BOLD + "PLANTS VS ZOMBIES 2 | Minigames" + RESET + "  🧟";
        sb.append(engine.getHeaderBox(title, GREEN));
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
        return sb.toString();
    }

    private String buildLevelPhase(GameNavigationState gameNavigation) {
        StringBuilder sb = new StringBuilder();
        String chapterName = ChapterCommands.displayName(gameNavigation.selectedChapter);
        String title = "🌱  " + BOLD + "PLANTS VS ZOMBIES 2 | " + chapterName + RESET + "  🧟";
        sb.append(engine.getHeaderBox(title, GREEN));
        sb.append("\n");
        sb.append("  " + CYAN + "1." + RESET + " Select Level: " + GREEN
                + "select level -n <level_number>" + RESET + "\n");
        sb.append("  " + CYAN + "2." + RESET + " Back: " + GREEN + "menu exit" + RESET + "\n");
        sb.append("  " + CYAN + "3." + RESET + " Quit: " + GREEN + "quit" + RESET + "\n");
        sb.append("\n");
        sb.append("  " + BOLD + "Levels:" + RESET + "\n");
        for (int i = 1; i <= ChapterCatalog.LEVELS_PER_CHAPTER; i++) {
            boolean unlocked = gameNavigation.isLevelUnlocked(gameNavigation.selectedChapter, i);
            int highScore = gameNavigation.getLevelHighScore(gameNavigation.selectedChapter, i);
            sb.append("    ").append(CYAN).append(i).append(RESET);
            sb.append(" - ").append(unlocked ? GREEN + "unlocked" + RESET : RED + "locked" + RESET);
            if (highScore > 0) {
                sb.append(" - high score: ").append(GREEN).append(highScore).append(RESET);
            } else {
                sb.append(" - high score: ").append(GRAY).append("-").append(RESET);
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private String buildPlantPhase(GameNavigationState gameNavigation) {
        StringBuilder sb = new StringBuilder();
        String title = "🌱  " + BOLD + "PLANTS VS ZOMBIES 2 | Pick Plants" + RESET + "  🧟";
        sb.append(engine.getHeaderBox(title, GREEN));
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
        return sb.toString();
    }
}
