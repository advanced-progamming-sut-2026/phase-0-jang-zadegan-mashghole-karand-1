package view.gdx.catalog;

import java.util.*;

import model.data.zombie.ZombieType;

public final class ZombieVisualDefs {
    private static final String DEFAULT_ZOMBIES = "768/FULL/ZOMBIE/ZOMBIE_DARK_BASIC/ZOMBIE_DARK_BASIC.PAM";
    private static final String EGYPT_BASIC = "768/INITIAL/ZOMBIE/ZOMBIE_EGYPT_BASIC/ZOMBIE_EGYPT_BASIC.PAM";
    private static final String DEFAULT_GARGANTUAR = "768/FULL/ZOMBIE/DARK_GARGANTUAR/DARK_GARGANTUAR.PAM";
    private static final String IMP_BITCH = "768/FULL/ZOMBIE/ZOMBIE_BEACH_IMP_MERMAID/ZOMBIE_BEACH_IMP_MERMAID.PAM";
    private static final String ALL_STAR = "768/FULL/ZOMBIE/ZOMBIE_MODERN_ALLSTAR/ZOMBIE_MODERN_ALLSTAR.PAM";
    private static final String ARCADE = "768/FULL/ZOMBIE/ZOMBIE_80S_ARCADE/ZOMBIE_80S_ARCADE.PAM";
    private static final String PARASOL = "768/FULL/ZOMBIE/ZOMBIE_LOSTCITY_JANE/ZOMBIE_LOSTCITY_JANE.PAM";
    private static final String TURQUOISE = "768/FULL/ZOMBIE/ZOMBIE_LOSTCITY_CRYSTALSKULL/ZOMBIE_LOSTCITY_CRYSTALSKULL.PAM";
    private static final String PROSPECTOR = "768/FULL/ZOMBIE/ZOMBIE_PROSPECTOR/ZOMBIE_PROSPECTOR.PAM";
    private static final String PIANO = "768/FULL/ZOMBIE/ZOMBIE_PIANO/ZOMBIE_PIANO.PAM";
    private static final String NEWSPAPER = "768/FULL/ZOMBIE/ZOMBIE_MODERN_NEWSPAPER/ZOMBIE_MODERN_NEWSPAPER.PAM";
    private static final String BARREL_ROLLER = "768/FULL/ZOMBIE/ZOMBIE_PIRATE_BARREL_PUSHER/ZOMBIE_PIRATE_BARREL_PUSHER.PAM";
    private static final String RA = "768/INITIAL/ZOMBIE/ZOMBIE_EGYPT_RA/ZOMBIE_EGYPT_RA.PAM";
    private static final String EXPLORER = "768/INITIAL/ZOMBIE/ZOMBIE_EXPLORER/ZOMBIE_EXPLORER.PAM";
    private static final String TOMBRAISER = "768/INITIAL/ZOMBIE/ZOMBIE_EGYPT_TOMBRAISER/ZOMBIE_EGYPT_TOMBRAISER.PAM";
    private static final String DODO_RIDER = "768/FULL/ZOMBIE/ZOMBIE_STPATRICKS_DODORIDER/ZOMBIE_STPATRICKS_DODORIDER.PAM";
    private static final String HUNTER = "768/FULL/ZOMBIE/ZOMBIE_ICEAGE_HUNTER/ZOMBIE_ICEAGE_HUNTER.PAM";
    private static final String TROGLOBITE = "768/FULL/ZOMBIE/ZOMBIE_ICEAGE_TROGLOBITE/ZOMBIE_ICEAGE_TROGLOBITE.PAM";
    private static final String FISHERMAN = "768/FULL/ZOMBIE/ZOMBIE_RAINCOAT/ZOMBIE_RAINCOAT.PAM"; // temporary
    private static final String SNORKEL = "768/FULL/ZOMBIE/ZOMBIE_BEACH_SNORKELER/ZOMBIE_BEACH_SNORKELER.PAM";
    private static final String OCTOPUS = "768/FULL/ZOMBIE/ZOMBIE_BEACH_OCTOPUS/ZOMBIE_BEACH_OCTOPUS.PAM";
    private static final String JESTER = "768/FULL/ZOMBIE/ZOMBIE_JESTER_BIRTHDAY/ZOMBIE_JESTER_BIRTHDAY.PAM";
    private static final String WIZARD = "768/FULL/ZOMBIE/ZOMBIE_EASTER_WIZARD/ZOMBIE_EASTER_WIZARD.PAM";
    private static final String KING = "768/FULL/ZOMBIE/ZOMBIE_ROMAN_HEALER/ZOMBIE_ROMAN_HEALER.PAM";
    private static final String IMP_DRAGON = "768/FULL/ZOMBIE/ZOMBIE_DARK_IMP_DRAGON/ZOMBIE_DARK_IMP_DRAGON.PAM";

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
        ArmorVisualRecipe newspaper = new ArmorVisualRecipe(
                "_zombie_newspaper",
                "_zombie_newspaper",
                "_zombie_newspaper_dmg1",
                "_zombie_newspaper_dmg2");

        zombies.put(ZombieType.BASIC, ZombieVisualDef.plain(DEFAULT_ZOMBIES, "idle", "walk", "eat", "die"));
        zombies.put(ZombieType.CONE_HEAD, new ZombieVisualDef(DEFAULT_ZOMBIES, "idle", "walk", "eat", "die", cone, Collections.emptyList()));
        zombies.put(ZombieType.BUCKET_HEAD, new ZombieVisualDef(DEFAULT_ZOMBIES, "idle", "walk", "eat", "die", bucket, Collections.emptyList()));
        zombies.put(ZombieType.BRICK_HEAD, ZombieVisualDef.plain(EGYPT_BASIC, "idle", "walk", "eat", "die"));
        zombies.put(ZombieType.KNIGHT, ZombieVisualDef.plain(DEFAULT_ZOMBIES, "idle", "walk", "eat", "die"));
        zombies.put(ZombieType.GARGANTUAR, ZombieVisualDef.plain(DEFAULT_GARGANTUAR, "idle", "walk", "eat", "die"));
        zombies.put(ZombieType.IMP, ZombieVisualDef.plain(IMP_BITCH, "idle", "walk", "eat", "die"));
        zombies.put(ZombieType.ALL_STAR, ZombieVisualDef.plain(ALL_STAR, "idle", "walk", "eat", "die"));
        zombies.put(ZombieType.ARCADE_ZOMBIE, new ZombieVisualDef(ARCADE, "idle", "walk", "eat", "die",null,
                List.of(new CompanionVisual("768/FULL/EFFECTS/80S_ARCADE_CABINET/80S_ARCADE_CABINET.PAM"
                        ,"idle", -100,0,true))));
        zombies.put(ZombieType.PARASOL_ZOMBIE, ZombieVisualDef.plain(PARASOL, "idle", "walk", "eat", "die"));
        zombies.put(ZombieType.TURQUOISE_ZOMBIE, ZombieVisualDef.plain(TURQUOISE, "idle", "walk", "eat", "die"));
        zombies.put(ZombieType.PROSPECTOR_ZOMBIE, ZombieVisualDef.plain(PROSPECTOR, "idle", "walk", "eat", "die"));
        // piano PAM has no walk/eat clips
        zombies.put(ZombieType.PIANIST,new ZombieVisualDef(PIANO,"idle","play", "idle","die", null, List.of(
                new CompanionVisual("768/FULL/ZOMBIE/PIANO/PIANO.PAM", "play",-70,0,false))));
        zombies.put(ZombieType.NEWSPAPER_ZOMBIE,
                new ZombieVisualDef(NEWSPAPER, "idle_newspaper", "walk_newspaper", "eat_newspaper", "die", newspaper, Collections.emptyList()));
        zombies.put(ZombieType.BARREL_ROLLER, ZombieVisualDef.plain(BARREL_ROLLER, "idle", "walk", "eat", "die"));
        zombies.put(ZombieType.RA_ZOMBIE, ZombieVisualDef.plain(RA, "idle", "walk", "eat", "die"));
        zombies.put(ZombieType.EXPLORER_ZOMBIE, ZombieVisualDef.plain(EXPLORER, "idle", "walk", "eat", "die"));
        zombies.put(ZombieType.TOMB_RAISER, ZombieVisualDef.plain(TOMBRAISER, "idle", "walk", "eat", "die"));
        zombies.put(ZombieType.DODO_RIDER_ZOMBIE, ZombieVisualDef.plain(DODO_RIDER, "idle", "walk", "eat", "die"));
        zombies.put(ZombieType.HUNTER, ZombieVisualDef.plain(HUNTER, "idle", "walk", "eat", "die"));
        zombies.put(ZombieType.TROGLOBITE, ZombieVisualDef.plain(TROGLOBITE, "idle", "walk", "eat", "die"));
        zombies.put(ZombieType.FISHERMAN_ZOMBIE, ZombieVisualDef.plain(FISHERMAN, "idle", "walk", "eat", "die"));
        zombies.put(ZombieType.SNORKEL_ZOMBIE, ZombieVisualDef.plain(SNORKEL, "idle", "walk", "eat", "die"));
        zombies.put(ZombieType.OCTOPUS_ZOMBIE, ZombieVisualDef.plain(OCTOPUS, "idle", "walk", "eat", "die"));
        zombies.put(ZombieType.JESTER_ZOMBIE, ZombieVisualDef.plain(JESTER, "idle", "walk", "eat", "die"));
        zombies.put(ZombieType.WIZARD_ZOMBIE, ZombieVisualDef.plain(WIZARD, "idle", "walk", "eat", "die"));
        zombies.put(ZombieType.KING, ZombieVisualDef.plain(KING, "idle", "walk", "eat", "die"));
        zombies.put(ZombieType.IMP_DRAGON, ZombieVisualDef.plain(IMP_DRAGON, "idle", "walk", "eat", "die"));
        zombies.put(ZombieType.SUN_ZOMBIE, ZombieVisualDef.plain(DEFAULT_ZOMBIES, "idle", "walk", "eat", "die")); // temporary

        return Collections.unmodifiableMap(zombies);
    }
}
