package model.data.zombie;

import java.util.Collections;
import java.util.List;

import model.data.zombie.abilities.config.ZombieAbilityConfig;
import model.data.zombie.abilities.runtime.ZombieAllStarAbility;
import model.data.zombie.abilities.runtime.ZombieArcadeAbility;
import model.data.zombie.abilities.runtime.ZombieStealSunAbility;
import model.data.zombie.abilities.runtime.ZombieThrowImpAbility;
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
                        List.of(new ZombieThrowImpAbility())),
        IMP("Imp",new ZombieBaseStats(190,100,0.22f,100,1000),null,
                        Collections.emptyList()),
        ALL_STAR("All Star",new ZombieBaseStats(1100,100,0.16f,1000,3500),null,
                        List.of(new ZombieAllStarAbility())),
        ARCADE_ZOMBIE("Arcade Zombie",new ZombieBaseStats(190,100,0.19f,600,1000),
                        ZombieArmorConfig.bucket(),
                        List.of(new ZombieArcadeAbility())),
        PARASOL_ZOMBIE("Parasol Zombie",new ZombieBaseStats(350,100,0.25f,200,3000),null,
                       Collections.emptyList()),
        TURQUOISE_ZOMBIE("Turquoise Zombie", new ZombieBaseStats(250,100,0.185f,500,3000),null,
                        List.of(new ZombieStealSunAbility())),
        PROSPECTOR_ZOMBIE("Prospector Zombie", new ZombieBaseStats(190,100,0.16f,200,3000),null,
                         List.of(new ZombieThrowImpAbility()));


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