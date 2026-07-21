package controller;

import controller.CommandResult.CommandResult;
import model.core.Position;
import model.data.plant.PlantType;
import model.data.zombie.Zombie;
import model.greenhouse.Pot;
import model.storage.StorageManager;
import model.storage.user.User;
import view.ScreenType;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class GreenhouseController {
    ControllerManager controllerManager;
    private final StorageManager storage;
    private boolean potsVisible = false;
    public GreenhouseController(ControllerManager controllerManager, StorageManager storage ) {
        this.controllerManager = controllerManager;
        this.storage = storage;
    }

    public CommandResult showGreenhouse() {
        User user = storage.getCurrentUser();
        if (user == null) {
            return failure("You must be logged in.");
        }
        potsVisible= true;
        storage.saveProgress();
        controllerManager.setScreen(ScreenType.GREEN_HOUSE);
        return success("Greenhouse displayed.");
    }

    public CommandResult plantPot( Position position) {
        PlantType plantType;
        Pot.PlantClass plantClass;
        User user = storage.getCurrentUser();
        if (user == null) {
            return failure("You must be logged in.");
        }
        if (user.greenhouse == null){
            return failure("There is no Greenhouse");
        }
        if (ThreadLocalRandom.current().nextBoolean()) {
            plantClass = Pot.PlantClass.NORMAL_PLANT;
            plantType = null;
        } else {
            plantClass = Pot.PlantClass.UNLOCK_PLANT;
            plantType = pickRandomUnlockedPlant();
            if (plantType == null) {
                return failure("No unlocked plant with plant food.");
            }
        }
        boolean ok = user.greenhouse.plantAt(position, plantClass , plantType);
        if (!ok) {
            return failure("Cannot plant here.");
        }
        storage.saveProgress();
        controllerManager.refreshView();
        return success("Planted " + plantClass + " " + plantType + " at (" + position.x + ", " + position.y + ").");
    }

    public CommandResult collect(Position position) {
        User user = storage.getCurrentUser();
        if (user == null) {
            return failure("You must be logged in.");
        }
        if (user.greenhouse == null)
            return failure("There is no Greenhouse");
        Pot pot = user.greenhouse.getPot(position);
        if (pot == null || pot.isLocked())
            return failure("Invalid pot.");
        if (pot.isEmpty())
            return failure("Pot is empty.");
        if (!pot.isReady())
            return failure("Plant is not ready yet.");
        PlantType type = pot.getPlantType();
        Pot.PlantClass plantClass = pot.getPlantClass();
        String message;
        if (plantClass == Pot.PlantClass.UNLOCK_PLANT && type != null) {
              if (user.storedBoosts.contains(type)){
                  message = "Collected " + type.name + ". Boost already stored.";
              }else{
                  user.storedBoosts.add(type);
                  controllerManager.refreshView();
                  message = "Collected " + type.name + ". Boost stored.";
              }
        }else if (plantClass == Pot.PlantClass.NORMAL_PLANT){
            user.coins += 500;
            controllerManager.refreshView();
            message = "Collected Marigold. +500 coins.";
        }else {
            return failure("Nothing to collect.");
        }
        pot.clear();
        storage.saveProgress();
        return success(message);
    }

    public CommandResult grow(Position position) {
        User user = storage.getCurrentUser();
        if (user == null) {
            return failure("You must be logged in.");
        }
        if (user.greenhouse == null)
            return failure("There is no Greenhouse");
        Pot pot = user.greenhouse.getPot(position);
        if (pot == null || pot.isLocked())
            return failure("Invalid pot.");
        if (pot.isReady())
            return failure("Plant is ready!");
        if (pot.isEmpty())
            return failure("Pot is empty.");
        long remainingHours = pot.getRemainingHours();
        int gemCost = (int) remainingHours;
        if (user.gems < gemCost)
            return failure("Not enough gems. Need " + gemCost + ".");
        user.gems -= gemCost;
        pot.forceReady();
        storage.saveProgress();
        controllerManager.refreshView();
        return success("Grew for " + gemCost + " gem(s).");
    }

    public CommandResult enterShop() {
        controllerManager.getShopController().setShopDisplayMode(ShopController.ShopDisplayMode.MENU);
        controllerManager.setScreen(ScreenType.SHOP);
        return success("Entered shop");
    }
    public PlantType pickRandomUnlockedPlant() {
        User user = storage.getCurrentUser();
        if (user == null) {
            return null;
        }
        List<PlantType> unlocked = Arrays.stream(PlantType.values())
                .filter(p -> user.collection.isPlantUnlocked(p))
                .filter(p -> p.plantFoodEffect != null)
                .toList();
        if (unlocked.isEmpty()) {
            return null;
        }
        return unlocked.get(ThreadLocalRandom.current().nextInt(unlocked.size()));
    }
    public boolean isPotsVisible() {
        return potsVisible;
    }
    public void hidePots() {
        potsVisible = false;
    }
    public User getUser() {
        return storage.getCurrentUser();
    }

    private CommandResult success(String message) {
        return new CommandResult(message, true);
    }
    private CommandResult failure(String message) {
        return new CommandResult(message, false);
    }



}
