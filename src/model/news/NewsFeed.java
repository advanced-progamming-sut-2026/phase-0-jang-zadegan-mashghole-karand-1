package pvz.model.news;

import java.util.List;

public class NewsFeed {

    private List<NewsItem> newsList;

    public boolean hasUnread() {
        return false;
    }

    public void markAsRead(String newsId) {
    }
}
