package model.data.zombie;

import java.util.Collections;
import java.util.List;

import model.data.zombie.abilities.config.ZombieAbilityConfig;
import model.data.zombie.armor.config.ZombieArmorConfig;

public enum ZombieType {
        BASIC("Basic", new ZombieBaseStats(190, 100, 0.185f, 100, 10000), null,
                        Collections.emptyList()),
        CONE_HEAD("ConeHead", new ZombieBaseStats(190, 100, 0.185f, 200, 30000),
                        ZombieArmorConfig.cone(),
                        Collections.emptyList());

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