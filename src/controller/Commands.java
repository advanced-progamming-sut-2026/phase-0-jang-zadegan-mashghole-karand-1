package controller;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum Commands {
    CHANGE_MENU("menu\\s+enter\\s+(?<menu_name>.*)$"),
    SHOW_MENU("menu\\s+show\\s+current"),
    EXIT_MENU("menu\\s+exit"),

    //register
    REGISTER_USER("register\\s+-u\\s+(?<username>.*?)\\s+-p\\s+(?<password>.*)\\s+(?<passwordconfirm>.*?)\\s+-n\\s+(?<nickname>.*?)\\s+-e\\s+(?<email>.*?)\\s+-g\\s+(?<gender>.*)$"),
    PICK_QUESTION("pick\\s+question\\s+-q\\s+(?<questionnumber>.*?)\\s+-a\\s+(?<answer>.*?)\\s+-c\\s+(?<answer_confirm>.*)$"),

    //login
    LOGIN("login\\s+-u\\s+(?<username>.*?)\\s+-p\\s+(?<password>.*)$"),
    LOGIN_STAY_LOGGED_IN("login\\s+-u\\s+(?<username>.*?)\\s+-p\\s+(?<password>.*?)\\s+-stay-logged-in"),
    FORGET_PASS("forget\\s+password\\s+-u\\s+(?<username>.*?)\\s+-e\\s+(?<email>.*)$"),
    ANSWER("answer\\s+-a\\s+(?<answer>.*)$"),

    //main menu
    LOGOUT("menu\\s+logout"),


    //game menu
    ENTER_CHAPTER("menu\\s+enter\\s+chapter\\s+-c\\s+(?<chaptername>.*)"),
    GREENHOUSE("menu\\s+greenhouse"),
    TRAVEL_LOG("menu\\s+travel-log"),
    LEADERBOARD("menu\\s+leaderboard"),
    COIN_WALLET("menu\\s+coin-wallet"),
    GEM_WALLET("menu\\s+gem-wallet"),
    ADD_COIN("menu\\s+cheat\\s+add\\s+(?<n>\\d+)\\s+coin"),
    ADD_DIAMOND("menu\\s+cheat\\s+add\\s+(?<n>\\d+)\\s+diamond"),

    //setting menu
    CHANGE_DIFFICULTY("menu\\s+settings\\s+change-difficulty\\s+-l\\s+(?<difficulty_level>.*)$"),

    //news menu
    UNREAD_NEWS("menu\\s+news\\s+show-unread"),
    ALL_NEWS("menu\\s+news\\s+show-all"),

    //profile menu
    CHANGE_USERNAME("menu\\s+profile\\s+change-username\\s+-u\\s+(?<username>.*)$"),
    CHANGE_NICKNAME("menu\\s+profile\\s+change-nickname\\s+-u\\s+(?<nickname>.*)$"),
    CHANGE_EMAIL("menu profile change-email\\s+-e\\s+(?<email>.*)$"),
    CHANGE_PASSWORD("menu\\s+profile\\s+change-password\\s+-p\\s+(?<new_password>.*?)\\s+-o\\s+(?<old_password>.*)$"),
    SHOW_PROFILE_INFO("menu\\s+profile\\s+show-info"),


    //collection
    SHOW_PLANTS("menu\\s+collection\\s+show-plants"),
    SHOW_ALL_PLANTS("menu\\s+collection\\s+show-all-plants"),
    SHOW_ZOMBIES("menu\\s+collection\\s+show-zombies"),
    SHOW_ALL_ZOMBIES("menu\\s+collection\\s+show-all-zombies"),
    SHOW_PLANT("menu\\s+collection\\s+show-plant\\s+-p\\s+(?<plant_name>.*)$"),
    SHOW_ZOMBIE("menu\\s+collection\\s+show-zombie\\s+-z\\s+(?<zombie_name>.*)$"),
    UPGRADE_PLANT("menu\\s+collection\\s+upgrade-plant\\s+-p\\s+(?<plant_name>.*)$"),
    PURCHASE_PLANT("menu\\s+collection\\s+purchase-plant\\s+-p\\s+(?<plant_name>.*)$"),

    //plant selection menu
    SHOW_ALL_PLANTS_SELECT("show\\s+all\\s+plants"),
    SHOW_AVAILABLE_PLANTS("show\\s+available\\s+plants"),
    ADD_PLANT("add\\s+plant\\s+-t\\s+(?<type>.*)$"),
    REMOVE_PLANT("remove\\s+plant\\s+-t\\s+(?<type>.*)$"),
    BOOST_PLANT("boost\\s+plant\\s+-t\\s+(?<type>.*)$"),
    START_GAME("start\\s+game"),

    //main game mechanics
    ADVANCE_TIME("advance\\s+time\\s+-t\\s+(?<count>\\d+)\\s+ticks"),
    COLLECT_SUN("collect\\s+sun\\s+-l\\s*\\(\\s*(?<x>\\d+)\\s*,\\s*(?<y>\\d+)\\s*\\)"),
    SHOW_SUN_AMOUNT("show\\s+sun\\s+amount"),
    CHEAT_ADD_SUNS("cheat\\s+add\\s+-n\\s+(?<count>\\d+)\\s+suns"),
    RELEASE_NUKE("release\\s+the\\s+nuke"),
    PLANT_PLANT("plant\\s+plant\\s+-t\\s+(?<type>.*?)\\s+-l\\s*\\(\\s*(?<x>\\d+)\\s*,\\s*(?<y>\\d+)\\s*\\)"),
    CHEAT_REMOVE_COOLDOWN("cheat\\s+remove-cooldown"),
    PLUCK_PLANT("pluck\\s+plant\\s+-l\\s*\\(\\s*(?<x>\\d+)\\s*,\\s*(?<y>\\d+)\\s*\\)"),
    FEED_PLANT("feed\\s+plant\\s+-l\\s*\\(\\s*(?<x>\\d+)\\s*,\\s*(?<y>\\d+)\\s*\\)"),
    CHEAT_ADD_PLANT_FOOD("cheat\\s+add-plant-food"),
    SHOW_MAP("show\\s+map"),
    SHOW_PLANTS_STATUS("show\\s+plants\\s+status"),
    SHOW_TILE_STATUS("show\\s+tile\\s+status\\s+-l\\s*\\(\\s*(?<x>\\d+)\\s*,\\s*(?<y>\\d+)\\s*\\)"),

    //zombie commands
    ZOMBIES_INFO("zombies\\s+info"),
    CHEAT_SPAWN_ZOMBIE("cheat\\s+spawn-zombie\\s+-t\\s+(?<zombie_type>.*?)\\s+-l\\s*(?<x>\\d+)\\s*,\\s*(?<y>\\d+)"),

    //special level: get what you plant
    START_ZOMBIE_WAVES("start\\s+zombie\\s+waves"),

    //greenhouse
    SHOW_GREENHOUSE("show\\s+greenhouse"),
    PLANT_POT("plant\\s+pot\\s+at\\s*\\(\\s*(?<x>\\d+)\\s*,\\s*(?<y>\\d+)\\s*\\)"),
    COLLECT_POT("collect\\s*\\(\\s*(?<x>\\d+)\\s*,\\s*(?<y>\\d+)\\s*\\)"),
    GROW_POT("grow\\s*\\(\\s*(?<x>\\d+)\\s*,\\s*(?<y>\\d+)\\s*\\)"),

    //shop
    ENTER_SHOP("enter\\s+shop"),
    SHOP_LIST("shop\\s+list"),
    SHOP_DAILY("shop\\s+daily"),
    SHOP_BUY("shop\\s+buy\\s+-i\\s+(?<item_id>.*?)\\s+-n\\s+(?<count>\\d+)$"),
    SHOP_BUY_WITH_TYPE("shop\\s+buy\\s+-i\\s+(?<item_id>.*?)\\s+-n\\s+(?<count>\\d+)\\s+-t\\s+(?<plant_type>.*)$"),

    //quests / travel log
    TRAVEL_LOG_PAGE("travel\\s+log\\s+page\\s+(?<page_name>.*)$");

    private final String regex;
    private final Pattern pattern;
    Commands(String regex){
        this.regex =regex;
        this.pattern = Pattern.compile(regex);
    }

    public String getRegex() {
        return regex;
    }

    public Matcher getMatcher(String input){
        return this.pattern.matcher(input);
    }
}
