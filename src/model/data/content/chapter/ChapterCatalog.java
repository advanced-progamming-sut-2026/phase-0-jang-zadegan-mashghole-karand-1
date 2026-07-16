package model.data.content.chapter;

public final class ChapterCatalog {

    public static final int LEVELS_PER_CHAPTER = 5;

    private ChapterCatalog() {
    }

    public static ChapterType fromCommandName(String name) {
        if (name == null) {
            return null;
        }
        String normalized = name.trim().toLowerCase().replace('_', '-').replace(' ', '-');
        switch (normalized) {
            case "egypt":
            case "ancient-egypt":
            case "ancientegypt":
                return ChapterType.ANCIENT_EGYPT;
            case "frostbite":
            case "frostbite-caves":
            case "frostbitecaves":
                return ChapterType.FROSTBITE_CAVES;
            case "beach":
            case "big-wave-beach":
            case "bigwavebeach":
                return ChapterType.BIG_WAVE_BEACH;
            case "dark-ages":
            case "darkages":
                return ChapterType.DARK_AGES;
            default:
                return null;
        }
    }

    public static String toChapterId(ChapterType type) {
        switch (type) {
            case ANCIENT_EGYPT:
                return "egypt";
            case FROSTBITE_CAVES:
                return "frostbite";
            case BIG_WAVE_BEACH:
                return "beach";
            case DARK_AGES:
                return "darkages";
            default:
                return "egypt";
        }
    }

    public static String displayName(ChapterType type) {
        switch (type) {
            case ANCIENT_EGYPT:
                return "Ancient Egypt";
            case FROSTBITE_CAVES:
                return "Frostbite Caves";
            case BIG_WAVE_BEACH:
                return "Big Wave Beach";
            case DARK_AGES:
                return "Dark Ages";
            default:
                return type.name();
        }
    }

    public static String commandName(ChapterType type) {
        switch (type) {
            case ANCIENT_EGYPT:
                return "ancient-egypt";
            case FROSTBITE_CAVES:
                return "frostbite-caves";
            case BIG_WAVE_BEACH:
                return "big-wave-beach";
            case DARK_AGES:
                return "dark-ages";
            default:
                return type.name().toLowerCase();
        }
    }
}
