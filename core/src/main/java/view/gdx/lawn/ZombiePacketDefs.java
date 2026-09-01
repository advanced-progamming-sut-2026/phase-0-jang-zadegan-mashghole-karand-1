package view.gdx.lawn;

import java.util.EnumMap;
import java.util.Map;

import model.data.zombie.ZombieType;

public final class ZombiePacketDefs {
    public static final String FALLBACK = "IMAGE_UI_ALMANAC_ZOMBIE_SEED_PKT";

    private static final Map<ZombieType, String> PACKET_IDS = createPacketIds();

    private ZombiePacketDefs() {
    }

    public static String packetId(ZombieType type) {
        if (type == null) {
            return null;
        }
        return PACKET_IDS.getOrDefault(type, FALLBACK);
    }

    public static String packetId(String zombieName) {
        return packetId(ZombieType.fromName(zombieName));
    }

    private static Map<ZombieType, String> createPacketIds() {
        Map<ZombieType, String> map = new EnumMap<>(ZombieType.class);
        map.put(ZombieType.BASIC, FALLBACK);
        map.put(ZombieType.CONE_HEAD, FALLBACK);
        map.put(ZombieType.BUCKET_HEAD, FALLBACK);
        map.put(ZombieType.IMP, FALLBACK);
        map.put(ZombieType.NEWSPAPER_ZOMBIE, FALLBACK);
        return map;
    }
}
