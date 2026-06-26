package model.storage.user;

import model.gameSetting.GameSetting;
import model.greenhouse.Greenhouse;
import model.news.NewsFeed;
import model.quest.Quest;
import model.storage.collection.Collection;

import java.util.ArrayList;
import java.util.List;

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
    }
}
