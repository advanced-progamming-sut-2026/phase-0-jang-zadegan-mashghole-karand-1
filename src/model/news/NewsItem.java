package model.news;

import java.time.LocalDateTime;
import java.util.UUID;

public class NewsItem {

    private final String id;
    private final String message;
    private boolean read;
    private final LocalDateTime timestamp;

    public NewsItem(String message) {
        this(UUID.randomUUID().toString(), message, false, LocalDateTime.now());
    }

    public NewsItem(String id, String message, boolean read, LocalDateTime timestamp) {
        this.id = id;
        this.message = message;
        this.read = read;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public boolean isRead() {
        return read;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void markRead() {
        read = true;
    }
}
