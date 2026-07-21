package model.rule.rules.minigame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import model.board.Tile;
import model.core.EventBus;
import model.core.GameState;
import model.data.plant.PlantType;
import model.data.vase.Vase;
import model.data.vase.VaseType;
import model.data.zombie.ZombieType;
import model.rule.LevelRule;
import model.rule.SessionContext;

public class VaseBreakerRules implements LevelRule {
    private static final Random RANDOM = new Random();

    private static final int FIRST_VASE_COL = 3;
    private static final int ZOMBIE_VASE_COUNT = 1;
    private static final int MIN_PLANT_VASES = 3;
    private static final int MAX_PLANT_VASES = 5;

    private static final List<ZombieType> VASE_ZOMBIE_POOL = List.of(
            ZombieType.BASIC,
            ZombieType.CONE_HEAD,
            ZombieType.BUCKET_HEAD,
            ZombieType.IMP,
            ZombieType.NEWSPAPER_ZOMBIE);

    private static final List<PlantType> FALLBACK_PLANT_POOL = List.of(
            PlantType.Sunflower,
            PlantType.PeaShooter,
            PlantType.Wall_nut,
            PlantType.SnowPea);

    private boolean boardReady;

    @Override
    public boolean skipsPlantSelection() {
        return true;
    }

    @Override
    public boolean shouldDropSkySun() {
        return false;
    }

    @Override
    public boolean shouldSpawnWaves() {
        return false;
    }

    @Override
    public boolean lawnMowersEnabled() {
        return false;
    }

    @Override
    public boolean usesSunCurrency() {
        return false;
    }

    @Override
    public void onSessionStart(SessionContext context, GameState state, EventBus bus) {
        boardReady = false;
        placeVases(context, state);
        boardReady = !state.vases.isEmpty();
    }

    @Override
    public boolean canPlant(PlantType type, int row, int col, GameState state, SessionContext context) {
        if (context == null || !context.hasHeldSeed(type)) {
            return false;
        }
        return state.getVaseAt(row, col) == null;
    }

    @Override
    public void postTick(SessionContext context, GameState state, EventBus bus) {
        if (!boardReady || state.gameOver || state.levelComplete) {
            return;
        }
        if (!state.vases.isEmpty()) {
            return;
        }
        boolean zombiesAlive = state.zombies.stream().anyMatch(z -> z.isAlive);
        if (!zombiesAlive) {
            state.levelComplete = true;
        }
    }

    private void placeVases(SessionContext context, GameState state) {
        List<int[]> slots = new ArrayList<>();
        for (int row = 0; row < GameState.GRID_ROWS; row++) {
            for (int col = FIRST_VASE_COL; col < GameState.GRID_COLS; col++) {
                slots.add(new int[] { row, col });
            }
        }
        Collections.shuffle(slots, RANDOM);

        int plantVaseCount = MIN_PLANT_VASES
                + RANDOM.nextInt(MAX_PLANT_VASES - MIN_PLANT_VASES + 1);
        plantVaseCount = Math.min(plantVaseCount, slots.size() - ZOMBIE_VASE_COUNT);

        List<PlantType> plantPool = resolvePlantPool(context);
        int index = 0;

        for (int i = 0; i < ZOMBIE_VASE_COUNT && index < slots.size(); i++, index++) {
            int[] slot = slots.get(index);
            placeVase(state, Vase.zombie(slot[0], slot[1], VaseType.ZOMBIE, ZombieType.GARGANTUAR));
        }

        for (int i = 0; i < plantVaseCount && index < slots.size(); i++, index++) {
            int[] slot = slots.get(index);
            PlantType plant = pickPlant(plantPool);
            placeVase(state, Vase.plantSeed(slot[0], slot[1], VaseType.PLANT, plant));
        }

        while (index < slots.size()) {
            int[] slot = slots.get(index++);
            placeVase(state, createNormalVase(slot[0], slot[1], plantPool));
        }
    }

    private Vase createNormalVase(int row, int col, List<PlantType> plantPool) {
        double roll = RANDOM.nextDouble();
        if (roll < 0.50) {
            return Vase.empty(row, col, VaseType.NORMAL);
        }
        if (roll < 0.75) {
            return Vase.zombie(row, col, VaseType.NORMAL, pickZombie());
        }
        return Vase.plantSeed(row, col, VaseType.NORMAL, pickPlant(plantPool));
    }

    private void placeVase(GameState state, Vase vase) {
        Tile tile = state.getBoard().getTile(vase.row, vase.col);
        if (tile == null || !tile.canSetVase()) {
            return;
        }
        tile.setVase(vase);
        state.vases.add(vase);
    }

    private List<PlantType> resolvePlantPool(SessionContext context) {
        List<PlantType> selected = context.getConfig().selectedPlants;
        if (selected != null && !selected.isEmpty()) {
            List<PlantType> filtered = selected.stream()
                    .filter(p -> p != null && !p.isBowlingExclusive())
                    .toList();
            if (!filtered.isEmpty()) {
                return filtered;
            }
        }
        return FALLBACK_PLANT_POOL;
    }

    private PlantType pickPlant(List<PlantType> plantPool) {
        return plantPool.get(RANDOM.nextInt(plantPool.size()));
    }

    private ZombieType pickZombie() {
        return VASE_ZOMBIE_POOL.get(RANDOM.nextInt(VASE_ZOMBIE_POOL.size()));
    }
}
