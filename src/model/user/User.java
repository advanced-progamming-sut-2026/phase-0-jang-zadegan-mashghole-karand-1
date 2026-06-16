package model.user;

import model.collection.Collection;
import model.gameSetting.GameSetting;
import model.greenhouse.Greenhouse;
import model.news.NewsFeed;
import model.quest.Quest;

import java.util.List;

public class User {

    private String username;
    private String passwordHash;
    private String email;
    private GameSetting preferredSetting;
    private GameProgress gameProgress;
    private Collection collection;
    private Greenhouse greenhouse;
    private NewsFeed newsFeed;
    private List<Quest> quests;
    private Gender gender;
    private SafetyQuestion Safetyquestion;
}
