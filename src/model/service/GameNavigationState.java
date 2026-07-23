package model.service;

import java.util.ArrayList;
import java.util.List;

import model.data.content.chapter.ChapterType;
import model.data.content.minigame.MiniGameType;
import model.data.content.specialLevel.SpecialLevelType;
import model.data.plant.PlantType;
import model.data.wave.LevelConfig;

public class GameNavigationState {

    public enum Phase {
        NONE, CHAPTER, LEVEL, PLANT, MINIGAME
    }

    public Phase phase = Phase.NONE;
    public ChapterType selectedChapter;
    public int selectedLevel;
    public LevelConfig pendingLevel;
    public SpecialLevelType pendingSpecialLevel;
    public MiniGameType pendingMiniGame;
    public final List<PlantType> selectedPlants = new ArrayList<>();
    public List<ChapterType> unlockedChapters = new ArrayList<>();
    public List<PlantType> unlockedPlants = new ArrayList<>();
    public List<MiniGameType> unlockedMinigames = new ArrayList<>();
    public PlantType imitatorTarget;

    public void reset() {
        phase = Phase.NONE;
        selectedChapter = null;
        selectedLevel = 0;
        pendingLevel = null;
        pendingSpecialLevel = null;
        pendingMiniGame = null;
        selectedPlants.clear();
        imitatorTarget = null;
    }
}
