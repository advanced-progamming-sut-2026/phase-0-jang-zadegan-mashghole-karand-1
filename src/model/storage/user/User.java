package model.storage.user;

import model.data.plant.PlantStats;
import model.data.plant.PlantType;
import model.gameSetting.GameSetting;
import model.greenhouse.Greenhouse;
import model.news.NewsFeed;
import model.quest.Quest;
import model.shop.DailyDeal;
import model.storage.collection.Collection;

import java.util.*;

public class User {
    public String username;
    public String password;
    public String email;
    public String nickname;
    public GameSetting preferredSetting;
    public GameProgress gameProgress;
    public Collection collection;
    public Greenhouse greenhouse;
    public NewsFeed newsFeed;
    public List<Quest> quests;
    public Gender gender;
    public SafetyQuestion safetyQuestion;
    public int coins;
    public int gems;
    public int highestScore;
    public int gamesPlayed;
    public Map<PlantType, Integer> seedPackets;
    public Map<PlantType, Integer> plantLevels;
    public int plantFood;
    public DailyDeal dailyDeal;
    public Set<PlantType> storedBoosts;

    public User(String username, String password, String email, String nickname, Gender gender, SafetyQuestion safety) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.nickname = nickname;
        this.gender = gender;
        this.safetyQuestion = safety;
        this.gameProgress = new GameProgress();
        this.collection = new Collection();
        this.preferredSetting = new GameSetting();
        this.quests = new ArrayList<>();
        this.newsFeed = new NewsFeed();
        this.seedPackets = new HashMap<>();
        this.plantLevels = new HashMap<>();
        this.dailyDeal = new DailyDeal();
        this.greenhouse = new Greenhouse();
        this.storedBoosts = new HashSet<>();
    }

    public int getSeedPackets(PlantType plant) {
        return seedPackets.getOrDefault(plant, 0);
    }

    public void addSeedPackets(PlantType plant, int amount) {
        seedPackets.put(plant, getSeedPackets(plant) + amount);
    }

    public boolean useSeedPackets(PlantType plant, int amount) {
        if (getSeedPackets(plant) < amount) {
            return false;
        }
        seedPackets.put(plant, getSeedPackets(plant) - amount);
        return true;
    }

    public int getPlantLevel(PlantType plant) {
        return plantLevels.getOrDefault(plant, PlantStats.DEFAULT_LEVEL);
    }

    public void setPlantLevel(PlantType plant, int level) {
        if (plant == null) {
            return;
        }
        int clamped = Math.max(PlantStats.DEFAULT_LEVEL, Math.min(level, PlantStats.MAX_LEVEL));
        plantLevels.put(plant, clamped);
    }

    public int getCoins() {
        return coins;
    }

    public int getGems() {
        return gems;
    }
}
