package model.storage;

import model.data.content.chapter.ChapterType;
import model.data.content.minigame.MiniGameType;
import model.service.Hash;
import model.storage.user.Gender;
import model.storage.user.SafetyQuestion;
import model.storage.user.SafetyQuestionType;
import model.storage.user.User;

final class SqlDemoUserSeeder {

    private final SqlUserLoader userLoader;
    private final SqlUserSaver userSaver;
    private final SqlAccountManager accountManager;

    SqlDemoUserSeeder(SqlUserLoader userLoader, SqlUserSaver userSaver, SqlAccountManager accountManager) {
        this.userLoader = userLoader;
        this.userSaver = userSaver;
        this.accountManager = accountManager;
    }

    void seedIfMissing() {
        String demoPasswordHash = Hash.hashPassword("password");
        if (accountManager.usernameExists("player")) {
            repairExistingDemoUser(demoPasswordHash);
            return;
        }

        SafetyQuestion safetyQuestion = new SafetyQuestion(SafetyQuestionType.BIRTH_CITY, "DEMO_ANSWER");
        accountManager.register("player", "password", "test@test.com", "player", Gender.MALE, safetyQuestion);

        User demoUser = userLoader.loadUser("player");
        if (demoUser == null) {
            return;
        }

        demoUser.gameProgress.unlockChapter(ChapterType.ANCIENT_EGYPT);
        demoUser.gameProgress.unlockMinigame(MiniGameType.VASE_BREAKER);
        userSaver.saveUserProgress(demoUser);
    }

    private void repairExistingDemoUser(String demoPasswordHash) {
        User demoUser = userLoader.loadUser("player");
        if (demoUser == null) {
            return;
        }
        // Repair older DBs that stored the demo password in plaintext.
        boolean repaired = false;
        if (!demoPasswordHash.equals(demoUser.password)) {
            demoUser.password = demoPasswordHash;
            repaired = true;
        }
        if (!demoUser.gameProgress.isChapterUnlocked(ChapterType.ANCIENT_EGYPT)) {
            demoUser.gameProgress.unlockChapter(ChapterType.ANCIENT_EGYPT);
            repaired = true;
        }
        if (!demoUser.gameProgress.isMinigameUnlocked(MiniGameType.VASE_BREAKER)) {
            demoUser.gameProgress.unlockMinigame(MiniGameType.VASE_BREAKER);
            repaired = true;
        }
        if (repaired) {
            userSaver.saveUserProgress(demoUser);
        }
    }
}
