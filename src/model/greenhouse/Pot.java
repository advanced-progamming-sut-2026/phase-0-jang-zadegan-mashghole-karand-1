package model.greenhouse;

import model.core.Position;
import model.data.plant.PlantType;

import java.time.Duration;
import java.time.LocalDateTime;

public class Pot {
    public enum PlantClass{
        NORMAL_PLANT(2),
        UNLOCK_PLANT(8);
        private  int hour;

        PlantClass(int hour) {
            this.hour = hour;
        }

        public int getHour() {
            return hour;
        }
    }
    private Position position;
    private boolean locked;
    private boolean empty;
    private PlantClass plantClass;
    private LocalDateTime plantedAt;
    private PlantType plantType;
    public Pot(Position position, boolean locked) {
        this.position = position;
        this.locked = locked;
        this.empty = true;
    }
    public boolean plant(PlantClass plantClass , PlantType type) {
        if (locked || !empty) return false;
        this.plantClass = plantClass;
        this.plantType = type;
        this.plantedAt = LocalDateTime.now();
        this.empty = false;
        setFull();
        return true;
    }
    public void clear(){
        this.empty = true;
        this.plantedAt = null;
        this.plantType = null;
        this.plantClass = null;

    }
    public boolean isReady() {
        if (empty || plantedAt == null) return false;
        LocalDateTime readyAt = plantedAt.plusHours(plantClass.getHour());
        return !readyAt.isAfter(LocalDateTime.now());
    }

    public long getRemainingHours() {
        if (empty || plantedAt == null || plantClass == null) return 0;
        if (isReady()) return 0;
        LocalDateTime readyAt = plantedAt.plusHours(plantClass.getHour());
        Duration left = Duration.between(LocalDateTime.now(), readyAt);
        long seconds = Math.max(0, left.getSeconds());
        return (long) Math.ceil(seconds / 3600.0);
    }
    public void forceReady() {
        if (empty || plantClass == null) return;
        this.plantedAt = LocalDateTime.now().minusHours(plantClass.getHour());
    }
    public String getRemainingTimeText() {
        if (empty || plantedAt == null || plantClass == null) return "";
        if (isReady()) return "READY";

        LocalDateTime readyAt = plantedAt.plusHours(plantClass.getHour());
        Duration left = Duration.between(LocalDateTime.now(), readyAt);
        long totalMinutes = Math.max(0, left.toMinutes());

        long hours = totalMinutes / 60;
        long minutes = totalMinutes % 60;

        if (hours > 0) return hours + "h " + minutes + "m";
        return minutes + "m";
    }
    public Position getPosition() {
        return position;
    }

    public boolean isLocked() {
        return locked;
    }

    public boolean isEmpty() {
        return empty;
    }

    public void setFull() {
         this.empty = false;
    }

    public void setUnlocked() {
        this.locked = false;
    }

    public PlantClass getPlantClass() {
        return plantClass;
    }

    public PlantType getPlantType() {
        return plantType;
    }

    public void setPlantedAt(LocalDateTime plantedAt) {
        this.plantedAt = plantedAt;
    }

    public LocalDateTime getPlantedAt() {
        return plantedAt;
    }
}
