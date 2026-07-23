package model.quest;

import model.core.GameState;
import model.data.content.chapter.ChapterType;
import model.data.plant.PlantCategory;
import model.data.plant.PlantType;
import model.storage.user.User;

public class QuestAssigner {
    public static void ensureAssigned(User user) {
        if (user == null || user.quests == null || !user.quests.isEmpty()) {
            return;
        }

        user.quests.add(new KillWithPlantQuest(
                "Only Cactus",
                QuestPriority.HIGH, QuestCategory.DAILY,
                "Kill 10 zombies with cactus", 10,
                RewardType.DIAMOND, 20,
                null, PlantType.Cactus));
        user.quests.add(new KillWithPlantQuest(
                "Plant professional",
                QuestPriority.HIGH, QuestCategory.DAILY,
                "Kill 10 zombies with every attacker plant", 10,
                RewardType.DIAMOND, 20,
                null));
        user.quests.add(new KillZombieInChapter("Ancient_Egypt Hunter",
                QuestPriority.HIGH, QuestCategory.MAIN, "kill 50 zombies in Ancient_Egypt Chapter",
                50, ChapterType.ANCIENT_EGYPT,
                RewardType.SEED_PACK, 10, PlantType.Cactus));
        user.quests.add(new KillZombieInChapter("FrostBite_Caves Hunter",
                QuestPriority.HIGH, QuestCategory.MAIN, "kill 50 zombies in FrostBite_Caves Chapter",
                50, ChapterType.FROSTBITE_CAVES,
                RewardType.SEED_PACK, 10, PlantType.Pepper_pult));
        user.quests.add(new KillZombieInChapter("Big_Wave_Beach Hunter",
                QuestPriority.HIGH, QuestCategory.MAIN, "kill 50 zombies in Big_Wave_Beach Chapter",
                50, ChapterType.BIG_WAVE_BEACH,
                RewardType.SEED_PACK, 10, PlantType.BowlingBulb));
        user.quests.add(new KillZombieInChapter("Dark_Ages Hunter",
                QuestPriority.HIGH, QuestCategory.MAIN, "kill 50 zombies Dark_Ages Chapter",
                50, ChapterType.DARK_AGES,
                RewardType.SEED_PACK, 10, PlantType.Magnet_shroom));
        user.quests.add(new CollectSunQuest("Daily sunCatcher", QuestPriority.MEDIUM, QuestCategory.DAILY,
                "collect 3000 sun in a day", 3000, RewardType.COIN,
                30, null, true));
        user.quests.add(new CollectSunQuest("Daily sunCatcher", QuestPriority.MEDIUM, QuestCategory.DAILY,
                "collect 4000 sun in a day", 4000, RewardType.COIN,
                40, null, true));
        user.quests.add(new CollectSunQuest("Daily sunCatcher", QuestPriority.MEDIUM, QuestCategory.DAILY,
                "collect 5000 sun in a day", 5000, RewardType.COIN,
                50, null, true));
        user.quests.add(new CollectSunQuest("Cloudy day", QuestPriority.HIGH, QuestCategory.DAILY,
                "finish a level with only 3 sun producer plants", 3,
                RewardType.DIAMOND, 10, null, false));
        user.quests.add(new KillCountQuest("Speed of action", QuestPriority.MEDIUM, QuestCategory.MAIN,
                KillCountQuest.specificQuest.SPEED_KILLING,
                "kill 10 zombies in less than 30 seconds passed in first wave",
                10, RewardType.COIN, 500, null));
        user.quests.add(new KillCountQuest("Almost Winner", QuestPriority.MEDIUM, QuestCategory.DAILY,
                KillCountQuest.specificQuest.NO_LAWNMOWER_KILLING,
                "kill 10 zombies in first column of a row with no lawnmower",
                10, RewardType.COIN, 300, null));
        user.quests.add(new KillCountQuest("Mow time", QuestPriority.MEDIUM, QuestCategory.EPIC,
                KillCountQuest.specificQuest.LAWNMOWER_KILLING, "kill 10 zombies with lawnmower",
                10, RewardType.DIAMOND, 10, null));
        user.quests.add(new KillCountQuest("Mow time", QuestPriority.MEDIUM, QuestCategory.EPIC,
                KillCountQuest.specificQuest.LAWNMOWER_KILLING, "kill 20 zombies with lawnmower",
                20, RewardType.DIAMOND, 20, null));
        user.quests.add(new KillCountQuest("Mow time", QuestPriority.MEDIUM, QuestCategory.EPIC,
                KillCountQuest.specificQuest.LAWNMOWER_KILLING, "kill 30 zombies with lawnmower",
                30, RewardType.DIAMOND, 30, null));
        user.quests.add(new KillCountQuest("Mow time", QuestPriority.MEDIUM, QuestCategory.EPIC,
                KillCountQuest.specificQuest.LAWNMOWER_KILLING, "kill 40 zombies with lawnmower",
                40, RewardType.DIAMOND, 40, null));
        user.quests.add(new KillCountQuest("Mow time", QuestPriority.MEDIUM, QuestCategory.EPIC,
                KillCountQuest.specificQuest.LAWNMOWER_KILLING, "kill 50 zombies with lawnmower",
                50, RewardType.DIAMOND, 50, null));
        user.quests.add(new KillFamilyQuest("Family Killer", QuestPriority.MEDIUM, QuestCategory.MAIN,
                "for each attacker family, win a level using only that family to kill",
                RewardType.COIN, 1000, null));
        user.quests.add(new KillFamilyQuest("Professional Destroyer", QuestPriority.HIGH, QuestCategory.EPIC,
                "kill 3 zombies using explosive plants", 3, RewardType.COIN,
                100, null, PlantCategory.EXPLOSIVE));
        for (int maxLoss = 0; maxLoss <= 5; maxLoss++) {
            user.quests.add(new WinConstraintQuest(
                    "Economic Herbivore",
                    QuestPriority.HIGH, QuestCategory.MAIN,
                    "win a level without losing more than " + maxLoss + " plants",
                    1, RewardType.SEED_PACK, Math.max(1, 20 - maxLoss), PlantType.PeaShooter,
                    maxLoss, 0, WinConstraintQuest.Constraint.MAX_PLANT_LOSS, null));
        }
        user.quests.add(new WinConstraintQuest("Defense Master",
                QuestPriority.CRITICAL, QuestCategory.EPIC, "finish a level with exactly zero sun",
                1, RewardType.DIAMOND, 200, null,
                0, 0, WinConstraintQuest.Constraint.SUN_AT_END, null));
        user.quests.add(new WinConstraintQuest(
                "Night or Morning", QuestPriority.HIGH, QuestCategory.EPIC,
                "finish a day level using only night (mushroom) plants",
                1, RewardType.DIAMOND, 20, null, 0, 0,
                WinConstraintQuest.Constraint.NIGHT_OR_MORNING, null));
        user.quests.add(new WinConstraintQuest("Win After Win",
                QuestPriority.MEDIUM, QuestCategory.DAILY,
                "win 5 levels in a row on maximum difficulty",
                5, RewardType.COIN, 5000, null,
                0, 0, WinConstraintQuest.Constraint.WIN_HARD, null));
        user.quests.add(new WinConstraintQuest("Cloudy Day",
                QuestPriority.HIGH, QuestCategory.DAILY,
                "finish a level with exactly 3 sun producer plants",
                1, RewardType.DIAMOND, 10, null,
                0, 3, WinConstraintQuest.Constraint.CLOUDY_DAY, null));
        for (PlantCategory family : PlantCategory.values()) {
            user.quests.add(new WinConstraintQuest(
                    "Winning in Limitations",
                    QuestPriority.HIGH, QuestCategory.DAILY,
                    "win a level without using any plant from the " + family.name() + " family",
                    1, RewardType.DIAMOND, 100, null,
                    0, 0, WinConstraintQuest.Constraint.FORBIDDEN_FAMILY, family));
        }
        user.quests.add(new GameBoardQuest("Symmetry", QuestPriority.HIGH, QuestCategory.DAILY,
                "finish the level with a symmetric garden", 1, RewardType.COIN, 500,
                null, 0, 0, GameBoardQuest.boardState.SYMMETRIC));
        user.quests.add(new GameBoardQuest("No OCD!", QuestPriority.MEDIUM, QuestCategory.DAILY,
                "finish the level with no garden symmetry (except middle row)", 1, RewardType.COIN, 800,
                null, 0, 0, GameBoardQuest.boardState.ASYMMETRIC));
        for (int col = 0; col < GameState.GRID_COLS; col++) {
            user.quests.add(new GameBoardQuest(
                    "One Less Column",
                    QuestPriority.HIGH, QuestCategory.DAILY,
                    "win a level with no plants in column " + col,
                    1, RewardType.DIAMOND, 10, null,
                    0, col, GameBoardQuest.boardState.COL));
        }
        for (int row = 0; row < GameState.GRID_ROWS; row++) {
            user.quests.add(new GameBoardQuest(
                    "Defenseless Row",
                    QuestPriority.HIGH, QuestCategory.DAILY,
                    "win a level with no plants in row " + row,
                    1, RewardType.DIAMOND, 20, null,
                    row, 0, GameBoardQuest.boardState.ROW));
        }
        int crossCount = Math.min(GameState.GRID_ROWS, GameState.GRID_COLS);
        for (int n = 0; n < crossCount; n++) {
            user.quests.add(new GameBoardQuest(
                    "Defenseless Cross",
                    QuestPriority.HIGH, QuestCategory.DAILY,
                    "win a level with no plants in row " + n + " and column " + n,
                    1, RewardType.DIAMOND, 25, null,
                    n, n, GameBoardQuest.boardState.ROW_COL));
        }
    }
}
