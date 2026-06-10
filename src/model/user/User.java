package pvz.model.user;

import pvz.model.collection.Collection;
import pvz.model.greenhouse.Greenhouse;
import pvz.model.news.NewsFeed;
import pvz.model.quest.Quest;

import java.util.List;

public class User {

    private String username;
    private String passwordHash;
    private GameProgress gameProgress;
    private Collection collection;
    private Greenhouse greenhouse;
    private NewsFeed newsFeed;
    private List<Quest> quests;
}
