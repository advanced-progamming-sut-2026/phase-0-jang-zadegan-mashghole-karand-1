package controller;

import model.CommandResult.CommandResult;
import model.core.Position;
import model.data.plant.PlantType;

import java.util.regex.Matcher;

public class InputHandler {
    private ControllerManager controllerManager;
    private Matcher matcher;

    public InputHandler(ControllerManager controllerManager) {
        this.controllerManager = controllerManager;
    }

    public void handleInput(String input) {

        if ((matcher = Commands.REGISTER_USER.getMatcher(input)).matches()) {
            String username = matcher.group(1);
            String password = matcher.group(2);
            String password_confirm = matcher.group(3);
            String nickname = matcher.group(4);
            String email = matcher.group(5);
            String genderString = matcher.group(6);
            CommandResult result = controllerManager.getAuthController().register(username, password, password_confirm,
                    nickname, email, genderString);
            // output?
        } else if ((matcher = Commands.PICK_QUESTION.getMatcher(input)).matches()) {
            int QuestionNum = Integer.parseInt(matcher.group(1));
            String answer = matcher.group(2);
            String answer_confirm = matcher.group(3);
            CommandResult result = controllerManager.getAuthController().pickQuestion(QuestionNum, answer,
                    answer_confirm);
        } else if ((matcher = Commands.LOGIN_STAY_LOGGED_IN.getMatcher(input)).matches()) {
            String username = matcher.group(1);
            String password = matcher.group(2);
            CommandResult result = controllerManager.getLoginController().login(username, password, true);
        } else if ((matcher = Commands.LOGIN.getMatcher(input)).matches()) {
            String username = matcher.group(1);
            String password = matcher.group(2);
            CommandResult result = controllerManager.getLoginController().login(username, password, false);
        } else if ((matcher = Commands.FORGET_PASS.getMatcher(input)).matches()) {
            String username = matcher.group(1);
            String email = matcher.group(2);
            CommandResult result = controllerManager.getLoginController().forgotPassword(username, email);
        } else if ((matcher = Commands.ANSWER.getMatcher(input)).matches()) {
            String answer = matcher.group(1);
            CommandResult result = controllerManager.getLoginController().answer(answer);
        } else if ((matcher = Commands.LOGOUT.getMatcher(input)).matches()) {
            controllerManager.getMainMenuController().logout();
        } else if ((matcher = Commands.ENTER_CHAPTER.getMatcher(input)).matches()) {
            String chapterName = matcher.group(1);
            controllerManager.getGameMenuController().enterChapter(chapterName);
        } else if (Commands.GREENHOUSE.getMatcher(input).matches()) {
            controllerManager.getGameMenuController().greenHouse();
        } else if (Commands.TRAVEL_LOG.getMatcher(input).matches()) {
            controllerManager.getGameMenuController().quest();
        } else if (Commands.LEADERBOARD.getMatcher(input).matches()) {
            controllerManager.getGameMenuController().leaderboard();
        } else if (Commands.COIN_WALLET.getMatcher(input).matches()) {
            controllerManager.getGameMenuController().coin_wallet();
        } else if (Commands.GEM_WALLET.getMatcher(input).matches()) {
            controllerManager.getGameMenuController().gem_wallet();
        } else if ((matcher = Commands.ADD_COIN.getMatcher(input)).matches()) {
            int amount = Integer.parseInt(matcher.group(1));
            controllerManager.getGameMenuController().CHEAT_add_coin(amount);
        } else if ((matcher = Commands.ADD_DIAMOND.getMatcher(input)).matches()) {
            int amount = Integer.parseInt(matcher.group(1));
            controllerManager.getGameMenuController().CHEAT_add_gem(amount);
        } else if ((matcher = Commands.CHANGE_DIFFICULTY.getMatcher(input)).matches()) {
            int level = Integer.parseInt(matcher.group(1));
            controllerManager.getSettingController().changeDifficulty(level);
        } else if (Commands.UNREAD_NEWS.getMatcher(input).matches()) {
            controllerManager.getNewsMenuController().showUnreadNews();
        } else if (Commands.ALL_NEWS.getMatcher(input).matches()) {
            controllerManager.getNewsMenuController().allShowNews();
        } else if ((matcher = Commands.CHANGE_USERNAME.getMatcher(input)).matches()) {
            String username = matcher.group(1);
            // CommandResult result =
            // controllerManager.getProfileController().changeUsername(user , username);
            // current user??
        } else if ((matcher = Commands.CHANGE_PASSWORD.getMatcher(input)).matches()) {
            String password = matcher.group(1);
            String newPassword = matcher.group(2);
            // CommandResult result =
            // controllerManager.getProfileController().changePassword(user,
            // password,newPassword);
        } else if ((matcher = Commands.CHANGE_NICKNAME.getMatcher(input)).matches()) {
            String nickname = matcher.group(1);
            // CommandResult result =
            // controllerManager.getProfileController().changeNickname(user ,nickname);
        } else if ((matcher = Commands.CHANGE_EMAIL.getMatcher(input)).matches()) {
            String email = matcher.group(1);
            // CommandResult result =
            // controllerManager.getProfileController().changeEmail(user , email);
        } else if (Commands.SHOW_PROFILE_INFO.getMatcher(input).matches()) {
            // CommandResult result =
            // controllerManager.getProfileController().Show_info(user);
        } else if (Commands.SHOW_PLANTS.getMatcher(input).matches()) {
            String plants = controllerManager.getCollectionController().ShowPlants();
        } else if (Commands.SHOW_ALL_PLANTS.getMatcher(input).matches()) {
            String plants = controllerManager.getCollectionController().showAllPlants();
        } else if (Commands.SHOW_ZOMBIES.getMatcher(input).matches()) {
            String zombies = controllerManager.getCollectionController().showZombies();
        } else if (Commands.SHOW_ALL_ZOMBIES.getMatcher(input).matches()) {
            String zombies = controllerManager.getCollectionController().showAllZombies();
        } else if ((matcher = Commands.SHOW_PLANT.getMatcher(input)).matches()) {
            String plantName = matcher.group(1);
            String plant = controllerManager.getCollectionController().showPlant(plantName);
        } else if ((matcher = Commands.SHOW_ZOMBIE.getMatcher(input)).matches()) {
            String zombieName = matcher.group(1);
            String zombie = controllerManager.getCollectionController().showZombie(zombieName);
        } else if ((matcher = Commands.UPGRADE_PLANT.getMatcher(input)).matches()) {
            String plantName = matcher.group(1);
            CommandResult result = controllerManager.getCollectionController().upgradePlant(plantName);
        } else if ((matcher = Commands.PURCHASE_PLANT.getMatcher(input)).matches()) {
            String plantName = matcher.group(1);
            CommandResult result = controllerManager.getCollectionController().purchasePlant(plantName);
        } else if (Commands.SHOW_ALL_PLANTS_SELECT.getMatcher(input).matches()) {
            String plants = controllerManager.getPickPlantsController().showAllPlants();
        } else if (Commands.SHOW_AVAILABLE_PLANTS.getMatcher(input).matches()) {
            CommandResult result = controllerManager.getPickPlantsController().showAvailablePlants();
        } else if ((matcher = Commands.ADD_PLANT.getMatcher(input)).matches()) {
            String typeStr = matcher.group(1);
            PlantType type = PlantType.fromName(typeStr);
            CommandResult result = controllerManager.getPickPlantsController().addPlant(type);
        } else if ((matcher = Commands.REMOVE_PLANT.getMatcher(input)).matches()) {
            String typeStr = matcher.group(1);
            PlantType type = PlantType.fromName(typeStr);
            CommandResult result = controllerManager.getPickPlantsController().removePlant(type);
        } else if ((matcher = Commands.BOOST_PLANT.getMatcher(input)).matches()) {
            String typeStr = matcher.group(1);
            PlantType type = PlantType.fromName(typeStr);
            CommandResult result = controllerManager.getPickPlantsController().boostPlant(type);
        } else if (Commands.START_GAME.getMatcher(input).matches()) {
            controllerManager.getPickPlantsController().startGame();
        } else if ((matcher = Commands.ADVANCE_TIME.getMatcher(input)).matches()) {
            int count = Integer.parseInt(matcher.group(1));
            controllerManager.getGameMechanismController().AdvanceTicks(count);
        } else if ((matcher = Commands.COLLECT_SUN.getMatcher(input)).matches()) {
            float x = Float.parseFloat(matcher.group(1));
            float y = Float.parseFloat(matcher.group(2));
            Position pos = new Position(x, y);
            controllerManager.getGameMechanismController().collectSun(pos);
        } else if (Commands.SHOW_SUN_AMOUNT.getMatcher(input).matches()) {
            int sunAmount = controllerManager.getGameMechanismController().showSunAmount();
        } else if ((matcher = Commands.CHEAT_ADD_SUNS.getMatcher(input)).matches()) {
            int amount = Integer.parseInt(matcher.group(1));
            controllerManager.getGameMechanismController().addSun(amount);
        } else if (Commands.RELEASE_NUKE.getMatcher(input).matches()) {
            controllerManager.getGameMechanismController().releaseNuke();
        } else if ((matcher = Commands.PLANT_PLANT.getMatcher(input)).matches()) {
            String plantType = matcher.group(1);
            PlantType type = PlantType.fromName(plantType);
            int x = Integer.parseInt(matcher.group(2));
            int y = Integer.parseInt(matcher.group(3));
            Position pos = new Position(x, y);
            controllerManager.getGameMechanismController().plantPlant(pos, type);
        } else if (Commands.CHEAT_REMOVE_COOLDOWN.getMatcher(input).matches()) {
            controllerManager.getGameMechanismController().removeCooldown();
        } else if ((matcher = Commands.PLUCK_PLANT.getMatcher(input)).matches()) {
            int x = Integer.parseInt(matcher.group(1));
            int y = Integer.parseInt(matcher.group(2));
            Position pos = new Position(x, y);
            controllerManager.getGameMechanismController().pluckPlant(pos);
        } else if ((matcher = Commands.FEED_PLANT.getMatcher(input)).matches()) {
            int x = Integer.parseInt(matcher.group(1));
            int y = Integer.parseInt(matcher.group(2));
            Position pos = new Position(x, y);
            controllerManager.getGameMechanismController().feedPlant(pos);
        } else if (Commands.CHEAT_ADD_PLANT_FOOD.getMatcher(input).matches()) {
            controllerManager.getGameMechanismController().addPlantFood();
        } else if (Commands.SHOW_MAP.getMatcher(input).matches()) {
            controllerManager.getGameMechanismController().showMap();
        } else if (Commands.SHOW_PLANTS_STATUS.getMatcher(input).matches()) {
            controllerManager.getGameMechanismController().showPlantsStatus();
        } else if ((matcher = Commands.SHOW_TILE_STATUS.getMatcher(input)).matches()) {
            int x = Integer.parseInt(matcher.group(1));
            int y = Integer.parseInt(matcher.group(2));
            Position pos = new Position(x, y);
            controllerManager.getGameMechanismController().showTilesStatus(pos);
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
            controllerManager.getGreenhouseController().enterShop();
        } else if (Commands.SHOP_LIST.getMatcher(input).matches()) {
            String list = controllerManager.getShopController().List();
        } else if (Commands.SHOP_DAILY.getMatcher(input).matches()) {
            String dailyList = controllerManager.getShopController().daily();
        } else if ((matcher = Commands.SHOP_BUY.getMatcher(input)).matches()) {
            String itemId = matcher.group(1);
            int quantity = Integer.parseInt(matcher.group(2));
            CommandResult result = controllerManager.getShopController().buy(itemId, quantity);
        } else if ((matcher = Commands.SHOP_BUY_WITH_TYPE.getMatcher(input)).matches()) {
            String itemId = matcher.group(1);
            int quantity = Integer.parseInt(matcher.group(2));
            String plantTypeStr = matcher.group(3);
            PlantType plantType = PlantType.fromName(plantTypeStr);
            CommandResult result = controllerManager.getShopController().buy(itemId, quantity, plantType);
        } else if ((matcher = Commands.TRAVEL_LOG_PAGE.getMatcher(input)).matches()) {
            String pageName = matcher.group(1);
            CommandResult result = controllerManager.getQuestMenuController().enterPage(pageName);
        }
        // leaderboard and minigames...

    }

}
