package model.storage;

import java.util.List;

import model.data.content.chapter.ChapterType;
import model.data.plant.PlantType;
import model.storage.user.Gender;
import model.storage.user.SafetyQuestion;
import model.storage.user.User;

public interface StorageManager {

    boolean register(String username, String password, String email, String nickname, Gender gender,
            SafetyQuestion safety);

    boolean login(String username, String password, boolean stayLoggedIn);

    void logout();

    User getCurrentUser();
    List<User> getUsers();
    boolean isLoggedIn();

    boolean usernameExists(String username);

    User getUserByUsername(String username);

    boolean updatePassword(String username, String newPassword);

    String getCurrentUsername();

    void saveProgress();

    void loadProgress();

    void updateUserProfile(User profile);

    void recordGamePlayed();

    void markLevelCompleted(String levelId);

    boolean changeUsername(String newUsername);

    void changeNickname(String nickname);

    void changeEmail(String email);

    boolean changeProfilePassword(String oldPassword, String newPassword);

    void addNews(String message);

    void markAllNewsAsRead();

    void unlockMinigame(model.minigame.MinigameType minigame);

    void unlockChapter(ChapterType chapter);

    boolean isChapterUnlocked(ChapterType chapter);

    List<ChapterType> getUnlockedChapters();

    void unlockPlant(PlantType plantName);

    boolean isPlantUnlocked(PlantType plantName);

    List<PlantType> getUnlockedPlants();

    void unlockZombie(model.data.zombie.ZombieType zombieType);

    boolean isZombieUnlocked(model.data.zombie.ZombieType zombieType);

    List<model.data.zombie.ZombieType> getUnlockedZombies();

    class SaveFile {
        public final String name;
        public final String chapterId;
        public final int levelNumber;
        public final List<String> unlockedPlants;
        public final List<String> unlockedChapters;
        public final long timestamp;

        public SaveFile(String name, String chapterId, int levelNumber, List<String> unlockedPlants,
                List<String> unlockedChapters) {
            this.name = name;
            this.chapterId = chapterId;
            this.levelNumber = levelNumber;
            this.unlockedPlants = unlockedPlants;
            this.unlockedChapters = unlockedChapters;
            this.timestamp = System.currentTimeMillis();
        }
    }
}