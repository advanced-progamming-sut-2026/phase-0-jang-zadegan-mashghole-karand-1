package model.rule.rules.specialLevel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import model.core.EventBus;
import model.core.GameState;
import model.core.SessionEnd;
import model.data.plant.Plant;
import model.data.plant.PlantType;
import model.event.events.GameOverReason;
import model.rule.LevelRule;
import model.rule.SessionContext;

public class SaveOurSeedsRules implements LevelRule {
    private static final Random RANDOM = new Random();
    public static final int PROTECTED_COL = 4;
    public static final int[] PROTECTED_ROWS = { 0, 2, 4 };

    private static final List<PlantType> SEED_POOL = Arrays.asList(
            PlantType.Sunflower,
            PlantType.PeaShooter,
            PlantType.Wall_nut,
            PlantType.SnowPea,
            PlantType.Repeater);

    private List<Integer> protectedPlantIds = new ArrayList<>();

    public List<Integer> getProtectedPlantIds() {
        return List.copyOf(protectedPlantIds);
    }

    public int getProtectedCol() {
        return PROTECTED_COL;
    }

    public int getProtectedAliveCount() {
        return protectedPlantIds.size();
    }

    public int getProtectedTotalSlots() {
        return PROTECTED_ROWS.length;
    }

    @Override
    public void onSessionStart(SessionContext context, GameState state, EventBus bus) {
        protectedPlantIds.clear();

        int placed = 0;

        for (int row : PROTECTED_ROWS) {
            if (state.getPlantAt(row, PROTECTED_COL) == null) {
                PlantType type = SEED_POOL.get(RANDOM.nextInt(SEED_POOL.size()));

                if (type != null && state.getBoard().getTile(row, PROTECTED_COL).isPlantable(type)) {
                    Plant plant = new Plant(type, row, PROTECTED_COL, 1, bus);
                    state.addPlant(plant);
                    protectedPlantIds.add(plant.instanceId);
                    placed++;
                }
            }
        }
    }

    @Override
    public void onPlantDied(Plant plant, GameState state, EventBus bus) {
        if (protectedPlantIds.contains(plant.instanceId)) {
            protectedPlantIds.remove(Integer.valueOf(plant.instanceId));
            SessionEnd.lose(state, bus, GameOverReason.PROTECTED_PLANT_DIED);
        }
    }
}
