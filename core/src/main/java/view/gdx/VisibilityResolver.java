package view.gdx;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import model.data.zombie.Zombie;
import model.data.zombie.armor.runtime.ZombieArmor;
import view.gdx.catalog.ArmVisualRecipe;
import view.gdx.catalog.ArmorVisualRecipe;
import view.gdx.catalog.ZombieVisualDef;

public final class VisibilityResolver {
    public Map<String, Boolean> forZombie(Zombie zombie, ZombieVisualDef visual) {
        if (visual == null) {
            return Collections.emptyMap();
        }
        Map<String, Boolean> map = new HashMap<>();
        applyArmor(map, zombie, visual);
        applyLostArm(map, zombie, visual);
        return map.isEmpty() ? Collections.emptyMap() : map;
    }

    private static void applyArmor(Map<String, Boolean> map, Zombie zombie, ZombieVisualDef visual) {
        if (visual.armor == null) {
            return;
        }
        ZombieArmor armor = zombie.armor;
        if (armor == null || !armor.isIntact()) {
            return;
        }

        ArmorVisualRecipe recipe = visual.armor;
        if (recipe.groupPart != null) {
            map.put(recipe.groupPart, true);
        }

        String stagePart = pickStage(recipe, armor);
        if (stagePart != null) {
            map.put(stagePart, true);
        }
    }

    private static void applyLostArm(Map<String, Boolean> map, Zombie zombie, ZombieVisualDef visual) {
        if (!zombie.lostArm || visual.arm == null) {
            return;
        }
        ArmVisualRecipe arm = visual.arm;
        if (arm.hideParts != null) {
            for (String part : arm.hideParts) {
                if (part != null) {
                    map.put(part, false);
                }
            }
        }
        if (arm.bonePart != null) {
            map.put(arm.bonePart, true);
        }
    }

    public Map<String, Boolean> intactArmor(ZombieVisualDef visual) {
        if (visual == null || visual.armor == null) {
            return Collections.emptyMap();
        }
        ArmorVisualRecipe recipe = visual.armor;
        Map<String, Boolean> map = new HashMap<>();
        if (recipe.groupPart != null) {
            map.put(recipe.groupPart, true);
        }
        if (recipe.intactPart != null) {
            map.put(recipe.intactPart, true);
        }
        return map;
    }

    private static String pickStage(ArmorVisualRecipe recipe, ZombieArmor armor) {
        float ratio = armor.type.hp <= 0 ? 0f : (float) armor.currentHealth / armor.type.hp;
        if (ratio > 0.66f) {
            return recipe.intactPart;
        }
        if (ratio > 0.33f) {
            return recipe.midDamagePart != null ? recipe.midDamagePart : recipe.intactPart;
        }
        return recipe.lowDamagePart != null ? recipe.lowDamagePart : recipe.intactPart;
    }
}
