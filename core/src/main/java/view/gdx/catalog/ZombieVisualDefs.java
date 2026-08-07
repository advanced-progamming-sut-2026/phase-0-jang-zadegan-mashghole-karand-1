package view.gdx.catalog;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import model.data.zombie.ZombieType;

public final class ZombieVisualDefs {
    private static final String EGYPT_BASIC = "768/INITIAL/ZOMBIE/ZOMBIE_EGYPT_BASIC/ZOMBIE_EGYPT_BASIC.PAM";

    private ZombieVisualDefs() {
    }

    public static Map<ZombieType, ZombieVisualDef> create() {
        Map<ZombieType, ZombieVisualDef> zombies = new EnumMap<>(ZombieType.class);

        ArmorVisualRecipe cone = new ArmorVisualRecipe(
                "_zombie_egypt_armor1_states",
                "zombie_armor_cone_norm",
                "zombie_armor_cone_damage_01",
                "zombie_armor_cone_damage_02");
        ArmorVisualRecipe bucket = new ArmorVisualRecipe(
                "_zombie_egypt_armor2_states",
                "zombie_armor_bucket_norm",
                "zombie_armor_bucket_damage_01",
                "zombie_armor_bucket_damage_02");

        zombies.put(ZombieType.BASIC, ZombieVisualDef.plain(EGYPT_BASIC, "idle", "walk", "eat", "die"));
        zombies.put(ZombieType.CONE_HEAD, new ZombieVisualDef(EGYPT_BASIC, "idle", "walk", "eat", "die", cone));
        zombies.put(ZombieType.BUCKET_HEAD, new ZombieVisualDef(EGYPT_BASIC, "idle", "walk", "eat", "die", bucket));

        return Collections.unmodifiableMap(zombies);
    }
}
