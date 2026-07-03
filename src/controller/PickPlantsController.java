package controller;

import java.util.stream.Collectors;

import controller.CommandResult.CommandResult;
import model.ModelManager;
import model.data.plant.PlantType;
import model.service.GameNavigationState;
import model.service.GameNavigationState.Phase;
import model.storage.StorageManager;
import view.ScreenType;
import view.messages.ErrorMessages;

public class PickPlantsController {

    public static final int MAX_SELECTED_PLANTS = 6;

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

    public CommandResult addPlant(PlantType plantType) {
        if (!isPlantSelectionActive()) {
            return failure("Plant selection is not available right now.");
        }
        if (plantType == null) {
            return failure(ErrorMessages.PLANT_NOT_FOUND.getMessage());
        }
        if (!storage.isPlantUnlocked(plantType)) {
            return failure(ErrorMessages.PLANT_LOCKED.getMessage());
        }
        if (gameNavigation.selectedPlants.contains(plantType)) {
            return failure(ErrorMessages.PLANT_ALREADY_ADDED.getMessage());
        }
        if (gameNavigation.selectedPlants.size() >= MAX_SELECTED_PLANTS) {
            return failure("You can only select up to " + MAX_SELECTED_PLANTS + " plants.");
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
        controllerManager.refreshView();
        return success("Removed " + plantType.name + " from your loadout.");
    }

    public CommandResult boostPlant(PlantType plantType) {
        return failure("Plant boost is not available yet.");
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

        model.startLevel(gameNavigation.pendingLevel);
        gameNavigation.reset();
        controllerManager.setScreen(ScreenType.GAME);
        return success("Game started! Good luck.");
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
