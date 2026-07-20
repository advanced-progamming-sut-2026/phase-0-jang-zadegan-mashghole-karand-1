package controller;

import model.data.content.chapter.ChapterType;

public final class ChapterCommands {

    private ChapterCommands() {
    }

    public static ChapterType fromCommandName(String name) {
        if (name == null) {
            return null;
        }
        String normalized = name.trim().toLowerCase().replace('_', '-').replace(' ', '-');
        return switch (normalized) {
            case "egypt", "ancient-egypt", "ancientegypt" -> ChapterType.ANCIENT_EGYPT;
            case "frostbite", "frostbite-caves", "frostbitecaves" -> ChapterType.FROSTBITE_CAVES;
            case "beach", "big-wave-beach", "bigwavebeach" -> ChapterType.BIG_WAVE_BEACH;
            case "dark-ages", "darkages" -> ChapterType.DARK_AGES;
            default -> null;
        };
    }

    public static String commandName(ChapterType type) {
        if (type == null) {
            return "";
        }
        return switch (type) {
            case ANCIENT_EGYPT -> "ancient-egypt";
            case FROSTBITE_CAVES -> "frostbite-caves";
            case BIG_WAVE_BEACH -> "big-wave-beach";
            case DARK_AGES -> "dark-ages";
        };
    }

    public static String displayName(ChapterType type) {
        if (type == null) {
            return "";
        }
        return switch (type) {
            case ANCIENT_EGYPT -> "Ancient Egypt";
            case FROSTBITE_CAVES -> "Frostbite Caves";
            case BIG_WAVE_BEACH -> "Big Wave Beach";
            case DARK_AGES -> "Dark Ages";
        };
    }
}
