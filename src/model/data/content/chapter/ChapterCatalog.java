package model.data.content.chapter;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import model.data.content.specialLevel.SpecialLevelType;
import model.data.wave.LevelConfig;
import model.data.zombie.ZombieType;

public final class ChapterCatalog {

    public static final int LEVELS_PER_CHAPTER = 4;

    private static final List<ZombieType> PLACEHOLDER_ZOMBIES = List.of(
            ZombieType.BASIC, ZombieType.CONE_HEAD);

    private static final Map<ChapterType, ChapterDefinition> CHAPTERS = buildChapters();

    private ChapterCatalog() {
    }

    public static ChapterDefinition getChapter(ChapterType type) {
        return CHAPTERS.get(type);
    }

    public static LevelConfig getLevel(ChapterType chapter, int levelNumber) {
        ChapterDefinition definition = CHAPTERS.get(chapter);
        if (definition == null) {
            return null;
        }
        return definition.getLevel(levelNumber);
    }

    private static Map<ChapterType, ChapterDefinition> buildChapters() {
        Map<ChapterType, ChapterDefinition> map = new EnumMap<>(ChapterType.class);
        map.put(ChapterType.ANCIENT_EGYPT, ancientEgypt());
        map.put(ChapterType.FROSTBITE_CAVES, frostbiteCaves());
        map.put(ChapterType.BIG_WAVE_BEACH, bigWaveBeach());
        map.put(ChapterType.DARK_AGES, darkAges());
        return Collections.unmodifiableMap(map);
    }

    private static ChapterDefinition ancientEgypt() {
        ChapterType chapter = ChapterType.ANCIENT_EGYPT;
        return new ChapterDefinition(chapter, List.of(
                normalLevel(chapter, 1),
                normalLevel(chapter, 2),
                specialLevel(chapter, 3, SpecialLevelType.CONVEYOR_BELT),
                specialLevel(chapter, 4, SpecialLevelType.SAVE_OUR_SEEDS)));
    }

    private static ChapterDefinition frostbiteCaves() {
        ChapterType chapter = ChapterType.FROSTBITE_CAVES;
        return new ChapterDefinition(chapter, List.of(
                normalLevel(chapter, 1),
                normalLevel(chapter, 2),
                specialLevel(chapter, 3, SpecialLevelType.LOVE_YOUR_PLANTS),
                specialLevel(chapter, 4, SpecialLevelType.NIGHT_OPS)));
    }

    private static ChapterDefinition bigWaveBeach() {
        ChapterType chapter = ChapterType.BIG_WAVE_BEACH;
        return new ChapterDefinition(chapter, List.of(
                normalLevel(chapter, 1),
                normalLevel(chapter, 2),
                specialLevel(chapter, 3, SpecialLevelType.NIGHT_OPS),
                specialLevel(chapter, 4, SpecialLevelType.CONVEYOR_BELT)));
    }

    private static ChapterDefinition darkAges() {
        ChapterType chapter = ChapterType.DARK_AGES;
        return new ChapterDefinition(chapter, List.of(
                normalLevel(chapter, 1),
                normalLevel(chapter, 2),
                specialLevel(chapter, 3, SpecialLevelType.SAVE_OUR_SEEDS),
                specialLevel(chapter, 4, SpecialLevelType.LOVE_YOUR_PLANTS)));
    }

    private static LevelConfig normalLevel(ChapterType chapter, int number) {
        return LevelConfig.builder(chapter, number)
                .zombies(PLACEHOLDER_ZOMBIES)
                .build();
    }

    private static LevelConfig specialLevel(ChapterType chapter, int number, SpecialLevelType special) {
        return LevelConfig.builder(chapter, number).zombies(PLACEHOLDER_ZOMBIES).special(special).build();
    }
}
