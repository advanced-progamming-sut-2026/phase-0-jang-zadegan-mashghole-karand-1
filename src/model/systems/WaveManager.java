package model.systems;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import model.core.EventBus;
import model.core.GameState;
import model.core.Position;
import model.data.pool.ZombiePool;
import model.data.wave.LevelConfig;
import model.data.wave.ZombieSpawn;
import model.data.wave.ZombieWave;
import model.data.zombie.Zombie;
import model.data.zombie.ZombieType;
import model.events.WaveCompleteEvent;
import model.events.WaveStartedEvent;
import model.events.ZombieSpawnedEvent;

public class WaveManager {
    private static final int SPAWN_INTERVAL_TICKS = 30; // 3 seconds between spawns

    private LevelConfig levelConfig;
    private ZombiePool zombiePool;

    private int totalWaves = 0;
    private boolean finalWave = false;
    private boolean waveActive = false;

    private Set<Integer> currentWaveZombieIds = new HashSet<>();
    private int totalZombiesInWave = 0;
    private int zombiesSpawnedInWave = 0;

    private Queue<ZombieSpawn> pendingSpawns = new LinkedList<>();
    private int ticksUntilNextSpawn = 0;

    public void initialize(LevelConfig config) {
        this.levelConfig = config;
        this.totalWaves = config.totalWaves;
        this.zombiePool = ZombiePool.forChapter(config.chapterType);
        this.waveActive = false;
        clearWaveTracking();
    }

    public void update(GameState state, EventBus eventBus) {
        if (state.isGameOver())
            return;
        if (levelConfig == null)
            return;

        if (!waveActive && state.getCurrentWave() < totalWaves) {
            startNextWave(state, eventBus);
            return;
        }

        if (waveActive) {
            if (!pendingSpawns.isEmpty()) {
                ticksUntilNextSpawn--;
                if (ticksUntilNextSpawn <= 0) {
                    spawnNextZombie(state, eventBus);
                    ticksUntilNextSpawn = SPAWN_INTERVAL_TICKS;
                }
            }

            if (shouldStartNextWave(state)) {
                if (state.getCurrentWave() >= totalWaves) {
                    waveActive = false;
                    eventBus.publish(new WaveCompleteEvent(state.getCurrentWave(), true));
                } else {
                    eventBus.publish(new WaveCompleteEvent(state.getCurrentWave(), false));
                    startNextWave(state, eventBus);
                }
            }
        }
    }

    private void startNextWave(GameState state, EventBus eventBus) {
        state.incrementCurrentWave();
        finalWave = state.getCurrentWave() == totalWaves;
        waveActive = true;

        clearWaveTracking();

        int budget = calculateWaveBudget(state.getCurrentWave());

        ZombieWave wave = buildWaveFromBudget(budget);
        totalZombiesInWave = wave.getTotalZombies();

        pendingSpawns.clear();
        for (ZombieSpawn spawn : wave.getSpawns()) {
            pendingSpawns.offer(spawn);
        }
        ticksUntilNextSpawn = SPAWN_INTERVAL_TICKS / 2;// faster first batch

        eventBus.publish(new WaveStartedEvent(state.getCurrentWave(), totalZombiesInWave, finalWave));
    }

    private void clearWaveTracking() {
        currentWaveZombieIds.clear();
        totalZombiesInWave = 0;
        zombiesSpawnedInWave = 0;
    }

    private void spawnNextZombie(GameState state, EventBus eventBus) {
        if (pendingSpawns.isEmpty())
            return;

        ZombieSpawn spawn = pendingSpawns.peek();
        if (spawn == null)
            return;

        int row = (int) (Math.random() * GameState.GRID_ROWS);
        Zombie zombie = new Zombie(spawn.type, row, GameState.GRID_COLS - 1,
                new Position(GameState.SCREEN_WIDTH, GameState.CELL_HEIGHT * row + (GameState.CELL_HEIGHT / 2)),
                eventBus);
        state.addZombie(zombie);
        eventBus.publish(new ZombieSpawnedEvent(zombie));
        currentWaveZombieIds.add(zombie.instanceId);
        zombiesSpawnedInWave++;

        spawn.count--;
        if (spawn.count <= 0) {
            pendingSpawns.poll();
        }
    }

    private int calculateWaveBudget(int waveNumber) {
        int baseBudget = levelConfig.wavePointBase;

        float multiplier = finalWave ? 2.0f : (float) Math.pow(1.25, waveNumber - 1);

        return (int) (baseBudget * multiplier);
    }

    private ZombieWave buildWaveFromBudget(int budget) {
        List<ZombieSpawn> spawns = new ArrayList<>();
        int remainingBudget = budget;

        while (remainingBudget >= 0) {
            ZombieType type = zombiePool.getRandomZombie(remainingBudget);

            if (type == null) {
                ZombieType cheapest = zombiePool.getCheapestZombie();
                if (cheapest != null) {
                    spawns.add(new ZombieSpawn(cheapest, 1));
                    remainingBudget = 0;
                }
                break;
            }

            int cost = zombiePool.getCost(type);

            int maxCount = Math.min(3, remainingBudget / cost);
            int count = 1 + (int) (Math.random() * Math.min(2, maxCount));

            spawns.add(new ZombieSpawn(type, count));
            remainingBudget -= cost * count;
        }

        if (spawns.isEmpty() && !zombiePool.isEmpty()) {
            ZombieType defaultType = zombiePool.getCheapestZombie();
            if (defaultType != null) {
                spawns.add(new ZombieSpawn(defaultType, 1));
            }
        }

        return new ZombieWave(spawns);
    }

    public boolean shouldStartNextWave(GameState state) {
        boolean allSpawned = zombiesSpawnedInWave >= totalZombiesInWave;
        if (!allSpawned)
            return false;

        if (!waveActive)
            return false;

        if (totalZombiesInWave == 0)
            return true;

        int totalHp = 0;
        int currentHp = 0;

        for (Zombie zombie : state.getZombies()) {
            if (currentWaveZombieIds.contains(zombie.instanceId)) {
                totalHp += zombie.totalHp;
                currentHp += zombie.hp;
            }
        }
        if (totalHp == 0) {
            return true;
        }

        return (float) currentHp / (float) totalHp < 0.25f;
    }

    public void spawnIcedZombies(int row, GameState state, EventBus eventBus) {
        int col = 3+ (int) (Math.random() * 6);

        int budget = calculateWaveBudget(1);

        ZombieType type = zombiePool != null ? zombiePool.getRandomZombie(budget): null;
        if (type == null) type = ZombieType.BASIC;

        Zombie zombie = new Zombie(type,row,col,new Position(
                col * GameState.CELL_WIDTH + GameState.CELL_WIDTH / 2f,
                row * GameState.CELL_HEIGHT + GameState.CELL_HEIGHT / 2f),eventBus);
        zombie.ice();
        state.addZombie(zombie);
        eventBus.publish(new ZombieSpawnedEvent(zombie));

    }

    public int getTotalWaves() {
        return totalWaves;
    }

    public boolean isFinalWave() {
        return finalWave;
    }

    public boolean isWaveActive() {
        return waveActive;
    }
}