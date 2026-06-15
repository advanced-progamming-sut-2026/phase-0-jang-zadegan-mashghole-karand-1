package model.data.zombie.armor.runtime;

import model.data.zombie.armor.config.ZombieArmorConfig;
import model.data.zombie.armor.config.ZombieArmorType;

public class ZombieArmor {
    public final ZombieArmorType type;
    public final boolean enragesOnBreak;
    public int currentHealth;
    public boolean isIntact = true;

    public float speedModifier = 1.0f;

    public ZombieArmor(ZombieArmorConfig config) {
        this.type = config.type;
        this.currentHealth = config.hp;
        this.enragesOnBreak = config.enragesOnBreak;

        if (type == ZombieArmorType.SARCOPHAGUS) {
            this.speedModifier = 0.5f; // Slower while in sarcophagus
        }
    }

    public int absorbDamage(int damage) {
        if (!isIntact)
            return damage;

        if (currentHealth >= damage) {
            currentHealth -= damage;
            return 0;
        } else {
            int remainingDamage = damage - currentHealth;
            currentHealth = 0;
            isIntact = false;

            if (enragesOnBreak) {
                // Handle armor breaking special effects
            }
            // if (type == ZombieArmorType.SARCOPHAGUS) {
            // this.speedModifier = 1.0f;
            // }
            return remainingDamage;
        }
    }

    public boolean isIntact() {
        return isIntact;
    }
}