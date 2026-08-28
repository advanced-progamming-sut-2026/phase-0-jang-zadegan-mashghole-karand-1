package view.gdx.lawn;

import java.util.EnumMap;
import java.util.Map;

import model.data.content.chapter.ChapterType;
import model.data.plant.PlantType;

public final class SeedPacketDefs {
    private static final Map<PlantType, String> PACKET_IDS = createPacketIds();

    public SeedPacketDefs() {
    }

    public static String worldBack(ChapterType chapter) {
        if (chapter == null) {
            return "IMAGE_UI_PACKETS_EGYPT";
        }
        return switch (chapter) {
            case ANCIENT_EGYPT -> "IMAGE_UI_PACKETS_EGYPT";
            case FROSTBITE_CAVES -> "IMAGE_UI_PACKETS_ICEAGE";
            case BIG_WAVE_BEACH -> "IMAGE_UI_PACKETS_BEACH";
            case DARK_AGES -> "IMAGE_UI_PACKETS_DARK";
        };
    }

   public static String packetId(PlantType type) {
        if (type == null) {
            return null;
        }
        return PACKET_IDS.get(type);
    }

   public static String packetId(String plantName) {
        return packetId(PlantType.fromName(plantName));
    }

    public static final String EMPTY = "IMAGE_UI_PACKETS_EMPTY_PACKET";
    public static final String COOLDOWN = "IMAGE_UI_PACKETS_COOLDOWN";
    public static final String READY = "IMAGE_UI_PACKETS_READY";
    public static final String SELECT = "IMAGE_UI_PACKETS_SELECT";
    public static final String PRICE_TAB = "IMAGE_UI_PACKETS_PRICE_TAB";
    public static final String CONVEYOR_BELT = "IMAGE_UI_CONVEYOR_CONVEYOR_BELT";
    public static final String CONVEYOR_SIDE = "IMAGE_UI_CONVEYOR_CONVEYOR_SIDE";
    public static final String CONVEYOR_TOP = "IMAGE_UI_CONVEYOR_CONVEYOR_TOP";

    public static Map<PlantType, String> createPacketIds() {
        Map<PlantType, String> map = new EnumMap<>(PlantType.class);
        map.put(PlantType.Sunflower, "IMAGE_UI_PACKETS_SUNFLOWER");
        map.put(PlantType.TwinSunflower, "IMAGE_UI_PACKETS_TWINSUNFLOWER");
        map.put(PlantType.SunShroom, "IMAGE_UI_PACKETS_SUNSHROOM");
        map.put(PlantType.PrimalSunflower, "IMAGE_UI_PACKETS_PRIMALSUNFLOWER");
        map.put(PlantType.GoldBloom, "IMAGE_UI_PACKETS_GOLDBLOOM");
        map.put(PlantType.PeaShooter, "IMAGE_UI_PACKETS_PEASHOOTER");
        map.put(PlantType.Repeater, "IMAGE_UI_PACKETS_REPEATER");
        map.put(PlantType.Threepeater, "IMAGE_UI_PACKETS_THREEPEATER");
        map.put(PlantType.SnowPea, "IMAGE_UI_PACKETS_SNOWPEA");
        map.put(PlantType.Rotobaga, "IMAGE_UI_PACKETS_XSHOT");
        map.put(PlantType.SplitPea, "IMAGE_UI_PACKETS_SPLITPEA");
        map.put(PlantType.Citron, "IMAGE_UI_PACKETS_CITRON");
        map.put(PlantType.Caulipower, "IMAGE_UI_PACKETS_CAULIPOWER");
        map.put(PlantType.Electric_Blueberry, "IMAGE_UI_PACKETS_ELECTRICBLUEBERRY");
        map.put(PlantType.BowlingBulb, "IMAGE_UI_PACKETS_BOWLINGBULB");
        map.put(PlantType.Cactus, "IMAGE_UI_PACKETS_CACTUS");
        map.put(PlantType.FirePeashooter, "IMAGE_UI_PACKETS_FIREPEASHOOTER");
        map.put(PlantType.Starfruit, "IMAGE_UI_PACKETS_STARFRUIT");
        map.put(PlantType.GooPeashooter, "IMAGE_UI_PACKETS_POISONPEASHOOTER");
        map.put(PlantType.MegaGatlingPea, "IMAGE_UI_PACKETS_MEGAGATLING");
        map.put(PlantType.SeaShroom, "IMAGE_UI_PACKETS_SEASHROOM");
        map.put(PlantType.PuffShroom, "IMAGE_UI_PACKETS_PUFFSHROOM");
        map.put(PlantType.Fume_shroom, "IMAGE_UI_PACKETS_FUMESHROOM");
        map.put(PlantType.Cabbage_pult, "IMAGE_UI_PACKETS_CABBAGEPULT");
        map.put(PlantType.Kernel_pult, "IMAGE_UI_PACKETS_KERNELPULT");
        map.put(PlantType.Melon_pult, "IMAGE_UI_PACKETS_MELONPULT");
        map.put(PlantType.Winter_Melon, "IMAGE_UI_PACKETS_WINTERMELON");
        map.put(PlantType.Pepper_pult, "IMAGE_UI_PACKETS_PEPPERPULT");
        map.put(PlantType.Potato_Mine, "IMAGE_UI_PACKETS_POTATOMINE");
        map.put(PlantType.Primal_Potato_Mine, "IMAGE_UI_PACKETS_PRIMALPOTATOMINE");
        map.put(PlantType.Cherry_Bomb, "IMAGE_UI_PACKETS_CHERRY_BOMB");
        map.put(PlantType.Squash, "IMAGE_UI_PACKETS_SQUASH");
        map.put(PlantType.Jalapeno, "IMAGE_UI_PACKETS_JALAPENO");
        map.put(PlantType.Doom_shroom, "IMAGE_UI_PACKETS_DOOMSHROOM");
        map.put(PlantType.Tangle_Kelp, "IMAGE_UI_PACKETS_TANGLEKELP");
        map.put(PlantType.Iceberg_Lettuce, "IMAGE_UI_PACKETS_ICEBURG");
        map.put(PlantType.Bonk_Choy, "IMAGE_UI_PACKETS_BONKCHOY");
        map.put(PlantType.Phat_Beet, "IMAGE_UI_PACKETS_PHATBEET");
        map.put(PlantType.Chomper, "IMAGE_UI_PACKETS_CHOMPER");
        map.put(PlantType.Wasabi_Whip, "IMAGE_UI_PACKETS_WASABIWHIP");
        map.put(PlantType.Wall_nut, "IMAGE_UI_PACKETS_WALLNUT");
        map.put(PlantType.Tall_nut, "IMAGE_UI_PACKETS_TALLNUT");
        map.put(PlantType.Endurian, "IMAGE_UI_PACKETS_ENDURIAN");
        map.put(PlantType.Garlic, "IMAGE_UI_PACKETS_GARLIC");
        map.put(PlantType.Sweet_Potato, "IMAGE_UI_PACKETS_SWEETPOTATO");
        map.put(PlantType.Explode_o_nut, "IMAGE_UI_PACKETS_EXPLODEONUT");
        map.put(PlantType.Pumpkin, "IMAGE_UI_PACKETS_PUMPKIN");
        map.put(PlantType.Sun_Bean, "IMAGE_UI_PACKETS_SUNBEAN");
        map.put(PlantType.Torchwood, "IMAGE_UI_PACKETS_TORCHWOOD");
        map.put(PlantType.Magnet_shroom, "IMAGE_UI_PACKETS_MAGNETSHROOM");
        map.put(PlantType.Hypno_shroom, "IMAGE_UI_PACKETS_HYPNOSHROOM");
        map.put(PlantType.Cat_tail, "IMAGE_UI_PACKETS_LIGHTNINGREED");
        map.put(PlantType.Imitater, "IMAGE_UI_PACKETS_IMITATER");
        map.put(PlantType.Ice_shroom, "IMAGE_UI_PACKETS_ICESHROOM");
        map.put(PlantType.Lily_Pad, "IMAGE_UI_PACKETS_LILYPAD");
        map.put(PlantType.Hot_Potato, "IMAGE_UI_PACKETS_HOTPOTATO");
        map.put(PlantType.Grave_Buster, "IMAGE_UI_PACKETS_GRAVEBUSTER");
        map.put(PlantType.Enlighten_mint, "IMAGE_UI_PACKETS_ENLIGHTENMINT");
        map.put(PlantType.Appease_mint, "IMAGE_UI_PACKETS_APPEASEMINT");
        map.put(PlantType.Arma_mint, "IMAGE_UI_PACKETS_ARMAMINT");
        map.put(PlantType.Bombard_mint, "IMAGE_UI_PACKETS_SPEARMINT");
        map.put(PlantType.Enforce_mint, "IMAGE_UI_PACKETS_ENFORCEMINT");
        map.put(PlantType.Reinforce_mint, "IMAGE_UI_PACKETS_REINFORCEMINT");
        map.put(PlantType.Enchant_mint, "IMAGE_UI_PACKETS_CONTAINMINT");
        map.put(PlantType.Pierce_mint, "IMAGE_UI_PACKETS_PEPPERMINT");
        map.put(PlantType.catTail_mint, "IMAGE_UI_PACKETS_AILMINT");
        map.put(PlantType.Bowling_Wall_nut, "IMAGE_UI_PACKETS_WALLNUT");
        map.put(PlantType.Bowling_Explode_o_nut, "IMAGE_UI_PACKETS_EXPLODEONUT");
        map.put(PlantType.Giant_Bowling_Wall_nut, "IMAGE_UI_PACKETS_WALLNUT");
        return map;
    }
}
