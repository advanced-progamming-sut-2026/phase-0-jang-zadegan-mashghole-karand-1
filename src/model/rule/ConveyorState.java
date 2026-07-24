package model.rule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import model.core.GameLoop;
import model.data.plant.PlantType;

public class ConveyorState {
    private static final int OFFER_INTERVAL_TICKS = 12 * GameLoop.TICKS_PER_SECOND;

    private final List<PlantType> availablePlants;
    private final Queue<PlantType> queue = new LinkedList<>();
    private int ticksUntilNextOffer = OFFER_INTERVAL_TICKS;
    private PlantType currentOffer = null;
    private boolean isActive = true;
    private int totalOffersMade = 0;

    public ConveyorState(List<PlantType> availablePlants) {
        if (availablePlants == null || availablePlants.isEmpty()) {
            this.availablePlants = new ArrayList<>();
            this.isActive = false;
        } else {
            this.availablePlants = new ArrayList<>(availablePlants);
            shuffleQueue();
        }
    }

    private void shuffleQueue() {
        if (availablePlants.isEmpty()) {
            isActive = false;
            return;
        }
        List<PlantType> shuffled = new ArrayList<>(availablePlants);
        Collections.shuffle(shuffled);
        queue.clear();
        queue.addAll(shuffled);
        isActive = true;
    }

    public PlantType tickTimer() {
        if (!isActive) {
            return null;
        }

        if (queue.isEmpty() && currentOffer == null) {
            if (!availablePlants.isEmpty()) {
                shuffleQueue();
            }
            return null;
        }

        ticksUntilNextOffer--;
        if (ticksUntilNextOffer > 0) {
            return null;
        }

        if (queue.isEmpty() && !availablePlants.isEmpty()) {
            shuffleQueue();
        }

        if (!queue.isEmpty()) {
            currentOffer = queue.poll();
            totalOffersMade++;
        } else {
            currentOffer = null;
            isActive = false;
        }

        ticksUntilNextOffer = OFFER_INTERVAL_TICKS;
        return currentOffer;
    }

    public PlantType getCurrentOffer() {
        return currentOffer;
    }

    public void consumeOffer() {
        currentOffer = null;
        ticksUntilNextOffer = OFFER_INTERVAL_TICKS / 2;
    }

    public boolean hasOffer() {
        return currentOffer != null;
    }

    public int getRemainingPlants() {
        return queue.size();
    }

    public List<PlantType> getUpcomingQueue() {
        return List.copyOf(queue);
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
