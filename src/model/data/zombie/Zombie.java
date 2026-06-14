package model.data.zombie;

import java.util.*;

import model.core.EventBus;
import model.core.GameState;
import model.data.zombie.abilities.config.ZombieAbilityConfig;
import model.data.zombie.armor.runtime.ZombieArmor;
import model.game.Position;

public class Zombie {
    public final int instanceId;
    public final ZombieType type;
    public final int row;
    public final int col;
    public Position position;
    public int hp;
    public boolean isAlive = true;

    public List<ZombieAbilityConfig> abilities = new ArrayList<>();

    public ZombieArmor armor;

    public boolean isFrozen = false;
    public int frozenTicks = 0;
    public boolean isHypnotized = false;

    private static int nextId = 0;

    public Zombie(ZombieType type, int row, int col, Position position) {
        this.instanceId = nextId++;
        this.type = type;
        this.row = row;
        this.col = col;
        this.position = position;
        this.hp = type.baseStats.hp;

        for (ZombieAbilityConfig config : type.abilities) {
            ZombieAbilityConfig ability = config.createInstance(this);
            if (ability != null) {
                abilities.add(ability);
                ability.onAttach(this);
            }
        }

        if (type.armorConfig != null) {
            this.armor = new ZombieArmor(type.armorConfig);
        }
    }

    public void takeDamage(int damage) {
        if (armor != null && armor.isIntact()) {
            damage = armor.absorbDamage(damage);
        }

        this.hp -= damage;

        if (this.hp <= 0) {
            this.isAlive = false;
        }
    }

    public void tick(GameState state, EventBus bus) {
        // if (isFrozen) {
        // frozenTicks--;
        // if (frozenTicks <= 0) {
        // isFrozen = false;
        // }
        // return;
        // }

        for (ZombieAbilityConfig ability : abilities) {
            ability.onTick(this, state, bus);
        }
    }

    public void onDeath(GameState state, EventBus bus) {
        for (ZombieAbilityConfig ability : abilities) {
            ability.onDeath(this, state, bus);
        }
    }
}