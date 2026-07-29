package controller;

import model.core.Position;
import model.data.plant.PlantType;
import model.data.zombie.ZombieType;
import view.messages.ErrorMessages;

import java.util.regex.Matcher;

import controller.CommandResult.CommandResult;

public class InputHandler {
    private ControllerManager controllerManager;
    private Matcher matcher;

    public InputHandler(ControllerManager controllerManager) {
        this.controllerManager = controllerManager;
    }

    public void handleInput(String input) {
        if (!dispatchCommand(input)) {
            controllerManager.showError(ErrorMessages.UNKNOWN_COMMAND.getMessage());
            controllerManager.refreshView();
        }
    }

    public void handleMessageScroll(int olderDelta) {
        if (controllerManager.scrollMessages(olderDelta)) {
            controllerManager.refreshView();
        }
    }

    private boolean dispatchCommand(String input) {
        return dispatchNavigation(input)
                || dispatchAuth(input)
                || dispatchMenuExtras(input)
                || dispatchProfileAndCollection(input)
                || dispatchPlantSelection(input)
                || dispatchGameTickAndCollect(input)
                || dispatchGamePlacement(input)
                || dispatchGameCheatsAndStatus(input)
                || dispatchGreenhouseAndShop(input)
                || dispatchTravelAndLeaderboard(input);
    }

    private boolean dispatchNavigation(String input) {
        if (Commands.QUIT.getMatcher(input).matches()) {
            controllerManager.quit();
            return true;
        }
        if ((matcher = Commands.ENTER_CHAPTER.getMatcher(input)).matches()) {
            handle(controllerManager.getGameMenuController().enterChapter(matcher.group(1)));
            return true;
        }
        if ((matcher = Commands.SELECT_MINIGAME.getMatcher(input)).matches()) {
            handle(controllerManager.getGameMenuController().selectMinigame(matcher.group("minigamename")));
            return true;
        }
        if ((matcher = Commands.SELECT_LEVEL.getMatcher(input)).matches()) {
            handle(controllerManager.getGameMenuController().selectLevel(Integer.parseInt(matcher.group(1))));
            return true;
        }
        if ((matcher = Commands.CHANGE_MENU.getMatcher(input)).matches()) {
            handle(controllerManager.enterMenu(matcher.group(1)));
            return true;
        }
        if (Commands.SHOW_MENU.getMatcher(input).matches()) {
            handle(controllerManager.showCurrentMenu());
            return true;
        }
        if (Commands.EXIT_MENU.getMatcher(input).matches()) {
            handle(controllerManager.exitMenu());
            return true;
        }
        return false;
    }

    private boolean dispatchAuth(String input) {
        if ((matcher = Commands.REGISTER_USER.getMatcher(input)).matches()) {
            handle(controllerManager.getAuthController().register(
                    matcher.group(1), matcher.group(2), matcher.group(3),
                    matcher.group(4), matcher.group(5), matcher.group(6)));
            return true;
        }
        if ((matcher = Commands.PICK_QUESTION.getMatcher(input)).matches()) {
            handle(controllerManager.getAuthController().pickQuestion(
                    Integer.parseInt(matcher.group(1)), matcher.group(2), matcher.group(3)));
            return true;
        }
        if ((matcher = Commands.LOGIN_STAY_LOGGED_IN.getMatcher(input)).matches()) {
            handle(controllerManager.getAuthController().login(matcher.group(1), matcher.group(2), true));
            return true;
        }
        if ((matcher = Commands.LOGIN.getMatcher(input)).matches()) {
            handle(controllerManager.getAuthController().login(matcher.group(1), matcher.group(2), false));
            return true;
        }
        if ((matcher = Commands.FORGET_PASS.getMatcher(input)).matches()) {
            handle(controllerManager.getAuthController().forgotPassword(matcher.group(1), matcher.group(2)));
            return true;
        }
        if ((matcher = Commands.ANSWER.getMatcher(input)).matches()) {
            handle(controllerManager.getAuthController().answer(matcher.group(1)));
            return true;
        }
        if ((matcher = Commands.RESET_PASSWORD.getMatcher(input)).matches()) {
            handle(controllerManager.getAuthController().resetPassword(matcher.group(1), matcher.group(2)));
            return true;
        }
        if (Commands.LOGOUT.getMatcher(input).matches()) {
            handle(controllerManager.getMainMenuController().logout());
            return true;
        }
        return false;
    }

    private boolean dispatchMenuExtras(String input) {
        if ((matcher = Commands.ADD_COIN.getMatcher(input)).matches()) {
            handle(controllerManager.getGameMenuController().cheatAddCoin(Integer.parseInt(matcher.group(1))));
            return true;
        }
        if ((matcher = Commands.ADD_DIAMOND.getMatcher(input)).matches()) {
            handle(controllerManager.getGameMenuController().cheatAddGem(Integer.parseInt(matcher.group(1))));
            return true;
        }
        if ((matcher = Commands.CHANGE_DIFFICULTY.getMatcher(input)).matches()) {
            try {
                int level = Integer.parseInt(matcher.group("difficultylevel").trim());
                handle(controllerManager.getSettingController().changeDifficulty(level));
            } catch (NumberFormatException e) {
                controllerManager.showError("Difficulty level must be a number.");
            }
            return true;
        }
        if (Commands.UNREAD_NEWS.getMatcher(input).matches()) {
            handle(controllerManager.getNewsMenuController().showUnreadNews());
            return true;
        }
        if (Commands.ALL_NEWS.getMatcher(input).matches()) {
            handle(controllerManager.getNewsMenuController().showAllNews());
            return true;
        }
        if ((matcher = Commands.DEBUG_ADD_NEWS.getMatcher(input)).matches()) {
            handle(controllerManager.getNewsMenuController().addDebugNews(matcher.group("message")));
            return true;
        }
        return false;
    }

    private boolean dispatchProfileAndCollection(String input) {
        if ((matcher = Commands.CHANGE_USERNAME.getMatcher(input)).matches()) {
            handle(controllerManager.getProfileController().changeUsername(matcher.group("username")));
            return true;
        }
        if ((matcher = Commands.CHANGE_PASSWORD.getMatcher(input)).matches()) {
            handle(controllerManager.getProfileController()
                    .changePassword(matcher.group("oldpassword"), matcher.group("newpassword")));
            return true;
        }
        if ((matcher = Commands.CHANGE_NICKNAME.getMatcher(input)).matches()) {
            handle(controllerManager.getProfileController().changeNickname(matcher.group("nickname")));
            return true;
        }
        if ((matcher = Commands.CHANGE_EMAIL.getMatcher(input)).matches()) {
            handle(controllerManager.getProfileController().changeEmail(matcher.group("email")));
            return true;
        }
        if (Commands.SHOW_PROFILE_INFO.getMatcher(input).matches()) {
            handle(controllerManager.getProfileController().refreshInfo());
            return true;
        }
        return dispatchCollection(input);
    }

    private boolean dispatchCollection(String input) {
        if (Commands.SHOW_PLANTS.getMatcher(input).matches()) {
            handle(controllerManager.getCollectionController().showPlants());
            return true;
        }
        if (Commands.SHOW_ALL_PLANTS.getMatcher(input).matches()) {
            handle(controllerManager.getCollectionController().showAllPlants());
            return true;
        }
        if (Commands.SHOW_ZOMBIES.getMatcher(input).matches()) {
            handle(controllerManager.getCollectionController().showZombies());
            return true;
        }
        if (Commands.SHOW_ALL_ZOMBIES.getMatcher(input).matches()) {
            handle(controllerManager.getCollectionController().showAllZombies());
            return true;
        }
        if ((matcher = Commands.SHOW_PLANT.getMatcher(input)).matches()) {
            handle(controllerManager.getCollectionController().showPlant(matcher.group("plantname")));
            return true;
        }
        if ((matcher = Commands.SHOW_ZOMBIE.getMatcher(input)).matches()) {
            handle(controllerManager.getCollectionController().showZombie(matcher.group("zombiename")));
            return true;
        }
        if ((matcher = Commands.DEBUG_SHOW_PLANT.getMatcher(input)).matches()) {
            handle(controllerManager.getCollectionController().showPlantDebug(matcher.group("plantname")));
            return true;
        }
        if ((matcher = Commands.DEBUG_SHOW_ZOMBIE.getMatcher(input)).matches()) {
            handle(controllerManager.getCollectionController().showZombieDebug(matcher.group("zombiename")));
            return true;
        }
        if ((matcher = Commands.UPGRADE_PLANT.getMatcher(input)).matches()) {
            handle(controllerManager.getCollectionController().upgradePlant(matcher.group("plantname")));
            return true;
        }
        if ((matcher = Commands.PURCHASE_PLANT.getMatcher(input)).matches()) {
            handle(controllerManager.getCollectionController().purchasePlant(matcher.group("plantname")));
            return true;
        }
        return false;
    }

    private boolean dispatchPlantSelection(String input) {
        if (Commands.SHOW_ALL_PLANTS_SELECT.getMatcher(input).matches()) {
            handle(controllerManager.getPickPlantsController().showAllPlants());
            return true;
        }
        if (Commands.SHOW_AVAILABLE_PLANTS.getMatcher(input).matches()) {
            handle(controllerManager.getPickPlantsController().showAvailablePlants());
            return true;
        }
        if ((matcher = Commands.ADD_PLANT.getMatcher(input)).matches()) {
            handle(controllerManager.getPickPlantsController()
                    .addPlant(PlantType.fromName(matcher.group(1)), PlantType.fromName(matcher.group(2))));
            return true;
        }
        if ((matcher = Commands.REMOVE_PLANT.getMatcher(input)).matches()) {
            handle(controllerManager.getPickPlantsController().removePlant(PlantType.fromName(matcher.group(1))));
            return true;
        }
        if ((matcher = Commands.BOOST_PLANT.getMatcher(input)).matches()) {
            handle(controllerManager.getPickPlantsController().boostPlant(PlantType.fromName(matcher.group(1))));
            return true;
        }
        if (Commands.START_GAME.getMatcher(input).matches()) {
            handle(controllerManager.getPickPlantsController().startGame());
            return true;
        }
        return false;
    }

    private boolean dispatchGameTickAndCollect(String input) {
        GameMechanismController game = controllerManager.getGameMechanismController();
        if ((matcher = Commands.ADVANCE_TIME.getMatcher(input)).matches()) {
            handle(game.advanceTicks(Integer.parseInt(matcher.group("count")), input.contains("-real")));
            return true;
        }
        if ((matcher = Commands.DEBUG_AUTO_TICK.getMatcher(input)).matches()) {
            String state = matcher.group("state");
            handle(game.setAutoTick(state == null ? null : state.equals("on")));
            return true;
        }
        if ((matcher = Commands.COLLECT_SUN.getMatcher(input)).matches()) {
            handle(game.collectSun(Integer.parseInt(matcher.group("x")), Integer.parseInt(matcher.group("y"))));
            return true;
        }
        if ((matcher = Commands.COLLECT_SEED.getMatcher(input)).matches()) {
            handle(game.collectSeed(Integer.parseInt(matcher.group("x")), Integer.parseInt(matcher.group("y"))));
            return true;
        }
        if ((matcher = Commands.BREAK_VASE.getMatcher(input)).matches()) {
            handle(game.breakVase(Integer.parseInt(matcher.group("x")), Integer.parseInt(matcher.group("y"))));
            return true;
        }
        if (Commands.SHOW_HELD_SEEDS.getMatcher(input).matches()) {
            handle(game.showHeldSeeds());
            return true;
        }
        return false;
    }

    private boolean dispatchGamePlacement(String input) {
        GameMechanismController game = controllerManager.getGameMechanismController();
        if ((matcher = Commands.PLACE_ZOMBIE.getMatcher(input)).matches()) {
            handle(game.placeZombie(Integer.parseInt(matcher.group("x")), Integer.parseInt(matcher.group("y")),
                    ZombieType.fromName(matcher.group("type"))));
            return true;
        }
        if (Commands.SHOW_AVAILABLE_ZOMBIES.getMatcher(input).matches()) {
            handle(game.showAvailableZombies());
            return true;
        }
        if (Commands.SHOW_SUN_AMOUNT.getMatcher(input).matches()) {
            handle(game.showSunAmount());
            return true;
        }
        if ((matcher = Commands.CHEAT_ADD_SUNS.getMatcher(input)).matches()) {
            handle(game.addSun(Integer.parseInt(matcher.group("count"))));
            return true;
        }
        if (Commands.RELEASE_NUKE.getMatcher(input).matches()) {
            handle(game.releaseNuke());
            return true;
        }
        if (Commands.START_ZOMBIE_WAVES.getMatcher(input).matches()) {
            handle(game.startZombieWaves());
            return true;
        }
        return false;
    }

    private boolean dispatchGameCheatsAndStatus(String input) {
        GameMechanismController game = controllerManager.getGameMechanismController();
        if ((matcher = Commands.PLANT_PLANT.getMatcher(input)).matches()) {
            handle(game.plantPlant(Integer.parseInt(matcher.group("x")), Integer.parseInt(matcher.group("y")),
                    PlantType.fromName(matcher.group("type"))));
            return true;
        }
        if ((matcher = Commands.PLANT_CONVEYOR.getMatcher(input)).matches()) {
            handle(game.placeConveyorPlant(
                    Integer.parseInt(matcher.group("x")), Integer.parseInt(matcher.group("y"))));
            return true;
        }
        if (Commands.CHEAT_REMOVE_COOLDOWN.getMatcher(input).matches()) {
            handle(game.removeCooldown());
            return true;
        }
        if ((matcher = Commands.PLUCK_PLANT.getMatcher(input)).matches()) {
            handle(game.pluckPlant(Integer.parseInt(matcher.group("x")), Integer.parseInt(matcher.group("y"))));
            return true;
        }
        if ((matcher = Commands.FEED_PLANT.getMatcher(input)).matches()) {
            handle(game.feedPlant(Integer.parseInt(matcher.group("x")), Integer.parseInt(matcher.group("y"))));
            return true;
        }
        if (Commands.CHEAT_ADD_PLANT_FOOD.getMatcher(input).matches()) {
            handle(game.addPlantFood());
            return true;
        }
        if ((matcher = Commands.CHEAT_SPAWN_ZOMBIE.getMatcher(input)).matches()) {
            handle(game.cheatSpawnZombie(Integer.parseInt(matcher.group("x")), Integer.parseInt(matcher.group("y")),
                    ZombieType.fromName(matcher.group("zombietype"))));
            return true;
        }
        if (Commands.SHOW_MAP.getMatcher(input).matches()) {
            handle(game.showMap());
            return true;
        }
        if (Commands.SHOW_PLANTS_STATUS.getMatcher(input).matches()) {
            handle(game.showPlantsStatus());
            return true;
        }
        if ((matcher = Commands.SHOW_TILE_STATUS.getMatcher(input)).matches()) {
            handle(game.showTileStatus(Integer.parseInt(matcher.group("x")), Integer.parseInt(matcher.group("y"))));
            return true;
        }
        return false;
    }

    private boolean dispatchGreenhouseAndShop(String input) {
        if (Commands.SHOW_GREENHOUSE.getMatcher(input).matches()) {
            handle(controllerManager.getGreenhouseController().showGreenhouse());
            return true;
        }
        if ((matcher = Commands.PLANT_POT.getMatcher(input)).matches()) {
            handle(controllerManager.getGreenhouseController()
                    .plantPot(new Position(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)))));
            return true;
        }
        if ((matcher = Commands.COLLECT_POT.getMatcher(input)).matches()) {
            handle(controllerManager.getGreenhouseController()
                    .collect(new Position(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)))));
            return true;
        }
        if ((matcher = Commands.GROW_POT.getMatcher(input)).matches()) {
            handle(controllerManager.getGreenhouseController()
                    .grow(new Position(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)))));
            return true;
        }
        if (Commands.ENTER_SHOP.getMatcher(input).matches()) {
            handle(controllerManager.getGreenhouseController().enterShop());
            return true;
        }
        if (Commands.SHOP_LIST.getMatcher(input).matches()) {
            handle(controllerManager.getShopController().list());
            return true;
        }
        if (Commands.SHOP_DAILY.getMatcher(input).matches()) {
            handle(controllerManager.getShopController().daily());
            return true;
        }
        if ((matcher = Commands.SHOP_BUY.getMatcher(input)).matches()) {
            handle(controllerManager.getShopController().buy(matcher.group(1), Integer.parseInt(matcher.group(2))));
            return true;
        }
        if ((matcher = Commands.SHOP_BUY_WITH_TYPE.getMatcher(input)).matches()) {
            handle(controllerManager.getShopController().buy(
                    matcher.group(1), Integer.parseInt(matcher.group(2)), PlantType.fromName(matcher.group(3))));
            return true;
        }
        return false;
    }

    private boolean dispatchTravelAndLeaderboard(String input) {
        if ((matcher = Commands.TRAVEL_LOG_PAGE.getMatcher(input)).matches()) {
            handle(controllerManager.getQuestMenuController().enterPage(matcher.group(1)));
            return true;
        }
        if ((matcher = Commands.SORT_SCORE.getMatcher(input)).matches()) {
            handle(controllerManager.getLeaderboardMenuController()
                    .sort(matcher.group("sortclass"), matcher.group("sorttype")));
            return true;
        }
        return false;
    }

    private void handle(CommandResult result) {
        controllerManager.handleCommandResult(result);
    }
}
