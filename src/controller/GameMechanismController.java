package controller;

import java.util.stream.Collectors;

import controller.CommandResult.CommandResult;
import model.ModelManager;
import model.core.GameLoop;
import model.core.GameState;
import model.core.ReadOnlyGameState;
import model.data.plant.Plant;
import model.data.plant.PlantType;
import model.data.zombie.Zombie;
import view.ScreenType;

public class GameMechanismController {
    private final ControllerManager controllerManager;
    private final GameLoop gameLoop;
    private final ModelManager model;
    private final GameState gameState;

    public GameMechanismController(ControllerManager controllerManager, GameLoop gameLoop, ModelManager model) {
        this.controllerManager = controllerManager;
        this.gameLoop = gameLoop;
        this.model = model;
        this.gameState = model.getState();
    }

    public CommandResult advanceTicks(int amount, boolean realTime) {
        CommandResult screenCheck = requireGameScreen();
        if (screenCheck != null) {
            return screenCheck;
        }
        if (amount <= 0) {
            return failure("Tick count must be positive.");
        }
        gameLoop.tick(amount, realTime);
        return success("Advanced " + amount + " tick(s)" + (realTime ? " in real time." : "."));
    }

    public CommandResult setAutoTick(Boolean enable) {
        CommandResult screenCheck = requireGameScreen();
        if (screenCheck != null) {
            return screenCheck;
        }
        if (enable == null) {
            gameLoop.toggleAutoTick();
        } else if (enable) {
            gameLoop.startAutoTick();
        } else {
            gameLoop.stopAutoTick();
        }
        return success("Auto-tick " + (gameLoop.isAutoTickRunning() ? "enabled" : "disabled") + ".");
    }

    public CommandResult collectSun(int row, int col) {
        CommandResult screenCheck = requireGameScreen();
        if (screenCheck != null) {
            return screenCheck;
        }
        if (!isValidCell(row, col)) {
            return failure("Invalid cell (" + row + ", " + col + ").");
        }
        if (model.collectSunAt(row, col)) {
            return success("Collected sun at (" + row + ", " + col + ").");
        }
        return failure("No collectible sun at (" + row + ", " + col + ").");
    }

    public CommandResult showSunAmount() {
        CommandResult screenCheck = requireGameScreen();
        if (screenCheck != null) {
            return screenCheck;
        }
        return success("Current sun: " + gameState.sunAmount + ".");
    }

    public CommandResult addSun(int count) {
        CommandResult screenCheck = requireGameScreen();
        if (screenCheck != null) {
            return screenCheck;
        }
        model.addSun(count);
        return success("Added " + count + " sun.");
    }

    public CommandResult releaseNuke() {
        CommandResult screenCheck = requireGameScreen();
        if (screenCheck != null) {
            return screenCheck;
        }
        int cleared = gameState.zombies.size();
        model.releaseNuke();
        return success("Nuke released. Eliminated " + cleared + " zombie(s).");
    }

    public CommandResult plantPlant(int row, int col, PlantType plantType) {
        CommandResult screenCheck = requireGameScreen();
        if (screenCheck != null) {
            return screenCheck;
        }
        if (plantType == null) {
            return failure("Plant type not found.");
        }
        if (!isValidCell(row, col)) {
            return failure("Invalid cell (" + row + ", " + col + ").");
        }
        if (gameState.getPlantAt(row, col) != null) {
            return failure("Cell (" + row + ", " + col + ") already has a plant.");
        }
        if (gameState.sunAmount < plantType.baseStats.cost) {
            return failure("Not enough sun. Need " + plantType.baseStats.cost + ", have " + gameState.sunAmount + ".");
        }
        if (model.placePlant(row, col, plantType, 1)) {
            return success("Planted " + plantType.name + " at (" + row + ", " + col + ").");
        }
        return failure("Could not plant " + plantType.name + " at (" + row + ", " + col + ").");
    }

    public CommandResult removeCooldown() {
        CommandResult screenCheck = requireGameScreen();
        if (screenCheck != null) {
            return screenCheck;
        }
        model.removeCooldowns();
        return success("Plant cooldowns removed.");
    }

    public CommandResult pluckPlant(int row, int col) {
        CommandResult screenCheck = requireGameScreen();
        if (screenCheck != null) {
            return screenCheck;
        }
        if (!isValidCell(row, col)) {
            return failure("Invalid cell (" + row + ", " + col + ").");
        }
        if (model.pluckPlant(row, col)) {
            return success("Removed plant at (" + row + ", " + col + ").");
        }
        return failure("No plant at (" + row + ", " + col + ").");
    }

    public CommandResult feedPlant(int row, int col) {
        CommandResult screenCheck = requireGameScreen();
        if (screenCheck != null) {
            return screenCheck;
        }
        if (!isValidCell(row, col)) {
            return failure("Invalid cell (" + row + ", " + col + ").");
        }
        if (gameState.plantFoodAmount <= 0) {
            return failure("No plant food available.");
        }
        if (gameState.getPlantAt(row, col) == null) {
            return failure("No plant at (" + row + ", " + col + ").");
        }
        if (model.feedPlant(row, col)) {
            return success("Fed plant at (" + row + ", " + col + ").");
        }
        return failure("Could not feed plant at (" + row + ", " + col + ").");
    }

    public CommandResult addPlantFood() {
        CommandResult screenCheck = requireGameScreen();
        if (screenCheck != null) {
            return screenCheck;
        }
        model.addPlantFood();
        return success("Added plant food.");
    }

    public CommandResult showMap() {
        CommandResult screenCheck = requireGameScreen();
        if (screenCheck != null) {
            return screenCheck;
        }
        controllerManager.refreshView();
        return success("Map refreshed.");
    }

    public CommandResult showPlantsStatus() {
        CommandResult screenCheck = requireGameScreen();
        if (screenCheck != null) {
            return screenCheck;
        }
        if (gameState.plants.isEmpty()) {
            return success("No plants on the lawn.");
        }
        String status = gameState.plants.stream()
                .map(plant -> plant.type.name + " at (" + plant.row + ", " + plant.col + ") HP "
                        + plant.hp + "/" + plant.totalHP)
                .collect(Collectors.joining("; "));
        return success(status);
    }

    public CommandResult showTileStatus(int row, int col) {
        CommandResult screenCheck = requireGameScreen();
        if (screenCheck != null) {
            return screenCheck;
        }
        if (!isValidCell(row, col)) {
            return failure("Invalid cell (" + row + ", " + col + ").");
        }

        Plant plant = gameState.getPlantAt(row, col);
        if (plant != null) {
            return success("Plant: " + plant.type.name + " HP " + plant.hp + "/" + plant.totalHP + ".");
        }

        int cellStartX = col * ReadOnlyGameState.CELL_WIDTH;
        int cellEndX = (col + 1) * ReadOnlyGameState.CELL_WIDTH;
        long zombiesInCell = gameState.zombies.stream()
                .filter(z -> z.row == row && z.position.x >= cellStartX && z.position.x < cellEndX)
                .count();
        boolean hasSun = gameState.sunDrops.stream()
                .anyMatch(s -> s.row == row && !s.isFalling
                        && s.position.x >= cellStartX && s.position.x < cellEndX);

        if (zombiesInCell > 0 || hasSun) {
            return success("Tile (" + row + ", " + col + "): zombies=" + zombiesInCell + ", sun=" + hasSun + ".");
        }
        return success("Tile (" + row + ", " + col + ") is empty.");
    }

    private CommandResult requireGameScreen() {
        if (controllerManager.getCurrentScreen() != ScreenType.GAME) {
            return failure("This command is only available during a game.");
        }
        return null;
    }

    private boolean isValidCell(int row, int col) {
        return row >= 0 && row < ReadOnlyGameState.GRID_ROWS
                && col >= 0 && col < ReadOnlyGameState.GRID_COLS;
    }

    private CommandResult success(String message) {
        return new CommandResult(message, true);
    }

    private CommandResult failure(String message) {
        return new CommandResult(message, false);
    }
}
