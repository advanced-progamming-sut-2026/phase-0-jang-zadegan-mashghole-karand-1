package model.data.plant.effects.runtime;

import model.core.EventBus;
import model.core.GameLoop;
import model.core.GameState;
import model.data.plant.Plant;
import model.data.plant.abilities.config.PlantAbilityConfig;
import model.data.plant.abilities.runtime.PlantMagnetAbility;
import model.data.plant.effects.config.PlantEffectConfig;
import model.data.zombie.Zombie;

import java.util.List;

public class PlantMagnetEffect implements PlantEffectConfig {

    private int cooldown = 0;

    public PlantMagnetEffect() {
    }

    @Override
    public PlantEffectConfig createInstance(Plant plant) {
        return new PlantMagnetEffect();
    }

//    @Override
//    public void trigger(Plant plant, GameState state) {
//        List<Zombie> allMetalZombies = state.zombies.stream()
//                .filter(z -> z.isAlive && z.hasMetalArmor)
//                .toList();
//
//        for (Zombie z : allMetalZombies) {
//            z.hasMetalArmor = false;
//        }
//
//        for (PlantAbilityConfig ability : plant.abilities) {
//            if (ability instanceof PlantMagnetAbility magnet) {
//                magnet.resetCooldown();
//            }
//        }
//    }
}
