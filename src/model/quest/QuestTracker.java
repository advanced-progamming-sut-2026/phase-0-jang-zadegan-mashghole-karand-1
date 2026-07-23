package model.quest;

import model.core.GameState;
import model.data.content.chapter.ChapterType;
import model.rule.SessionContext;
import model.storage.StorageManager;
import model.storage.user.User;

public class QuestTracker {
    private final StorageManager storage;

    public QuestTracker(StorageManager storage) {
        this.storage = storage;
    }

    public void onGameEvent(Object event, GameState state, SessionContext session) {
        User user = storage.getCurrentUser();
        if(user == null || user.quests == null) return;
        ChapterType chapter = null;
        if (session != null && session.getConfig() != null
                && session.getConfig().levelConfig != null) {
            chapter = session.getConfig().levelConfig.chapterType;
        }

        for(Quest quest : user.quests){
            if(!quest.completed){
                quest.onEvent(event, user, state, chapter);
            }
        }
    }

    public void onTick(GameState state, SessionContext session) {
        for(Quest q : storage.getCurrentUser().quests){
            if(!q.completed && q instanceof KillCountQuest k){

            }
        }
    }
}
