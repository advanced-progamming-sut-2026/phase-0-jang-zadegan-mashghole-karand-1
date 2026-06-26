package model.service;

import java.util.ArrayList;
import java.util.List;

import model.data.plant.PlantType;
import model.data.wave.LevelConfig;
import model.world.ChapterType;

public class GameNavigationState {

    public enum Phase {
        NONE, CHAPTER, LEVEL, PLANT
    }

    public Phase phase = Phase.NONE;
    public ChapterType selectedChapter;
    public int selectedLevel;
    public LevelConfig pendingLevel;
    public final List<PlantType> selectedPlants = new ArrayList<>();
    public List<ChapterType> unlockedChapters = new ArrayList<>();
    public List<PlantType> unlockedPlants = new ArrayList<>();

    public void reset() {
        phase = Phase.NONE;
        selectedChapter = null;
        selectedLevel = 0;
        pendingLevel = null;
        selectedPlants.clear();
    }
}
