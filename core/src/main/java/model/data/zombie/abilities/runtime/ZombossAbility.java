package model.data.zombie.abilities.runtime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import model.board.Tile;
import model.board.TileType;
import model.core.EventBus;
import model.core.GameLoop;
import model.core.GameState;
import model.core.Position;
import model.core.SessionEnd;
import model.data.Grave.Grave;
import model.data.Grave.GraveContent;
import model.data.plant.Plant;
import model.data.vfx.LawnEffect;
import model.data.zombie.Zombie;
import model.data.zombie.ZombieType;
import model.data.zombie.abilities.config.ZombieAbilityConfig;
import model.event.events.GraveCreatedEvent;
import model.event.events.ZombieSpawnedEvent;
import model.rule.rules.chapter.FrostbiteCavesRules;

public class ZombossAbility implements ZombieAbilityConfig {
    private static final int MOVE_COOLDOWN = 4 * GameLoop.TICKS_PER_SECOND;
    private static final int SPAWN_COOLDOWN = 18 * GameLoop.TICKS_PER_SECOND;
    private static final int MAX_MINIONS = 3;
    private static final int FIRE_TICKS = 4 * GameLoop.TICKS_PER_SECOND;
    private static final int FIRE_BOMB_TICKS = 18;
    private static final int FIRE_BOMB_FALL_TICKS = 2;
    private static final int FIRE_BOMB_END_TICKS = 9;
    private static final int FIRE_BREATH_TICKS = 18;
    private static final int FIRE_BREATH_END_TICKS = 8;
    private static final int FIREBALL_IMPACT_TICKS = 14;
    private static final int DASH_TICKS = 6;
    private static final int TURBINE_ON_TICKS = 21;
    private static final int TURBINE_LOOP_TICKS = 20;
    private static final int TURBINE_OFF_TICKS = 25;
    private static final int MISSILE_AIM_TICKS = 33;
    private static final int MISSILE_FIRE_TICKS = 18;
    private static final int MISSILE_FALL_TICKS = 3;
    private static final int MISSILE_EXPLOSION_TICKS = 17;
    private static final int ICE_MISSILE_TICKS = 35;
    private static final int ICE_WIND_TICKS = 28;
    private static final int CHILL_WIND_TICKS = 26;
    private static final int PORTAL_START_TICKS = 23;
    private static final int PORTAL_LOOP_TICKS = 20;
    private static final int PORTAL_END_TICKS = 16;
    private static final float HOME_X = GameState.SCREEN_WIDTH - GameState.CELL_WIDTH * 0.4f;
    private static final String EGYPT_MISSILE_PAM =
            "768/INITIAL/EFFECTS/ZOMBOSS_MISSILE_EXPLOSION_EGYPT/ZOMBOSS_MISSILE_EXPLOSION_EGYPT.PAM";
    private static final String TURBINE_WIND_PAM =
            "768/FULL/EFFECTS/ZOMBOSS_TURBINE_WIND/ZOMBOSS_TURBINE_WIND.PAM";
    private static final String DARK_FIREBALL_PAM =
            "768/FULL/EFFECTS/ZOMBOSS_DARK_FIREBALL/ZOMBOSS_DARK_FIREBALL.PAM";
    private static final String ICEAGE_MISSILE_PAM =
            "768/FULL/EFFECTS/ZOMBOSS_MISSILE_EXPLOSION_ICEAGE/ZOMBOSS_MISSILE_EXPLOSION_ICEAGE.PAM";
    private static final String CHILL_WIND_PAM =
            "768/FULL/EFFECTS/FROSTBITE_CHILL_WIND/FROSTBITE_CHILL_WIND.PAM";

    private enum Move {
        SWITCH_ROW,
        FIREBALLS, BURN_ROWS,
        MISSILE, DASH,
        SHARK_PUPS, TURBINE,
        ICE_MISSILE, ICE_WIND
    }

    private enum Busy {
        NONE, DASH_FORWARD, DASH_BACK,
        TURBINE_ON, TURBINE_LOOP, TURBINE_OFF,
        MISSILE_AIM, MISSILE_FIRE,
        PORTAL_START, PORTAL_LOOP, PORTAL_END,
        FIRE_BOMB, FIRE_BOMB_FALL, FIRE_BOMB_END,
        FIRE_BREATH, FIRE_BREATH_END,
        ICE_MISSILE, ICE_WIND
    }

    private final Random rng = new Random();
    private int cooldown = MOVE_COOLDOWN / 2;
    private int spawnCooldown = SPAWN_COOLDOWN;
    private Busy busy = Busy.NONE;
    private int busyTicks;
    private float homeX = HOME_X;
    private int pendingRow;
    private int pendingCol;
    private final List<int[]> pendingShots = new ArrayList<>();
    private boolean won;

    @Override
    public void onTick(Zombie zombie, GameState state, EventBus bus) {
        if (!zombie.isAlive || zombie.type == null || !zombie.type.isZomboss()) {
            return;
        }
        zombie.col = Math.max(0, Math.min(GameState.GRID_COLS - 1,
                (int) (zombie.position.x / GameState.CELL_WIDTH)));
        zombie.syncY();

        if (busy != Busy.NONE) {
            tickBusy(zombie, state, bus);
            return;
        }

        zombie.forceWalkAnim = false;
        tickMinionSpawn(zombie, state, bus);
        if (busy != Busy.NONE) {
            return;
        }
        cooldown--;
        if (cooldown > 0) {
            return;
        }
        cooldown = MOVE_COOLDOWN;
        perform(pickMove(zombie, state), zombie, state, bus);
    }

    @Override
    public void onDeath(Zombie zombie, GameState state, EventBus bus) {
        if (won || state == null) {
            return;
        }
        EventBus publish = bus != null ? bus : (zombie != null ? zombie.eventBus : null);
        if (publish == null) {
            return;
        }
        won = true;
        SessionEnd.win(state, publish);
    }

    private Move pickMove(Zombie zombie, GameState state) {
        List<Move> pool = new ArrayList<>();
        if (zombie.type != ZombieType.ZOMBOT_MAMMOTH) {
            pool.add(Move.SWITCH_ROW);
        }
        switch (zombie.type) {
            case ZOMBOT_DRAGON -> {
                pool.add(Move.FIREBALLS);
                pool.add(Move.BURN_ROWS);
            }
            case ZOMBOT_SPHINX -> {
                if (randomPlant(state) != null) {
                    pool.add(Move.MISSILE);
                }
                pool.add(Move.DASH);
            }
            case ZOMBOT_SHARK -> {
                pool.add(Move.SHARK_PUPS);
                pool.add(Move.TURBINE);
            }
            case ZOMBOT_MAMMOTH -> {
                pool.add(Move.ICE_MISSILE);
                pool.add(Move.ICE_WIND);
            }
            default -> {
            }
        }
        return pool.get(rng.nextInt(pool.size()));
    }

    private void tickMinionSpawn(Zombie boss, GameState state, EventBus bus) {
        if (boss.type == ZombieType.ZOMBOT_MAMMOTH) {
            return;
        }
        spawnCooldown--;
        if (spawnCooldown > 0) {
            return;
        }
        spawnCooldown = SPAWN_COOLDOWN;
        if (boss.type == ZombieType.ZOMBOT_SPHINX) {
            startPortal(boss, state);
        } else {
            spawnMinions(boss, state, bus);
        }
    }

    private void perform(Move move, Zombie zombie, GameState state, EventBus bus) {
        switch (move) {
            case SWITCH_ROW -> switchRow(zombie);
            case FIREBALLS -> startFireballs(zombie, state);
            case BURN_ROWS -> startBurnRows(zombie);
            case MISSILE -> startMissile(zombie, state);
            case DASH -> startDash(zombie, state, bus);
            case SHARK_PUPS -> sharkPups(state, bus);
            case TURBINE -> startTurbine(zombie, state);
            case ICE_MISSILE -> startIceMissile(zombie, state);
            case ICE_WIND -> startIceWind(zombie, state);
        }
    }

    private void tickBusy(Zombie zombie, GameState state, EventBus bus) {
        switch (busy) {
            case DASH_FORWARD -> {
                zombie.forceWalkAnim = true;
                zombie.position.x -= GameState.CELL_WIDTH * 1.2f;
                crushUnderBoss(zombie, state, bus);
                busyTicks--;
                if (busyTicks <= 0 || zombie.position.x <= GameState.CELL_WIDTH * 1.5f) {
                    busy = Busy.DASH_BACK;
                    busyTicks = DASH_TICKS;
                }
            }
            case DASH_BACK -> {
                zombie.forceWalkAnim = true;
                zombie.position.x = homeX;
                busy = Busy.NONE;
                zombie.forceWalkAnim = false;
            }
            case TURBINE_ON -> {
                busyTicks--;
                if (busyTicks <= 0) {
                    busy = Busy.TURBINE_LOOP;
                    busyTicks = TURBINE_LOOP_TICKS;
                    zombie.playAnim("suction_loop", true);
                }
            }
            case TURBINE_LOOP -> {
                pullToward(zombie, state, bus);
                busyTicks--;
                if (busyTicks <= 0) {
                    busy = Busy.TURBINE_OFF;
                    busyTicks = TURBINE_OFF_TICKS;
                    zombie.playAnim("suction_off", false);
                }
            }
            case TURBINE_OFF -> {
                busyTicks--;
                if (busyTicks <= 0) {
                    zombie.clearAnim();
                    busy = Busy.NONE;
                }
            }
            case MISSILE_AIM -> {
                busyTicks--;
                if (busyTicks <= 0) {
                    busy = Busy.MISSILE_FIRE;
                    busyTicks = MISSILE_FIRE_TICKS;
                    zombie.playAnim("rocket_launch", false);
                }
            }
            case MISSILE_FIRE -> {
                if (busyTicks == MISSILE_FALL_TICKS) {
                    addVfx(state, EGYPT_MISSILE_PAM, "missile", pendingRow, pendingCol, MISSILE_FALL_TICKS, false);
                }
                busyTicks--;
                if (busyTicks <= 0) {
                    destroyTile(state, bus, pendingRow, pendingCol);
                    placeGraves(state, bus, 2);
                    addVfx(state, EGYPT_MISSILE_PAM, "missile_explosion", pendingRow, pendingCol,
                            MISSILE_EXPLOSION_TICKS, false);
                    zombie.clearAnim();
                    busy = Busy.NONE;
                }
            }
            case PORTAL_START -> {
                busyTicks--;
                if (busyTicks <= 0) {
                    spawnMinions(zombie, state, bus);
                    busy = Busy.PORTAL_LOOP;
                    busyTicks = PORTAL_LOOP_TICKS;
                    zombie.playAnim("zombie_portal_loop", true);
                }
            }
            case PORTAL_LOOP -> {
                busyTicks--;
                if (busyTicks <= 0) {
                    busy = Busy.PORTAL_END;
                    busyTicks = PORTAL_END_TICKS;
                    zombie.playAnim("zombie_portal_end", false);
                }
            }
            case PORTAL_END -> {
                busyTicks--;
                if (busyTicks <= 0) {
                    zombie.clearAnim();
                    busy = Busy.NONE;
                }
            }
            case FIRE_BOMB -> {
                busyTicks--;
                if (busyTicks <= 0) {
                    for (int[] shot : pendingShots) {
                        addVfx(state, DARK_FIREBALL_PAM, "fall", shot[0], shot[1], FIRE_BOMB_FALL_TICKS, false);
                    }
                    busy = Busy.FIRE_BOMB_FALL;
                    busyTicks = FIRE_BOMB_FALL_TICKS;
                    zombie.playAnim("fire_bomb_loop", true);
                }
            }
            case FIRE_BOMB_FALL -> {
                busyTicks--;
                if (busyTicks <= 0) {
                    impactFireballs(state, bus);
                    busy = Busy.FIRE_BOMB_END;
                    busyTicks = FIRE_BOMB_END_TICKS;
                    zombie.playAnim("fire_bomb_end", false);
                }
            }
            case FIRE_BOMB_END -> {
                busyTicks--;
                if (busyTicks <= 0) {
                    pendingShots.clear();
                    zombie.clearAnim();
                    busy = Busy.NONE;
                }
            }
            case FIRE_BREATH -> {
                busyTicks--;
                if (busyTicks <= 0) {
                    burnRows(zombie, state, bus);
                    busy = Busy.FIRE_BREATH_END;
                    busyTicks = FIRE_BREATH_END_TICKS;
                    zombie.playAnim("fire_attack_end", false);
                }
            }
            case FIRE_BREATH_END -> {
                busyTicks--;
                if (busyTicks <= 0) {
                    zombie.clearAnim();
                    busy = Busy.NONE;
                }
            }
            case ICE_MISSILE -> {
                if (busyTicks == MISSILE_FALL_TICKS) {
                    addVfx(state, ICEAGE_MISSILE_PAM, "missile", pendingRow, pendingCol, MISSILE_FALL_TICKS, false);
                }
                busyTicks--;
                if (busyTicks <= 0) {
                    destroyTile(state, bus, pendingRow, pendingCol);
                    addVfx(state, ICEAGE_MISSILE_PAM, "missile_explosion", pendingRow, pendingCol,
                            MISSILE_EXPLOSION_TICKS, false);
                    zombie.clearAnim();
                    busy = Busy.NONE;
                }
            }
            case ICE_WIND -> {
                busyTicks--;
                if (busyTicks <= 0) {
                    pendingShots.clear();
                    zombie.clearAnim();
                    busy = Busy.NONE;
                }
            }
            default -> busy = Busy.NONE;
        }
    }

    private int countMinions(Zombie boss, GameState state) {
        int livingMinions = 0;
        for (Zombie z : state.zombies) {
            if (z.isAlive && z != boss && z.type != null && !z.type.isZomboss()) {
                livingMinions++;
            }
        }
        return livingMinions;
    }

    private void spawnMinions(Zombie boss, GameState state, EventBus bus) {
        if (countMinions(boss, state) >= MAX_MINIONS) {
            return;
        }
        ZombieType[] pool = minionPool(boss.type);
        int count = 1;
        for (int i = 0; i < count; i++) {
            ZombieType type = pool[rng.nextInt(pool.length)];
            int row = rng.nextInt(GameState.GRID_ROWS);
            Zombie z = new Zombie(type, row, GameState.GRID_COLS - 1,
                    new Position(GameState.SCREEN_WIDTH,
                            GameState.CELL_HEIGHT * row + GameState.CELL_HEIGHT / 2f),
                    bus);
            state.addZombie(z);
            bus.publish(new ZombieSpawnedEvent(z));
        }
    }

    private ZombieType[] minionPool(ZombieType boss) {
        return switch (boss) {
            case ZOMBOT_SPHINX -> new ZombieType[] {
                    ZombieType.BASIC, ZombieType.CONE_HEAD, ZombieType.RA_ZOMBIE,
                    ZombieType.EXPLORER_ZOMBIE, ZombieType.TOMB_RAISER, ZombieType.BUCKET_HEAD
            };
            case ZOMBOT_DRAGON -> new ZombieType[] {
                    ZombieType.BASIC, ZombieType.KNIGHT, ZombieType.JESTER_ZOMBIE,
                    ZombieType.WIZARD_ZOMBIE, ZombieType.CONE_HEAD, ZombieType.IMP_DRAGON,
                    ZombieType.BUCKET_HEAD, ZombieType.KING
            };
            case ZOMBOT_SHARK -> new ZombieType[] {
                    ZombieType.BASIC, ZombieType.SNORKEL_ZOMBIE, ZombieType.OCTOPUS_ZOMBIE,
                    ZombieType.IMP, ZombieType.CONE_HEAD, ZombieType.BUCKET_HEAD,
                    ZombieType.FISHERMAN_ZOMBIE, ZombieType.BARREL_ROLLER
            };
            case ZOMBOT_MAMMOTH -> new ZombieType[] {
                    ZombieType.BASIC, ZombieType.CONE_HEAD, ZombieType.BUCKET_HEAD,
                    ZombieType.HUNTER, ZombieType.TROGLOBITE, ZombieType.DODO_RIDER_ZOMBIE
            };
            default -> new ZombieType[] { ZombieType.BASIC, ZombieType.CONE_HEAD };
        };
    }

    private void switchRow(Zombie zombie) {
        int maxPrimary = GameState.GRID_ROWS - zombie.rowSpan();
        if (maxPrimary <= 0) {
            return;
        }
        int next = rng.nextInt(maxPrimary + 1);
        int guard = 0;
        while (next == zombie.row && guard++ < 8) {
            next = rng.nextInt(maxPrimary + 1);
        }
        zombie.row = next;
        zombie.forceWalkAnim = true;
        zombie.syncY();
    }

    private void startFireballs(Zombie zombie, GameState state) {
        pendingShots.clear();
        int shots = 2 + rng.nextInt(2);
        for (int i = 0; i < shots; i++) {
            pendingShots.add(new int[] { rng.nextInt(GameState.GRID_ROWS), rng.nextInt(GameState.GRID_COLS) });
        }
        busy = Busy.FIRE_BOMB;
        busyTicks = FIRE_BOMB_TICKS;
        zombie.playAnim("fire_bomb", false);
    }

    private void impactFireballs(GameState state, EventBus bus) {
        int impIndex = pendingShots.isEmpty() || rng.nextInt(4) != 0
                ? -1
                : rng.nextInt(pendingShots.size());
        for (int i = 0; i < pendingShots.size(); i++) {
            int[] shot = pendingShots.get(i);
            destroyTile(state, bus, shot[0], shot[1]);
            ignite(state, shot[0], shot[1]);
            addVfx(state, DARK_FIREBALL_PAM, "impact", shot[0], shot[1], FIREBALL_IMPACT_TICKS, false);
            if (i == impIndex) {
                spawnAt(state, bus, ZombieType.IMP_DRAGON, shot[0], shot[1]);
            }
        }
    }

    private void startBurnRows(Zombie zombie) {
        busy = Busy.FIRE_BREATH;
        busyTicks = FIRE_BREATH_TICKS;
        zombie.playAnim("fire_attack", false);
    }

    private void burnRows(Zombie zombie, GameState state, EventBus bus) {
        for (int r = zombie.row; r < zombie.row + zombie.rowSpan(); r++) {
            for (int c = 0; c < GameState.GRID_COLS; c++) {
                destroyTile(state, bus, r, c);
                ignite(state, r, c);
            }
        }
    }

    private void startMissile(Zombie zombie, GameState state) {
        Plant target = randomPlant(state);
        if (target == null) {
            return;
        }
        pendingRow = target.row;
        pendingCol = target.col;
        busy = Busy.MISSILE_AIM;
        busyTicks = MISSILE_AIM_TICKS;
        zombie.playAnim("missile_start", false);
        addVfx(state, EGYPT_MISSILE_PAM, "missile_lock_reticle", pendingRow, pendingCol,
                MISSILE_AIM_TICKS + MISSILE_FIRE_TICKS, true);
    }

    private void startIceMissile(Zombie zombie, GameState state) {
        pendingRow = rng.nextInt(GameState.GRID_ROWS);
        pendingCol = rng.nextInt(GameState.GRID_COLS);
        busy = Busy.ICE_MISSILE;
        busyTicks = ICE_MISSILE_TICKS;
        zombie.playAnim("slingshot", false);
        addVfx(state, ICEAGE_MISSILE_PAM, "missile_lock_reticle", pendingRow, pendingCol,
                ICE_MISSILE_TICKS, true);
    }

    private void startIceWind(Zombie zombie, GameState state) {
        pendingShots.clear();
        int first = rng.nextInt(GameState.GRID_ROWS);
        int second = rng.nextInt(GameState.GRID_ROWS);
        int guard = 0;
        while (second == first && guard++ < 8) {
            second = rng.nextInt(GameState.GRID_ROWS);
        }
        pendingShots.add(new int[] { first });
        if (second != first) {
            pendingShots.add(new int[] { second });
        }
        List<Integer> rows = new ArrayList<>();
        for (int[] shot : pendingShots) {
            rows.add(shot[0]);
            addVfx(state, CHILL_WIND_PAM, "animation", shot[0], 4, CHILL_WIND_TICKS, false);
        }
        FrostbiteCavesRules.applyIceWind(state, rows);
        busy = Busy.ICE_WIND;
        busyTicks = ICE_WIND_TICKS;
        int windLane = Math.min(first + 1, 4);
        zombie.playAnim("wind_" + windLane, false);
    }

    private Plant randomPlant(GameState state) {
        List<Plant> living = new ArrayList<>();
        for (Plant plant : state.plants) {
            if (plant != null && plant.isAlive) {
                living.add(plant);
            }
        }
        if (living.isEmpty()) {
            return null;
        }
        return living.get(rng.nextInt(living.size()));
    }

    private void startPortal(Zombie boss, GameState state) {
        if (countMinions(boss, state) >= MAX_MINIONS) {
            return;
        }
        busy = Busy.PORTAL_START;
        busyTicks = PORTAL_START_TICKS;
        boss.playAnim("zombie_portal_start", false);
    }

    private void startTurbine(Zombie zombie, GameState state) {
        busy = Busy.TURBINE_ON;
        busyTicks = TURBINE_ON_TICKS;
        zombie.playAnim("suction_on", false);
        int windTicks = TURBINE_ON_TICKS + TURBINE_LOOP_TICKS + TURBINE_OFF_TICKS;
        for (int r = zombie.row; r < zombie.row + zombie.rowSpan(); r++) {
            addVfx(state, TURBINE_WIND_PAM, "animation", r, 3, windTicks, true);
            addVfx(state, TURBINE_WIND_PAM, "animation", r, 6, windTicks, true);
        }
    }

    private void addVfx(GameState state, String pam, String clip, int row, int col, int ticks, boolean loop) {
        state.addLawnEffect(new LawnEffect(pam, clip, row, col, ticks, loop));
    }

    private void startDash(Zombie zombie, GameState state, EventBus bus) {
        homeX = zombie.position.x;
        busy = Busy.DASH_FORWARD;
        busyTicks = DASH_TICKS;
        crushUnderBoss(zombie, state, bus);
    }

    private void sharkPups(GameState state, EventBus bus) {
        List<int[]> waterPlants = new ArrayList<>();
        for (int r = 0; r < GameState.GRID_ROWS; r++) {
            for (int c = 0; c < GameState.GRID_COLS; c++) {
                Tile tile = state.getBoard().getTile(r, c);
                if (tile != null && tile.getType() == TileType.WATER && state.getPlantAt(r, c) != null) {
                    waterPlants.add(new int[] { r, c });
                }
            }
        }
        Collections.shuffle(waterPlants, rng);
        int n = Math.min(3, waterPlants.size());
        for (int i = 0; i < n; i++) {
            destroyTile(state, bus, waterPlants.get(i)[0], waterPlants.get(i)[1]);
        }
    }

    private void pullToward(Zombie boss, GameState state, EventBus bus) {
        for (Plant plant : List.copyOf(state.plants)) {
            if (!plant.isAlive || !boss.occupiesRow(plant.row)) {
                continue;
            }
            if (plant.col >= boss.col - 1) {
                plant.kill(state, bus);
            } else {
                state.movePlant(plant, plant.row, plant.col + 1);
            }
        }
        for (Zombie z : List.copyOf(state.zombies)) {
            if (z == boss || !z.isAlive || z.type.isZomboss() || !boss.occupiesRow(z.row)) {
                continue;
            }
            z.position.x += GameState.CELL_WIDTH;
            if (z.position.x >= boss.position.x - GameState.CELL_WIDTH * 0.5f) {
                z.kill(state);
            }
        }
    }

    private void crushUnderBoss(Zombie zombie, GameState state, EventBus bus) {
        float left = zombie.position.x - GameState.CELL_WIDTH * 1.2f;
        float right = zombie.position.x + GameState.CELL_WIDTH * 0.6f;
        for (Plant plant : List.copyOf(state.plants)) {
            if (!plant.isAlive || !zombie.occupiesRow(plant.row)) {
                continue;
            }
            float px = plant.getX();
            if (px >= left && px <= right) {
                plant.kill(state, bus);
            }
        }
    }

    private void destroyTile(GameState state, EventBus bus, int row, int col) {
        while (true) {
            Plant plant = state.getPlantAt(row, col);
            if (plant == null) {
                break;
            }
            plant.kill(state, bus);
        }
    }

    private void ignite(GameState state, int row, int col) {
        Tile tile = state.getBoard().getTile(row, col);
        if (tile != null) {
            tile.ignite(FIRE_TICKS);
        }
    }

    private void spawnAt(GameState state, EventBus bus, ZombieType type, int row, int col) {
        Zombie z = new Zombie(type, row, col,
                new Position((col + 0.5f) * GameState.CELL_WIDTH,
                        row * GameState.CELL_HEIGHT + GameState.CELL_HEIGHT / 2f),
                bus);
        state.addZombie(z);
        bus.publish(new ZombieSpawnedEvent(z));
    }

    private void placeGraves(GameState state, EventBus bus, int count) {
        List<int[]> empty = new ArrayList<>();
        for (int r = 0; r < GameState.GRID_ROWS; r++) {
            for (int c = 0; c < GameState.GRID_COLS; c++) {
                Tile tile = state.getBoard().getTile(r, c);
                if (tile != null && tile.canSetGrave()) {
                    empty.add(new int[] { r, c });
                }
            }
        }
        Collections.shuffle(empty, rng);
        int n = Math.min(count, empty.size());
        for (int i = 0; i < n; i++) {
            Grave grave = new Grave(empty.get(i)[0], empty.get(i)[1], GraveContent.NONE);
            state.addGrave(grave);
            bus.publish(new GraveCreatedEvent(grave));
        }
    }

    @Override
    public ZombieAbilityConfig createInstance(Zombie zombie) {
        return new ZombossAbility();
    }
}
