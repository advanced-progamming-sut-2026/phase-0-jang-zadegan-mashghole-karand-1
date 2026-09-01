package model.storage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import model.data.content.chapter.ChapterType;
import model.data.content.minigame.MiniGameCatalog;
import model.data.content.minigame.MiniGameType;
import model.data.plant.PlantType;
import model.data.zombie.ZombieType;
import model.news.NewsItem;
import model.service.Hash;
import model.storage.user.Gender;
import model.storage.user.SafetyQuestion;
import model.storage.user.User;

public class SqlStorageManager implements StorageManager {
    private static final String DEFAULT_DB_ADDRESS = "data/game.db";

    private final Object lock = new Object();

    private final SqlProgressLoader progressLoader = new SqlProgressLoader();
    private final SqlProgressStore progressStore;
    private final SqlUserLoader userLoader;
    private final SqlUserSaver userSaver;
    private final SqlAccountManager accountManager;
    private final SqlDemoUserSeeder demoUserSeeder;
    private final SqlUnlockService unlockService;

    private User currentUser = null;
    private String sessionToken = null;

    public SqlStorageManager() {
        this(DEFAULT_DB_ADDRESS);
    }

    public SqlStorageManager(String databasePath) {
        this.progressStore = new SqlProgressStore(databasePath);
        this.userLoader = new SqlUserLoader(databasePath, progressLoader);
        this.userSaver = new SqlUserSaver(databasePath, progressStore);
        this.accountManager = new SqlAccountManager(databasePath, progressStore);
        this.demoUserSeeder = new SqlDemoUserSeeder(userLoader, userSaver, accountManager);
        this.unlockService = new SqlUnlockService(progressStore);
        SqlSchemaInitializer.initialize(databasePath);
        demoUserSeeder.seedIfMissing();
    }

    @Override
    public boolean register(String username, String password, String email, String nickname, Gender gender,
            SafetyQuestion safetyQuestion) {
        if (username == null || username.trim().isEmpty()
                || password == null || password.trim().isEmpty()
                || safetyQuestion == null) {
            return false;
        }

        synchronized (lock) {
            if (usernameExists(username)) {
                return false;
            }
            return accountManager.register(username, password, email, nickname, gender, safetyQuestion);
        }
    }

    @Override
    public boolean login(String username, String password, boolean stayLoggedIn) {
        if (username == null || password == null) {
            return false;
        }

        synchronized (lock) {
            User user = userLoader.loadUser(username);
            if (user == null || !user.password.equals(Hash.hashPassword(password))) {
                return false;
            }

            currentUser = user;
            sessionToken = UUID.randomUUID().toString();
            accountManager.persistSession(stayLoggedIn ? username : null, stayLoggedIn, null);
            return true;
        }
    }

    @Override
    public boolean forceLogin(String username, boolean stayLoggedIn) {
        return forceLogin(username, stayLoggedIn, null);
    }

    public boolean forceLogin(String username, boolean stayLoggedIn, String authToken) {
        if (username == null) {
            return false;
        }
        synchronized (lock) {
            User user = userLoader.loadUser(username);
            if (user == null) {
                return false;
            }
            currentUser = user;
            sessionToken = UUID.randomUUID().toString();
            accountManager.persistSession(
                    stayLoggedIn ? username : null,
                    stayLoggedIn,
                    stayLoggedIn ? authToken : null);
            return true;
        }
    }

    public String loadPersistedAuthToken() {
        synchronized (lock) {
            return accountManager.loadPersistedAuthToken();
        }
    }

    @Override
    public void logout() {
        synchronized (lock) {
            currentUser = null;
            sessionToken = null;
            accountManager.clearPersistedSession();
        }
    }

    @Override
    public User getCurrentUser() {
        return currentUser;
    }

    @Override
    public boolean isLoggedIn() {
        return currentUser != null && sessionToken != null;
    }

    @Override
    public boolean usernameExists(String username) {
        if (username == null) {
            return false;
        }

        synchronized (lock) {
            return accountManager.usernameExists(username);
        }
    }

    @Override
    public List<User> getUsers() {
        synchronized (lock) {
            List<User> users = new ArrayList<>();
            for (String username : accountManager.listUsernames()) {
                User user = userLoader.loadUser(username);
                if (user != null) {
                    users.add(user);
                }
            }
            return users;
        }
    }

    @Override
    public User getUserByUsername(String username) {
        if (username == null) {
            return null;
        }

        synchronized (lock) {
            return userLoader.loadUser(username);
        }
    }

    @Override
    public boolean updatePassword(String username, String newPassword) {
        if (username == null || newPassword == null || newPassword.isEmpty()) {
            return false;
        }
        String hashedPassword = Hash.hashPassword(newPassword);
        synchronized (lock) {
            boolean updated = accountManager.updatePassword(username, hashedPassword);
            if (updated && currentUser != null && currentUser.username.equals(username)) {
                currentUser.password = hashedPassword;
            }
            return updated;
        }
    }

    @Override
    public String getCurrentUsername() {
        return currentUser != null ? currentUser.username : null;
    }

    @Override
    public void saveProgress() {
        if (!isLoggedIn()) {
            return;
        }
        synchronized (lock) {
            userSaver.saveUserProgress(currentUser);
        }
    }

    @Override
    public void loadProgress() {
        synchronized (lock) {
            String username = accountManager.loadPersistedUsername();
            if (username == null) {
                return;
            }

            User user = userLoader.loadUser(username);
            if (user == null) {
                accountManager.clearPersistedSession();
                return;
            }

            currentUser = user;
            sessionToken = UUID.randomUUID().toString();
        }
    }

    @Override
    public void recordGamePlayed() {
        if (!isLoggedIn()) {
            return;
        }
        synchronized (lock) {
            currentUser.gamesPlayed++;
            userSaver.saveUserProfile(currentUser);
        }
    }

    @Override
    public void markLevelCompleted(ChapterType chapter, int levelNumber) {
        if (!isLoggedIn() || chapter == null) {
            return;
        }
        synchronized (lock) {
            currentUser.gameProgress.setLastProgress(chapter, levelNumber);
            recordLevelCompletion(CompletedLevelKey.campaign(chapter, levelNumber),
                    "You completed level: " + chapter.name() + " " + levelNumber);
        }
    }

    @Override
    public void markMinigameCompleted(MiniGameType miniGame) {
        if (!isLoggedIn() || miniGame == null) {
            return;
        }
        synchronized (lock) {
            recordLevelCompletion(CompletedLevelKey.minigame(miniGame),
                    "You completed minigame: " + miniGame.name().replace('_', ' '));
        }
    }

    private void recordLevelCompletion(String levelId, String newsMessage) {
        boolean alreadyCompleted = currentUser.gameProgress.getCompletedLevelIds().contains(levelId);
        currentUser.gameProgress.completeLevel(levelId);
        progressStore.persistCompletedLevelId(currentUser.username, levelId);
        if (!alreadyCompleted) {
            addNews(newsMessage);
        }
    }

    @Override
    public boolean recordLevelHighScore(ChapterType chapter, int levelNumber, int score) {
        return false;
    }

    @Override
    public void recordRankedScore(int highestScore) {
        if (!isLoggedIn() || highestScore < 0) {
            return;
        }
        synchronized (lock) {
            if (highestScore > currentUser.highestScore) {
                currentUser.highestScore = highestScore;
            } else {
                currentUser.highestScore = Math.max(currentUser.highestScore, highestScore);
            }
            // Server is absolute source — sync to reported value
            currentUser.highestScore = highestScore;
            userSaver.saveUserProfile(currentUser);
        }
    }

    @Override
    public int getLevelHighScore(ChapterType chapter, int levelNumber) {
        return 0;
    }

    @Override
    public Map<String, Integer> getLevelHighScores() {
        return Map.of();
    }

    @Override
    public boolean changeUsername(String newUsername) {
        if (!isLoggedIn() || newUsername == null || newUsername.isBlank()) {
            return false;
        }
        synchronized (lock) {
            String oldUsername = currentUser.username;
            if (oldUsername.equals(newUsername)) {
                return false;
            }
            if (usernameExists(newUsername)) {
                return false;
            }

            boolean renamed = accountManager.renameUsername(oldUsername, newUsername);
            if (renamed) {
                currentUser.username = newUsername;
            }
            return renamed;
        }
    }

    @Override
    public void changeNickname(String nickname) {
        if (!isLoggedIn() || nickname == null) {
            return;
        }
        synchronized (lock) {
            currentUser.nickname = nickname;
            userSaver.saveUserProfile(currentUser);
        }
    }

    @Override
    public void changeEmail(String email) {
        if (!isLoggedIn() || email == null) {
            return;
        }
        synchronized (lock) {
            currentUser.email = email;
            userSaver.saveUserProfile(currentUser);
        }
    }

    @Override
    public boolean changeProfilePassword(String oldPassword, String newPassword) {
        if (!isLoggedIn() || oldPassword == null || newPassword == null || newPassword.isEmpty()) {
            return false;
        }
        synchronized (lock) {
            if (!currentUser.password.equals(Hash.hashPassword(oldPassword))) {
                return false;
            }
            currentUser.password = Hash.hashPassword(newPassword);
            userSaver.saveUserProfile(currentUser);
            return true;
        }
    }

    @Override
    public void updateUserProfile(User profile) {
        if (profile == null || currentUser == null) {
            return;
        }
        synchronized (lock) {
            if (!currentUser.username.equals(profile.username)) {
                return;
            }
            currentUser = profile;
            userSaver.saveUserProfile(profile);
        }
    }

    @Override
    public void addNews(String message) {
        if (!isLoggedIn() || message == null || message.isBlank()) {
            return;
        }
        synchronized (lock) {
            NewsItem item = currentUser.newsFeed.addNews(message);
            if (item != null) {
                progressStore.insertNewsItem(currentUser.username, item);
            }
        }
    }

    @Override
    public void markAllNewsAsRead() {
        if (!isLoggedIn()) {
            return;
        }
        synchronized (lock) {
            currentUser.newsFeed.markAllUnreadAsRead();
            progressStore.markAllNewsRead(currentUser.username);
        }
    }

    @Override
    public void unlockMinigame(MiniGameType minigame) {
        if (!isLoggedIn() || minigame == null || !MiniGameCatalog.isPlayable(minigame)) {
            return;
        }
        synchronized (lock) {
            if (!unlockService.unlockMinigame(currentUser, minigame)) {
                return;
            }
            addNews("You unlocked a new minigame: " + formatMinigameName(minigame));
        }
    }

    @Override
    public boolean isMinigameUnlocked(MiniGameType minigame) {
        return isLoggedIn() && minigame != null && MiniGameCatalog.isPlayable(minigame)
                && currentUser.gameProgress.isMinigameUnlocked(minigame);
    }

    @Override
    public List<MiniGameType> getUnlockedMinigames() {
        if (!isLoggedIn()) {
            return new ArrayList<>();
        }
        return currentUser.gameProgress.getUnlockedMinigames().stream().filter(MiniGameCatalog::isPlayable).toList();
    }

    @Override
    public void unlockChapter(ChapterType chapter) {
        if (!isLoggedIn() || chapter == null) {
            return;
        }
        synchronized (lock) {
            unlockService.unlockChapter(currentUser, chapter);
        }
    }

    @Override
    public boolean isChapterUnlocked(ChapterType chapter) {
        return isLoggedIn() && chapter != null && currentUser.gameProgress.isChapterUnlocked(chapter);
    }

    @Override
    public List<ChapterType> getUnlockedChapters() {
        return isLoggedIn() ? new ArrayList<>(currentUser.gameProgress.getUnlockedChapters()) : new ArrayList<>();
    }

    @Override
    public void unlockPlant(PlantType plant) {
        if (!isLoggedIn() || plant == null) {
            return;
        }
        synchronized (lock) {
            if (!unlockService.unlockPlant(currentUser, plant)) {
                return;
            }
            addNews("You unlocked a new plant: " + plant.name);
        }
    }

    @Override
    public boolean isPlantUnlocked(PlantType plant) {
        return isLoggedIn() && plant != null && currentUser.collection.isPlantUnlocked(plant);
    }

    @Override
    public List<PlantType> getUnlockedPlants() {
        return isLoggedIn() ? new ArrayList<>(currentUser.collection.getUnlockedPlants()) : new ArrayList<>();
    }

    @Override
    public void unlockZombie(ZombieType zombie) {
        if (!isLoggedIn() || zombie == null) {
            return;
        }
        synchronized (lock) {
            if (!unlockService.unlockZombie(currentUser, zombie)) {
                return;
            }
            addNews("You unlocked a new zombie: " + zombie.name);
        }
    }

    @Override
    public boolean isZombieUnlocked(ZombieType zombie) {
        return isLoggedIn() && zombie != null && currentUser.collection.isZombieUnlocked(zombie);
    }

    @Override
    public List<ZombieType> getUnlockedZombies() {
        return isLoggedIn() ? new ArrayList<>(currentUser.collection.getUnlockedZombies()) : new ArrayList<>();
    }

    @Override
    public void loadQuestProgress(User user) {
        if (user == null) {
            return;
        }
        synchronized (lock) {
            userLoader.loadQuestProgress(user);
        }
    }

    private String formatMinigameName(MiniGameType minigame) {
        return Arrays.stream(minigame.name().split("_"))
                .filter(word -> !word.isEmpty())
                .map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }
}
