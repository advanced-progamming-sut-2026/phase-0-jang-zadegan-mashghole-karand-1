package controller;

import controller.CommandResult.CommandResult;
import model.news.NewsFeed;
import model.news.NewsItem;
import model.service.NewsViewState;
import model.storage.StorageManager;
import view.MenuType;
import view.ScreenType;

import java.util.List;
import java.util.stream.Collectors;

public class NewsMenuController {

    public enum NewsFilter {
        NONE,
        ALL,
        UNREAD
    }

    private final ControllerManager controllerManager;
    private final StorageManager storage;
    private NewsFilter filter = NewsFilter.NONE;

    public NewsMenuController(ControllerManager controllerManager, StorageManager storage) {
        this.controllerManager = controllerManager;
        this.storage = storage;
    }

    public void onMenuOpened() {
        filter = NewsFilter.NONE;
    }

    public void onMenuClosed() {
        if (filter == NewsFilter.UNREAD && storage.isLoggedIn()) {
            storage.markAllNewsAsRead();
        }
        filter = NewsFilter.NONE;
    }

    public NewsViewState getViewState() {
        if (!storage.isLoggedIn()) {
            return NewsViewState.empty();
        }

        NewsFeed feed = storage.getCurrentUser().newsFeed;
        List<String> messages = switch (filter) {
            case ALL -> feed.getAll().stream().map(NewsItem::getMessage).collect(Collectors.toList());
            case UNREAD -> feed.getUnread().stream().map(NewsItem::getMessage).collect(Collectors.toList());
            case NONE -> List.of();
        };
        return new NewsViewState(messages);
    }

    public CommandResult showUnreadNews() {
        CommandResult menuCheck = requireNewsMenu();
        if (menuCheck != null) {
            return menuCheck;
        }

        NewsFeed feed = storage.getCurrentUser().newsFeed;
        if (!feed.hasUnread()) {
            filter = NewsFilter.UNREAD;
            return success("No unread news.");
        }

        filter = NewsFilter.UNREAD;
        return success("Showing unread news.");
    }

    public CommandResult showAllNews() {
        CommandResult menuCheck = requireNewsMenu();
        if (menuCheck != null) {
            return menuCheck;
        }

        filter = NewsFilter.ALL;
        return success("Showing all news.");
    }

    public CommandResult addDebugNews(String message) {
        CommandResult loggedInCheck = controllerManager.requireLoggedIn();
        if (loggedInCheck != null) {
            return loggedInCheck;
        }
        if (message == null || message.isBlank()) {
            return failure("Message is required. Usage: debug add-news -m <message>");
        }
        storage.addNews(message.trim());
        if (controllerManager.getCurrentMenu() == MenuType.NEWS && filter == NewsFilter.NONE) {
            filter = NewsFilter.UNREAD;
        }
        return success("Added debug news.");
    }

    private CommandResult requireNewsMenu() {
        CommandResult screenCheck = controllerManager.requireScreen(ScreenType.MAIN);
        if (screenCheck != null) {
            return screenCheck;
        }
        CommandResult loggedInCheck = controllerManager.requireLoggedIn();
        if (loggedInCheck != null) {
            return loggedInCheck;
        }
        if (controllerManager.getCurrentMenu() != MenuType.NEWS) {
            return failure("Open the news menu first with: menu enter news");
        }
        return null;
    }

    private CommandResult success(String message) {
        return new CommandResult(message, true);
    }

    private CommandResult failure(String message) {
        return new CommandResult(message, false);
    }
}
