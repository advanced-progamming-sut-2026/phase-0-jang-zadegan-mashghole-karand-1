package model.rule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import model.core.GameLoop;
import model.data.plant.PlantType;

public class ConveyorState {
    public static final int MAX_BELT_SIZE = 8;

    private static final int OFFER_INTERVAL_TICKS = 8 * GameLoop.TICKS_PER_SECOND;

    private final List<PlantType> availablePlants;
    private final List<PlantType> belt = new ArrayList<>();
    private final Queue<PlantType> pool = new LinkedList<>();
    private int ticksUntilNextOffer = OFFER_INTERVAL_TICKS;
    private boolean isActive = true;
    private int totalOffersMade = 0;

    public ConveyorState(List<PlantType> availablePlants) {
        if (availablePlants == null || availablePlants.isEmpty()) {
            this.availablePlants = new ArrayList<>();
            this.isActive = false;
        } else {
            this.availablePlants = new ArrayList<>(availablePlants);
            shufflePool();
            addPlantToBelt();
        }
        ticksUntilNextOffer = OFFER_INTERVAL_TICKS;
    }

    private void shufflePool() {
        if (availablePlants.isEmpty()) {
            isActive = false;
            return;
        }
        List<PlantType> shuffled = new ArrayList<>(availablePlants);
        Collections.shuffle(shuffled);
        pool.clear();
        pool.addAll(shuffled);
        isActive = true;
    }

    public PlantType tickTimer() {
        if (!isActive) {
            return null;
        }

        ensurePoolHasPlants();
        if (!isActive) {
            return null;
        }

        if (isBeltFull()) {
            return null;
        }

        ticksUntilNextOffer--;
        if (ticksUntilNextOffer > 0) {
            return null;
        }

        ticksUntilNextOffer = OFFER_INTERVAL_TICKS;
        return addPlantToBelt();
    }

    private void ensurePoolHasPlants() {
        if (pool.isEmpty() && !availablePlants.isEmpty()) {
            shufflePool();
        }
        if (pool.isEmpty() && belt.isEmpty()) {
            isActive = false;
        }
    }

    private PlantType addPlantToBelt() {
        if (isBeltFull()) {
            return null;
        }
        if (pool.isEmpty()) {
            if (!availablePlants.isEmpty()) {
                shufflePool();
            }
            if (pool.isEmpty()) {
                isActive = false;
                return null;
            }
        }
        PlantType plant = pool.poll();
        belt.add(plant);
        totalOffersMade++;
        return plant;
    }

    public PlantType getCurrentOffer() {
        return belt.isEmpty() ? null : belt.get(0);
    }

    public PlantType getBeltPlant(int index) {
        if (index < 0 || index >= belt.size()) {
            return null;
        }
        return belt.get(index);
    }

    public void consumeOffer() {
        consumeOfferAt(0);
    }

    public void consumeOfferAt(int index) {
        if (index < 0 || index >= belt.size()) {
            return;
        }
        belt.remove(index);
        if (belt.isEmpty() && pool.isEmpty() && !availablePlants.isEmpty()) {
            shufflePool();
        }
        if (belt.isEmpty() && pool.isEmpty()) {
            isActive = false;
        }
    }

    public boolean hasOffer() {
        return !belt.isEmpty();
    }

    public boolean isBeltFull() {
        return belt.size() >= MAX_BELT_SIZE;
    }

    public int getBeltCount() {
        return belt.size();
    }

    public List<PlantType> getBeltPlants() {
        return List.copyOf(belt);
    }

    public int getRemainingPlants() {
        return pool.size();
    }

    public List<PlantType> getUpcomingQueue() {
        return List.copyOf(pool);
    }

    public int getTicksUntilNextOffer() {
        return ticksUntilNextOffer;
    }

    public int getSecondsUntilNextOffer() {
        return Math.max(0, ticksUntilNextOffer / GameLoop.TICKS_PER_SECOND);
    }

    public int getTotalOffersMade() {
        return totalOffersMade;
    }

    public boolean isActive() {
        return isActive;
    }

    public List<PlantType> getAvailablePlants() {
        return Collections.unmodifiableList(availablePlants);
    }
}
