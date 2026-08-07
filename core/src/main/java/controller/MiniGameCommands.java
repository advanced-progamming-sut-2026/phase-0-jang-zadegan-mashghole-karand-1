package controller;

import model.data.content.minigame.MiniGameType;

public final class MiniGameCommands {

    private MiniGameCommands() {
    }

    public static MiniGameType fromCommandName(String name) {
        if (name == null) {
            return null;
        }
        String normalized = name.trim().toLowerCase().replace('_', '-').replace(' ', '-');
        return switch (normalized) {
            case "vase-breaker", "vasebreaker", "vase" -> MiniGameType.VASE_BREAKER;
            case "wallnut-bowling", "wall-nut-bowling", "bowling" -> MiniGameType.WALLNUT_BOWLING;
            case "i-zombie", "izombie", "i,zombie" -> MiniGameType.I_ZOMBIE;
            case "beghouled" -> MiniGameType.BEGHOULED;
            case "zombotany" -> MiniGameType.ZOMBOTANY;
            default -> null;
        };
    }

    public static String commandName(MiniGameType type) {
        if (type == null) {
            return "";
        }
        return switch (type) {
            case VASE_BREAKER -> "vase-breaker";
            case WALLNUT_BOWLING -> "wallnut-bowling";
            case I_ZOMBIE -> "i-zombie";
            case BEGHOULED -> "beghouled";
            case ZOMBOTANY -> "zombotany";
        };
    }

    public static String displayName(MiniGameType type) {
        if (type == null) {
            return "";
        }
        return switch (type) {
            case VASE_BREAKER -> "Vase Breaker";
            case WALLNUT_BOWLING -> "Wall-nut Bowling";
            case I_ZOMBIE -> "I, Zombie";
            case BEGHOULED -> "Beghouled";
            case ZOMBOTANY -> "Zombotany";
        };
    }
}
