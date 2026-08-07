package model.news;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NewsFeed {

    private final List<NewsItem> items = new ArrayList<>();

    public NewsItem addNews(String message) {
        if (message == null || message.isBlank()) {
            return null;
        }
        NewsItem item = new NewsItem(message);
        items.add(0, item);
        return item;
    }

    public boolean hasUnread() {
        for (NewsItem item : items) {
            if (!item.isRead()) {
                return true;
            }
        }
        return false;
    }

    public List<NewsItem> getUnread() {
        List<NewsItem> unread = new ArrayList<>();
        for (NewsItem item : items) {
            if (!item.isRead()) {
                unread.add(item);
            }
        }
        return unread;
    }

    public List<NewsItem> getAll() {
        return Collections.unmodifiableList(items);
    }

    public void markAllUnreadAsRead() {
        for (NewsItem item : items) {
            if (!item.isRead()) {
                item.markRead();
            }
        }
    }

    public void replaceItems(List<NewsItem> loadedItems) {
        items.clear();
        if (loadedItems != null) {
            items.addAll(loadedItems);
        }
    }
}
