package model.data.content.minigame;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import model.data.zombie.ZombieType;

/**
 * Purchasable zombies and sun costs for I, Zombie.
 */
public final class IZombieShop {
    private static final Map<ZombieType, Integer> COSTS = new LinkedHashMap<>();

    static {
        COSTS.put(ZombieType.BASIC, 50);
        COSTS.put(ZombieType.CONE_HEAD, 75);
        COSTS.put(ZombieType.BUCKET_HEAD, 125);
        COSTS.put(ZombieType.IMP, 50);
        COSTS.put(ZombieType.NEWSPAPER_ZOMBIE, 100);
    }

    private IZombieShop() {
    }

    public static Map<ZombieType, Integer> getCosts() {
        return Collections.unmodifiableMap(COSTS);
    }

    public static Set<ZombieType> getAvailableTypes() {
        return COSTS.keySet();
    }

    public static boolean isPurchasable(ZombieType type) {
        return type != null && COSTS.containsKey(type);
    }

    public static int getCost(ZombieType type) {
        return COSTS.getOrDefault(type, -1);
    }

    public static int getCheapestCost() {
        return COSTS.values().stream().mapToInt(Integer::intValue).min().orElse(Integer.MAX_VALUE);
    }
}
