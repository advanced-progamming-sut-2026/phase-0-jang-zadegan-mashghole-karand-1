package controller;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import controller.CommandResult.CommandResult;
import model.ModelManager;
import model.data.content.specialLevel.SpecialLevelType;
import model.data.plant.PlantType;
import model.rule.SessionConfig;
import model.rule.SessionRules;
import model.service.GameNavigationState;
import model.service.GameNavigationState.Phase;
import model.storage.StorageManager;
import model.storage.user.User;
import view.ScreenType;
import view.messages.ErrorMessages;

public class PickPlantsController {

    public static final int MAX_SELECTED_PLANTS = 8;

    private final ControllerManager controllerManager;
    private final ModelManager model;
    private final StorageManager storage;
    private final GameNavigationState gameNavigation;

    public PickPlantsController(ControllerManager controllerManager, ModelManager model,
            StorageManager storage, GameNavigationState gameNavigation) {
        this.controllerManager = controllerManager;
        this.model = model;
        this.storage = storage;
        this.gameNavigation = gameNavigation;
    }

    public CommandResult showAllPlants() {
        if (!isPlantSelectionActive()) {
            return failure("Plant selection is not available right now.");
        }
        String names = gameNavigation.unlockedPlants.stream()
                .map(type -> type.name)
                .collect(Collectors.joining(", "));
        if (names.isEmpty()) {
            names = "(none unlocked)";
        }
        return success("Unlocked plants: " + names);
    }

    public CommandResult showAvailablePlants() {
        return showAllPlants();
    }

    public CommandResult addPlant(PlantType plantType , PlantType target) {
        if (!isPlantSelectionActive()) {
            return failure("Plant selection is not available right now.");
        }
        if (plantType == null) {
            return failure(ErrorMessages.PLANT_NOT_FOUND.getMessage());
        }
        if (!storage.isPlantUnlocked(plantType)) {
            return failure(ErrorMessages.PLANT_LOCKED.getMessage());
        }
        if (plantType.isBowlingExclusive()) {
            return failure("Bowling plants are only used in Wall-nut Bowling.");
        }
        if (plantType == PlantType.Imitater){
            if (target == null || target == PlantType.Imitater){
                return failure("Please choose a valid target plant");
            }
            gameNavigation.imitatorTarget = target;
        }
        if (gameNavigation.selectedPlants.contains(plantType)) {
            return failure(ErrorMessages.PLANT_ALREADY_ADDED.getMessage());
        }
        if (gameNavigation.selectedPlants.size() >= MAX_SELECTED_PLANTS) {
            return failure("You can only select up to " + MAX_SELECTED_PLANTS + " plants.");
        }

        SessionConfig probe = buildProbeConfig();
        if (!SessionRules.canSelectPlant(probe, plantType, gameNavigation.selectedPlants)) {
            return failure(selectionRejectedMessage(plantType));
        }

        gameNavigation.selectedPlants.add(plantType);
        controllerManager.refreshView();
        return success("Added " + plantType.name + " to your loadout.");
    }

    public CommandResult removePlant(PlantType plantType) {
        if (!isPlantSelectionActive()) {
            return failure("Plant selection is not available right now.");
        }
        if (plantType == null) {
            return failure(ErrorMessages.PLANT_NOT_FOUND.getMessage());
        }
        if (!gameNavigation.selectedPlants.remove(plantType)) {
            return failure(ErrorMessages.PLANT_NOT_SELECTED.getMessage());
        }
        if (plantType == PlantType.Imitater) {
            gameNavigation.imitatorTarget = null;
        }
        controllerManager.refreshView();
        return success("Removed " + plantType.name + " from your loadout.");
    }

    public CommandResult boostPlant(PlantType plantType) {
        User user = storage.getCurrentUser();
        if (user == null)
            return failure("you should login first");
        if (plantType == null)
            return failure("you should choose plant.");
        if (!gameNavigation.selectedPlants.contains(plantType)) return failure("you should select a plant");

        if (gameNavigation.boostedPlants.contains(plantType))
            return failure(ErrorMessages.PLANT_ALREADY_BOOSTED.getMessage());
        if (user.gems < 2)
            return failure(ErrorMessages.NOT_ENOUGH_GEMS.getMessage());
        user.gems -= 2;
        gameNavigation.boostedPlants.add(plantType);
        storage.saveProgress();
        return success(plantType.name +"boosted!");
    }

    public CommandResult startGame() {
        if (!isPlantSelectionActive()) {
            return failure("Select a level and pick your plants first.");
        }
        CommandResult loggedInCheck = controllerManager.requireLoggedIn();
        if (loggedInCheck != null) {
            return loggedInCheck;
        }
        if (gameNavigation.selectedPlants.isEmpty()) {
            return failure("Pick at least one plant before starting.");
        }
        if (gameNavigation.pendingLevel == null) {
            return failure("No level selected.");
        }

        SessionConfig.Builder sessionBuilder = SessionConfig.builder()
                .levelConfig(gameNavigation.pendingLevel)
                .selectedPlants(gameNavigation.selectedPlants)
                .imitatorTarget(gameNavigation.imitatorTarget)
                .boostedPlant(gameNavigation.boostedPlants);

        if (gameNavigation.pendingMiniGame != null) {
            sessionBuilder.miniGame(gameNavigation.pendingMiniGame);
        } else if (gameNavigation.pendingSpecialLevel != null) {
            sessionBuilder.specialLevel(gameNavigation.pendingSpecialLevel);
        }

        model.startSession(sessionBuilder.build());
        storage.recordGamePlayed();
        gameNavigation.reset();
        controllerManager.setScreen(ScreenType.GAME);
        return success("Game started! Good luck.");
    }

    private SessionConfig buildProbeConfig() {
        SessionConfig.Builder builder = SessionConfig.builder()
                .levelConfig(gameNavigation.pendingLevel)
                .selectedPlants(List.copyOf(gameNavigation.selectedPlants));
        if (gameNavigation.pendingMiniGame != null) {
            builder.miniGame(gameNavigation.pendingMiniGame);
        } else if (gameNavigation.pendingSpecialLevel != null) {
            builder.specialLevel(gameNavigation.pendingSpecialLevel);
        }
        return builder.build();
    }

    private String selectionRejectedMessage(PlantType plantType) {
        if (gameNavigation.pendingSpecialLevel == SpecialLevelType.LOCKED_PLANTS) {
            return "You can only choose one plant from the " + plantType.category.name().toLowerCase()
                    .replace('_', ' ') + " family.";
        }
        if (gameNavigation.pendingSpecialLevel == SpecialLevelType.PLANT_WHAT_YOU_GET) {
            return "Sun-producing plants are not allowed in Plant What You Get.";
        }
        return "That plant can't be selected for this level.";
    }

    private boolean isPlantSelectionActive() {
        return controllerManager.getCurrentScreen() == ScreenType.LEVEL_SELECTOR
                && gameNavigation.phase == Phase.PLANT;
    }

    private CommandResult success(String message) {
        return new CommandResult(message, true);
    }

    private CommandResult failure(String message) {
        return new CommandResult(message, false);
    }
}
