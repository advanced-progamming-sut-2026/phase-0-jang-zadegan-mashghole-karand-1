package controller;

import java.util.List;
import java.util.stream.Collectors;

import controller.CommandResult.CommandResult;
import model.ModelManager;
import model.core.GameLoop;
import model.core.GameState;
import model.core.ReadOnlyGameState;
import model.data.content.minigame.IZombieShop;
import model.data.plant.PlantStats;
import model.data.plant.PlantType;
import model.data.seed.PlantSeedDrop;
import model.data.vase.Vase;
import model.data.zombie.Zombie;
import model.data.zombie.ZombieType;
import model.storage.user.User;
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
        CommandResult activeCheck = requireSessionActive();
        if (activeCheck != null) {
            return activeCheck;
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
        boolean enabling = enable == null ? !gameLoop.isAutoTickRunning() : enable;
        if (enabling && (gameState.gameOver || gameState.levelComplete)) {
            return failure("Cannot resume ticks after the session has ended. Use 'menu exit'.");
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
        CommandResult activeCheck = requireSessionActive();
        if (activeCheck != null) {
            return activeCheck;
        }
        if (!isValidCell(row, col)) {
            return failure("Invalid cell (" + row + ", " + col + ").");
        }
        if (model.collectSunAt(row, col)) {
            return success("Collected sun at (" + row + ", " + col + ").");
        }
        return failure("No collectible sun at (" + row + ", " + col + ").");
    }

    public CommandResult collectSunAtPosition(float modelX, float modelY, float hitRadius) {
        CommandResult screenCheck = requireGameScreen();
        if (screenCheck != null) {
            return screenCheck;
        }
        CommandResult activeCheck = requireSessionActive();
        if (activeCheck != null) {
            return activeCheck;
        }
        if (model.collectSunAtPosition(modelX, modelY, hitRadius)) {
            return success("Collected sun.");
        }
        return failure("No sun at that position.");
    }

    public CommandResult collectSeed(int row, int col) {
        CommandResult screenCheck = requireGameScreen();
        if (screenCheck != null) {
            return screenCheck;
        }
        CommandResult activeCheck = requireSessionActive();
        if (activeCheck != null) {
            return activeCheck;
        }
        if (!isValidCell(row, col)) {
            return failure("Invalid cell (" + row + ", " + col + ").");
        }
        PlantSeedDrop seed = gameState.getSeedDropAt(row, col);
        if (seed == null) {
            return failure("No plant seed at (" + row + ", " + col + ").");
        }
        PlantType type = seed.plantType;
        if (model.collectSeedAt(row, col)) {
            return success("Collected " + type.name + " seed at (" + row + ", " + col + ").");
        }
        return failure("Could not collect seed at (" + row + ", " + col + ").");
    }

    public CommandResult breakVase(int row, int col) {
        CommandResult screenCheck = requireGameScreen();
        if (screenCheck != null) {
            return screenCheck;
        }
        CommandResult activeCheck = requireSessionActive();
        if (activeCheck != null) {
            return activeCheck;
        }
        if (!isValidCell(row, col)) {
            return failure("Invalid cell (" + row + ", " + col + ").");
        }
        Vase vase = model.breakVase(row, col);
        if (vase == null) {
            return failure("No vase at (" + row + ", " + col + ").");
        }
        return success(GameMechanismControllerSupport.describeBrokenVase(vase, row, col));
    }

    public CommandResult showHeldSeeds() {
        CommandResult screenCheck = requireGameScreen();
        if (screenCheck != null) {
            return screenCheck;
        }
        if (model.getPlayContext() == null || model.getPlayContext().getHeldSeeds().isEmpty()) {
            return success("No held seeds.");
        }
        String seeds = model.getPlayContext().getHeldSeeds().entrySet().stream()
                .map(e -> e.getKey().name + " x" + e.getValue())
                .collect(Collectors.joining(", "));
        return success("Held seeds: " + seeds + ".");
    }

    public CommandResult placeZombie(int row, int col, ZombieType zombieType) {
        CommandResult screenCheck = requireGameScreen();
        if (screenCheck != null) {
            return screenCheck;
        }
        CommandResult activeCheck = requireSessionActive();
        if (activeCheck != null) {
            return activeCheck;
        }
        if (!model.getRuleEngine().canPlaceZombies()) {
            return failure("Zombie placement is not available in this mode.");
        }
        if (zombieType == null) {
            return failure("Zombie type not found.");
        }
        if (!IZombieShop.isPurchasable(zombieType)) {
            return failure(zombieType.name + " is not available in I, Zombie.");
        }
        if (!isValidCell(row, col)) {
            return failure("Invalid cell (" + row + ", " + col + ").");
        }
        int cost = IZombieShop.getCost(zombieType);
        int available = gameState.dualSunMode ? gameState.zombieSun : gameState.sunAmount;
        if (available < cost) {
            return failure("Not enough sun. Need " + cost + ", have " + available + ".");
        }
        if (model.placeZombie(row, col, zombieType)) {
            return success("Spawned " + zombieType.name + " at (" + row + ", " + col + ") (-" + cost + " sun).");
        }
        return failure("Could not place " + zombieType.name + " at (" + row + ", " + col + ").");
    }

    public CommandResult showAvailableZombies() {
        CommandResult screenCheck = requireGameScreen();
        if (screenCheck != null) {
            return screenCheck;
        }
        if (!model.getRuleEngine().canPlaceZombies()) {
            return failure("Zombie placement is not available in this mode.");
        }
        String list = IZombieShop.getCosts().entrySet().stream()
                .map(e -> e.getKey().name + " (" + e.getValue() + " sun)")
                .collect(Collectors.joining(", "));
        return success("Available zombies: " + list + ".");
    }

    public CommandResult showSunAmount() {
        CommandResult screenCheck = requireGameScreen();
        if (screenCheck != null) {
            return screenCheck;
        }
        if (gameState.dualSunMode) {
            return success("Plant sun: " + gameState.plantSun + ", Zombie sun: " + gameState.zombieSun + ".");
        }
        return success("Current sun: " + gameState.sunAmount + ".");
    }

    public CommandResult addSun(int count) {
        CommandResult screenCheck = requireGameScreen();
        if (screenCheck != null) {
            return screenCheck;
        }
        CommandResult activeCheck = requireSessionActive();
        if (activeCheck != null) {
            return activeCheck;
        }
        model.addSun(count);
        return success("Added " + count + " sun.");
    }

    public CommandResult releaseNuke() {
        CommandResult screenCheck = requireGameScreen();
        if (screenCheck != null) {
            return screenCheck;
        }
        CommandResult activeCheck = requireSessionActive();
        if (activeCheck != null) {
            return activeCheck;
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
        CommandResult activeCheck = requireSessionActive();
        if (activeCheck != null) {
            return activeCheck;
        }
        User user = controllerManager.getStorage().getCurrentUser();
        String validationError = GameMechanismControllerSupport.validatePlantPlacement(
                model, gameState, user, plantType, row, col);
        if (validationError != null) {
            return failure(validationError);
        }
        int level = user != null ? user.getPlantLevel(plantType) : PlantStats.DEFAULT_LEVEL;
        if (model.placePlant(row, col, plantType, level)) {
            return success("Planted " + plantType.name + " (Lv." + level + ") at (" + row + ", " + col + ").");
        }
        if (GameMechanismControllerSupport.isPlantWhatYouGetPlantingLocked(model)) {
            return failure("Planting is locked after zombie waves have started.");
        }
        return failure("Could not plant " + plantType.name + " at (" + row + ", " + col + ").");
    }

    public CommandResult startZombieWaves() {
        CommandResult screenCheck = requireGameScreen();
        if (screenCheck != null) {
            return screenCheck;
        }
        CommandResult activeCheck = requireSessionActive();
        if (activeCheck != null) {
            return activeCheck;
        }
        if (model.startDeferredWaves()) {
            return success("Zombie waves started! Planting is now locked.");
        }
        return failure("Zombie waves are already running or this level does not use deferred waves.");
    }

    public CommandResult placeConveyorPlant(int row, int col) {
        return placeConveyorPlant(row, col, 0);
    }

    public CommandResult placeConveyorPlant(int row, int col, int beltIndex) {
        CommandResult screenCheck = requireGameScreen();
        if (screenCheck != null) {
            return screenCheck;
        }
        CommandResult activeCheck = requireSessionActive();
        if (activeCheck != null) {
            return activeCheck;
        }
        if (model.getPlayContext() == null || !model.getPlayContext().isConveyorMode()) {
            return failure("Not in Conveyor Belt mode.");
        }
        if (!isValidCell(row, col)) {
            return failure("Invalid cell (" + row + ", " + col + ").");
        }
        if (beltIndex < 0 || beltIndex >= model.getPlayContext().getConveyorState().getBeltCount()) {
            return failure("No plant is available at that conveyor slot.");
        }
        PlantType offered = model.getPlayContext().getConveyorPlant(beltIndex);
        if (offered == null) {
            return failure("No plant is currently offered on the conveyor.");
        }
        if (model.placeConveyorPlant(row, col, beltIndex)) {
            return success("Planted " + offered.name + " from conveyor at (" + row + ", " + col + ").");
        }
        return failure("Could not place " + offered.name + " at (" + row + ", " + col + ").");
    }

    public CommandResult removeCooldown() {
        CommandResult screenCheck = requireGameScreen();
        if (screenCheck != null) {
            return screenCheck;
        }
        CommandResult activeCheck = requireSessionActive();
        if (activeCheck != null) {
            return activeCheck;
        }

        model.removeCooldowns();
        return success("Plant cooldowns removed.");
    }

    public CommandResult pluckPlant(int row, int col) {
        CommandResult screenCheck = requireGameScreen();
        if (screenCheck != null) {
            return screenCheck;
        }
        CommandResult activeCheck = requireSessionActive();
        if (activeCheck != null) {
            return activeCheck;
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
        CommandResult activeCheck = requireSessionActive();
        if (activeCheck != null) {
            return activeCheck;
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
        CommandResult activeCheck = requireSessionActive();
        if (activeCheck != null) {
            return activeCheck;
        }
        model.addPlantFood();
        return success("Added plant food.");
    }

    public CommandResult cheatSpawnZombie(int row, int col, ZombieType zombieType) {
        CommandResult screenCheck = requireGameScreen();
        if (screenCheck != null) {
            return screenCheck;
        }
        CommandResult activeCheck = requireSessionActive();
        if (activeCheck != null) {
            return activeCheck;
        }
        if (zombieType == null) {
            return failure("Zombie type not found.");
        }
        if (model.cheatSpawnZombie(row, col, zombieType)) {
            return success("Cheat spawned " + zombieType.name + " at (" + row + ", " + col + ").");
        }
        return failure("Could not spawn zombie.");
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

    public CommandResult showZombiesInfo() {
        CommandResult screenCheck = requireGameScreen();
        if (screenCheck != null) {
            return screenCheck;
        }

        List<Zombie> alive = gameState.zombies.stream()
                .filter(z -> z != null && z.isAlive)
                .toList();

        if (alive.isEmpty()) {
            return success("No alive zombies.");
        }

        for (int i = 0; i < alive.size(); i++) {
            if (i > 0) {
                controllerManager.sendMessage("");
            }
            for (String line : GameMechanismControllerSupport.formatZombieInfo(alive.get(i)).split("\\R")) {
                controllerManager.sendMessage(line);
            }
        }

        return success("Zombies info listed.");
    }

    public CommandResult showTileStatus(int row, int col) {
        CommandResult screenCheck = requireGameScreen();
        if (screenCheck != null) {
            return screenCheck;
        }
        if (!isValidCell(row, col)) {
            return failure("Invalid cell (" + row + ", " + col + ").");
        }
        return success(GameMechanismControllerSupport.describeTileStatus(gameState, row, col));
    }

    private CommandResult requireGameScreen() {
        if (controllerManager.getCurrentScreen() != ScreenType.GAME) {
            return failure("This command is only available during a game.");
        }
        return null;
    }

    private CommandResult requireSessionActive() {
        if (gameState.gameOver || gameState.levelComplete) {
            return failure("The session has ended. Use 'menu exit' to return.");
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
