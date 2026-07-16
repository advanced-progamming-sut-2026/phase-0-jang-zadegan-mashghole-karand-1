package model.rule;

import java.util.ArrayList;
import java.util.List;

import model.data.plant.PlantType;
import model.data.wave.LevelConfig;
import model.data.content.specialLevel.SpecialLevelType;
import model.data.content.minigame.MiniGameType;

public class SessionConfig {
    public final boolean isSpecialLevel;
    public final SpecialLevelType specialLevelType; // null if not special
    public final MiniGameType miniGameType; // null if not minigame
    public final List<PlantType> selectedPlants;
    public final LevelConfig levelConfig;
    public final SessionType sessionType;

    public enum SessionType {
        NORMAL,
        SPECIAL,
        MINIGAME
    }

    private SessionConfig(Builder builder) {
        this.isSpecialLevel = builder.isSpecialLevel;
        this.specialLevelType = builder.specialLevelType;
        this.miniGameType = builder.miniGameType;
        this.selectedPlants = builder.selectedPlants;
        this.levelConfig = builder.levelConfig;
        this.sessionType = builder.sessionType;
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean isMinigame() {
        return sessionType == SessionType.MINIGAME;
    }

    public boolean isSpecial() {
        return sessionType == SessionType.SPECIAL;
    }

    public boolean isNormal() {
        return sessionType == SessionType.NORMAL;
    }

    public static class Builder {
        private boolean isSpecialLevel = false;
        private SpecialLevelType specialLevelType;
        private MiniGameType miniGameType;
        private List<PlantType> selectedPlants = new ArrayList<>();
        private LevelConfig levelConfig;
        private SessionType sessionType = SessionType.NORMAL;

        public Builder specialLevel(SpecialLevelType type) {
            this.isSpecialLevel = true;
            this.specialLevelType = type;
            this.sessionType = SessionType.SPECIAL;
            return this;
        }

        public Builder miniGame(MiniGameType type) {
            this.miniGameType = type;
            this.sessionType = SessionType.MINIGAME;
            return this;
        }

        public Builder selectedPlants(List<PlantType> plants) {
            this.selectedPlants = new ArrayList<>(plants);
            return this;
        }

        public Builder levelConfig(LevelConfig config) {
            this.levelConfig = config;
            return this;
        }

        public SessionConfig build() {
            return new SessionConfig(this);
        }
    }
}