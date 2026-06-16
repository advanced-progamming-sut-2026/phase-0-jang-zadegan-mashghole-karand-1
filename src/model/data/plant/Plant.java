package model.data.plant;

import java.util.ArrayList;
import java.util.List;

import model.core.EventBus;
import model.core.GameState;
import model.data.plant.abilities.config.PlantAbilityConfig;
import model.data.plant.effects.config.PlantEffectConfig;
import model.data.plant.upgrades.PlantLevelUpgrade;

public class Plant {

    public final int instanceId;
    public final PlantType type;
    public final int row;
    public final int col;
    public final int level;

    public int hp;
    public int cost;
    public int damage;
    public float actionInterval;

    public List<PlantAbilityConfig> abilities = new ArrayList<>();

    public PlantEffectConfig plantFoodEffect;
    public boolean isPlantFoodActive = false;
    public int plantFoodDuration = 0;

    public boolean isAlive = true;

    public EventBus eventBus;

    private static int nextId = 0;

    public Plant(PlantType type, int row, int col, int level, EventBus bus) {
        this.instanceId = nextId++;
        this.type = type;
        this.row = row;
        this.col = col;
        this.level = Math.min(level, 4);

        this.hp = type.baseStats.hp;
        this.cost = type.baseStats.cost;
        this.damage = type.baseStats.damage;
        this.actionInterval = type.baseStats.actionInterval;

        this.eventBus = bus;

        applyLevelUpgrades();

        for (PlantAbilityConfig def : type.abilities) {
            PlantAbilityConfig ability = def.createInstance(this);
            if (ability != null) {
                abilities.add(ability);
                // ability.onAttach(this);
            }
        }

        if (type.plantFoodEffect != null) {
            this.plantFoodEffect = type.plantFoodEffect.createInstance(this);
        }
    }

    private void applyLevelUpgrades() {
        List<PlantLevelUpgrade> upgrades = type.levelUpgrades.getForLevel(level);

        for (PlantLevelUpgrade upgrade : upgrades) {
            switch (upgrade.stat) {
                case HP:
                    this.hp += upgrade.value;
                    break;
                case DAMAGE:
                    this.damage += upgrade.value;
                    break;
                case COST:
                    this.cost = Math.max(0, this.cost + upgrade.value);
                    break;
                case COOLDOWN:
                    // Cooldown reduction handled by abilities when created
                    break;
                case RANGE:
                    // Range increase handled by abilities when created
                    break;
                case PIERCE_COUNT:
                    // Pierce count handled by abilities when created
                    break;
                case DOUBLE_SUN_CHANCE:
                    // Handled by SunProduceAbility
                    break;
                // ...
            }
        }
    }

    public void activatePlantFood() {
        if (plantFoodEffect != null && !isPlantFoodActive) {
            isPlantFoodActive = true;
            // plantFoodDuration = plantFoodEffect.getDuration();
            // plantFoodEffect.onActivate(this);
        }
    }

    public void tickPlantFood() {
        if (isPlantFoodActive) {
            plantFoodDuration--;
            // plantFoodEffect.onTick(this, state, bus);

            if (plantFoodDuration <= 0) {
                isPlantFoodActive = false;
                // plantFoodEffect.onDeactivate(this);
            }
        }
    }

    public float getX() {
        return row * GameState.CELL_WIDTH + GameState.CELL_WIDTH / 2;
    }

    public float getY() {
        return col * GameState.CELL_HEIGHT + GameState.CELL_HEIGHT / 2;
    }
}