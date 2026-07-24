package controller;

import controller.CommandResult.CommandResult;
import model.service.QuestViewState;
import model.storage.user.User;
import view.ScreenType;

public class QuestMenuController {
    private final ControllerManager controllerManager;
    private String pageFilter = "all";

    public QuestMenuController(ControllerManager controllerManager) {
        this.controllerManager = controllerManager;
    }

    public CommandResult enterPage(String pageName) {
        CommandResult loggedIn = controllerManager.requireLoggedIn();
        if (loggedIn != null) {
            return loggedIn;
        }
        CommandResult screenCheck = controllerManager.requireScreen(ScreenType.LEVEL_SELECTOR);
        if (screenCheck != null) {
            return screenCheck;
        }
        String page = pageName == null ? "all" : pageName.trim().toLowerCase();
        if (page.isEmpty()) {
            page = "all";
        }
        pageFilter = page;
        return controllerManager.openTravelLogMenu();
    }

    public String getPageFilter() {
        return pageFilter;
    }

    public QuestViewState getViewState() {
        User user = controllerManager.getStorage().getCurrentUser();
        if (user == null || user.quests == null) {
            return QuestViewState.empty();
        }
        return QuestViewState.fromQuests(user.quests, pageFilter);
    }
}
