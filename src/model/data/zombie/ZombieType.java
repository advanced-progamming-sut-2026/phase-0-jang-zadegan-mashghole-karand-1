package model.data.zombie;

import java.util.Collections;
import java.util.List;

import model.data.plant.PlantType;
import model.data.zombie.abilities.config.ZombieAbilityConfig;
import model.data.zombie.armor.config.ZombieArmorConfig;

public enum ZombieType {
        // Ancient Egypt
        MUMMY("ZombieMummyDefault", new ZombieBaseStats(190, 100, 0.185f, 100, 1000), null, Collections.emptyList()),
        MUMMY_CONE("ZombieMummyArmor1Default", new ZombieBaseStats(190, 100, 0.185f, 200, 3000),
                        ZombieArmorConfig.cone(), Collections.emptyList()),
        MUMMY_BUCKET("ZombieMummyArmor2Default", new ZombieBaseStats(190, 100, 0.185f, 400, 4000),
                        ZombieArmorConfig.bucket(), Collections.emptyList()),
        MUMMY_BRICK("ZombieMummyArmor4Default", new ZombieBaseStats(190, 100, 0.185f, 700, 3000),
                        ZombieArmorConfig.brick(), Collections.emptyList());

        public final String alias;
        public final ZombieBaseStats baseStats;
        public final ZombieArmorConfig armorConfig;
        public final List<ZombieAbilityConfig> abilities;

        ZombieType(String alias, ZombieBaseStats baseStats, ZombieArmorConfig armor,
                        List<ZombieAbilityConfig> abilities) {
                this.alias = alias;
                this.baseStats = baseStats;
                this.armorConfig = armor;
                this.abilities = abilities;
        }

        public static ZombieType fromName(String name) {
                if (name == null || name.isEmpty()) {
                        return null;
                }

                for (ZombieType type : ZombieType.values()) {
                        if (type.alias.equalsIgnoreCase(name)) {
                                return type;
                        }
                }
                return null;
        }
}