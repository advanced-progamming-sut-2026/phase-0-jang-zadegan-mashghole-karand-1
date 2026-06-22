package model.data.zombie;

import java.util.Collections;
import java.util.List;

import model.data.zombie.abilities.config.ZombieAbilityConfig;
import model.data.zombie.abilities.runtime.*;
import model.data.zombie.armor.config.ZombieArmorConfig;

public enum ZombieType {
        BASIC("Basic", new ZombieBaseStats(190, 100, 0.185f, 100, 1000), null,
                        Collections.emptyList()),
        CONE_HEAD("Cone Head", new ZombieBaseStats(190, 100, 0.185f, 200, 3000),
                        ZombieArmorConfig.cone(),
                        Collections.emptyList()),
        BUCKET_HEAD("Bucket Head",new ZombieBaseStats(190,100,0.185f,400,4000),
                        ZombieArmorConfig.bucket(),
                        Collections.emptyList()),
        BRICK_HEAD("Brick Head",new ZombieBaseStats(190,100,0.185f,700,3000),
                        ZombieArmorConfig.brick(),
                        Collections.emptyList()),
        KNIGHT("Knight",new ZombieBaseStats(190,100,0.185f,550,4500),
                        ZombieArmorConfig.knight_armor(),
                        Collections.emptyList()),
        GARGANTUAR("Gargantuar",new ZombieBaseStats(3600,1500,0.24f,1500,3000),null,
                        List.of(new ZombieThrowImpAbility(), new ZombieKillForwardAbility())),
        IMP("Imp",new ZombieBaseStats(190,100,0.22f,100,1000),null,
                        Collections.emptyList()),
        ALL_STAR("All Star",new ZombieBaseStats(1100,100,0.16f,1000,3500),null,
                        List.of(new ZombieKillForwardAbility())),
        ARCADE_ZOMBIE("Arcade Zombie",new ZombieBaseStats(190,100,0.19f,600,1000),
                        ZombieArmorConfig.bucket(),
                        List.of(new ZombieKillForwardAbility())),
        PARASOL_ZOMBIE("Parasol Zombie",new ZombieBaseStats(350,100,0.25f,200,3000),null,
                        List.of(new ZombieUmbrellaAbility())),
        TURQUOISE_ZOMBIE("Turquoise Zombie", new ZombieBaseStats(250,100,0.185f,500,3000),null,
                        List.of(new ZombieStealSunAbility())),
        PROSPECTOR_ZOMBIE("Prospector Zombie", new ZombieBaseStats(190,100,0.16f,200,3000),null,
                        List.of(new ZombieDynamiteAbility())),
        PIANIST("Pianist Zombie",new ZombieBaseStats(840,4000,0.12f,450,0),null,
                        List.of(new ZombiePianoAbility())),
        NEWSPAPER_ZOMBIE("Newspaper Zombie",new ZombieBaseStats(460,200,0.22f,700,0),
                        ZombieArmorConfig.newspaper(), null),
        //BARREL_ROLLER("Barrel Roller Zombie",new ZombieBaseStats()) no data?

        RA_ZOMBIE("Ra Zombie",new ZombieBaseStats(190, 100, 0.2f,100, 700), null,
                        List.of(new ZombieStealSunAbility())),
        EXPLORER_ZOMBIE("Explorer Zombie",new ZombieBaseStats(250,100,0.25f,250,3000),null,
                        List.of(new ZombieKillForwardAbility())),
        TOMB_RAISER("Tomb Raiser", new ZombieBaseStats(380,100,0.185f,300,2000),null,
                        List.of(new ZombieTombRaiseAbility())),
        DODO_RIDER_ZOMBIE("Dodo Rider Zombie", new ZombieBaseStats(490,100,0.3f,600,3500),null,
                        List.of(new ZombieFlyAbility())),
        HUNTER("Hunter Zombie", new ZombieBaseStats(700,100,0.12f,500,3500),null,
                        List.of(new ZombieThrowAbility())),
        TROGLOBITE("Troglobite", new ZombieBaseStats(470,100,0.185f,600,3500),null,
                        List.of(new ZombieKillForwardAbility())),
        FISHERMAN_ZOMBIE("Fisherman Zombie",new ZombieBaseStats(1000,100,0.185f,700,2500),null,
                        List.of(new ZombieFishingAbility())),
        SNORKEL_ZOMBIE("Snorkel Zombie",new ZombieBaseStats(350,100,0.185f,200,3000), null,
                        List.of(new ZombieSnorkelAbility())),
        OCTOPUS_ZOMBIE("Octopus Zombie", new ZombieBaseStats(910,100,0.12f,900,3500),null,
                        List.of(new ZombieThrowAbility())),
        JESTER_ZOMBIE("Jester Zombie", new ZombieBaseStats(420,100,0.2f,450,3500),null,
                        List.of(new ZombieJesterAbility())),
        WIZARD_ZOMBIE("Wizard Zombie", new ZombieBaseStats(490,100,0.12f,800,3500),null,
                        List.of(new ZombieStunAbility())),
        KING("King", new ZombieBaseStats(1000,100,0f,750,2000),null,List.of(new ZombieKingAbility())),
        IMP_DRAGON("Imp Dragon",new ZombieBaseStats(190,100,0.185f,150,3500),null,List.of(new ZombieImmunityAbility()));


        public final String name;
        public final ZombieBaseStats baseStats;
        public final ZombieArmorConfig armorConfig;
        public final List<ZombieAbilityConfig> abilities;

        ZombieType(String name, ZombieBaseStats baseStats, ZombieArmorConfig armor,
                        List<ZombieAbilityConfig> abilities) {
                this.name = name;
                this.baseStats = baseStats;
                this.armorConfig = armor;
                this.abilities = abilities;
        }

        public static ZombieType fromName(String name) {
                if (name == null || name.isEmpty()) {
                        return null;
                }

                for (ZombieType type : ZombieType.values()) {
                        if (type.name.equalsIgnoreCase(name)) {
                                return type;
                        }
                }
                return null;
        }
}