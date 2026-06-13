package model.data.plant;

import java.util.ArrayList;
import java.util.List;

import model.core.EventBus;
import model.core.GameState;
import model.data.plant.abilities.PlantAbilityConfig;
import model.data.plant.effects.PlantEffectConfig;
import model.data.plant.upgrades.PlantLevelUpgrade;

public class Plant {

    public final int instanceId;
    public final PlantType type;
    public final int row;
    public final int col;
    public final int level;

    public int health;
    public int currentCost;
    public int currentDamage;
    public float currentActionInterval;

    public List<PlantAbilityConfig> abilities = new ArrayList<>();

    public PlantEffectConfig plantFoodEffect;
    public boolean isPlantFoodActive = false;
    public int plantFoodDuration = 0;

    public boolean isAlive = true;

    private static int nextId = 0;

    public Plant(PlantType type, int row, int col, int level) {
        this.instanceId = nextId++;
        this.type = type;
        this.row = row;
        this.col = col;
        this.level = Math.min(level, 4);

        this.health = type.baseStats.hp;
        this.currentCost = type.baseStats.cost;
        this.currentDamage = type.baseStats.damage;
        this.currentActionInterval = type.baseStats.actionInterval;

        applyLevelUpgrades();

        for (PlantAbilityConfig def : type.abilities) {
            PlantAbilityConfig ability = def.createInstance(this);
            abilities.add(ability);
            // ability.onAttach(this);
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
                    this.health += upgrade.value;
                    break;
                case DAMAGE:
                    this.currentDamage += upgrade.value;
                    break;
                case COST:
                    this.currentCost = Math.max(0, this.currentCost + upgrade.value);
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

    public void tickPlantFood(GameState state, EventBus bus) {
        if (isPlantFoodActive) {
            plantFoodDuration--;
            // plantFoodEffect.onTick(this, state, bus);

            if (plantFoodDuration <= 0) {
                isPlantFoodActive = false;
                // plantFoodEffect.onDeactivate(this);
            }
        }
    }
}