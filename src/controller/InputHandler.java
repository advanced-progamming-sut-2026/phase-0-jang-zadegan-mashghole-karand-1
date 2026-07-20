package controller;

import model.core.Position;
import model.data.plant.PlantType;
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

    private boolean dispatchCommand(String input) {

        if (Commands.QUIT.getMatcher(input).matches()) {
            controllerManager.quit();
        } else if ((matcher = Commands.ENTER_CHAPTER.getMatcher(input)).matches()) {
            String chapterName = matcher.group(1);
            CommandResult result = controllerManager.getGameMenuController().enterChapter(chapterName);
            controllerManager.handleCommandResult(result);
        } else if ((matcher = Commands.SELECT_LEVEL.getMatcher(input)).matches()) {
            int levelNumber = Integer.parseInt(matcher.group(1));
            CommandResult result = controllerManager.getGameMenuController().selectLevel(levelNumber);
            controllerManager.handleCommandResult(result);
        } else if ((matcher = Commands.CHANGE_MENU.getMatcher(input)).matches()) {
            String menuName = matcher.group(1);
            controllerManager.handleCommandResult(controllerManager.enterMenu(menuName));
        } else if (Commands.SHOW_MENU.getMatcher(input).matches()) {
            controllerManager.handleCommandResult(controllerManager.showCurrentMenu());
        } else if (Commands.EXIT_MENU.getMatcher(input).matches()) {
            controllerManager.handleCommandResult(controllerManager.exitMenu());
        } else if ((matcher = Commands.REGISTER_USER.getMatcher(input)).matches()) {
            String username = matcher.group(1);
            String password = matcher.group(2);
            String password_confirm = matcher.group(3);
            String nickname = matcher.group(4);
            String email = matcher.group(5);
            String genderString = matcher.group(6);
            CommandResult result = controllerManager.getAuthController().register(username, password, password_confirm,
                    nickname, email, genderString);
            controllerManager.handleCommandResult(result);
        } else if ((matcher = Commands.PICK_QUESTION.getMatcher(input)).matches()) {
            int QuestionNum = Integer.parseInt(matcher.group(1));
            String answer = matcher.group(2);
            String answer_confirm = matcher.group(3);
            CommandResult result = controllerManager.getAuthController().pickQuestion(QuestionNum, answer,
                    answer_confirm);
            controllerManager.handleCommandResult(result);
        } else if ((matcher = Commands.LOGIN_STAY_LOGGED_IN.getMatcher(input)).matches()) {
            String username = matcher.group(1);
            String password = matcher.group(2);
            CommandResult result = controllerManager.getAuthController().login(username, password, true);
            controllerManager.handleCommandResult(result);
        } else if ((matcher = Commands.LOGIN.getMatcher(input)).matches()) {
            String username = matcher.group(1);
            String password = matcher.group(2);
            CommandResult result = controllerManager.getAuthController().login(username, password, false);
            controllerManager.handleCommandResult(result);
        } else if ((matcher = Commands.FORGET_PASS.getMatcher(input)).matches()) {
            String username = matcher.group(1);
            String email = matcher.group(2);
            CommandResult result = controllerManager.getAuthController().forgotPassword(username, email);
            controllerManager.handleCommandResult(result);
        } else if ((matcher = Commands.ANSWER.getMatcher(input)).matches()) {
            String answer = matcher.group(1);
            CommandResult result = controllerManager.getAuthController().answer(answer);
            controllerManager.handleCommandResult(result);
        } else if ((matcher = Commands.RESET_PASSWORD.getMatcher(input)).matches()) {
            String password = matcher.group(1);
            String passwordConfirm = matcher.group(2);
            CommandResult result = controllerManager.getAuthController().resetPassword(password, passwordConfirm);
            controllerManager.handleCommandResult(result);
        } else if (Commands.LOGOUT.getMatcher(input).matches()) {
            controllerManager.handleCommandResult(controllerManager.getMainMenuController().logout());
        } else if (Commands.GREENHOUSE.getMatcher(input).matches()) {
            controllerManager.handleCommandResult(controllerManager.getGameMenuController().greenHouse());
        } else if (Commands.TRAVEL_LOG.getMatcher(input).matches()) {
            controllerManager.handleCommandResult(controllerManager.getGameMenuController().quest());
        } else if (Commands.LEADERBOARD.getMatcher(input).matches()) {
            controllerManager.handleCommandResult(controllerManager.getGameMenuController().leaderboard());
        } else if (Commands.COIN_WALLET.getMatcher(input).matches()) {
            controllerManager.handleCommandResult(controllerManager.getGameMenuController().coin_wallet());
        } else if (Commands.GEM_WALLET.getMatcher(input).matches()) {
            controllerManager.handleCommandResult(controllerManager.getGameMenuController().gem_wallet());
        } else if ((matcher = Commands.ADD_COIN.getMatcher(input)).matches()) {
            int amount = Integer.parseInt(matcher.group(1));
            controllerManager.handleCommandResult(controllerManager.getGameMenuController().CHEAT_add_coin(amount));
        } else if ((matcher = Commands.ADD_DIAMOND.getMatcher(input)).matches()) {
            int amount = Integer.parseInt(matcher.group(1));
            controllerManager.handleCommandResult(controllerManager.getGameMenuController().CHEAT_add_gem(amount));
        } else if ((matcher = Commands.CHANGE_DIFFICULTY.getMatcher(input)).matches()) {
            try {
                int level = Integer.parseInt(matcher.group("difficultylevel").trim());
                controllerManager.handleCommandResult(
                        controllerManager.getSettingController().changeDifficulty(level));
            } catch (NumberFormatException e) {
                controllerManager.showError("Difficulty level must be a number.");
            }
        } else if (Commands.UNREAD_NEWS.getMatcher(input).matches()) {
            controllerManager.handleCommandResult(
                    controllerManager.getNewsMenuController().showUnreadNews());
        } else if (Commands.ALL_NEWS.getMatcher(input).matches()) {
            controllerManager.handleCommandResult(
                    controllerManager.getNewsMenuController().showAllNews());
        } else if ((matcher = Commands.DEBUG_ADD_NEWS.getMatcher(input)).matches()) {
            controllerManager.handleCommandResult(
                    controllerManager.getNewsMenuController().addDebugNews(matcher.group("message")));
        } else if ((matcher = Commands.CHANGE_USERNAME.getMatcher(input)).matches()) {
            String username = matcher.group("username");
            controllerManager.handleCommandResult(
                    controllerManager.getProfileController().changeUsername(username));
        } else if ((matcher = Commands.CHANGE_PASSWORD.getMatcher(input)).matches()) {
            String newPassword = matcher.group("newpassword");
            String oldPassword = matcher.group("oldpassword");
            controllerManager.handleCommandResult(
                    controllerManager.getProfileController().changePassword(oldPassword, newPassword));
        } else if ((matcher = Commands.CHANGE_NICKNAME.getMatcher(input)).matches()) {
            String nickname = matcher.group("nickname");
            controllerManager.handleCommandResult(
                    controllerManager.getProfileController().changeNickname(nickname));
        } else if ((matcher = Commands.CHANGE_EMAIL.getMatcher(input)).matches()) {
            String email = matcher.group("email");
            controllerManager.handleCommandResult(
                    controllerManager.getProfileController().changeEmail(email));
        } else if (Commands.SHOW_PROFILE_INFO.getMatcher(input).matches()) {
            controllerManager.handleCommandResult(
                    controllerManager.getProfileController().refreshInfo());
        } else if (Commands.SHOW_PLANTS.getMatcher(input).matches()) {
            controllerManager.handleCommandResult(
                    controllerManager.getCollectionController().showPlants());
        } else if (Commands.SHOW_ALL_PLANTS.getMatcher(input).matches()) {
            controllerManager.handleCommandResult(
                    controllerManager.getCollectionController().showAllPlants());
        } else if (Commands.SHOW_ZOMBIES.getMatcher(input).matches()) {
            controllerManager.handleCommandResult(
                    controllerManager.getCollectionController().showZombies());
        } else if (Commands.SHOW_ALL_ZOMBIES.getMatcher(input).matches()) {
            controllerManager.handleCommandResult(
                    controllerManager.getCollectionController().showAllZombies());
        } else if ((matcher = Commands.SHOW_PLANT.getMatcher(input)).matches()) {
            String plantName = matcher.group("plantname");
            controllerManager.handleCommandResult(
                    controllerManager.getCollectionController().showPlant(plantName));
        } else if ((matcher = Commands.SHOW_ZOMBIE.getMatcher(input)).matches()) {
            String zombieName = matcher.group("zombiename");
            controllerManager.handleCommandResult(
                    controllerManager.getCollectionController().showZombie(zombieName));
        } else if ((matcher = Commands.DEBUG_SHOW_PLANT.getMatcher(input)).matches()) {
            String plantName = matcher.group("plantname");
            controllerManager.handleCommandResult(
                    controllerManager.getCollectionController().showPlantDebug(plantName));
        } else if ((matcher = Commands.DEBUG_SHOW_ZOMBIE.getMatcher(input)).matches()) {
            String zombieName = matcher.group("zombiename");
            controllerManager.handleCommandResult(
                    controllerManager.getCollectionController().showZombieDebug(zombieName));
        } else if ((matcher = Commands.UPGRADE_PLANT.getMatcher(input)).matches()) {
            String plantName = matcher.group("plantname");
            controllerManager.handleCommandResult(
                    controllerManager.getCollectionController().upgradePlant(plantName));
        } else if ((matcher = Commands.PURCHASE_PLANT.getMatcher(input)).matches()) {
            String plantName = matcher.group("plantname");
            controllerManager.handleCommandResult(
                    controllerManager.getCollectionController().purchasePlant(plantName));
        } else if (Commands.SHOW_ALL_PLANTS_SELECT.getMatcher(input).matches()) {
            CommandResult result = controllerManager.getPickPlantsController().showAllPlants();
            controllerManager.handleCommandResult(result);
        } else if (Commands.SHOW_AVAILABLE_PLANTS.getMatcher(input).matches()) {
            CommandResult result = controllerManager.getPickPlantsController().showAvailablePlants();
            controllerManager.handleCommandResult(result);
        } else if ((matcher = Commands.ADD_PLANT.getMatcher(input)).matches()) {
            String typeStr = matcher.group(1);
            PlantType type = PlantType.fromName(typeStr);
            CommandResult result = controllerManager.getPickPlantsController().addPlant(type);
            controllerManager.handleCommandResult(result);
        } else if ((matcher = Commands.REMOVE_PLANT.getMatcher(input)).matches()) {
            String typeStr = matcher.group(1);
            PlantType type = PlantType.fromName(typeStr);
            CommandResult result = controllerManager.getPickPlantsController().removePlant(type);
            controllerManager.handleCommandResult(result);
        } else if ((matcher = Commands.BOOST_PLANT.getMatcher(input)).matches()) {
            String typeStr = matcher.group(1);
            PlantType type = PlantType.fromName(typeStr);
            CommandResult result = controllerManager.getPickPlantsController().boostPlant(type);
            controllerManager.handleCommandResult(result);
        } else if (Commands.START_GAME.getMatcher(input).matches()) {
            CommandResult result = controllerManager.getPickPlantsController().startGame();
            controllerManager.handleCommandResult(result);
        } else if ((matcher = Commands.ADVANCE_TIME.getMatcher(input)).matches()) {
            int count = Integer.parseInt(matcher.group("count"));
            boolean realTime = input.contains("-real");
            controllerManager.handleCommandResult(
                    controllerManager.getGameMechanismController().advanceTicks(count, realTime));
        } else if ((matcher = Commands.DEBUG_AUTO_TICK.getMatcher(input)).matches()) {
            String state = matcher.group("state");
            Boolean enable = state == null ? null : state.equals("on");
            controllerManager.handleCommandResult(
                    controllerManager.getGameMechanismController().setAutoTick(enable));
        } else if ((matcher = Commands.COLLECT_SUN.getMatcher(input)).matches()) {
            int row = Integer.parseInt(matcher.group("x"));
            int col = Integer.parseInt(matcher.group("y"));
            controllerManager.handleCommandResult(
                    controllerManager.getGameMechanismController().collectSun(row, col));
        } else if (Commands.SHOW_SUN_AMOUNT.getMatcher(input).matches()) {
            controllerManager.handleCommandResult(
                    controllerManager.getGameMechanismController().showSunAmount());
        } else if ((matcher = Commands.CHEAT_ADD_SUNS.getMatcher(input)).matches()) {
            int amount = Integer.parseInt(matcher.group("count"));
            controllerManager.handleCommandResult(
                    controllerManager.getGameMechanismController().addSun(amount));
        } else if (Commands.RELEASE_NUKE.getMatcher(input).matches()) {
            controllerManager.handleCommandResult(
                    controllerManager.getGameMechanismController().releaseNuke());
        } else if ((matcher = Commands.PLANT_PLANT.getMatcher(input)).matches()) {
            PlantType type = PlantType.fromName(matcher.group("type"));
            int row = Integer.parseInt(matcher.group("x"));
            int col = Integer.parseInt(matcher.group("y"));
            controllerManager.handleCommandResult(
                    controllerManager.getGameMechanismController().plantPlant(row, col, type));
        } else if ((matcher = Commands.PLANT_CONVEYOR.getMatcher(input)).matches()) {
            int row = Integer.parseInt(matcher.group("x"));
            int col = Integer.parseInt(matcher.group("y"));
            controllerManager.handleCommandResult(
                    controllerManager.getGameMechanismController().placeConveyorPlant(row, col));
        } else if (Commands.CHEAT_REMOVE_COOLDOWN.getMatcher(input).matches()) {
            controllerManager.handleCommandResult(
                    controllerManager.getGameMechanismController().removeCooldown());
        } else if ((matcher = Commands.PLUCK_PLANT.getMatcher(input)).matches()) {
            int row = Integer.parseInt(matcher.group("x"));
            int col = Integer.parseInt(matcher.group("y"));
            controllerManager.handleCommandResult(
                    controllerManager.getGameMechanismController().pluckPlant(row, col));
        } else if ((matcher = Commands.FEED_PLANT.getMatcher(input)).matches()) {
            int row = Integer.parseInt(matcher.group("x"));
            int col = Integer.parseInt(matcher.group("y"));
            controllerManager.handleCommandResult(
                    controllerManager.getGameMechanismController().feedPlant(row, col));
        } else if (Commands.CHEAT_ADD_PLANT_FOOD.getMatcher(input).matches()) {
            controllerManager.handleCommandResult(
                    controllerManager.getGameMechanismController().addPlantFood());
        } else if (Commands.SHOW_MAP.getMatcher(input).matches()) {
            controllerManager.handleCommandResult(
                    controllerManager.getGameMechanismController().showMap());
        } else if (Commands.SHOW_PLANTS_STATUS.getMatcher(input).matches()) {
            controllerManager.handleCommandResult(
                    controllerManager.getGameMechanismController().showPlantsStatus());
        } else if ((matcher = Commands.SHOW_TILE_STATUS.getMatcher(input)).matches()) {
            int row = Integer.parseInt(matcher.group("x"));
            int col = Integer.parseInt(matcher.group("y"));
            controllerManager.handleCommandResult(
                    controllerManager.getGameMechanismController().showTileStatus(row, col));
        } else if (Commands.SHOW_GREENHOUSE.getMatcher(input).matches()) {
            // CommandResult result =
            // controllerManager.getGreenhouseController().showGreenhouse(user);
        } else if ((matcher = Commands.PLANT_POT.getMatcher(input)).matches()) {
            int x = Integer.parseInt(matcher.group(1));
            int y = Integer.parseInt(matcher.group(2));
            Position pos = new Position(x, y);
            // controllerManager.getGreenhouseController().plantPot(user,pos);
        } else if ((matcher = Commands.COLLECT_POT.getMatcher(input)).matches()) {
            int x = Integer.parseInt(matcher.group(1));
            int y = Integer.parseInt(matcher.group(2));
            Position pos = new Position(x, y);
            // controllerManager.getGreenhouseController().collect(user,pos);
        } else if ((matcher = Commands.GROW_POT.getMatcher(input)).matches()) {
            int x = Integer.parseInt(matcher.group(1));
            int y = Integer.parseInt(matcher.group(2));
            Position pos = new Position(x, y);
            // controllerManager.getGreenhouseController().grow(user,pos)
        } else if (Commands.ENTER_SHOP.getMatcher(input).matches()) {
            CommandResult result = controllerManager.getGreenhouseController().enterShop();
            controllerManager.handleCommandResult(result);
        } else if (Commands.SHOP_LIST.getMatcher(input).matches()) {
            CommandResult list = controllerManager.getShopController().List();
            controllerManager.handleCommandResult(list);
        } else if (Commands.SHOP_DAILY.getMatcher(input).matches()) {
            CommandResult dailyList = controllerManager.getShopController().daily();
            controllerManager.handleCommandResult(dailyList);
        } else if ((matcher = Commands.SHOP_BUY.getMatcher(input)).matches()) {
            String itemId = matcher.group(1);
            int quantity = Integer.parseInt(matcher.group(2));
            CommandResult result = controllerManager.getShopController().buy(itemId, quantity);
            controllerManager.handleCommandResult(result);
        } else if ((matcher = Commands.SHOP_BUY_WITH_TYPE.getMatcher(input)).matches()) {
            String itemId = matcher.group(1);
            int quantity = Integer.parseInt(matcher.group(2));
            String plantTypeStr = matcher.group(3);
            PlantType plantType = PlantType.fromName(plantTypeStr);
            CommandResult result = controllerManager.getShopController().buy(itemId, quantity, plantType);
            controllerManager.handleCommandResult(result);
        } else if ((matcher = Commands.TRAVEL_LOG_PAGE.getMatcher(input)).matches()) {
            String pageName = matcher.group(1);
            CommandResult result = controllerManager.getQuestMenuController().enterPage(pageName);
        } else if ((matcher = Commands.SORT_SCORE.getMatcher(input)).matches()) {
            String sortClass = matcher.group(1);
            String sortType = matcher.group(2);
            controllerManager.handleCommandResult(
                    controllerManager.getLeaderboardMenuController().sort(sortClass, sortType));
        } else {
            return false;
        }
        // leaderboard and minigames...

        return true;
    }

}
