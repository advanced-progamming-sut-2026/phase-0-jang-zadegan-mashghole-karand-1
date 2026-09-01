package view.renderer;

import static view.renderer.ConsoleTheme.BLUE;
import static view.renderer.ConsoleTheme.BOLD;
import static view.renderer.ConsoleTheme.CYAN;
import static view.renderer.ConsoleTheme.GRAY;
import static view.renderer.ConsoleTheme.GREEN;
import static view.renderer.ConsoleTheme.ORANGE;
import static view.renderer.ConsoleTheme.PURPLE;
import static view.renderer.ConsoleTheme.RED;
import static view.renderer.ConsoleTheme.RESET;
import static view.renderer.ConsoleTheme.SCREEN_WIDTH;
import static view.renderer.ConsoleTheme.YELLOW;

import java.util.List;

import controller.ShopController;
import model.data.plant.PlantType;
import model.service.CollectionViewState;
import model.service.LeaderboardViewState;
import model.service.NewsViewState;
import model.service.ProfileViewState;
import model.service.QuestViewState;
import model.service.SettingsViewState;
import model.shop.ShopCurrency;
import model.shop.ShopItem;
import model.shop.ShopItems;

final class ConsoleOverlayScreens {

    private final ConsoleRenderEngine engine;

    ConsoleOverlayScreens(ConsoleRenderEngine engine) {
        this.engine = engine;
    }

    String getCollectionScreen(CollectionViewState collection) {
        StringBuilder sb = new StringBuilder();
        sb.append(buildCollectionHeader(collection));
        sb.append(buildCollectionBody(collection));
        sb.append("\n");
        sb.append(COLLECTION_MENU);
        sb.append("\n");
        sb.append(engine.getMessages());
        return sb.toString();
    }

    private static final String COLLECTION_MENU = "  " + CYAN + "1." + RESET + " Unlocked Plants: " + GREEN
            + "menu collection show-plants" + RESET + "\n"
            + "  " + CYAN + "2." + RESET + " Unlocked Zombies: " + GREEN
            + "menu collection show-zombies" + RESET + "\n"
            + "  " + CYAN + "3." + RESET + " All Plants: " + GREEN
            + "menu collection show-all-plants" + RESET + "\n"
            + "  " + CYAN + "4." + RESET + " All Zombies: " + GREEN
            + "menu collection show-all-zombies" + RESET + "\n"
            + "  " + CYAN + "5." + RESET + " Plant Details: " + GREEN
            + "menu collection show-plant -p <name>" + RESET + "\n"
            + "  " + CYAN + "6." + RESET + " Zombie Details: " + GREEN
            + "menu collection show-zombie -z <name>" + RESET + "\n"
            + "  " + CYAN + "7." + RESET + " Buy Plant (2000 coins): " + GREEN
            + "menu collection purchase-plant -p <name>" + RESET + "\n"
            + "  " + CYAN + "8." + RESET + " Upgrade Plant: " + GREEN
            + "menu collection upgrade-plant -p <name>" + RESET + "\n"
            + "  " + CYAN + "9." + RESET + " Back: " + GREEN + "menu exit" + RESET + "\n";

    private String buildCollectionHeader(CollectionViewState collection) {
        boolean plantsTab = collection.tab == CollectionViewState.Tab.PLANTS;
        boolean unlockedMode = collection.mode == CollectionViewState.Mode.UNLOCKED;
        String tabLabel = plantsTab ? "Plants" : "Zombies";
        String modeLabel = unlockedMode ? "Unlocked" : "All";
        String title = "🌱  " + BOLD + "PLANTS VS ZOMBIES 2 | Collection | " + tabLabel
                + " (" + modeLabel + ")" + RESET + "  🧟";

        String plantsTabMark = plantsTab ? GREEN + "[plants]" + RESET : GRAY + "plants" + RESET;
        String zombiesTabMark = !plantsTab ? GREEN + "[zombies]" + RESET : GRAY + "zombies" + RESET;

        StringBuilder sb = new StringBuilder();
        sb.append(engine.getHeaderBox(title, PURPLE));
        sb.append("\n");
        sb.append("  Tabs: ").append(plantsTabMark).append("  ").append(zombiesTabMark).append("\n");
        sb.append("\n");
        return sb.toString();
    }

    private String buildCollectionBody(CollectionViewState collection) {
        StringBuilder sb = new StringBuilder();
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

    String getSettingsOverlay(SettingsViewState settings) {
        StringBuilder sb = new StringBuilder();
        String title = "🌱  " + BOLD + "PLANTS VS ZOMBIES 2 | Settings" + RESET + "  🧟";

        sb.append(engine.getHeaderBox(title, YELLOW));
        sb.append("\n");
        sb.append(getDifficultyBar(settings)).append("\n");
        sb.append("\n");
        sb.append("  " + CYAN + "1." + RESET + " Change Difficulty: " + GREEN
                + "menu settings change-difficulty -l <level>" + RESET + "\n");
        sb.append("  " + CYAN + "2." + RESET + " Back: " + GREEN + "menu exit" + RESET + "\n");
        sb.append("\n");
        sb.append(engine.getMessages());
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

    String getNewsOverlay(NewsViewState news) {
        StringBuilder sb = new StringBuilder();
        String title = "🌱  " + BOLD + "PLANTS VS ZOMBIES 2 | News" + RESET + "  🧟";

        sb.append(engine.getHeaderBox(title, BLUE));
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
        sb.append(engine.getMessages());
        return sb.toString();
    }

    String getLeaderboardOverlay(LeaderboardViewState leaderboard) {
        StringBuilder sb = new StringBuilder();
        String title = "🌱  " + BOLD + "PLANTS VS ZOMBIES 2 | Leaderboard" + RESET + "  🧟";
        sb.append(engine.getHeaderBox(title, YELLOW));
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
        sb.append(engine.getMessages());
        return sb.toString();
    }

    String getProfileOverlay(ProfileViewState profile) {
        StringBuilder sb = new StringBuilder();
        String title = "🌱  " + BOLD + "PLANTS VS ZOMBIES 2 | Profile" + RESET + "  🧟";

        sb.append(engine.getHeaderBox(title, PURPLE));
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
        sb.append(engine.getMessages());
        return sb.toString();
    }

    String getShopScreen(int coins, int gems, PlantType dailyPlant, int dailyPrice, boolean dailyPurchased,
            ShopController.ShopDisplayMode mode) {
        StringBuilder sb = new StringBuilder();
        String title = "🌱  " + BOLD + "PLANTS VS ZOMBIES 2 | Shop" + RESET + "  🧟";

        sb.append(engine.getHeaderBox(title, YELLOW));
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
        sb.append(engine.getMessages());

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

    String getQuestsOverlay(QuestViewState quests) {
        StringBuilder sb = new StringBuilder();
        String title = "🌱  " + BOLD + "PLANTS VS ZOMBIES 2 | Travel Log" + RESET + "  🧟";
        sb.append(engine.getHeaderBox(title, GREEN));
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
        sb.append("  " + CYAN + "5." + RESET + " Main: " + GREEN + "travel log page main" + RESET + "\n");
        sb.append("  " + CYAN + "6." + RESET + " Epic: " + GREEN + "travel log page epic" + RESET + "\n");
        sb.append("  " + CYAN + "7." + RESET + " Active: " + GREEN + "travel log page active" + RESET + "\n");
        sb.append("  " + CYAN + "8." + RESET + " Completed: " + GREEN + "travel log page completed" + RESET + "\n");
        sb.append("  " + CYAN + "9." + RESET + " Minigames: " + GREEN + "menu enter minigames" + RESET + "\n");
        sb.append("  " + CYAN + "10." + RESET + " Back: " + GREEN + "menu exit" + RESET + "\n");
        sb.append("\n");
        sb.append(engine.getMessages());
        return sb.toString();
    }

    private int appendQuestSection(StringBuilder sb, int startIndex, String sectionTitle,
            String color, List<QuestViewState.Entry> entries) {
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
                + BOLD + engine.truncate(entry.name, Math.max(6, width - 22)) + RESET
                + " (" + status + ") " + reward;
        String description = "   " + engine.truncate(entry.description, width - 3);

        return new String[] {
                engine.padVisible(title, width),
                engine.padVisible(description, width)
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
        return engine.truncate(label.replace(' ', '_').toLowerCase(), 12);
    }
}
