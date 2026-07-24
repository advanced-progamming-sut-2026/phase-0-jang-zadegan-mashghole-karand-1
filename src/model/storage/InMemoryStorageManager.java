package model.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import model.data.content.chapter.ChapterType;
import model.data.content.minigame.MiniGameCatalog;
import model.data.content.minigame.MiniGameType;
import model.data.plant.PlantType;
import model.data.zombie.ZombieType;
import model.service.Hash;
import model.storage.user.Gender;
import model.storage.user.SafetyQuestion;
import model.storage.user.SafetyQuestionType;
import model.storage.user.User;

public class InMemoryStorageManager implements StorageManager {

    private final Map<String, User> users = new HashMap<>();
    private User currentUser = null;
    private String sessionToken = null;
    private boolean stayLoggedIn = false;
    private String persistedUsername = null;

    private static final String DEMO_USER = "player";
    private static final String DEMO_PASSWORD = "password";
    private static final String DEMO_EMAIL = "test@test.com";
    private static final String DEMO_NICKNAME = "password";
    private static final SafetyQuestion DEMO_SAFETY = new SafetyQuestion(SafetyQuestionType.BIRTH_CITY, "DEMO_ANSWER");

    public InMemoryStorageManager() {
        // Add demo user for testing
        User demoUser = new User(DEMO_USER, Hash.hashPassword(DEMO_PASSWORD), DEMO_EMAIL, DEMO_NICKNAME, Gender.MALE,
                DEMO_SAFETY);
        demoUser.gameProgress.unlockChapter(ChapterType.ANCIENT_EGYPT);
        demoUser.gameProgress.unlockMinigame(MiniGameType.VASE_BREAKER);
        demoUser.collection.unlockStarterPlants();
        users.put(DEMO_USER, demoUser);
    }

    @Override
    public boolean register(String username, String password, String email, String nickname, Gender gender,
            SafetyQuestion safetyQuestion) {
        if (username == null || username.trim().isEmpty())
            return false;
        if (password == null || password.trim().isEmpty())
            return false;
        if (safetyQuestion == null)
            return false;
        if (users.containsKey(username))
            return false;
        String hashedPassword = Hash.hashPassword(password);
        User profile = new User(username, hashedPassword, email, nickname, gender, safetyQuestion);
        profile.collection.unlockStarterPlants();
        users.put(username, profile);
        return true;
    }

    @Override
    public boolean login(String username, String password, boolean stayLoggedIn) {
        if (username == null || password == null)
            return false;

        User profile = users.get(username);
        if (profile == null)
            return false;
        if (!profile.password.equals(Hash.hashPassword(password)))
            return false;

        currentUser = profile;
        sessionToken = UUID.randomUUID().toString();
        this.stayLoggedIn = stayLoggedIn;
        if (stayLoggedIn) {
            persistedUsername = username;
        } else {
            persistedUsername = null;
        }
        return true;
    }

    @Override
    public void logout() {
        currentUser = null;
        sessionToken = null;
        stayLoggedIn = false;
        persistedUsername = null;
    }

    @Override
    public User getCurrentUser() {
        return currentUser;
    }

    @Override
    public List<User> getUsers() {
        List<User> userList = new ArrayList<>(users.values());
        return userList;
    }

    @Override
    public boolean isLoggedIn() {
        return currentUser != null && sessionToken != null;
    }

    @Override
    public boolean usernameExists(String username) {
        return username != null && users.containsKey(username);
    }

    @Override
    public User getUserByUsername(String username) {
        return users.get(username);
    }

    @Override
    public boolean updatePassword(String username, String newPassword) {
        User user = users.get(username);
        if (user == null || newPassword == null || newPassword.isEmpty()) {
            return false;
        }
        user.password = Hash.hashPassword(newPassword);
        return true;
    }

    @Override
    public String getCurrentUsername() {
        return currentUser != null ? currentUser.username : null;
    }

    @Override
    public void saveProgress() {
    }

    @Override
    public void loadProgress() {
        if (stayLoggedIn && persistedUsername != null) {
            User user = users.get(persistedUsername);
            if (user != null) {
                currentUser = user;
                sessionToken = UUID.randomUUID().toString();
            }
        }
    }

    @Override
    public void updateUserProfile(User profile) {
        if (profile != null && currentUser != null) {
            users.put(currentUser.username, profile);
        }
    }

    @Override
    public void recordGamePlayed() {
        if (isLoggedIn()) {
            currentUser.gamesPlayed++;
        }
    }

    @Override
    public void markLevelCompleted(ChapterType chapter, int levelNumber) {
        if (!isLoggedIn() || chapter == null) {
            return;
        }
        String levelId = CompletedLevelKey.campaign(chapter, levelNumber);
        boolean alreadyCompleted = currentUser.gameProgress.getCompletedLevelIds().contains(levelId);
        currentUser.gameProgress.completeLevel(levelId);
        currentUser.gameProgress.setLastProgress(chapter, levelNumber);
        if (!alreadyCompleted) {
            addNews("You completed level: " + chapter.name() + " " + levelNumber);
        }
    }

    @Override
    public void markMinigameCompleted(MiniGameType miniGame) {
        if (!isLoggedIn() || miniGame == null) {
            return;
        }
        String levelId = CompletedLevelKey.minigame(miniGame);
        boolean alreadyCompleted = currentUser.gameProgress.getCompletedLevelIds().contains(levelId);
        currentUser.gameProgress.completeLevel(levelId);
        if (!alreadyCompleted) {
            addNews("You completed minigame: " + miniGame.name().replace('_', ' '));
        }
    }

    @Override
    public boolean recordLevelHighScore(ChapterType chapter, int levelNumber, int score) {
        if (!isLoggedIn() || chapter == null || score <= 0) {
            return false;
        }
        boolean isLevelRecord = currentUser.gameProgress.recordLevelHighScore(chapter, levelNumber, score);
        if (score > currentUser.highestScore) {
            currentUser.highestScore = score;
        }
        return isLevelRecord;
    }

    @Override
    public int getLevelHighScore(ChapterType chapter, int levelNumber) {
        if (!isLoggedIn() || chapter == null) {
            return 0;
        }
        return currentUser.gameProgress.getLevelHighScore(chapter, levelNumber);
    }

    @Override
    public Map<String, Integer> getLevelHighScores() {
        if (!isLoggedIn()) {
            return Map.of();
        }
        return currentUser.gameProgress.getLevelHighScores();
    }

    @Override
    public void addNews(String message) {
        if (!isLoggedIn() || message == null || message.isBlank()) {
            return;
        }
        currentUser.newsFeed.addNews(message);
    }

    @Override
    public void markAllNewsAsRead() {
        if (isLoggedIn()) {
            currentUser.newsFeed.markAllUnreadAsRead();
        }
    }

    @Override
    public void unlockMinigame(MiniGameType minigame) {
        if (!isLoggedIn() || minigame == null || !MiniGameCatalog.isPlayable(minigame)) {
            return;
        }
        if (!currentUser.gameProgress.isMinigameUnlocked(minigame)) {
            currentUser.gameProgress.unlockMinigame(minigame);
            addNews("You unlocked a new minigame: " + minigame.name().replace('_', ' '));
        }
    }

    @Override
    public boolean isMinigameUnlocked(MiniGameType minigame) {
        if (!isLoggedIn() || minigame == null || !MiniGameCatalog.isPlayable(minigame)) {
            return false;
        }
        return currentUser.gameProgress.isMinigameUnlocked(minigame);
    }

    @Override
    public List<MiniGameType> getUnlockedMinigames() {
        if (!isLoggedIn()) {
            return new ArrayList<>();
        }
        return currentUser.gameProgress.getUnlockedMinigames().stream()
                .filter(MiniGameCatalog::isPlayable)
                .toList();
    }

    @Override
    public boolean changeUsername(String newUsername) {
        if (!isLoggedIn() || newUsername == null || newUsername.isBlank()) {
            return false;
        }
        if (currentUser.username.equals(newUsername) || users.containsKey(newUsername)) {
            return false;
        }
        String oldUsername = currentUser.username;
        currentUser.username = newUsername;
        users.remove(oldUsername);
        users.put(newUsername, currentUser);
        return true;
    }

    @Override
    public void changeNickname(String nickname) {
        if (isLoggedIn() && nickname != null) {
            currentUser.nickname = nickname;
        }
    }

    @Override
    public void changeEmail(String email) {
        if (isLoggedIn() && email != null) {
            currentUser.email = email;
        }
    }

    @Override
    public boolean changeProfilePassword(String oldPassword, String newPassword) {
        if (!isLoggedIn() || oldPassword == null || newPassword == null || newPassword.isEmpty()) {
            return false;
        }
        if (!currentUser.password.equals(Hash.hashPassword(oldPassword))) {
            return false;
        }
        currentUser.password = Hash.hashPassword(newPassword);
        return true;
    }

    @Override
    public void unlockChapter(ChapterType chapter) {
        if (!isLoggedIn())
            return;
        if (!currentUser.gameProgress.isChapterUnlocked(chapter)) {
            currentUser.gameProgress.unlockChapter(chapter);
        }
    }

    @Override
    public boolean isChapterUnlocked(ChapterType chapter) {
        if (!isLoggedIn())
            return false;
        return currentUser.gameProgress.isChapterUnlocked(chapter);
    }

    @Override
    public List<ChapterType> getUnlockedChapters() {
        if (!isLoggedIn())
            return new ArrayList<>();
        return new ArrayList<>(currentUser.gameProgress.getUnlockedChapters());
    }

    @Override
    public void unlockPlant(PlantType type) {
        if (!isLoggedIn())
            return;
        if (!currentUser.collection.isPlantUnlocked(type)) {
            currentUser.collection.unlockPlant(type);
            addNews("You unlocked a new plant: " + type.name);
        }
    }

    @Override
    public boolean isPlantUnlocked(PlantType plantName) {
        if (!isLoggedIn())
            return false;
        return currentUser.collection.isPlantUnlocked(plantName);
    }

    @Override
    public List<PlantType> getUnlockedPlants() {
        if (!isLoggedIn()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(currentUser.collection.getUnlockedPlants());
    }

    @Override
    public void unlockZombie(ZombieType zombie) {
        if (!isLoggedIn() || zombie == null) {
            return;
        }
        if (!currentUser.collection.isZombieUnlocked(zombie)) {
            currentUser.collection.unlockZombie(zombie);
            addNews("You unlocked a new zombie: " + zombie.name);
        }
    }

    @Override
    public boolean isZombieUnlocked(ZombieType zombie) {
        if (!isLoggedIn() || zombie == null) {
            return false;
        }
        return currentUser.collection.isZombieUnlocked(zombie);
    }

    @Override
    public List<ZombieType> getUnlockedZombies() {
        if (!isLoggedIn()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(currentUser.collection.getUnlockedZombies());
    }
}