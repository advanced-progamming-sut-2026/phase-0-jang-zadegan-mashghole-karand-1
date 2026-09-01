package controller;

import controller.CommandResult.CommandResult;
import model.ranked.RankedChallengeSupport;
import model.service.GameNavigationState;
import model.service.GameNavigationState.Phase;
import model.storage.StorageManager;
import network.NetworkSession;
import shared.dto.RankedChallengeDto;
import shared.dto.RankedTodayResponse;
import view.ScreenType;

public class RankedChallengeController {
    private final ControllerManager controllerManager;
    private final StorageManager storage;
    private final GameNavigationState gameNavigation;

    public RankedChallengeController(ControllerManager controllerManager, StorageManager storage,
            GameNavigationState gameNavigation) {
        this.controllerManager = controllerManager;
        this.storage = storage;
        this.gameNavigation = gameNavigation;
    }

    public CommandResult enter() {
        CommandResult loggedIn = controllerManager.requireLoggedIn();
        if (loggedIn != null) {
            return loggedIn;
        }
        NetworkSession net = controllerManager.getNetworkSession();
        if (net == null || !net.isLoggedIn()) {
            return new CommandResult("Ranked Challenge requires an online login.", false);
        }
        try {
            RankedTodayResponse today = net.authApi().rankedToday(net.token());
            if (today == null || !today.ok || today.challenge == null) {
                return new CommandResult(today != null && today.error != null
                        ? today.error
                        : "Could not load today's Ranked Challenge.", false);
            }
            if (today.highestScore >= 0) {
                storage.recordRankedScore(today.highestScore);
                storage.saveProgress();
            }
            if (today.alreadyPlayed) {
                return new CommandResult(
                        "You already played Ranked Challenge today (UTC). Highest MewPoints: "
                                + today.highestScore,
                        false);
            }
            return beginPlantSelect(today.challenge);
        } catch (Exception e) {
            return new CommandResult("Server unavailable for Ranked Challenge.", false);
        }
    }

    private CommandResult beginPlantSelect(RankedChallengeDto challenge) {
        gameNavigation.reset();
        gameNavigation.pendingRankedChallenge = challenge;
        gameNavigation.pendingLevel = RankedChallengeSupport.toLevelConfig(challenge);
        gameNavigation.phase = Phase.RANKED_PLANT;
        gameNavigation.unlockedPlants = storage.getUnlockedPlants().stream()
                .filter(p -> p != null && !p.isBowlingExclusive())
                .toList();
        controllerManager.setScreen(ScreenType.LEVEL_SELECTOR);
        return new CommandResult(
                "Ranked Challenge ready (" + challenge.chapter + " L" + challenge.levelNumber
                        + "). Pick your plants.",
                true);
    }
}
