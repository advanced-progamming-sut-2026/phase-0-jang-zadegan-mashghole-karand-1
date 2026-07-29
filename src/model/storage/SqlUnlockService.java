package model.storage;

import model.data.content.chapter.ChapterType;
import model.data.content.minigame.MiniGameType;
import model.data.plant.PlantType;
import model.data.zombie.ZombieType;
import model.storage.user.User;

final class SqlUnlockService {

    private final SqlProgressStore progressStore;

    SqlUnlockService(SqlProgressStore progressStore) {
        this.progressStore = progressStore;
    }

    boolean unlockChapter(User user, ChapterType chapter) {
        if (user.gameProgress.isChapterUnlocked(chapter)) {
            return false;
        }
        user.gameProgress.unlockChapter(chapter);
        progressStore.insertUnlockedChapter(user.username, chapter);
        return true;
    }

    boolean unlockMinigame(User user, MiniGameType minigame) {
        if (user.gameProgress.isMinigameUnlocked(minigame)) {
            return false;
        }
        user.gameProgress.unlockMinigame(minigame);
        progressStore.insertUnlockedMinigame(user.username, minigame);
        return true;
    }

    boolean unlockPlant(User user, PlantType plant) {
        if (user.collection.isPlantUnlocked(plant)) {
            return false;
        }
        user.collection.unlockPlant(plant);
        progressStore.insertUnlockedPlant(user.username, plant);
        return true;
    }

    boolean unlockZombie(User user, ZombieType zombie) {
        if (user.collection.isZombieUnlocked(zombie)) {
            return false;
        }
        user.collection.unlockZombie(zombie);
        progressStore.insertUnlockedZombie(user.username, zombie);
        return true;
    }
}
